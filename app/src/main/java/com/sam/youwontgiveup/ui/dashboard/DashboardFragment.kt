package com.sam.youwontgiveup.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sam.youwontgiveup.R
import com.sam.youwontgiveup.database.DatabaseHelper

class DashboardFragment : Fragment() {

    private lateinit var etUrl: EditText
    private lateinit var btnAddUrl: Button
    private lateinit var rvUrls: RecyclerView

    private lateinit var adapter: UrlAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(
            R.layout.fragment_dashboard,
            container,
            false
        )

        etUrl = view.findViewById(R.id.etUrl)
        btnAddUrl = view.findViewById(R.id.btnAddUrl)
        rvUrls = view.findViewById(R.id.rvUrls)

        val databaseHelper = DatabaseHelper(requireContext())

        val urlList = databaseHelper.getAllUrls()


        adapter = UrlAdapter(urlList, databaseHelper )

        rvUrls.layoutManager =
            LinearLayoutManager(requireContext())

        rvUrls.adapter = adapter

        btnAddUrl.setOnClickListener {

            val url = etUrl.text.toString().trim()

            if (url.isNotEmpty()) {

                databaseHelper.addUrl(
                    url,
                    true
                )

                urlList.clear()

                urlList.addAll(
                    databaseHelper.getAllUrls()
                )

                adapter.notifyDataSetChanged()

                etUrl.setText("")
            }
        }

        return view
    }
}