package com.sdtechnocrat.ahoy.views.fragments

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdtechnocrat.ahoy.R
import com.sdtechnocrat.ahoy.data.SeasonItem
import com.sdtechnocrat.ahoy.views.ContentDetailsActivity

class SeasonFragment: DialogFragment() {

    private lateinit var seasonList : ArrayList<SeasonItem>
    lateinit var adapter: SeasonAdapter
    private var selectedPos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        seasonList = arguments?.getParcelableArrayList<SeasonItem>("seasons") as ArrayList<SeasonItem>
        selectedPos = arguments?.getInt("selected_season_pos", 0)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog: AlertDialog
        val builder = AlertDialog.Builder(requireContext()/*, android.R.style.Theme_Black_NoTitleBar_Fullscreen*/)
        val view = layoutInflater.inflate(R.layout.custom_season_dialog_layout, null)
        builder.setView(view)
        dialog = builder.create()

        val seasonRecycler: RecyclerView = view.findViewById(R.id.season_recycler)
        val imageViewClose: ImageView = view.findViewById(R.id.image_view_close)

        adapter = SeasonAdapter(seasonList, selectedPos, object : SeasonAdapter.OnItemClickListener {
            override fun onItemClicked(seasonItem: SeasonItem, position: Int) {
                adapter.setSelected(position)
                (requireActivity() as ContentDetailsActivity).setCurrentSeason(position)
                dialog.dismiss()
            }
        })
        seasonRecycler.layoutManager = LinearLayoutManager(requireContext())
        seasonRecycler.adapter = adapter

        imageViewClose.setOnClickListener {
            dialog.dismiss()
        }
        /*val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))*/
        return dialog
    }

    class SeasonAdapter(val list: List<SeasonItem>, private var currentSelected: Int, val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<SeasonAdapter.SeasonViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_season_list_layout, parent, false)
            return SeasonViewHolder(view)
        }

        override fun onBindViewHolder(holder: SeasonViewHolder, position: Int) {
            val item = list[position]

            holder.textView.text = item.title

            if (position == currentSelected) {
                holder.textView.isActivated = true
                val typeface = ResourcesCompat.getFont(holder.textView.context, R.font.sf_ui_text_bold)
                holder.textView.typeface = typeface
            } else {
                holder.textView.isActivated = false
                val typeface = ResourcesCompat.getFont(holder.textView.context, R.font.sf_ui_text_regular)
                holder.textView.typeface = typeface
            }

            holder.bind(item, position, onItemClickListener)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        class SeasonViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val textView: TextView = itemView.findViewById(R.id.textView)

            fun bind(seasonItem: SeasonItem, position: Int, listener: OnItemClickListener) {
                itemView.setOnClickListener {
                    listener.onItemClicked(seasonItem, position)
                }
            }
        }

        fun setSelected(pos: Int) {
            currentSelected = pos
            notifyDataSetChanged()
        }

        interface OnItemClickListener {
            fun onItemClicked(seasonItem: SeasonItem, position: Int)
        }
    }

    companion object {
        const val TAG = "SeasonFragment"
    }
}