package com.github.polybooks.core.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Sale
import com.github.polybooks.utils.StringsManip.listAuthorsToString

/**
 * Adapter for listing Sale via RecyclerView
 * @property salesList List of sales
 */
class SalesAdapter(var salesList: List<Sale>): RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {
    class SalesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val viewTitle: TextView = itemView.findViewById(R.id.text_view_title)
        val viewEdition: TextView = itemView.findViewById(R.id.text_view_edition)
        val viewBy: TextView = itemView.findViewById(R.id.text_view_by)
        val viewAuthor: TextView = itemView.findViewById(R.id.text_view_author)
        val viewCondition: TextView = itemView.findViewById(R.id.text_view_condition)
        val viewPrice: TextView = itemView.findViewById(R.id.text_view_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.sale_item, parent, false)
        return SalesViewHolder(v)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {

        val sale: Sale = salesList[position]

        holder.viewTitle.text = sale.book.title

        if (sale.book.edition != null) holder.viewEdition.text = sale.book.edition
        else holder.viewEdition.setVisibility(View.GONE)

        if (sale.book.authors?.isEmpty() != false) {
            holder.viewBy.setVisibility(View.GONE)
            holder.viewAuthor.setVisibility(View.GONE)
        } else {
            holder.viewAuthor.text = listAuthorsToString(sale.book.authors)
        }

        holder.viewCondition.text = sale.condition.name
        holder.viewPrice.text = String.format("%.2f", sale.price)
    }

    override fun getItemCount(): Int {
        return salesList.size
    }
}