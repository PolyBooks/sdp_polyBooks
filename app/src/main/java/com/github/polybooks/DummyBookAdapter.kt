package com.github.polybooks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DummyBookAdapter(val listBooks : List<DummyBook>) : RecyclerView.Adapter<DummyBookAdapter.DummyViewHolder>() {
    class DummyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val mId : TextView = itemView.findViewById(R.id.dummy_id)
        val mName : TextView = itemView.findViewById(R.id.dummy_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.dummybook_item, parent, false)
        return DummyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return listBooks.size
    }

    override fun onBindViewHolder(holder: DummyViewHolder, position: Int) {

        val currentItem : DummyBook = listBooks[position]


        holder.mId.setText(currentItem.id.toString())
        holder.mName.setText(currentItem.name)
    }
}