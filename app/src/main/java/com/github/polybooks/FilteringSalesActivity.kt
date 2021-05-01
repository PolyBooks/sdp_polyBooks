package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.adapter.*
import com.github.polybooks.core.*
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery
import com.github.polybooks.utils.setupNavbar
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Activity to filter the sales existing in the database given different parameters
 */
class FilteringSalesActivity: AppCompatActivity() {

    private val TAG: String = "FilteringSalesActivity"

    /**
     * A parameter with a set of different values to filter the Sales
     *
     * @param <VH>      A viewHolder holding the individual value items of the
     *                  parameter, need to implement ParameterViewHolder
     * @param viewId    The view id of the RecyclerView holding the values of that parameter
     * @param adapter   The adapter that will binds the different values to the recyclerView
     * @see ParameterAdapter
     */
    inner class RecyclerViewParameter<T>(
        viewId: Int,
        private val mAdapter: ParameterAdapter<T>
    ) {
        private val mView: RecyclerView = findViewById(viewId)
        private val mlayoutManager = LinearLayoutManager(
            this@FilteringSalesActivity,
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

    private lateinit var mReset: Button
    private lateinit var mResults: Button

    //--- hardcoded parameters: make it dynamic
    private lateinit var mName: EditText
    private lateinit var mISBN: EditText
    private lateinit var mPriceMin: EditText
    private lateinit var mPriceMax: EditText

    private lateinit var mSortParameter: RecyclerViewParameter<SaleOrdering>
    private lateinit var mStateParameter: RecyclerViewParameter<SaleState>
    private lateinit var mBookConditionParameter: RecyclerViewParameter<BookCondition>

    private lateinit var mCourseParameter: RecyclerViewParameter<Course>
    private lateinit var mSemesterParameter: RecyclerViewParameter<Semester>
    private lateinit var mFieldParameter: RecyclerViewParameter<Field>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_sales)

        // Get a reference to the UI parameters
        mReset = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)

        setParametersButtons()

        val navBarListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.books -> {
                        startActivity(Intent(this, FilteringBooksActivity::class.java))
                        true
                    }
                    R.id.user_profile -> {
                        // TODO: user sales
                        false
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.sales, navBarListener)
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

        mCourseParameter.resetItemsViews()
        mSemesterParameter.resetItemsViews()
        mFieldParameter.resetItemsViews()
    }

    fun getResults(view: View) {
        var query: SaleQuery = DummySalesQuery()

        //These 2 in front for dummy sales query
        if (mName.text.isNotEmpty())
            query.searchByTitle(mName.text.toString())

        /*
        if(mISBN.text.isNotEmpty())
            query = query.searchByTitle(mISBN.text.toString())
        */

        // price
        val minPrice =
            if (mPriceMin.text.isNotEmpty()) mPriceMin.text.toString().toFloat()
            else 0.0f

        val maxPrice =
            if (mPriceMax.text.isNotEmpty()) mPriceMax.text.toString().toFloat()
            else Float.MAX_VALUE

        query = query.searchByPrice(minPrice, maxPrice)

        resultByParameter(query)

        val querySettings = query.getSettings()
        val intent = Intent(this, ListSalesActivity::class.java)
        intent.putExtra(ListSalesActivity.EXTRA_SALE_QUERY_SETTINGS, querySettings)
        startActivity(intent)
    }


    private fun setParametersButtons() {
        setTextParameters()

        mSortParameter = RecyclerViewParameter(
            R.id.sale_sort_parameter,
            AdapterFactory.saleSortingAdapter()
        )
        mStateParameter = RecyclerViewParameter(
            R.id.sale_state_parameter,
            AdapterFactory.saleStateAdapter()
        )
        mBookConditionParameter = RecyclerViewParameter(
            R.id.sale_condition_parameter,
            AdapterFactory.saleBookConditionAdapter()
        )
        mCourseParameter = RecyclerViewParameter(
            R.id.sale_course_parameter,
            AdapterFactory.courseInterestAdapter()
        )
        mSemesterParameter = RecyclerViewParameter(
            R.id.sale_semester_parameter,
            AdapterFactory.semesterInterestAdapter()
        )
        mFieldParameter = RecyclerViewParameter(
            R.id.sale_field_parameter,
            AdapterFactory.fieldInterestAdapter()
        )
    }

    private fun setTextParameters() {
        mName = findViewById(R.id.book_name)
        mISBN = findViewById(R.id.book_isbn)
        mPriceMin = findViewById(R.id.price_min)
        mPriceMax = findViewById(R.id.price_max)
    }

    private fun resultByParameter(query: SaleQuery) {
        resultByParameter(query, mSortParameter) { q, orderings ->
            q.withOrdering(orderings[0])
        }
        resultByParameter(query, mStateParameter) { q, states ->
            q.searchByState(states.toSet())
        }
        resultByParameter(query, mBookConditionParameter) { q, conditions ->
            q.searchByCondition(conditions.toSet())
        }
        resultByParameter(query, mCourseParameter) { q, courses ->
            q.onlyIncludeInterests(courses.toSet())
        }
    }

    private fun <T> resultByParameter(
        query: SaleQuery,
        parameter: RecyclerViewParameter<T>,
        f: (SaleQuery, List<T>) -> Unit
    ) {
        val values: List<T> = parameter.getSelectedValues()
        if (values.isNotEmpty())
            f(query, values)
    }
}