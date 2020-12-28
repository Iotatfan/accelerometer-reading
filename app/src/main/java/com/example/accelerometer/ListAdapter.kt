package com.example.accelerometer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.collections.ArrayList

class ListAdapter(location: ArrayList<String>,activity: ArrayList<String>,recyclerView: RecyclerView) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private var locations : ArrayList<String> = location
    private var activity : ArrayList<String> = activity
    private var loading: Boolean = false
    lateinit var onLoadMoreListener: OnLoadMoreListener

//    init {
//        if(recyclerView.layoutManager is LinearLayoutManager){
//            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
//            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    val totalItemCount = linearLayoutManager.itemCount
//                    val lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
//                    if(!loading && totalItemCount - 1 <= lastVisible && lastVisible > location.size - 2){
//                        onLoadMoreListener.onLoadMore()
//                        loading = true
//                    }
//                }
//            })
//        }
//    }
    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v){
        private val locationHistory = v.findViewById<TextView>(R.id.locate)
        private val activityHistory = v.findViewById<TextView>(R.id.activity)

        fun bindUser(location : String,activities: String){
            locationHistory.text = location
            activityHistory.text = activities
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_list,parent,false))
    }

    override fun getItemCount(): Int = locations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        locations[position]?.let { holder.bindUser(it,activity[position]) }
    }

    fun setLoad(){
        loading = false
    }

    interface OnLoadMoreListener {
        fun onLoadMore()
    }
}