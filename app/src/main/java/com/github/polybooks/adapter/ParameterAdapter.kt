package com.github.polybooks.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R

typealias ParameterViewHolder<T> = ParameterAdapter<T>.ParameterViewHolder<T>

/**
 * ParametersAdapter provides a binding for the values items of any filtering or ordering parameter
 * to their corresponding views in the RecyclerView.
 *
 * @param <T>   The values type
 */
abstract class ParameterAdapter<T>(
    val itemViewId: Int,
    private val mValues: List<T>
):
    RecyclerView.Adapter<ParameterViewHolder<T>>() {

    companion object ViewTypes {
        const val VIEW_TYPE_CHECKBOX = 0
        const val VIEW_TYPE_RADIOBOX = 1
    }


    // useful in the case of VIEW_TYPE_RADIOBOX viewType
    private var lastSelectedButton: CheckBox? = null


    override fun getItemCount(): Int = mValues.size

    override fun getItemViewType(position: Int): Int {
        return getItemViewType()
    }

    /**
     * Child class must implement this function to provide the type of his value items views
     */
    protected abstract fun getItemViewType(): Int

    private var mContext: Context? = null

    /**
     * The adapter may need to know the Context to get string values
     *
     * @param context : Activity or Service
     */
    // TODO remove because context can be retrieved on bind to viewHolder
//    fun setContext(context: Context) {
//        mContext = context
//    }

    /**
     * Child class must implement this function to provide the names of the parameter's values
     */
    protected abstract fun getValueName(value: T, context: Context?): String


    /**
     * ParameterViewHolder holds the view of an individual parameter value and provides useful functions
     * to interact with the value and its view
     *
     * @param <T>       The values type
     * @param itemView  The view of the value item
     */
    inner class ParameterViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {

        var mValue: T? = null

        var mValueName: String = ""

        val mButton = itemView.findViewById<CheckBox>(itemViewId)

        /**
         * Reinitialize the view of the value item
         */
        fun resetItemView() {
            mButton.isChecked = false
        }

        /**
         * Get the value if its item view is selected
         *
         * @return value if the corresponding view has been selected, null otherwise
         */
        fun getValueIfSelected(): T? {
            return if (mButton.isChecked) mValue
            else null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParameterViewHolder<T> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)
        mContext = parent.context

        return ParameterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParameterViewHolder<T>, position: Int) {
        holder.mValue = mValues[position]
        holder.mButton.text = getValueName(mValues[position], mContext)

        if (getItemViewType() == VIEW_TYPE_RADIOBOX) {
            holder.mButton.setOnClickListener { v ->
                if (lastSelectedButton != null && lastSelectedButton != v) {
                    lastSelectedButton!!.isChecked = false
                }
                lastSelectedButton = v as CheckBox
            }
        }
    }
}
