package com.github.polybooks.adapter

import android.content.Context
import com.github.polybooks.utils.FieldWithName

/**
 * An adapter for an sorting parameter
 *
 * @param enumInstance any instance of class T to be able to use T functions
 * @see ParameterAdapter
 */
class SortingParameterAdapter<T: FieldWithName>(itemViewId: Int, enumInstance: T):
    ParameterAdapter<T>(
        itemViewId,
        enumInstance.javaClass.enumConstants.drop(1)
    ) {

    override fun getItemViewType(): Int {
        return ViewTypes.VIEW_TYPE_RADIOBOX
    }

    override fun getValueName(value: T, context: Context?): String {
        return value.fieldName(context)
    }
}
