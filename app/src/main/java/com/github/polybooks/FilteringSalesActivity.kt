package com.github.polybooks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.github.polybooks.core.*
import com.github.polybooks.core.database.DummySalesQuery //TODO @josh
import com.github.polybooks.core.database.SaleOrdering

class FilteringSalesActivity : AppCompatActivity() {

    private lateinit var mReset : Button
    private lateinit var mResults : Button
    private lateinit var mQuery : DummySalesQuery

    //--- hardcoded parameters: make it dynamic
    private lateinit var mName : EditText
    private lateinit var mISBN : EditText
    private lateinit var mPriceMin : EditText
    private lateinit var mPriceMax : EditText
    private var mMinPrice : Float = 0.0f
    private var mMaxPrice : Float = 0.0f

    private lateinit var mSortGroup : RadioGroup
    private lateinit var mSortTitleInc : RadioButton
    private lateinit var mSortTitleDec : RadioButton
    private lateinit var mSortPriceInc : RadioButton
    private lateinit var mSortPriceDec : RadioButton
    private lateinit var mSortPublishDateInc : RadioButton
    private lateinit var mSortPublishDateDec : RadioButton

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

    private lateinit var mStateActive : CheckBox
    private lateinit var mStateRetracted : CheckBox
    private lateinit var mStateConcluded : CheckBox
    private var mStates = mutableSetOf<SaleState>()

    private lateinit var mConditionNew : CheckBox
    private lateinit var mConditionGood : CheckBox
    private lateinit var mConditionWorn : CheckBox
    private var mConditions = mutableSetOf<BookCondition>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_sales)

        // Get a reference to the UI parameters
        mReset = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)
        mQuery = DummySalesQuery() //TODO @josh

        // Set behaviour Reset and Results
        setResetButtonBehaviour()
        setResultsButtonBehaviour()

        // hardcoded : make it dynamic
        setParametersButtons()
        setParametersListener()
    }

    private fun setResetButtonBehaviour() {
        mReset.setOnClickListener {
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
            mCourseCOM480.setChecked(false)
            mStateActive.setChecked(false)
            mStateRetracted.setChecked(false)
            mStateConcluded.setChecked(false)
            mConditionNew.setChecked(false)
            mConditionGood.setChecked(false)
            mConditionWorn.setChecked(false)

            // reset query
            mQuery = DummySalesQuery()
        }
    }

    private fun setResultsButtonBehaviour() {
        mResults.setOnClickListener {
            mQuery.onlyIncludeInterests(mInterests)
            mQuery.searchByState(mStates)
            mQuery.searchByCondition(mConditions)

            val intent : Intent = Intent(this, ListSalesActivity::class.java)
            intent.putExtra(ListSalesActivity.EXTRA_SALE_QUERY, DummySalesQuery())
            //TODO @josh ca fait planter le programme de passer mQuery
            startActivity(intent)
        }
    }

    private fun setParametersButtons() {
        mName = findViewById(R.id.book_name)
        mISBN = findViewById(R.id.book_isbn)
        mPriceMin = findViewById(R.id.price_min)
        mPriceMax = findViewById(R.id.price_max)

        mSortGroup = findViewById(R.id.sort_group)
        mSortTitleInc = findViewById(R.id.title_inc_sort)
        mSortTitleDec = findViewById(R.id.title_dec_sort)
        mSortPriceInc = findViewById(R.id.price_inc_sort)
        mSortPriceDec = findViewById(R.id.price_dec_sort)
        mSortPublishDateInc = findViewById(R.id.publish_date_inc_sort)
        mSortPublishDateDec = findViewById(R.id.publish_date_dec_sort)

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

        mStateActive = findViewById(R.id.state_active)
        mStateRetracted = findViewById(R.id.state_retracted)
        mStateConcluded = findViewById(R.id.state_concluded)

        mConditionNew = findViewById(R.id.condition_new)
        mConditionGood = findViewById(R.id.condition_good)
        mConditionWorn = findViewById(R.id.condition_worn)
    }

    private fun setParametersListener() {

        setClickListenerRadioButton(mSortTitleInc)
        setClickListenerRadioButton(mSortTitleDec)

        setClickListenerEditText(mName)
        setClickListenerEditText(mISBN)
        setClickListenerEditText(mPriceMin)
        setClickListenerEditText(mPriceMax)

        setClickListenerCheckBox(mFieldCS)
        setClickListenerCheckBox(mFieldBio)
        setClickListenerCheckBox(mFieldArchi)
        setClickListenerCheckBox(mSemBa1)
        setClickListenerCheckBox(mSemBa2)
        setClickListenerCheckBox(mSemBa3)
        setClickListenerCheckBox(mSemMa1)
        setClickListenerCheckBox(mSemMa2)
        setClickListenerCheckBox(mCourseCS306)
        setClickListenerCheckBox(mCourseCOM480)

        setClickListenerCheckBox(mStateActive)
        setClickListenerCheckBox(mStateConcluded)
        setClickListenerCheckBox(mStateRetracted)

        setClickListenerCheckBox(mConditionNew)
        setClickListenerCheckBox(mConditionGood)
        setClickListenerCheckBox(mConditionWorn)
    }

    private var setClickListenerRadioButton = { b: RadioButton ->
        when (b.id) {
            mSortTitleInc.id -> b.setOnClickListener{ mQuery.withOrdering(SaleOrdering.TITLE_INC) }
            mSortTitleDec.id -> b.setOnClickListener{ mQuery.withOrdering(SaleOrdering.TITLE_DEC) }
            mSortPriceInc.id -> b.setOnClickListener{ mQuery.withOrdering(SaleOrdering.PRICE_INC) }
            mSortPriceDec.id -> b.setOnClickListener{ mQuery.withOrdering(SaleOrdering.PRICE_DEC) }
            mSortPublishDateInc.id -> b.setOnClickListener{ mQuery.withOrdering(SaleOrdering.PUBLISH_DATE_INC) }
            mSortPublishDateDec.id -> b.setOnClickListener{ mQuery.withOrdering(SaleOrdering.PUBLISH_DATE_DEC) }
            else -> {}
        }
    }

    private var setClickListenerEditText = { b : EditText ->
        when (b.id) {
            mName.id -> b.setOnClickListener { mQuery.searchByTitle(b.text.toString()) }
            mISBN.id -> b.setOnClickListener { mQuery.searchByISBN13(b.text.toString()) }

            mPriceMin.id -> b.setOnClickListener {
                mMinPrice = b.text.toString().toFloat()
                mQuery.searchByPrice(mMinPrice,mMaxPrice)
            }

            mPriceMax.id -> b.setOnClickListener {
                mMaxPrice = b.text.toString().toFloat()
                mQuery.searchByPrice(mMinPrice,mMaxPrice)
            }
        }
    }

    private var setClickListenerCheckBox = { b: CheckBox ->
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

        when (b.id) {
            mStateActive.id -> b.setOnClickListener { mStates.add(SaleState.ACTIVE) }
            mStateRetracted.id -> b.setOnClickListener { mStates.add(SaleState.RETRACTED) }
            mStateConcluded.id -> b.setOnClickListener { mStates.add(SaleState.CONCLUDED) }

            mConditionNew.id -> b.setOnClickListener { mConditions.add(BookCondition.NEW) }
            mConditionGood.id -> b.setOnClickListener { mConditions.add(BookCondition.GOOD) }
            mConditionWorn.id -> b.setOnClickListener { mConditions.add(BookCondition.WORN) }

            //--- TODO ATTENTION!! : ne marchera pas pour l'instant!!!! (Interest doit implementer comparable)
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
            //---
            else -> {}
        }
    }
}