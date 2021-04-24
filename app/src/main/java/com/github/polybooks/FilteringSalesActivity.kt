package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.adapter.SortByAdapter
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.implementation.DummySalesQuery
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.core.database.interfaces.SaleQuery

class FilteringSalesActivity: AppCompatActivity() {

    private val TAG: String = "FilteringSalesActivity"
    private lateinit var mReset: Button
    private lateinit var mResults: Button

    //--- hardcoded parameters: make it dynamic
    private lateinit var mName: EditText
    private lateinit var mISBN: EditText
    private lateinit var mPriceMin: EditText
    private lateinit var mPriceMax: EditText

    private lateinit var mSortView: RecyclerView

    private lateinit var mStateActive: CheckBox
    private lateinit var mStateRetracted: CheckBox
    private lateinit var mStateConcluded: CheckBox

    private lateinit var mConditionNew: CheckBox
    private lateinit var mConditionGood: CheckBox
    private lateinit var mConditionWorn: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filtering_sales)

        // Get a reference to the UI parameters
        mReset = findViewById(R.id.reset_button)
        mResults = findViewById(R.id.results_button)

        mSortView = findViewById(R.id.sort_by)
        mSortView.setHasFixedSize(true)
        mSortView.adapter = SortByAdapter(SaleOrdering.values().drop(1))
        mSortView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // hardcoded : make it dynamic
        setParametersButtons()
    }

    fun resetParameters(view: View) {
        resetSortByButtons()

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

    fun getResults(view: View) {
        var query: SaleQuery = DummySalesQuery()

        checkAndSetOrdering(query)

        //These 2 in front for dummy sales query
        if (mName.text.isNotEmpty())
            query.searchByTitle(mName.text.toString())

        /*
        TODO With ordering
        if(mISBN.text.isNotEmpty())
            query = query.searchByTitle(mISBN.text.toString())
        */
        query.searchByState(getStates())
            .searchByCondition(getCondition())

        // price
        val minPrice =
            if (mPriceMin.text.isNotEmpty()) mPriceMin.text.toString().toFloat()
            else 0.0f

        val maxPrice =
            if (mPriceMax.text.isNotEmpty()) mPriceMax.text.toString().toFloat()
            else Float.MAX_VALUE

        query = query.searchByPrice(minPrice, maxPrice)
        //---
        //DEBUG query.getAll().thenAccept { list -> Log.d(TAG,list.toString())}
        val querySettings = query.getSettings()
        val intent = Intent(this, ListSalesActivity::class.java)
        intent.putExtra(ListSalesActivity.EXTRA_SALE_QUERY_SETTINGS, querySettings)
        startActivity(intent)
    }

    private fun setParametersButtons() {
        mName = findViewById(R.id.book_name)
        mISBN = findViewById(R.id.book_isbn)
        mPriceMin = findViewById(R.id.price_min)
        mPriceMax = findViewById(R.id.price_max)

        mStateActive = findViewById(R.id.state_active)
        mStateRetracted = findViewById(R.id.state_retracted)
        mStateConcluded = findViewById(R.id.state_concluded)

        mConditionNew = findViewById(R.id.condition_new)
        mConditionGood = findViewById(R.id.condition_good)
        mConditionWorn = findViewById(R.id.condition_worn)
    }

    private fun getStates(): Set<SaleState> {
        var state = mutableSetOf<SaleState>()
        if (mStateActive.isChecked) state.add(SaleState.ACTIVE)
        if (mStateConcluded.isChecked) state.add(SaleState.CONCLUDED)
        if (mStateRetracted.isChecked) state.add(SaleState.RETRACTED)
        return if (state.isEmpty()) SaleState.values().toSet() else state.toSet()
    }

    private fun getCondition(): Set<BookCondition> {
        var condition = mutableSetOf<BookCondition>()
        if (mConditionGood.isChecked) condition.add(BookCondition.GOOD)
        if (mConditionNew.isChecked) condition.add(BookCondition.NEW)
        if (mConditionWorn.isChecked) condition.add(BookCondition.WORN)
        return if (condition.isEmpty()) BookCondition.values().toSet() else condition.toSet()
    }

    private fun resetSortByButtons() {
        if (mSortView.adapter == null)
            return

        val itemCount = mSortView.adapter!!.itemCount
        for (i in 0 until itemCount) {
            val h = mSortView.findViewHolderForAdapterPosition(i)
            if (h != null) {
                val holder = h as SortByAdapter.SortByViewHolder
                val button = holder.mSortButton
                button.isChecked = false
            }
        }
    }

    private fun checkAndSetOrdering(query: SaleQuery) {
        if (mSortView.adapter == null)
            return

        val itemCount = mSortView.adapter!!.itemCount
        for (i in 0 until itemCount) {
            val h = mSortView.findViewHolderForAdapterPosition(i)
            if (h != null) {
                val holder = h as SortByAdapter.SortByViewHolder
                val button = holder.mSortButton
//                val value = holder.mSortValue

                if (button.isChecked) {
//                    query.withOrdering(value)
                }
            }
        }
    }
}