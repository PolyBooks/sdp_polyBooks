package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.polybooks.R
import com.github.polybooks.adapter.AdapterFactory
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.database.BookOrdering
import com.github.polybooks.database.BookQuery
import com.github.polybooks.database.Database
import com.github.polybooks.utils.GlobalVariables.EXTRA_BOOKS_QUERY_SETTINGS
import com.github.polybooks.utils.setupNavbar

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

    private val bookDB = Database.bookDatabase

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

        // --- TODO hardcoded : make it dynamic
        setParametersButtons()

        setupNavbar(findViewById(R.id.bottom_navigation), this)
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
        val query: BookQuery = bookDB.queryBooks()

        //These 2 in front for dummy books query
        if (mName.text.isNotEmpty())
            query.searchByTitle(mName.text.toString())

        resultByParameter(query)

        // pass query to new activity
        val querySettings = query.getSettings()
        val intent = Intent(this, ListBooksActivity::class.java)
        intent.putExtra(EXTRA_BOOKS_QUERY_SETTINGS, querySettings)
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