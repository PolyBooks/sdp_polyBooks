package com.github.polybooks.adapter

import com.github.polybooks.R
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.interfaces.*

object AdapterFactory {

    private const val VALUE_BUTTON = R.id.parameter_value_button

    /**
     * Create and instantiate an adapter for the SaleOrdering sorting parameter
     *
     * @see SaleDatabase
     */
    fun saleSortingAdapter(): ParameterAdapter<SaleOrdering> {
        return SortingParameterAdapter(VALUE_BUTTON, SaleOrdering.DEFAULT)
    }

    /**
     * Create and instantiate an adapter for the SaleState filtering parameter
     *
     * @see SaleDatabase
     */
    fun saleStateAdapter(): ParameterAdapter<SaleState> {
        return StaticValuesFilteringParameterAdapter(VALUE_BUTTON, SaleState.ACTIVE)
    }

    /**
     * Create and instantiate an adapter for the Sale BookCondition filtering parameter
     *
     * @see SaleDatabase
     */
    fun saleBookConditionAdapter() : ParameterAdapter<BookCondition> {
        return StaticValuesFilteringParameterAdapter(VALUE_BUTTON, BookCondition.NEW)
    }
}