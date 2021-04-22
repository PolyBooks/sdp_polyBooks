package com.github.polybooks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.utils.saleOrderingTextValues
import kotlinx.android.synthetic.main.sortby_item.view.*

class SortByAdapter(private val sortByParams: List<SaleOrdering>):
                    RecyclerView.Adapter<SortByAdapter.SortByViewHolder>() {

    private var lastSelectedButton : CheckBox? = null

    inner class SortByViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mSortButton : CheckBox = itemView.findViewById(R.id.sort_by_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortByViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)

        return SortByViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SortByViewHolder, position: Int) {
        viewHolder.mSortButton.text = saleOrderingTextValues[sortByParams[position]]

        viewHolder.mSortButton.setOnClickListener { v ->
            if(lastSelectedButton != null && lastSelectedButton != v) {
                lastSelectedButton!!.isChecked = false
            }
            lastSelectedButton = v as CheckBox
        }
    }

    override fun getItemCount() = sortByParams.size

    fun clearAllParameters() {
        for (i in 0 until itemCount) {
            
        }
    }
}