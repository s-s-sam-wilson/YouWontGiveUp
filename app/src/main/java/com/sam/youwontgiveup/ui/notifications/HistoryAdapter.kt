package com.sam.youwontgiveup.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sam.youwontgiveup.R

class HistoryAdapter(
    private val historyList: MutableList<HistoryItem>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtDomain: TextView =
            view.findViewById(R.id.txtDomain)

        val txtTime: TextView =
            view.findViewById(R.id.txtTime)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_history,
                parent,
                false
            )

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val item = historyList[position]

        holder.txtDomain.text = item.domain
        holder.txtTime.text = java.text.SimpleDateFormat(
            "dd MMM yyyy, hh:mm a",
            java.util.Locale.getDefault()
        ).format(
            java.util.Date(item.timestamp)
        )
    }

    override fun getItemCount(): Int {
        return historyList.size
    }
}