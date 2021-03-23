package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.core.database.BookQuery

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
    private lateinit var mQuery : BookQuery
//    private var mCourse : Course? = null
//    private var mField : Field? = null
//    private var mSemester : Semester ? = null

    //--- TODO hardcoded parameters: make it dynamic
    private lateinit var mSortGroup : RadioGroup
    private lateinit var mSortPopularity : RadioButton
    private lateinit var mSortTitleInc : RadioButton
    private lateinit var mSortTitleDec : RadioButton

    private lateinit var mFieldGroup : RadioGroup
    private lateinit var mFieldCS : RadioButton
    private lateinit var mFieldBio : RadioButton
    private lateinit var mFieldArchi : RadioButton

    private lateinit var mSemGroup : RadioGroup
    private lateinit var mSemBa1 : RadioButton
    private lateinit var mSemBa2 : RadioButton
    private lateinit var mSemBa3 : RadioButton
    private lateinit var mSemMa1 : RadioButton
    private lateinit var mSemMa2 : RadioButton

    private lateinit var mCourseGroup : RadioGroup
    private lateinit var mCourseCS306 : RadioButton
    private lateinit var mCourseCOM480 : RadioButton
    //------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_books)

        // Get a reference to the UI parameters
        mResetParameters = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)
//        mQuery = queryBook() use db when merged // TODO : uncomment when DB ready

        // Set behaviour Reset and Results
        setResetButtonBehaviour()
        setResultsButtonBehaviour()

        // --- TODO hardcoded : make it dynamic
        setParametersButtons()
        setParametersListener()
    }

    fun setResetButtonBehaviour() {
        mResetParameters.setOnClickListener {
            mSortGroup.clearCheck()
            mSemGroup.clearCheck()
            mFieldGroup.clearCheck()
            mCourseGroup.clearCheck()
        }
    }

    fun setResultsButtonBehaviour() {
        mResults.setOnClickListener {
            val i : Intent = Intent(this, ListSalesActivity::class.java)
            startActivity(i)
        }
    }

    fun setParametersButtons() {
        mSortGroup = findViewById(R.id.sortGroup)
        mSortPopularity = findViewById(R.id.Popularity_sort)
        mSortTitleInc = findViewById(R.id.Title_inc_sort)
        mSortTitleDec = findViewById(R.id.Title_dec_sort)

        mFieldGroup = findViewById(R.id.fieldGroup)
        mFieldCS = findViewById(R.id.CS)
        mFieldBio = findViewById(R.id.Biology)
        mFieldArchi = findViewById(R.id.Archi)

        mSemGroup = findViewById(R.id.semGroup)
        mSemBa1 = findViewById(R.id.ic_ba1)
        mSemBa2 = findViewById(R.id.ma_ba2)
        mSemBa3 = findViewById(R.id.sv_ba3)
        mSemMa1 = findViewById(R.id.gc_ma1)
        mSemMa2 = findViewById(R.id.mt_ma2)

        mCourseGroup = findViewById(R.id.courseGroup)
        mCourseCS306 = findViewById(R.id.CS306)
        mCourseCOM480 = findViewById(R.id.COM480)
    }

    fun setParametersListener() {
        setClickListener(mSortPopularity)
        setClickListener(mSortTitleInc)
        setClickListener(mSortTitleDec)

        setClickListener(mFieldCS)
        setClickListener(mFieldBio)
        setClickListener(mFieldArchi)

        setClickListener(mSemBa1)
        setClickListener(mSemBa2)
        setClickListener(mSemBa3)
        setClickListener(mSemMa1)
        setClickListener(mSemMa2)
        setClickListener(mCourseCS306)
        setClickListener(mCourseCOM480)
    }

    var setClickListener = { b: RadioButton ->
        b.setOnClickListener {
            when (b.id) {
                mSortPopularity.id -> print("TODO")
                    // mQuery.withOrdering(BookOrdering.DEFAULT)
                mSortTitleInc.id -> print("TODO")
                    // mQuery.withOrdering(BookOrdering.TITLE_INC)
                mSortTitleDec.id -> print("TODO")
                    // mQuery.withOrdering(BookOrdering.TITLE_DEC)
                else -> {
                    val parentId = (b.parent.parent as View).id
                    when (parentId) {
                        mFieldGroup.id -> print("TODO")
                        mSemGroup.id -> print("TODO")
                        mCourseGroup.id -> print("TODO")
                        // mField = Field(b.text)
                        // mSemester = Semester(b.text)
                        // mCourse = Course(b.text)
                    }
                }
            }
        }
    }

}