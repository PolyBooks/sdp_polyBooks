package com.github.polybooks.adapter

import android.content.Context
import com.github.polybooks.utils.FieldWithName

/**
 * An adapter for a filtering parameter, with a static list of filtering values
 *
 * @param itemViewId   view id in the xml layout of a value item
 * @param enumInstance any instance of class T (needed to be able to use methods of T)
 * @see   ParameterAdapter
 */
class StaticValuesFilteringParameterAdapter<T: FieldWithName>(itemViewId: Int, enumInstance: T):
    ParameterAdapter<T>(
        itemViewId,
        enumInstance.javaClass.enumConstants.asList()
    ) {

    override fun getItemViewType(): Int {
        return ViewTypes.VIEW_TYPE_CHECKBOX
    }

    override fun getValueName(value: T, context: Context?): String {
        return value.fieldName(context)
    }
}