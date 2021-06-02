package com.github.polybooks.adapter.database

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Interest
import com.github.polybooks.database.Database
import com.github.polybooks.utils.StringsManip.getName

class InterestAdapter: RecyclerView.Adapter<InterestAdapter.InterestHolder>(){

    private var userInterests = Database.interestDatabase.getCurrentUserInterests()
        .get().toMutableSet()
    private val interests : List<Interest> = Database.interestDatabase.listAllInterests()
        .thenApply { triple -> triple.first + triple.second + triple.third }.get()

    class InterestHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val button : CheckBox = itemView.findViewById(R.id.parameter_value_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.sortby_item, parent, false)
        return InterestHolder(v)
    }

    override fun onBindViewHolder(holder: InterestHolder, position: Int) {
        val interest : Interest = interests[position]
        holder.button.text = getName(interest)
        holder.button.isChecked = selected(interest)
        holder.button.setOnClickListener {
            if (selected(interest)){
                userInterests.remove(interest)
            } else {
                userInterests.add(interest)
            }
        }
    }

    override fun getItemCount(): Int {
        return interests.size
    }

    private fun selected(interest : Interest) : Boolean{
        return userInterests.contains(interest)
    }

    fun updateUserInterests() : Unit{
        Log.d("In adapter", "Updated ===================")
        Database.interestDatabase.setCurrentUserInterests(userInterests.toList())// TODO: check if we need to add .get()
    }
}