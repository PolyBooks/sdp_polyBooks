package com.github.polybooks.core.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Sale

/**
 * Adapter for listing Sale via RecyclerView
 * @property salesList List of sales
 */
class SalesAdapter(var salesList: List<Sale>): RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {
    class SalesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val mName: TextView = itemView.findViewById(R.id.BookName)
        val mPrice : TextView = itemView.findViewById(R.id.SalePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val v : View = LayoutInflater.from(parent.context).inflate(R.layout.sale_item, parent, false)
        return SalesAdapter.SalesViewHolder(v)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val currentSale : Sale = salesList[position]

        holder.mName.setText(currentSale.title)
        holder.mPrice.setText( String.format("%.2f",currentSale.price))
    }

    override fun getItemCount(): Int {
        return salesList.size
    }
}