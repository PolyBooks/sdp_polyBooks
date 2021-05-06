package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.polybooks.adapter.AdapterFactory
import com.github.polybooks.com.github.polybooks.FilteringActivity
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.core.database.implementation.FBBookDatabase
import com.github.polybooks.core.database.implementation.OLBookDatabase
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookQuery
import com.github.polybooks.utils.setupNavbar
import com.github.polybooks.utils.url2json
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

/**
 * This activity let the users to select the sorting and filtering parameters
 * by clicking on the corresponding value (eg. "Architecture" button for
 * parameter "Field"). If no value is selected for a given parameter, then the
 * query won't be filtered on this parameter
 *
 * Note : should implement a dynamic version of it soon
 */
class FilteringBooksActivity: FilteringActivity() {

    private val TAG: String = "FilteringBooksActivity"

    // TODO use future global static dbs
    private val firestore = FirebaseFirestore.getInstance()
    private val olBookDB = OLBookDatabase { string -> url2json(string) }
    private val bookDB = FBBookDatabase(firestore, olBookDB)

    private lateinit var mReset: Button
    private lateinit var mResults: Button

    private lateinit var mName: EditText
    private lateinit var mISBN: EditText

    private lateinit var mSortParameter: Parameter<BookOrdering>

    private lateinit var mFieldParameter: Parameter<Field>
    private lateinit var mCourseParameter: Parameter<Course>
    private lateinit var mSemesterParameter: Parameter<Semester>

    //------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_books)

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
                    R.id.sales -> {
                        startActivity(Intent(this, FilteringSalesActivity::class.java))
                        true
                    }
                    R.id.user_profile -> {
                        // TODO: user sales
                        false
                    }
                    else -> true
                }
            }
        setupNavbar(findViewById(R.id.bottom_navigation), this, R.id.books, navBarListener)
    }

    fun resetParameters(view: View) {
        mName.text.clear()
        mISBN.text.clear()

        mSortParameter.resetItemsViews()

        mCourseParameter.resetItemsViews()
        mSemesterParameter.resetItemsViews()
        mFieldParameter.resetItemsViews()
    }

    fun getResults(view: View) {
//        var query: BookQuery = DummyBookQuery()
        var query: BookQuery = bookDB.queryBooks()

        //These 2 in front for dummy books query
        if (mName.text.isNotEmpty())
            query.searchByTitle(mName.text.toString())

        resultByParameter(query)

        // pass query to new activity
        val querySettings = query.getSettings()
        val intent = Intent(this, ListSalesActivity::class.java) //TODO list books activity
        intent.putExtra(ListSalesActivity.EXTRA_BOOKS_QUERY_SETTINGS, querySettings)
        startActivity(intent)
    }

    private fun setParametersButtons() {
        setTextParameters()

        mSortParameter = Parameter(
            R.id.book_sort_parameter,
            AdapterFactory.bookSortingAdapter()
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
    }

    private fun resultByParameter(query: BookQuery) {
        val sortingValues = mSortParameter.getSelectedValues()
        if (sortingValues.isNotEmpty()) {
            query.withOrdering(sortingValues[0])
        }

        val interests: MutableSet<Interest> = mutableSetOf()
        interests.addAll(mFieldParameter.getSelectedValues())
        interests.addAll(mSemesterParameter.getSelectedValues())
        interests.addAll(mCourseParameter.getSelectedValues())
    }
}