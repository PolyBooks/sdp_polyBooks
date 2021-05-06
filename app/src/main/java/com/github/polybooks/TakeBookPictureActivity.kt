package com.github.polybooks

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_take_book_picture.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TakeBookPictureActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    // I think (hope) that using a single fileName is fine as here we are fine with overwriting it everytime a new picture is taken
    // might cause issue in between different isbns
    private val pictureFileName = "bookPictureFile"

    private lateinit var cameraExecutor: ExecutorService
    private var stringISBN: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_book_picture)

        stringISBN = intent.getStringExtra(EXTRA_ISBN)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Set up the listener for take photo button

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    fun takePhoto(view: View) {
        // TODO improve vastly this function (
        //  automatically go back to FillSaleActivity passing the taken picture as an intent which will be both displayed and saved to DB
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
                    // TODO The application is responsible for calling ImageProxy.close() to close the image.
                    super.onCaptureSuccess(image)
                    val msg = "Photo capture succeeded"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    //bundle.putParcelable(pictureBundleK, image) // TODO convert to parcellable, but also as needed by DB
                    // TODO Option 2: abandon this because apparently big sizes are hard to transfer and instead save to cache or user storage and retrieve in next step?
                    // https://stackoverflow.com/questions/4352172/how-do-you-pass-images-bitmaps-between-android-activities-using-bundles
                    // https://stackoverflow.com/questions/2459524/how-can-i-pass-a-bitmap-object-from-one-activity-to-another

                    val buffer: ByteBuffer = image.planes[0].buffer // TODO or image.image ?
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
                    saveBitmap(bitmap)

                    image.close()

                    val intent = Intent(baseContext, FillSaleActivity::class.java).apply {
                        val extras = Bundle()
                        extras.putString(EXTRA_ISBN, stringISBN)
                        extras.putString(EXTRA_PICTURE_FILE, pictureFileName)
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

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // TODO Other option:
            //       = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
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

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

}
