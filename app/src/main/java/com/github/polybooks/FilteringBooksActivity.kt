package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.core.database.implementation.DummyBookQuery
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.core.database.interfaces.BookQuery
import kotlin.coroutines.EmptyCoroutineContext

/**
 * This activity let the users to select the sorting and filtering parameters
 * by clicking on the corresponding value (eg. "Architecture" button for
 * parameter "Field"). If no value is selected for a given parameter, then the
 * query won't be filtered on this parameter
 *
 * Note : should implement a dynamic version of it soon
 */
class FilteringBooksActivity : AppCompatActivity() {

    private lateinit var mResetParameters : Button
    private lateinit var mResults : Button

    //--- TODO hardcoded parameters: make it dynamic
    private lateinit var mSortGroup : RadioGroup
    private lateinit var mSortPopularity : RadioButton
    private lateinit var mSortTitleInc : RadioButton
    private lateinit var mSortTitleDec : RadioButton

    private lateinit var mFieldCS : CheckBox
    private lateinit var mFieldBio : CheckBox
    private lateinit var mFieldArchi : CheckBox

    private lateinit var mSemBa1 : CheckBox
    private lateinit var mSemBa2 : CheckBox
    private lateinit var mSemBa3 : CheckBox
    private lateinit var mSemMa1 : CheckBox
    private lateinit var mSemMa2 : CheckBox

    private lateinit var mCourseCS306 : CheckBox
    private lateinit var mCourseCOM480 : CheckBox

    //------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_books)

        // Get a reference to the UI parameters
        mResetParameters = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)

        // --- TODO hardcoded : make it dynamic
        setParametersButtons()
    }

    fun resetParameters(view: View) {
        mSortGroup.clearCheck()

        mFieldCS.isChecked = false
        mFieldBio.isChecked = false
        mFieldArchi.isChecked = false
        mSemBa1.isChecked = false
        mSemBa2.isChecked = false
        mSemBa3.isChecked = false
        mSemMa1.isChecked = false
        mSemMa2.isChecked = false
        mCourseCS306.isChecked = false
        mCourseCOM480.isChecked = false
    }

    fun getResults(view: View) {
        var query : BookQuery = DummyBookQuery()
                .onlyIncludeInterests(getInterests())
                .withOrdering(getOrdering())

        val intent : Intent = Intent(this, ListSalesActivity::class.java)
        intent.putExtra(ListSalesActivity.EXTRA_BOOKS_QUERY, query)
        startActivity(intent)
    }

    private fun setParametersButtons() {
        mSortGroup = findViewById(R.id.sort_group)
        mSortPopularity = findViewById(R.id.popularity_sort)
        mSortTitleInc = findViewById(R.id.title_inc_sort)
        mSortTitleDec = findViewById(R.id.title_dec_sort)

        mFieldCS = findViewById(R.id.CS)
        mFieldBio = findViewById(R.id.Biology)
        mFieldArchi = findViewById(R.id.Archi)

        mSemBa1 = findViewById(R.id.ic_ba1)
        mSemBa2 = findViewById(R.id.ma_ba2)
        mSemBa3 = findViewById(R.id.sv_ba3)
        mSemMa1 = findViewById(R.id.gc_ma1)
        mSemMa2 = findViewById(R.id.mt_ma2)

        mCourseCS306 = findViewById(R.id.CS306)
        mCourseCOM480 = findViewById(R.id.COM480)
    }

    private fun getOrdering() : BookOrdering {
        if (mSortPopularity.isChecked) return BookOrdering.DEFAULT //add it when available
        if (mSortTitleDec.isChecked) return BookOrdering.TITLE_DEC //add it when available
        if (mSortTitleInc.isChecked) return BookOrdering.TITLE_INC //add it when available
        else return BookOrdering.DEFAULT
    }

    private fun getInterests() : Set<Interest> {
        val interests = mutableSetOf<Interest>()
        if (mFieldCS.isChecked) interests.add(Field(mFieldCS.text.toString()))
        if (mFieldBio.isChecked) interests.add(Field(mFieldBio.text.toString()))
        if (mFieldArchi.isChecked) interests.add(Field(mFieldArchi.text.toString()))

        if (mCourseCOM480.isChecked) interests.add(Course(mCourseCOM480.text.toString()))
        if (mCourseCS306.isChecked) interests.add(Course(mCourseCS306.text.toString()))

        if (mSemBa1.isChecked) interests.add(Semester(mSemBa1.text.toString(), "?"))
        if (mSemBa2.isChecked) interests.add(Semester(mSemBa2.text.toString(), "?"))
        if (mSemBa3.isChecked) interests.add(Semester(mSemBa3.text.toString(), "?"))
        if (mSemMa1.isChecked) interests.add(Semester(mSemMa1.text.toString(), "?"))
        if (mSemMa2.isChecked) interests.add(Semester(mSemMa2.text.toString(), "?"))

        return interests.toSet()
    }
}