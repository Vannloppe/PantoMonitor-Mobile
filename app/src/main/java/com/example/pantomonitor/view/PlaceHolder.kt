package com.example.pantomonitor.view

import  android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.FragmentPlaceHolderBinding
import com.example.pantomonitor.databinding.TrainnoBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.ImageUtils
import org.tensorflow.lite.DataType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors


interface PopupInteractionListener {
    fun updateTextView(text: String,text2: String)
}

class PlaceHolder : Fragment(),PopupInteractionListener {


    private lateinit var binding: FragmentPlaceHolderBinding





    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var imageCapture: ImageCapture


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaceHolderBinding.inflate(inflater, container, false)



        imageProcessor = ImageProcessor.Builder()
            //.add(NormalizeOp(0.0f, 225.0f))
            //.add(TransformToGrayscaleOp())
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()



        binding.Select.setOnClickListener {
            captureImage()
            //  uploadComplete()

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupcamera()

        binding.Frametrain.setOnClickListener {
            showPopup()

        }
    }

    private fun error_handling(imageUri: Uri): Int {
        val bitmap = uriToBitmap(imageUri)






        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val activity = requireActivity() as MainActivity
        //val model = activity.geterrorhandling()


        val model = activity.getLiteModel()


// Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray


        var maxIdx = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl) {
                maxIdx = index
            }
        }

        return maxIdx
    }


    private fun predict(imageUri: Uri) {
        val bitmap = uriToBitmap(imageUri)
        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)

        tensorImage = imageProcessor.process(tensorImage)

        val activity = requireActivity() as MainActivity
        val model = activity.getLiteModel()


// Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)

        val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray


        var maxIdx = 0
        outputFeature0.forEachIndexed { index, fl ->
            if (outputFeature0[maxIdx] < fl) {
                maxIdx = index
            }
        }
        var check = arrayOf("Not-Pantograph", "Bad", "Bad", "Good", "Good")
        var bindtext = check[maxIdx]
        val currentTime = Date()
        val timeFormattime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = System.currentTimeMillis() / 1000L
        val trainno = binding.textView12.text.toString()
        val cartno = binding.textView14.text.toString()

        val formattedDate = dateFormat

        val formattedTime = timeFormattime.format(currentTime)


        val upload =
            Upload("$bindtext", "$formattedDate", "${imageUri.lastPathSegment}", "$formattedTime","$trainno","$cartno")
        val uploadData = mapOf(
            "Assessment" to upload.Assessment,
            "Date" to upload.Date,
            "Img" to upload.Img,
            "Time" to upload.Time,
            "TrainNo" to upload.TrainNo,
            "CartNo" to upload.CartNo
        )


        database.push().setValue(uploadData)


// Releases model resources if no longer used.
        //  model.close()
    }

    private fun setupcamera() {
        val previewView: PreviewView = binding.preview
        val cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Build and bind the camera use cases
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider?.unbindAll()

                // Bind use cases to camera
                cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))


    }

    private fun captureImage() {
        val outputDirectory = getOutputDirectory()
        val imageFileName = generateImageFileName()
        val imageFile = File(outputDirectory, "${imageFileName}.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()


        //for cropping



        imageCapture.takePicture(
            outputFileOptions,
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image captured and saved to outputFileResults.savedUri

                    val savedUri = Uri.fromFile(imageFile)
                    val bitmap = uriToBitmap(savedUri)
                    val croppedBitmap = bitmap?.let { cropCenter(it, 4f) }
                    val bittiuri = croppedBitmap?.let { bitmapToUri(it) }







                    val errorchecking = bittiuri?.let { error_handling(it) }


                    if (errorchecking == 0) {
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(requireContext(), "Image Rejected", Toast.LENGTH_LONG)
                                .show()
                        }


                    } else {


                        if (bittiuri != null) {
                            predict(bittiuri)
                        }


                        if (bittiuri != null) {
                            uploadImageToFirebase(bittiuri)
                        }

                        Handler(Looper.getMainLooper()).post {

                            Toast.makeText(requireContext(), "Upload Completed", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }

                }

                override fun onError(exception: ImageCaptureException) {
                    // Handle error
                }
            })
    }

    private fun uploadImageToFirebase(imageUri: Uri) {

        val storage = FirebaseStorage.getInstance().reference
        val imageRef: StorageReference = storage.child("images/${imageUri.lastPathSegment}")


        imageRef.putFile(imageUri)
            .addOnSuccessListener {

            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
            }
    }

    fun generateImageFileName(): String {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val currentDateAndTime: String = sdf.format(Date())
        return "IMG_$currentDateAndTime"
    }


    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }

    fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = context?.contentResolver?.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun bitmapToUri(bitmap: Bitmap): Uri? {
        return try {
            val context = requireContext() // Get the context from the Fragment
            val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val imageFile = File.createTempFile(
                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()),
                ".jpg",
                imagesDir
            )
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            Uri.fromFile(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }






    private fun showPopup() {


        val popupView = layoutInflater.inflate(R.layout.trainno, null)
        // Create a PopupWindow with the inflated view
        val editText: EditText = popupView.findViewById(R.id.editTextNumber)
        val editText1: EditText = popupView.findViewById(R.id.editTextNumber2)
        val btnAcpt: Button = popupView.findViewById(R.id.buttonaccept)



        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        // Set some options for the popup window
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        // Show the popup window
        popupWindow.showAtLocation(binding.preview,Gravity.CENTER, 0, 0)

        val popupInteractionListener: PopupInteractionListener = this
        btnAcpt.setOnClickListener{
            popupInteractionListener.updateTextView(editText.text.toString(),editText1.text.toString())
            popupWindow.dismiss()


        }
    }

    override fun updateTextView(text: String,text2: String) {
            binding.textView12.text = text
            binding.textView14.text = text2
    }

    fun cropCenter(bitmap: Bitmap, aspectRatio: Float): Bitmap {
        // Get the dimensions of the original bitmap
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Calculate the dimensions of the cropped region
        val croppedWidth = originalWidth
        val croppedHeight = (croppedWidth / aspectRatio).toInt()

        // Calculate the coordinates of the center of the original bitmap
        val centerX = originalWidth / 2
        val centerY = originalHeight / 2

        // Calculate the coordinates of the top-left corner of the cropping area
        val cropX = centerX - (croppedWidth / 2)
        val cropY = centerY - (croppedHeight / 2)

        // Create a new Bitmap representing the cropped region
        return Bitmap.createBitmap(bitmap, cropX, cropY, croppedWidth, croppedHeight)
    }


}





data class Upload(
    val Assessment: String = "",
    val Date: String = "",
    val Img: String = "",
    val Time: String = "",
    val TrainNo: String = "",
    val CartNo: String = ""

)


