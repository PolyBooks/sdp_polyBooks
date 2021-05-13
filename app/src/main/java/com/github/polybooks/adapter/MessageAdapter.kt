package com.github.polybooks.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R

class MessageAdapter(var messagesList: List<String>):
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        const val VIEW_TYPE_LOCAL = 1
        const val VIEW_TYPE_DISTANT = 2
    }

    inner class MessageViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView) {

        val message = itemView as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            if (viewType == VIEW_TYPE_LOCAL) R.layout.local_message_item
            else R.layout.distant_message_item,
            parent,
            false
        )

        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messagesList[position]
        holder.message.text = message
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}