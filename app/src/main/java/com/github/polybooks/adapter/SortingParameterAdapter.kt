package com.github.polybooks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.github.polybooks.R
import com.github.polybooks.utils.FieldWithName

typealias SortingParameterViewHolder<T> = SortingParameterAdapter<T>.SortingParameterViewHolder<T>

/**
 * An adapter for an sorting parameter
 *
 * @param enumInstance any instance of class T to be able to use T functions
 * @see ParameterAdapter
 */
class SortingParameterAdapter<T: FieldWithName>(enumInstance: T): ParameterAdapter<T, SortingParameterViewHolder<T>>() {

    private val mValues = enumInstance.javaClass.enumConstants.drop(1)
    private var mContext: Context? = null

    private var lastSelectedButton: CheckBox? = null

    override fun getItemCount(): Int = mValues.size

    override fun setContext(context: Context) {
        mContext = context
    }

    /**
     * Holds the view of an sorting parameter value item
     */
    inner class SortingParameterViewHolder<T>(itemView: View): ParameterViewHolder<T>(itemView) {

        val mSortingButton: CheckBox = itemView.findViewById(R.id.sort_by_button)
        var mSortingValue: T? = null

        override fun resetItemView() {
            mSortingButton.isChecked = false
        }

        override fun getValueIfSelected(): T? {
            return if (mSortingButton.isChecked) mSortingValue
            else null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortingParameterViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)

        return SortingParameterViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SortingParameterViewHolder<T>, position: Int) {
        viewHolder.mSortingValue = mValues[position]
        viewHolder.mSortingButton.text = mValues[position].fieldName(mContext)

        viewHolder.mSortingButton.setOnClickListener { v ->
            if (lastSelectedButton != null && lastSelectedButton != v) {
                lastSelectedButton!!.isChecked = false
            }
            lastSelectedButton = v as CheckBox
        }
    }
}
