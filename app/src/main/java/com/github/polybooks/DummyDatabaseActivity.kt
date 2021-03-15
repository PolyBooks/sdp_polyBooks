package com.github.polybooks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DummyDatabaseActivity : AppCompatActivity() {
    private lateinit var mRecycler : RecyclerView
    private lateinit var mAdapter : RecyclerView.Adapter<DummyBookAdapter.DummyViewHolder>
    private val mLayout : RecyclerView.LayoutManager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy_database)
        val dummyList : List<DummyBook> = listOf(DummyBook(1,"Book1"), DummyBook(2,"Book2"), DummyBook(3,"Book4"), DummyBook(4,"Book3"))

        mRecycler = findViewById(R.id.recyclerView)
        mRecycler.setHasFixedSize(true)
        mAdapter = DummyBookAdapter(dummyList)
        mRecycler.layoutManager = mLayout
        mRecycler.adapter = mAdapter


    }
}