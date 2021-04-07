package com.github.polybooks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.github.polybooks.core.*
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.interfaces.SaleQuery


class FilteringSalesActivity : AppCompatActivity() {

    private lateinit var mReset : Button
    private lateinit var mResults : Button

    //--- hardcoded parameters: make it dynamic
    private lateinit var mName : EditText
    private lateinit var mISBN : EditText
    private lateinit var mPriceMin : EditText
    private lateinit var mPriceMax : EditText

    private lateinit var mSortGroup : RadioGroup
    private lateinit var mSortTitleInc : RadioButton
    private lateinit var mSortTitleDec : RadioButton
    private lateinit var mSortPriceInc : RadioButton
    private lateinit var mSortPriceDec : RadioButton

    private lateinit var mStateActive : CheckBox
    private lateinit var mStateRetracted : CheckBox
    private lateinit var mStateConcluded : CheckBox


    private lateinit var mConditionNew : CheckBox
    private lateinit var mConditionGood : CheckBox
    private lateinit var mConditionWorn : CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_sales)

        // Get a reference to the UI parameters
        mReset = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)

        // Set behaviour Reset and Results
        setResetButtonBehaviour()
        setResultsButtonBehaviour()

        // hardcoded : make it dynamic
        setParametersButtons()
        // setParametersListener()
    }

    private fun setResetButtonBehaviour() {
        mReset.setOnClickListener {
            mSortGroup.clearCheck()

            mStateActive.isChecked = false
            mStateRetracted.isChecked = false
            mStateConcluded.isChecked = false
            mConditionNew.isChecked = false
            mConditionGood.isChecked = false
            mConditionWorn.isChecked = false

            // reset the Edit Text views
            mName.text.clear()
            mISBN.text.clear()
            mPriceMin.text.clear()
            mPriceMax.text.clear()

        }
    }

    private fun setResultsButtonBehaviour() {
        mResults.setOnClickListener {
            
            // reset query
            var query : SaleQuery = DummySalesQuery()
                    .searchByState(getStates())
                    .searchByCondition(getCondition())

            if(mName.text.isNotEmpty())
                query = query.searchByTitle(mName.text.toString())

            if(mISBN.text.isNotEmpty())
                query = query.searchByTitle(mISBN.text.toString())

            // price
            val minPrice =
                    if(mPriceMin.text.isNotEmpty()) mPriceMin.text.toString().toFloat()
                    else 0.0f

            val maxPrice =
                    if(mPriceMax.text.isNotEmpty()) mPriceMax.text.toString().toFloat()
                    else Float.MAX_VALUE

            query = query.searchByPrice(minPrice,maxPrice)
            //---

            val intent : Intent = Intent(this, ListSalesActivity::class.java)
            intent.putExtra(ListSalesActivity.EXTRA_SALE_QUERY, query)
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

        mStateActive = findViewById(R.id.state_active)
        mStateRetracted = findViewById(R.id.state_retracted)
        mStateConcluded = findViewById(R.id.state_concluded)

        mConditionNew = findViewById(R.id.condition_new)
        mConditionGood = findViewById(R.id.condition_good)
        mConditionWorn = findViewById(R.id.condition_worn)
    }

    private fun getStates() : Set<SaleState> {
        var state = mutableSetOf<SaleState>()
        if(mStateActive.isChecked) state.add(SaleState.ACTIVE)
        if(mStateConcluded.isChecked) state.add(SaleState.CONCLUDED)
        if(mStateRetracted.isChecked) state.add(SaleState.RETRACTED)
        return if(state.isEmpty()) SaleState.values().toSet() else state.toSet()
    }

    private fun getCondition() : Set<BookCondition> {
        var condition = mutableSetOf<BookCondition>()
        if(mConditionGood.isChecked) condition.add(BookCondition.GOOD)
        if(mConditionNew.isChecked) condition.add(BookCondition.NEW)
        if(mConditionWorn.isChecked) condition.add(BookCondition.WORN)
        return if(condition.isEmpty()) BookCondition.values().toSet() else condition.toSet()
    }

}