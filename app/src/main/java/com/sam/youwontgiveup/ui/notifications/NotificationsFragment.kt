package com.sam.youwontgiveup.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sam.youwontgiveup.R
import com.sam.youwontgiveup.database.DatabaseHelper

class NotificationsFragment : Fragment() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var btnClearHistory: Button

    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var historyList: MutableList<HistoryItem>
    private lateinit var adapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_notifications,
            container,
            false
        )

        databaseHelper = DatabaseHelper(requireContext())

        historyList = databaseHelper.getHistory()

        rvHistory = view.findViewById(R.id.rvHistory)
        btnClearHistory = view.findViewById(R.id.btnClearHistory)

        adapter = HistoryAdapter(historyList)

        rvHistory.layoutManager =
            LinearLayoutManager(requireContext())

        rvHistory.adapter = adapter

        btnClearHistory.setOnClickListener {

            databaseHelper.clearHistory()

            historyList.clear()

            historyList.addAll(
                databaseHelper.getHistory()
            )

            adapter.notifyDataSetChanged()
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        historyList.clear()

        historyList.addAll(
            databaseHelper.getHistory()
        )

        adapter.notifyDataSetChanged()
    }
}