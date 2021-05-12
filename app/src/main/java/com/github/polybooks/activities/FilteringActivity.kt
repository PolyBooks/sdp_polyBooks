package com.github.polybooks.activities

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.adapter.*

/**
 * FilteringActivity is an abstract class, from which all filtering activities will inherit.
 * It defines an inner class Parameter representing any sorting or filtering parameter
 */
abstract class FilteringActivity: AppCompatActivity() {

    /**
     * A parameter with a set of different values to filter the Sales
     *
     * @param <VH>      A viewHolder holding the individual value items of the
     *                  parameter, need to implement ParameterViewHolder
     * @param viewId    The view id of the RecyclerView holding the values of that parameter
     * @param adapter   The adapter that will binds the different values to the recyclerView
     * @see ParameterAdapter
     */
    inner class Parameter<T>(
        viewId: Int,
        private val mAdapter: ParameterAdapter<T>
    ) {
        private val mView: RecyclerView = findViewById(viewId)
        private val mlayoutManager = LinearLayoutManager(
            this@FilteringActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        init {
            mView.adapter = mAdapter
            mView.layoutManager = mlayoutManager
        }

        /**
         * Reset the views of all the values items of the parameter
         */
        fun resetItemsViews() {
            performOnItems { viewHolder -> viewHolder.resetItemView() }
        }

        /**
         * Get the list of selected values
         */
        fun getSelectedValues(): List<T> {
            val res = mutableListOf<T>()
            performOnItems { viewHolder ->
                val item = viewHolder.getValueIfSelected()
                if (item != null) {
                    res.add(item)
                }
            }

            return res
        }

        private fun performOnItems(f: (ParameterViewHolder<T>) -> Unit) {
            for (i in 0 until mAdapter.itemCount) {
                val holder = mView.findViewHolderForAdapterPosition(i)
                if (holder != null) {
                    f(holder as ParameterViewHolder<T>)
                }
            }
        }
    }
}