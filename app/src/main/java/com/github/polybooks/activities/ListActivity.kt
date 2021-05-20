package com.github.polybooks.activities

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.database.Query

/**
 * Activity to list something using a recyclerview
 */

abstract class ListActivity<T>: AppCompatActivity() {

    companion object {
        private const val TAG = "ListActivity"
    }

    private lateinit var mRecycler: RecyclerView
    private val mLayout: RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        findViewById<TextView>(R.id.sale_or_book).text = getTitleText()
        findViewById<Button>(R.id.filter_button).setOnClickListener { onFilterButtonClick() }
        val query: Query<T> = getQuery()

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)

        // Links the database api to the recyclerView
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = adapter(emptyList())

        query.getAll().thenAccept { list -> this.updateAdapter(list) }

        setNavBar()
    }

    private fun updateAdapter(list: List<*>) {
        runOnUiThread {
            mRecycler.adapter = adapter(list as List<T>)
        }
    }

    abstract fun onFilterButtonClick()
    abstract fun adapter(list : List<T>): RecyclerView.Adapter<*>
    abstract fun setNavBar(): Unit
    abstract fun getQuery(): Query<T>
    abstract fun getTitleText(): String
}
