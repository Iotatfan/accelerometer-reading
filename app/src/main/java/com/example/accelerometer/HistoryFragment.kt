package com.example.accelerometer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HistoryFragment : Fragment() {

    private lateinit var list:RecyclerView
    private lateinit var listAdapter: ListAdapter
    private var location: ArrayList<String> = ArrayList()
    private var activity: ArrayList<String> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.history_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // COntoh nanti hapus
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        location.add("Semarang")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        activity.add("Run")
        initRecyclerView(view)
    }

    private fun initRecyclerView(view: View){
        list = view.findViewById(R.id.list1)
        list.itemAnimator = DefaultItemAnimator()
        list.layoutManager = LinearLayoutManager(this.requireContext())
        listAdapter = ListAdapter(location,activity,list)
        list.adapter = listAdapter
    }
}