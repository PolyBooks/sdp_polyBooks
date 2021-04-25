package com.github.polybooks.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ParametersAdapter provides a binding for the values items of any filtering parameter to their
 * corresponding views in the RecyclerView.
 *
 * @param <VH>  The type of the viewHolder
 */
abstract class ParameterAdapter<VH: ParameterViewHolder>:
    RecyclerView.Adapter<VH>() {
}

/**
 * ParameterViewHolder holds the view of an individual parameter value and provides useful functions
 * to interact with the value and its view
 *
 * @param itemView  The view of the value item
 */
abstract class ParameterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    /**
     * Reinitialize the view of the value item
     */
    abstract fun resetItemView()

    /**
     * Get the value if its item view is selected
     *
     * @return value if the corresponding view has been selected, null otherwise
     */
    abstract fun getValueIfSelected(): Any?
}
