package com.github.polybooks.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * ParametersAdapter provides a binding for the values items of any filtering or ordering parameter
 * to their corresponding views in the RecyclerView.
 *
 * @param <T>   The values type
 * @param <VH>  The type of the viewHolder, must inherit from ParameterViewHolder<T>
 */
abstract class ParameterAdapter<T, VH: ParameterViewHolder<T>>:
    RecyclerView.Adapter<VH>() {

    /**
     * The adapter may need to know the Context in which he is. By default does nothing
     *
     * @param context : Activity or Service
     */
    open fun setContext(context: Context) {}
}

/**
 * ParameterViewHolder holds the view of an individual parameter value and provides useful functions
 * to interact with the value and its view
 *
 * @param <T>       The values type
 * @param itemView  The view of the value item
 */
abstract class ParameterViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {

    /**
     * Reinitialize the view of the value item
     */
    abstract fun resetItemView()

    /**
     * Get the value if its item view is selected
     *
     * @return value if the corresponding view has been selected, null otherwise
     */
    abstract fun getValueIfSelected(): T?
}
