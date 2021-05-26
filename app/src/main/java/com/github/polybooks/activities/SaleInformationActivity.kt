package com.github.polybooks.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.core.Sale
import com.github.polybooks.utils.StringsManip

/**
 * This activity displays the detailed product information of a particular
 * registered sale given in .putExtra to the activity
 */
class SaleInformationActivity: AppCompatActivity() {
    companion object {
        const val EXTRA_SALE_INFORMATION: String = "EXTRA_SALE_INFORMATION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sale_information)

        val viewTitle: TextView = findViewById(R.id.sale_information_title)
        val viewEdition: TextView = findViewById(R.id.sale_information_edition)
        val viewAuthors: TextView = findViewById(R.id.sale_information_authors)
        // val countryFlag: TextView = findViewById(R.id.countryFlag)
        val viewPublishDate: TextView = findViewById(R.id.sale_information_book_publish_date)
        val viewPublisher: TextView = findViewById(R.id.sale_information_book_publisher)
        val viewFormat: TextView = findViewById(R.id.sale_information_book_format)
        // val bookImage = findViewById(R.id.proof_picture)

        val viewCondition: TextView = findViewById(R.id.sale_information_condition)
        val viewPrice: TextView = findViewById(R.id.sale_information_price)


        val sale = (intent.getSerializableExtra(EXTRA_SALE_INFORMATION) as Sale)

        viewTitle.text = sale.book.title
        viewEdition.text = sale.book.edition
        viewAuthors.text = StringsManip.listAuthorsToString(sale.book.authors)

        viewPublishDate.text = sale.book.publishDate.toString()

        viewPublisher.text = sale.book.publisher
        viewFormat.text = sale.book.format

        viewCondition.text = sale.condition.name
        viewPrice.text = sale.price.toString()
    }
}