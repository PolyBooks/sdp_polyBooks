package com.github.polybooks.adapter.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.core.Interest
import com.github.polybooks.database.Database
import com.github.polybooks.utils.StringsManip.getName
import com.github.polybooks.utils.fireBaseUsertoUser
import com.google.firebase.auth.FirebaseAuth

class InterestAdapter: RecyclerView.Adapter<InterestAdapter.InterestHolder>(){

    val user = fireBaseUsertoUser(FirebaseAuth.getInstance().currentUser)
    private var userInterests = Database.interestDatabase.getUserInterests(user)
        .thenApply { triple -> triple.first + triple.second + triple.third }.get().toMutableSet()
    val interests : List<Interest> = Database.interestDatabase.listAllInterests()
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
        holder.button.setOnClickListener { view ->
            holder.button.isChecked = !holder.button.isChecked
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
        Database.interestDatabase.setUserInterests(user, userInterests.toList())// TODO: check if we need to add .get()
    }
}