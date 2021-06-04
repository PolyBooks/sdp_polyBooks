package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.polybooks.R
import com.github.polybooks.adapter.AdapterFactory
import com.github.polybooks.core.*
import com.github.polybooks.database.*
import com.github.polybooks.database.Database
import com.github.polybooks.database.SaleOrdering
import com.github.polybooks.database.SaleQuery
import com.github.polybooks.utils.GlobalVariables.EXTRA_SALE_QUERY
import com.github.polybooks.utils.setupNavbar


/**
 * This activity let the users selecting the sorting and filtering the sales by clicking
 * on the corresponding values for each parameter (eg. "Architecture" button for
 * parameter "Field"). If no value is selected for a given parameter, then the
 * query won't be filtered on this parameter
 */
class FilteringSalesActivity: FilteringActivity() {

    private val TAG: String = "FilteringSalesActivity"

    private lateinit var saleDB: SaleDatabase

    private lateinit var mReset: Button
    private lateinit var mResults: Button

    private lateinit var mName: EditText
    private lateinit var mISBN: EditText
    private lateinit var mPriceMin: EditText
    private lateinit var mPriceMax: EditText

    private lateinit var mSortParameter: Parameter<SaleOrdering>
    private lateinit var mStateParameter: Parameter<SaleState>
    private lateinit var mBookConditionParameter: Parameter<BookCondition>

    private lateinit var mFieldParameter: Parameter<Field>
    private lateinit var mSemesterParameter: Parameter<Semester>
    private lateinit var mCourseParameter: Parameter<Course>

    // --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_sales)

        saleDB = Database.saleDatabase(applicationContext)
        // Get a reference to the UI parameters
        mReset = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)

        setParametersButtons()

        setupNavbar(findViewById(R.id.bottom_navigation), this)

    }

    fun resetParameters(view: View) {
        // reset the Edit Text views
        mName.text.clear()
        mISBN.text.clear()
        mPriceMin.text.clear()
        mPriceMax.text.clear()

        mSortParameter.resetItemsViews()
        mStateParameter.resetItemsViews()
        mBookConditionParameter.resetItemsViews()

        mFieldParameter.resetItemsViews()
        mSemesterParameter.resetItemsViews()
        mCourseParameter.resetItemsViews()
    }

    fun getResults(view: View) {
        val intent = Intent(this, ListSalesActivity::class.java)
        intent.putExtra(EXTRA_SALE_QUERY, getQuery())
        startActivity(intent)
    }


    private fun setParametersButtons() {
        setTextParameters()

        mSortParameter = Parameter(
            R.id.sale_sort_parameter,
            AdapterFactory.saleSortingAdapter()
        )
        mStateParameter = Parameter(
            R.id.sale_state_parameter,
            AdapterFactory.saleStateAdapter()
        )
        mBookConditionParameter = Parameter(
            R.id.sale_condition_parameter,
            AdapterFactory.saleBookConditionAdapter()
        )
        mFieldParameter = Parameter(
            R.id.field_parameter,
            AdapterFactory.fieldInterestAdapter()
        )
        mSemesterParameter = Parameter(
            R.id.semester_parameter,
            AdapterFactory.semesterInterestAdapter()
        )
        mCourseParameter = Parameter(
            R.id.course_parameter,
            AdapterFactory.courseInterestAdapter()
        )
    }

    private fun setTextParameters() {
        mName = findViewById(R.id.book_name)
        mISBN = findViewById(R.id.book_isbn)
        mPriceMin = findViewById(R.id.price_min)
        mPriceMax = findViewById(R.id.price_max)
    }

    private fun getQuery() : SaleQuery {
        var query = SaleQuery()

        //handle ordering
        query = resultByParameter(query, mSortParameter) { q, orderings ->
            q.withOrdering(orderings[0])
        }

        //handle state and condition
        query = resultByParameter(query, mStateParameter) { q, states ->
            q.searchByState(states)
        }
        query = resultByParameter(query, mBookConditionParameter) { q, conditions ->
            q.searchByCondition(conditions)
        }

        //handle price
        if (mPriceMin.text.isNotEmpty())
            query = query.searchByMinPrice(mPriceMin.text.toString().toFloat())
        if (mPriceMax.text.isNotEmpty())
            query = query.searchByMaxPrice(mPriceMax.text.toString().toFloat())

        //handle book filters
        val interests = mFieldParameter.getSelectedValues() + mSemesterParameter.getSelectedValues() + mCourseParameter.getSelectedValues()
        if (interests.isNotEmpty())
            query = query.searchByInterests(interests)
        if (mName.text.toString().isNotBlank())
            query = query.searchByTitle(mName.text.toString())
        if (mISBN.text.toString().isNotBlank())
            query = query.searchByISBN(mISBN.text.toString())

        return query
    }

    private fun <T> resultByParameter(
        query: SaleQuery,
        parameter: Parameter<T>,
        f: (SaleQuery, List<T>) -> SaleQuery
    ) : SaleQuery {
        val values: List<T> = parameter.getSelectedValues()
        return if (values.isNotEmpty())
            f(query, values)
        else
            query
    }
}