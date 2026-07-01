package com.sam.youwontgiveup.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sam.youwontgiveup.R
import com.sam.youwontgiveup.database.DatabaseHelper

class UrlAdapter(
    private val urlList: MutableList<UrlItem>,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<UrlAdapter.UrlViewHolder>() {

    inner class UrlViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtUrl: TextView =
            itemView.findViewById(R.id.txtUrl)

        val txtStatus: TextView =
            itemView.findViewById(R.id.txtStatus)

        val switchEnabled: Switch =
            itemView.findViewById(R.id.switchEnabled)

        val btnDelete: ImageButton =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UrlViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_url,
                parent,
                false
            )

        return UrlViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: UrlViewHolder,
        position: Int
    ) {

        val item = urlList[position]

        holder.txtUrl.text = item.url

        holder.switchEnabled.isChecked =
            item.enabled

        holder.txtStatus.text =
            if (item.enabled)
                "Enabled"
            else
                "Disabled"

        holder.switchEnabled.setOnCheckedChangeListener { _, isChecked ->

            item.enabled = isChecked

            databaseHelper.updateUrlStatus(
                item.id,
                isChecked
            )

            holder.txtStatus.text =
                if (isChecked)
                    "Enabled"
                else
                    "Disabled"
        }

        holder.btnDelete.setOnClickListener {

            databaseHelper.deleteUrl(item.id)

            urlList.removeAt(position)

            notifyItemRemoved(position)

            notifyItemRangeChanged(
                position,
                urlList.size
            )
        }
    }

    override fun getItemCount(): Int {
        return urlList.size
    }
}