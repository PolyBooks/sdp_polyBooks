package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.polybooks.adapter.AdapterFactory
import com.github.polybooks.com.github.polybooks.FilteringActivity
import com.github.polybooks.core.*
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.github.polybooks.core.database.implementation.SaleQueryBuilder
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.url2json
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore


/**
 * This activity let the users selecting the sorting and filtering the sales by clicking
 * on the corresponding values for each parameter (eg. "Architecture" button for
 * parameter "Field"). If no value is selected for a given parameter, then the
 * query won't be filtered on this parameter
 */
class FilteringSalesActivity: FilteringActivity() {

    private val TAG: String = "FilteringSalesActivity"

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

        // Get a reference to the UI parameters
        mReset = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)

        setParametersButtons()

        setNavBar()

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
//        var query: SaleQuery = DummySalesQuery()
        var builder: SaleQueryBuilder = SaleQueryBuilder()

        //These 2 in front for dummy sales query
        if (mName.text.isNotEmpty())
            builder.searchByTitle(mName.text.toString())

        if (mISBN.text.isNotEmpty())
            builder = builder.searchByISBN(mISBN.text.toString())

        // price
        val minPrice =
            if (mPriceMin.text.isNotEmpty()) mPriceMin.text.toString().toFloat()
            else 0.0f

        val maxPrice =
            if (mPriceMax.text.isNotEmpty()) mPriceMax.text.toString().toFloat()
            else Float.MAX_VALUE

        builder = builder.searchByPrice(minPrice, maxPrice)

        resultByParameter(builder)

        val intent = Intent(this, ListSalesActivity::class.java)
        intent.putExtra(ListSalesActivity.EXTRA_SALE_QUERY, builder.getQuery())
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

    private fun resultByParameter(query: SaleQueryBuilder) {
        resultByParameter(query, mSortParameter) { q, orderings ->
            q.withOrdering(orderings[0])
        }
        resultByParameter(query, mStateParameter) { q, states ->
            q.searchByState(states.toSet())
        }
        resultByParameter(query, mBookConditionParameter) { q, conditions ->
            q.searchByCondition(conditions.toSet())
        }

        val interests: MutableSet<Interest> = mutableSetOf()
        interests.addAll(mFieldParameter.getSelectedValues())
        interests.addAll(mSemesterParameter.getSelectedValues())
        interests.addAll(mCourseParameter.getSelectedValues())
    }

    private fun setNavBar(){
        val navBarListener : BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener{ item ->
                when(item.itemId){
                    R.id.home ->{
                        startActivity(Intent(this, MainActivity::class.java))
                        true
                    }
                    R.id.books ->{
                        startActivity(Intent(this, FilteringBooksActivity::class.java))
                        true
                    }
                    R.id.user_profile ->{
                        // TODO: user sales
                        false
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.sales, navBarListener)
    }

    private fun <T> resultByParameter(
        query: SaleQueryBuilder,
        parameter: Parameter<T>,
        f: (SaleQueryBuilder, List<T>) -> Unit
    ) {
        val values: List<T> = parameter.getSelectedValues()
        if (values.isNotEmpty())
            f(query, values)
    }
}