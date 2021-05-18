package com.github.polybooks.adapter

import com.github.polybooks.R
import com.github.polybooks.core.*
import com.github.polybooks.database.BookOrdering
import com.github.polybooks.database.BookQuery
import com.github.polybooks.database.SaleOrdering
import com.github.polybooks.database.SaleQuery

object AdapterFactory {

    private const val VALUE_BUTTON = R.id.parameter_value_button

    /**
     * Create and instantiate an adapter for the SaleOrdering sorting parameter
     *
     * @see SaleQuery.withOrdering
     */
    fun saleSortingAdapter(): ParameterAdapter<SaleOrdering> {
        return SortingParameterAdapter(VALUE_BUTTON, SaleOrdering.DEFAULT)
    }

    /**
     * Create and instantiate an adapter for the SaleState filtering parameter
     *
     * @see SaleQuery.searchByState
     */
    fun saleStateAdapter(): ParameterAdapter<SaleState> {
        return FiniteValuesParameterAdapter(VALUE_BUTTON, SaleState.ACTIVE)
    }

    /**
     * Create and instantiate an adapter for the Sale BookCondition filtering parameter
     *
     * @see SaleQuery.searchByCondition
     */
    fun saleBookConditionAdapter(): ParameterAdapter<BookCondition> {
        return FiniteValuesParameterAdapter(VALUE_BUTTON, BookCondition.NEW)
    }

    /**
     * Create and instantiate an adapter for the SaleOrdering sorting parameter
     *
     * @see BookQuery.withOrdering
     */
    fun bookSortingAdapter(): ParameterAdapter<BookOrdering> {
        return SortingParameterAdapter(VALUE_BUTTON, BookOrdering.DEFAULT)
    }

    /**
     * Create an instantiate an adapter to filter by Course
     *
     * @see SaleQuery.onlyIncludeInterests
     * @see BookQuery.onlyIncludeInterests
     */
    fun courseInterestAdapter(): ParameterAdapter<Course> {
        return InterestsParameterAdapter(VALUE_BUTTON, InterestsParameterAdapter.Interest.COURSE)
    }

    /**
     * Create an instantiate an adapter to filter by Semester
     *
     * @see SaleQuery.onlyIncludeInterests
     * @see BookQuery.onlyIncludeInterests
     */
    fun semesterInterestAdapter(): ParameterAdapter<Semester> {
        return InterestsParameterAdapter(VALUE_BUTTON, InterestsParameterAdapter.Interest.SEMESTER)
    }

    /**
     * Create an instantiate an adapter to filter by Semester
     *
     * @see SaleQuery.onlyIncludeInterests
     * @see BookQuery.onlyIncludeInterests
     */
    fun fieldInterestAdapter(): ParameterAdapter<Field> {
        return InterestsParameterAdapter(VALUE_BUTTON, InterestsParameterAdapter.Interest.FIELD)
    }
}