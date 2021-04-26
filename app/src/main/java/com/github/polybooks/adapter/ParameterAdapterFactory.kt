package com.github.polybooks.adapter

import com.github.polybooks.R
import com.github.polybooks.core.database.interfaces.SaleOrdering

object ParameterAdapterFactory {

    /**
     * Create and instantiate an adapter for the SaleOrdering sorting parameter
     */
    fun createSaleSortingAdapter(): ParameterAdapter<SaleOrdering> {
        return SortingParameterAdapter<SaleOrdering>(R.id.sort_by_button, SaleOrdering.DEFAULT)
    }
}