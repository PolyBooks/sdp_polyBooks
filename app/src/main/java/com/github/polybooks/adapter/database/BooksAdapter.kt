package com.github.polybooks.adapter.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Book
import com.github.polybooks.utils.StringsManip.listAuthorsToString

/**
 * Adapter for listing Sale via RecyclerView
 * @property salesList List of sales
 */
class BooksAdapter(internal var booksList: List<Book>): RecyclerView.Adapter<BooksAdapter.BooksViewHolder>() {
    class BooksViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val viewTitle: TextView = itemView.findViewById(R.id.text_view_title)
        val viewEdition: TextView = itemView.findViewById(R.id.text_view_edition)
        val viewBy: TextView = itemView.findViewById(R.id.text_view_by)
        val viewAuthor: TextView = itemView.findViewById(R.id.text_view_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BooksViewHolder(v)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {

        val book: Book = booksList[position]

        holder.viewTitle.text = book.title

        if (book.edition != null) holder.viewEdition.text = book.edition
        else holder.viewEdition.setVisibility(View.GONE)

        if (book.authors?.isEmpty() != false) {
            holder.viewBy.setVisibility(View.GONE)
            holder.viewAuthor.setVisibility(View.GONE)
        } else {
            holder.viewAuthor.text = listAuthorsToString(book.authors)
        }
    }

    override fun getItemCount(): Int {
        return booksList.size
    }
}