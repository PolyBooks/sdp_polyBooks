package com.github.polybooks.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.polybooks.R
import com.github.polybooks.utils.CameraManip.REQUEST_CODE_PERMISSIONS
import com.github.polybooks.utils.CameraManip.REQUIRED_PERMISSIONS
import com.github.polybooks.utils.CameraManip.TAG
import com.github.polybooks.utils.CameraManip.allPermissionsGranted
import com.github.polybooks.utils.CameraManip.startCamera
import kotlinx.android.synthetic.main.activity_take_book_picture.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakeBookPictureActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    // I think (hope) that using a single fileName is fine as here we are fine with overwriting it everytime a new picture is taken
    private val pictureFileName = "bookPictureFile"

    private lateinit var cameraExecutor: ExecutorService
    private var stringISBN: String? = null
    private var salePrice: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_book_picture)


        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            stringISBN = extras.getString(EXTRA_ISBN)
            salePrice = extras.getString(EXTRA_SALE_PRICE)
        }

        // Request camera permissions
        if (allPermissionsGranted(baseContext)) {
            startTakeBookPictureCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startTakeBookPictureCamera() {
        imageCapture = ImageCapture.Builder().build()
        startCamera(this, viewFinder, imageCapture!!)
    }

    fun takePhoto(view: View) {
        // TODO offer flash option and other quality of life upgrades

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return


        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    val msg = "Photo capture succeeded"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    val buffer: ByteBuffer = image.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                    saveBitmap(bitmap)

                    image.close()

                    val intent = Intent(baseContext, FillSaleActivity::class.java).apply {
                        val extras = Bundle()
                        extras.putString(EXTRA_ISBN, stringISBN)
                        extras.putString(EXTRA_PICTURE_FILE, pictureFileName)
                        extras.putString(EXTRA_SALE_PRICE, salePrice)
                        putExtras(extras)
                    }
                    startActivity(intent)
                }
            })
    }

    fun saveBitmap(bitmap: Bitmap): String {
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val fo: FileOutputStream = baseContext.openFileOutput(
                pictureFileName,
                Context.MODE_PRIVATE
            )
            fo.write(bytes.toByteArray())
            // remember close file output
            fo.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return pictureFileName
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(baseContext)) {
                startTakeBookPictureCamera()
            } else {
                Toast.makeText(this,
                    "You must grant camera permissions to take a picture.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


}
