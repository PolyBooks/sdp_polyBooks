package com.github.polybooks.activities

import android.R.id
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.polybooks.R
import com.github.polybooks.core.Sale
import com.github.polybooks.utils.StringsManip
import java.io.File

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import io.grpc.Context
import java.io.FileInputStream
import java.lang.Exception
import java.nio.file.Files.readAllBytes
import java.nio.file.Paths


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
        val bookImage = findViewById<ImageView>(R.id.proof_picture)

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




        
        val firebaseStorage = FirebaseStorage.getInstance()
        var imageData: ByteArray
/*
        try {
            val file = File("images", "chat.jpg")
            if (Build.VERSION.SDK_INT >= 28)
                imageData = readAllBytes(Paths.get(file.getPath()))
        } catch (e: Exception) {
            println("======== GOT EXCEP>TION : " + e.message)
        }
*/
        println("==== just before")
        // val storageRef: StorageReference = firebaseStorage.getReference("images").child("chat.jpg")
        val storageRef: StorageReference = firebaseStorage.reference.child("images/chat.jpg")
        val a = storageRef.getBytes(1 shl (20 + 4))
            .addOnSuccessListener { _ -> print("=== SUCCESS") }
            .addOnFailureListener { _ -> println("=== FAILURE") }
        println("========= " + a)

        /*
        println("sssssssssss======================================")
        val storage: FirebaseStorage = Firebase.storage

        // Create a storage reference from our app
        val storageRef = storage.reference

        val imgPath: String = "images/profiles/NicolasRaulin.jpg"
        val nicoRef = storageRef.child(imgPath)
        val ref = FirebaseStorage.getInstance().reference.child(imgPath)
        println("nico-ref: $nicoRef")
        println("ref: $ref")


        val file = File("/Users/wexus/Documents/EPFL/courses/CS-306_BA6_SDP/dev-fb-images/app/src/main/java/com/github/polybook s/core/database/interfaces/NicolasRaulin.jpg")
        // val file = File("NicolasRaulin.jpg")
        val bytes = file.readBytes()
        // val file = Uri.fromFile(File("java/com/github/polybooks/core/database/interfaces/NicolasRaulin.jpg"))
        // val file = Uri.fromFile(File("./core/database/interfaces/NicolasRaulin.jpg"))
        val uri = Uri.fromFile(file)
        val stream = FileInputStream(file)
        println("file: ${file.path}")

        // val uploadTask = nicoRef.putFile(uri)
        // val uploadTask = nicoRef.putStream(stream)
        // val uploadTask = nicoRef.putBytes(bytes)

        val chatRef = storageRef.child("images/chat.jpg")


        // val uploadTask = ref.putBytes(bytes)
        // uploadTask.addOnFailureListener {
        //    // Handle unsuccessful uploads
        //    println("failure =========================================")
        // }.addOnSuccessListener { taskSnapshot ->
        //    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
        //    // ...
        //    println("success =======================================")
        // }


        println("tttt =======================================")

        // val intent = Intent(this, AddSaleActivity::class.java)
        // startActivity(intent)
        */
    }
}