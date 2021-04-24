package com.github.polybooks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.github.polybooks.R
import com.github.polybooks.core.database.interfaces.SaleOrdering

class SalesSortByAdapter: ParametersAdapter<SalesSortByAdapter.SortByViewHolder>() {

    private val values = SaleOrdering.values().drop(1)

    private var lastSelectedButton: CheckBox? = null

    override fun getItemCount(): Int = values.size

    inner class SortByViewHolder(itemView: View):
        ParametersAdapter<SalesSortByAdapter.SortByViewHolder>.ParameterViewHolder(itemView) {

        val mSortButton: CheckBox = itemView.findViewById(R.id.sort_by_button)
        lateinit var mSortValue: SaleOrdering

        override fun resetItem() {
            mSortButton.isChecked = false
        }

        override fun getItemIfSelected(): Any? {
            return if(mSortButton.isChecked) mSortValue
            else null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortByViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)

        return SortByViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SortByViewHolder, position: Int) {
        viewHolder.mSortValue = values[position]
        viewHolder.mSortButton.text = values[position].orderingName

        viewHolder.mSortButton.setOnClickListener { v ->
            if (lastSelectedButton != null && lastSelectedButton != v) {
                lastSelectedButton!!.isChecked = false
            }
            lastSelectedButton = v as CheckBox
        }
    }

}