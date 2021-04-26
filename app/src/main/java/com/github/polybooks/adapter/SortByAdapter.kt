package com.github.polybooks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.github.polybooks.R
import com.github.polybooks.utils.FieldWithName

typealias SortByViewHolder<T> = SortByAdapter<T>.SortByViewHolder<T>

/**
 * An adapter for an ordering parameter
 *
 * @param enumInstance any instance of class T to be able to use T functions
 * @see ParameterAdapter
 */
class SortByAdapter<T: FieldWithName>(enumInstance: T): ParameterAdapter<T, SortByViewHolder<T>>() {

    private val mValues = enumInstance.javaClass.enumConstants.drop(1)
    private var mContext: Context? = null

    private var lastSelectedButton: CheckBox? = null

    override fun getItemCount(): Int = mValues.size

    override fun setContext(context: Context) {
        mContext = context
    }

    /**
     * Holds the view of a SortBy parameter value item
     */
    inner class SortByViewHolder<T>(itemView: View): ParameterViewHolder<T>(itemView) {

        val mSortButton: CheckBox = itemView.findViewById(R.id.sort_by_button)
        var mSortValue: T? = null

        override fun resetItemView() {
            mSortButton.isChecked = false
        }

        override fun getValueIfSelected(): T? {
            return if (mSortButton.isChecked) mSortValue
            else null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortByViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)

        return SortByViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SortByViewHolder<T>, position: Int) {
        viewHolder.mSortValue = mValues[position]
        viewHolder.mSortButton.text = mValues[position].fieldName(mContext)

        viewHolder.mSortButton.setOnClickListener { v ->
            if (lastSelectedButton != null && lastSelectedButton != v) {
                lastSelectedButton!!.isChecked = false
            }
            lastSelectedButton = v as CheckBox
        }
    }
}
