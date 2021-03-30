package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.core.database.BookOrdering
import com.github.polybooks.core.database.BookQuery
import com.github.polybooks.core.database.SaleOrdering

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
//    private lateinit var mQuery : BookQuery TODO decomment when ready

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

    private var mInterests = mutableSetOf<Interest> ()
    //------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_books)

        // Get a reference to the UI parameters
        mResetParameters = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)
//        mQuery = queryBook() // TODO : uncomment when DB ready

        // Set behaviour Reset and Results
        setResetButtonBehaviour()
        setResultsButtonBehaviour()

        // --- TODO hardcoded : make it dynamic
        setParametersButtons()
        setParametersListener()
    }

    private fun setResetButtonBehaviour() {
        mResetParameters.setOnClickListener {

            mSortGroup.clearCheck()

            mFieldCS.setChecked(false)
            mFieldBio.setChecked(false)
            mFieldArchi.setChecked(false)
            mSemBa1.setChecked(false)
            mSemBa2.setChecked(false)
            mSemBa3.setChecked(false)
            mSemMa1.setChecked(false)
            mSemMa2.setChecked(false)
            mCourseCS306.setChecked(false)
            mCourseCOM480.setChecked(false)

            // TODO reset query
            // mQuery = new Query
        }
    }

    private fun setResultsButtonBehaviour() {
        mResults.setOnClickListener {

//            mQuery.onlyIncludeInterests(mInterests) TODO decomment when ready

            val intent : Intent = Intent(this, ListSalesActivity::class.java)
            //intent.putExtra(ListSalesActivity.EXTRA_SALE_QUERY, mQuery)
            startActivity(intent)
        }
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

    private fun setParametersListener() {
        setRadioButtonClickListener(mSortPopularity)
        setRadioButtonClickListener(mSortTitleInc)
        setRadioButtonClickListener(mSortTitleDec)

        setCheckBoxClickListener(mFieldCS)
        setCheckBoxClickListener(mFieldBio)
        setCheckBoxClickListener(mFieldArchi)

        setCheckBoxClickListener(mSemBa1)
        setCheckBoxClickListener(mSemBa2)
        setCheckBoxClickListener(mSemBa3)
        setCheckBoxClickListener(mSemMa1)
        setCheckBoxClickListener(mSemMa2)
        setCheckBoxClickListener(mCourseCS306)
        setCheckBoxClickListener(mCourseCOM480)
    }

    private var setRadioButtonClickListener = { b: RadioButton ->
        when (b.id) {
            // TODO decomment when ready
//            mSortPopularity.id -> b.setOnClickListener{ mQuery.withOrdering(BookOrdering.POPULARITY) }
//            mSortTitleInc.id -> b.setOnClickListener{ mQuery.withOrdering(BookOrdering.TITLE_INC) }
//            mSortTitleDec.id -> b.setOnClickListener{ mQuery.withOrdering(BookOrdering.TITLE_DEC) }
        }
    }

    private var setCheckBoxClickListener = { b : CheckBox ->
        val addFieldInterest = { b.setOnClickListener {
            if(b.isChecked) { mInterests.add(Field(b.text.toString()))}
            else { mInterests.remove(Field(b.text.toString()))}
        }}

        val addSemesterInterest = { b.setOnClickListener {
            // TODO faux, corriger plus tard
            if(b.isChecked) { mInterests.add(Semester(b.text.toString(),b.text.toString()))}
            else { mInterests.remove(Semester(b.text.toString(),b.text.toString()))}
        }}

        val addCourseInterest = { b.setOnClickListener {
            // TODO faux, corriger plus tard
            if(b.isChecked) { mInterests.add(Course(b.text.toString()))}
            else { mInterests.remove(Course(b.text.toString()))}
        }}

        when(b.id) {
            mFieldCS.id -> addFieldInterest
            mFieldBio.id -> addFieldInterest
            mFieldArchi.id -> addFieldInterest
            mSemBa1.id -> addSemesterInterest
            mSemBa2.id -> addSemesterInterest
            mSemBa3.id -> addSemesterInterest
            mSemMa1.id -> addSemesterInterest
            mSemMa2.id -> addSemesterInterest
            mCourseCS306.id -> addCourseInterest
            mCourseCOM480.id -> addCourseInterest

            else ->{}
        }
    }
}