package com.example.pantomonitor.view


import android.content.ContentValues.TAG
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
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
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.FragmentPlaceHolderBinding
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.DataType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors


interface PopupInteractionListener {fun updateTextView(text: String,text2: String)
}

class PlaceHolder : Fragment(),PopupInteractionListener {


    private lateinit var binding: FragmentPlaceHolderBinding


    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private lateinit var imageProcessor: ImageProcessor
    private lateinit var imageCapture: ImageCapture
    private var isButtonClickable = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceHolderBinding.inflate(inflater, container, false)


        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()



        binding.Select.setOnClickListener {
            if (isButtonClickable) {
                // Disable the button to prevent multiple clicks
                isButtonClickable = false
                binding.Select.isEnabled = false
                captureImage()

                Handler().postDelayed({

                    isButtonClickable = true
                    binding.Select.isEnabled = true
                }, 2000) // 2000 milliseconds = 2 seconds
            }
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
        val model = activity.geterrorhandling()
// Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

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
        val model = activity.getLatestmodel()


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
        var check = arrayOf("Not-Pantograph", "Good","Replace","Replace" ,"Replace")
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

    }

    private fun errorhandling(imageUri: Uri) {
        val currentTime = Date()
        val timeFormattime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = System.currentTimeMillis() / 1000L
        val trainno = binding.textView12.text.toString()
        val cartno = binding.textView14.text.toString()

        val formattedDate = dateFormat
        val formattedTime = timeFormattime.format(currentTime)


        val upload =
            Upload("Not-Pantograph", "$formattedDate", "${imageUri.lastPathSegment}", "$formattedTime","$trainno","$cartno")
        val uploadData = mapOf(
            "Assessment" to upload.Assessment,
            "Date" to upload.Date,
            "Img" to upload.Img,
            "Time" to upload.Time,
            "TrainNo" to upload.TrainNo,
            "CartNo" to upload.CartNo
        )


        database.push().setValue(uploadData)


    }


    private fun setupcamera() {
        val previewView: PreviewView = binding.preview
        val cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {

                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))


    }

    fun captureImage() {
        val outputDirectory = getOutputDirectory()
        val imageFileName = generateImageFileName()
        val imageFile = File(outputDirectory, "${imageFileName}.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(imageFile).build()



        imageCapture.takePicture(
            outputFileOptions,
            Executors.newSingleThreadExecutor(),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(imageFile)

                    val bitmap = uriToBitmap(savedUri)
                    //for cropping
                        val croppedBitmap = bitmap?.let { cropCenter(it, 4f) }
                        val cropleft = croppedBitmap?.let { cropLeftToCenter(it) }
                        val cropright = croppedBitmap?.let { cropRightToCenter(it) }
                        val bittiurileft = cropleft?.let { bitmapToUri(it) }
                        val bittiuriright = cropright?.let { bitmapToUri(it) }
                        val errorcheckingleft = bittiurileft?.let { error_handling(it) }
                        val errorcheckingright = bittiurileft?.let { error_handling(it) }

                        if (errorcheckingright != null) {
                            if (errorcheckingleft != null) {
                                    if (errorcheckingleft == 0 && errorcheckingright == 0) {

                                  if (bittiuriright != null) {
                                    errorhandling(bittiuriright)
                                   uploadImageToFirebase(bittiuriright)


                                  }
                                  if (bittiurileft != null) {
                                       errorhandling(bittiurileft)
                                       uploadImageToFirebase(bittiurileft)
                                  }

                                   Handler(Looper.getMainLooper()).post {
                                     Toast.makeText(
                                       requireContext(),
                                        "Rejected",
                                        Toast.LENGTH_LONG
                                      )
                                       .show()
                                    }


                                } else {

                                if (bittiuriright != null) {
                                    predict(bittiuriright)
                                    uploadImageToFirebase(bittiuriright)
                                }

                                    if (bittiurileft != null) {
                                        predict(bittiurileft)
                                        uploadImageToFirebase(bittiurileft)
                                    }


                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        requireContext(),
                                        "Upload Completed",
                                        Toast.LENGTH_LONG
                                    )
                                        .show()
                                }


                            }
                        }
                         }


                }


                override fun onError(exception: ImageCaptureException) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(
                            requireContext(),
                            "Data Failed to Upload",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }

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
            val context = requireContext()
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
        val editText: EditText = popupView.findViewById(R.id.editTextNumber)
        val editText1: EditText = popupView.findViewById(R.id.editTextNumber2)
        val btnAcpt: Button = popupView.findViewById(R.id.buttonaccept)



        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

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

    fun cropLeftToCenter(bitmap: Bitmap): Bitmap {
        // Get the dimensions of the original bitmap
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Calculate the dimensions of the cropped region (full width, half of the original height)
        val croppedWidth = originalWidth
        val croppedHeight = 400

        // Calculate the coordinates of the top-left corner of the cropping area
        val cropX = 0 // Start from the left edge
        val cropY = 0

        // Create a new Bitmap representing the cropped region

        return Bitmap.createBitmap(bitmap, cropX, cropY, croppedWidth, croppedHeight)
    }

    fun cropRightToCenter(bitmap: Bitmap): Bitmap {
        // Get the dimensions of the original bitmap
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Calculate the dimensions of the cropped region (full width, half of the original height)

        val croppedWidth = originalWidth
        val croppedHeight = 400


        // Calculate the coordinates of the top-left corner of the cropping area
        val cropX = 0
        val cropY = 600

        // Create a new Bitmap representing the cropped region
        return Bitmap.createBitmap(bitmap, cropX, cropY, croppedWidth, croppedHeight)
    }

    fun resizeImage(originalBitmap: Bitmap): Bitmap {
        val newWidth = 224
        val newHeight = 224


        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false)
    }


    override fun onResume() {
        super.onResume()
        // Register volume button events when the fragment resumes
        activity?.let {
            it.volumeControlStream = KeyEvent.KEYCODE_VOLUME_DOWN // You can set to other volume button if you want
            it.volumeControlStream = KeyEvent.KEYCODE_VOLUME_UP
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister volume button events when the fragment pauses
        activity?.let {
            it.volumeControlStream = KeyEvent.KEYCODE_VOLUME_DOWN
            it.volumeControlStream = KeyEvent.KEYCODE_VOLUME_UP
        }
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


