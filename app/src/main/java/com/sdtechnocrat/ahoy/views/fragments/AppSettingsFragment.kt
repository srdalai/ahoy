package com.sdtechnocrat.ahoy.views.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.databinding.FragmentAppSettingsBinding
import com.sdtechnocrat.ahoy.databinding.FragmentSubscriptionBinding
import com.sdtechnocrat.ahoy.room.DownloadDao
import com.sdtechnocrat.ahoy.room.DownloadDatabase
import com.sdtechnocrat.ahoy.utilities.SharedPref
import com.sdtechnocrat.ahoy.views.CommonActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class AppSettingsFragment : Fragment() {

    private val TAG = "AppSettingsFragment"

    private var _binding: FragmentAppSettingsBinding? = null
    val binding get() = _binding!!

    lateinit var downloadDatabase: DownloadDatabase
    private lateinit var downloadDao: DownloadDao
    lateinit var sharedPref: SharedPref

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAppSettingsBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        downloadDatabase = DownloadDatabase.getInstance(requireContext())
        downloadDao = downloadDatabase.downloadDao()
        sharedPref = SharedPref.getInstance(requireContext())

        (requireActivity() as CommonActivity).setTitle("App Settings")


        getPreferences()
        setListeners()


        getDirectorySize()
    }

    private fun setListeners() {
        binding.notifications.setOnClickListener {
            sharedPref.setNotificationStatus(!sharedPref.getNotificationStatus())
            getPreferences()
        }
        binding.wifiOnly.setOnClickListener {
            sharedPref.setWifiOnlyStatus(!sharedPref.getWifiOnlyStatus())
            getPreferences()
        }
        binding.smartDownload.setOnClickListener {
            sharedPref.setSmartDownloadStatus(!sharedPref.getSmartDownloadStatus())
            getPreferences()
        }
        binding.deleteAll.setOnClickListener {
            deleteAllDownloads()
        }
    }

    private fun getPreferences() {
        binding.switchNotifications.setImageDrawable(if (sharedPref.getNotificationStatus()) {
            ContextCompat.getDrawable(requireContext(), R.drawable.toggle_on)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.toggle_off)
        })

        binding.switchWifiOnly.setImageDrawable(if (sharedPref.getWifiOnlyStatus()) {
            ContextCompat.getDrawable(requireContext(), R.drawable.toggle_on)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.toggle_off)
        })

        binding.switchSmartDownload.setImageDrawable(if (sharedPref.getSmartDownloadStatus()) {
            ContextCompat.getDrawable(requireContext(), R.drawable.toggle_on)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.toggle_off)
        })
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

        binding.tvDeleteAll.text = "Consuming ~$fileSize on Device"
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

    private fun deleteAllDownloads() {
        GlobalScope.launch {
            downloadDao.deleteAll()
            val dir = requireContext().getDir("downloads", Context.MODE_PRIVATE)
            if (dir.isDirectory) {
                val children: Array<String> = dir.list()
                for (i in children.indices) {
                    File(dir, children[i]).delete()
                }
            }
            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Deleted All Downloads", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}