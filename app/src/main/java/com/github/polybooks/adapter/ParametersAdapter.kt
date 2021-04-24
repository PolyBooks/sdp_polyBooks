package com.github.polybooks.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class ParametersAdapter<VH: ParametersAdapter<VH>.ParameterViewHolder>:
    RecyclerView.Adapter<VH>() {

    abstract inner class ParameterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        abstract fun resetItem()

        abstract fun getItemIfSelected(): Any?
    }
}