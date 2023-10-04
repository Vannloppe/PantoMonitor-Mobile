package com.example.pantomonitor.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.example.pantomonitor.databinding.FragmentPlaceHolderBinding
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.DataType
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PlaceHolder : Fragment() {

    private lateinit var binding: FragmentPlaceHolderBinding


    private var database = FirebaseDatabase.getInstance().getReference("New_Entries")
    private lateinit var bitmap: Bitmap
    private lateinit var imageProcessor: ImageProcessor
    private  var selectedImageUri: Uri? = null


    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlaceHolderBinding.inflate(inflater, container, false)


        val assetManager = requireActivity().assets
        val labels = assetManager.open("label.txt").bufferedReader().readLines()


        imageProcessor = ImageProcessor.Builder()
            //.add(NormalizeOp(0.0f, 225.0f))
            //.add(TransformToGrayscaleOp())
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        binding.Select.setOnClickListener {
           openImagepicker()
        }
        binding.predicbut.setOnClickListener {
           predict()
        }
        return binding.root
    }

    private fun openImagepicker() {
      //  val intent = Intent(Intent.ACTION_GET_CONTENT)


        // You can also use this to capture an image using the device's camera:
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         //   intent.type = "image/*"
        startActivityForResult(intent, CAMERA_REQUEST)

    }

    private fun predict() {
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
        var check = arrayOf("Good", "Good", "Bad", "Bad")
        var bindtext = check[maxIdx]
        val currentTime = Date()
        val timeFormattime = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())


        val formattedDate = dateFormat.format(currentTime)


        val formattedTime = timeFormattime.format(currentTime)


        val timestamp = System.currentTimeMillis()
        val img = "image_$timestamp.jpg"


        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.getReference("images/")
        val imageRef: StorageReference = storageRef.child("${img}.jpg")


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()








        data?.let { imageRef.putBytes(it) }?.addOnSuccessListener { taskSnapshot -> }


        val upload = Upload("$bindtext", "$formattedDate", "$img", "$formattedTime")
        val uploadData = mapOf(
            "Assessment" to upload.Assessment,
            "Date" to upload.Date,
            "Img" to upload.Img,
            "Time" to upload.Time
        )


        database.push().setValue(uploadData)

        binding.Result.text = check[maxIdx]

// Releases model resources if no longer used.
        // model.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    // User selected an image from the gallery
                    selectedImageUri = data?.data
                    try {
                         bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)

                        binding.imgplaceholder.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CAMERA_REQUEST -> {
                        // User captured an image using the camera (if you implemented it)

                    try {

                        val photo = data?.extras?.get("data") as Bitmap

                        bitmap = photo



                        binding.imgplaceholder.setImageBitmap(bitmap)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }




}

data class Upload(
    val Assessment: String = "",
    val Date: String = "",
    val Img: String = "",
    val Time: String = ""

)
