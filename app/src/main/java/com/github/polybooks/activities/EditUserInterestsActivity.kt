package com.github.polybooks.activities

import android.content.Intent
import android.os.Bundle
import android.transition.Visibility
import android.view.View.GONE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.polybooks.R
import com.github.polybooks.adapter.database.InterestAdapter
import com.github.polybooks.core.Interest
import com.github.polybooks.database.Query
import com.github.polybooks.utils.setupNavbar
import java.util.concurrent.CompletableFuture

class EditUserInterestsActivity: ListActivity<Interest>() {
    val adapter = InterestAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_edit_user_interests)
        findViewById<Button>(R.id.filter_button).visibility = GONE
        findViewById<RecyclerView>(R.id.recyclerView).setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, null))
    }

    override fun onFilterButtonClick() {
        startActivity(Intent(this, UserProfileActivity::class.java))
    }

    override fun adapter(list: List<Interest>): RecyclerView.Adapter<*> {
        return adapter
    }

    override fun getQuery(): Query<Interest> {
        return object: Query<Interest> {
            override fun getAll(): CompletableFuture<List<Interest>> {
                return CompletableFuture.completedFuture(emptyList())
            }
        }
    }

    override fun getTitleText(): String {
        return getString(R.string.user_edit_interests)
    }

    override fun setNavBar() {
        setupNavbar(findViewById(R.id.bottom_navigation), this)
    }

    override fun onPause() {
        super.onPause()
        adapter.updateUserInterests()
    }
}