package com.github.polybooks.adapter.database

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Interest
import com.github.polybooks.database.Database
import com.github.polybooks.database.UserInterestDBWithCache.getCachedUserInterests
import com.github.polybooks.database.UserInterestDBWithCache.storeUserInterests
import com.github.polybooks.utils.StringsManip.getName
import java.util.concurrent.CompletableFuture

class InterestAdapter(private val context: Context): RecyclerView.Adapter<InterestAdapter.InterestHolder>() {

    private var userInterests = getCachedUserInterests(context).thenApply { list -> list.toSet() }

    private val interests: CompletableFuture<List<Interest>> =
        Database.interestDatabase.listAllInterests()
            .thenApply { triple -> triple.first + triple.second + triple.third }

    class InterestHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val button: CheckBox = itemView.findViewById(R.id.parameter_value_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)
        return InterestHolder(v)
    }

    override fun onBindViewHolder(holder: InterestHolder, position: Int) {
        val interest: Interest = interests.get()[position]
        holder.button.text = getName(interest)
        holder.button.isChecked = selected(interest)
        holder.button.setOnClickListener {
            userInterests = if (selected(interest)) {
                userInterests.thenApply { i -> i.minusElement(interest) }
            } else {
                userInterests.thenApply { i -> i.plusElement(interest) }
            }
        }
    }

    override fun getItemCount(): Int {
        return interests.get().size
    }

    private fun selected(interest: Interest): Boolean {
        return userInterests.get().contains(interest)
    }

    fun updateUserInterests(): Unit {
        storeUserInterests(context, userInterests.get().toList())
    }
}