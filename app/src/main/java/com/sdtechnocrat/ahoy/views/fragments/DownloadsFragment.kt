package com.sdtechnocrat.ahoy.views.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.adapters.DownloadsAdapter
import com.sdtechnocrat.ahoy.data.DownloadItem
import com.sdtechnocrat.ahoy.databinding.DialogDowloadsActionLayoutBinding
import com.sdtechnocrat.ahoy.databinding.FragmentDownloadsBinding
import com.sdtechnocrat.ahoy.room.DownloadDatabase
import com.sdtechnocrat.ahoy.room.DownloadDbHelperImpl
import com.sdtechnocrat.ahoy.viewmodels.DownloadViewModelFactory
import com.sdtechnocrat.ahoy.viewmodels.DownloadsFragmentViewModel
import com.sdtechnocrat.ahoy.views.CommonActivity
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity
import com.sdtechnocrat.ahoy.views.PlayerActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.net.HttpURLConnection


class DownloadsFragment : Fragment() {

    companion object {
        private const val TAG = "DownloadsFragment"
        private const val BUFFER_LENGTH_BYTES = 1024 * 8
    }

    private var _binding: FragmentDownloadsBinding? = null
    private val binding get() = _binding!!

    var downloadItems = mutableListOf<DownloadItem>()
    lateinit var downloadsAdapter: DownloadsAdapter

    lateinit var viewModel: DownloadsFragmentViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObserver()

        (requireActivity() as CommonActivity).setTitle("Downloads")

        binding.downloadsRecycler.layoutManager = LinearLayoutManager(requireContext())
        downloadsAdapter = DownloadsAdapter(requireContext(), downloadItems, object : DownloadsAdapter.OnItemClickListener {
            override fun onItemClicked(item: DownloadItem) {
                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra("offline_playback", true)
                    putExtra("file_url", item.fileUri)
                }
                startActivity(intent)
            }

            override fun onOptionClicked(position: Int, item: DownloadItem) {
                showOptions(position, item)
            }
        })
        binding.downloadsRecycler.adapter = downloadsAdapter

        getDirectorySize()
    }

    private fun showOptions(position: Int, item: DownloadItem) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val binding = DialogDowloadsActionLayoutBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(binding.root)
        binding.downloadAgain.setOnClickListener {
            downloadContent(item)
            bottomSheetDialog.dismiss()
        }
        binding.deleteDownload.setOnClickListener {
            deleteDownload(item)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun deleteDownload(item: DownloadItem) {
        GlobalScope.launch {
            val file = File(item.fileUri)
            if (file.exists()) {
                file.delete()
                Log.d(TAG, "File Deleted")
            }
            viewModel.delete(item)
            requireActivity().runOnUiThread {
                getDirectorySize()
            }
        }
    }

    private fun downloadContent(item: DownloadItem) {
        if (item.videoUrl.isEmpty()) {
            Toast.makeText(requireContext(), "Unable o Re-download. Delete Item and try to download again from details page", Toast.LENGTH_LONG).show()
            return
        }
        GlobalScope.launch {
            val downloadItem = DownloadItem()

            val uri = downloadFile(downloadItem.fileUri, downloadItem.videoUrl)
            val fileSize: String
            val file = File(uri.toString())
            val fileSizeKb = (file.length() / 1024).toString().toInt()
            fileSize = if (fileSizeKb > 1024) {
                val fileSizeMb = (fileSizeKb / 1024).toString().toInt()
                "$fileSizeMb MB"
            } else {
                "$fileSizeKb KB"
            }

            downloadItem.fileUri = uri.toString()
            downloadItem.fileSize = fileSize
            downloadItem.downloadedSize = fileSize
            viewModel.insertDownload(downloadItem)
        }
    }

    private fun downloadFile(fileName: String, url: String): String? {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        try {
            var targetFile = File(fileName)
            if (targetFile.exists()) {
                targetFile.delete()
                targetFile = File(fileName)
            }

            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()
            val body = response.body()
            val responseCode = response.code()
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE && body != null) {
                //Read the file
                val length = body.contentLength()
                body.byteStream().apply {
                    targetFile.outputStream().use { fileOut ->
                        run {
                            var bytesCopied = 0
                            val buffer = ByteArray(BUFFER_LENGTH_BYTES)
                            var bytes = read(buffer)
                            while (bytes >= 0) {
                                fileOut.write(buffer, 0, bytes)
                                bytesCopied += bytes
                                bytes = read(buffer)
                            }
                        }
                    }
                }
                Log.d(TAG, "Download Completed. Path ->\n" + targetFile.absoluteFile)
                return targetFile.absoluteFile.path
            } else {
                //Report Error
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception -> $e")
            return null
        }
    }

    private fun setupObserver() {
        viewModel.getDownloadItems().observe(viewLifecycleOwner, {
            updateRecyclerView(it)
        })
    }
    private fun updateRecyclerView(list: List<DownloadItem>?) {
        if (list == null) return
        downloadItems.clear()
        downloadsAdapter.notifyDataSetChanged()
        downloadItems.addAll(list)
        downloadsAdapter.notifyItemInserted(0)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this,
            DownloadViewModelFactory(
                DownloadDbHelperImpl(DownloadDatabase.getInstance(requireContext()))
            )
        ).get(DownloadsFragmentViewModel::class.java)
    }

    private fun getDirectorySize() {
        val targetFile = requireContext().getDir("downloads", Context.MODE_PRIVATE)

        val fileSizeBytes = getFolderSize(targetFile)
        val fileSizeKb = (fileSizeBytes / 1024).toString().toInt()
        val fileSize: String = if (fileSizeKb > 1024) {
            val fileSizeMb = (fileSizeKb / 1024).toString().toInt()
            "$fileSizeMb MB"
        } else {
            "$fileSizeKb KB"
        }
        Log.d(TAG, "Folder Size -> $fileSize")

        binding.tvDownloadsSize.text = "Consuming ~$fileSize on Device"
    }

    private fun getFolderSize(f: File): Long {
        var size: Long = 0
        if (f.isDirectory) {
            for (file in f.listFiles()) {
                size += getFolderSize(file)
            }
        } else {
            size = f.length()
        }
        return size
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}