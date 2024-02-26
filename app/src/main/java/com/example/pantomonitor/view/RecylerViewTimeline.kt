package com.example.pantomonitor.view

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pantomonitor.R
import com.example.pantomonitor.databinding.RecylerviewtimelineBinding
import com.example.pantomonitor.viewmodel.BdMainViewModel
import com.example.pantomonitor.viewmodel.timelinephoto
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class RecylerViewTimeline(private var list: List<timelinephoto>): RecyclerView.Adapter<RecylerViewTimeline.MyView>() {
    private val storage = FirebaseStorage.getInstance()
    private val storageRef: StorageReference = storage.reference
    private var dataList: List<timelinephoto> = emptyList()


    class MyView(val itemBinding: RecylerviewtimelineBinding) :
    RecyclerView.ViewHolder(itemBinding.root

    )

    fun clearList() {
        list = emptyList()
        notifyDataSetChanged()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            RecylerviewtimelineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyView, position: Int) {
      val currentItem = list[position]
        val pic = gettimepic(currentItem.Img)


        //  Picasso.get()
        //   .load(currentItem.Img)
        //  .into(holder.itemBinding.imageViewtl)

        pic.downloadUrl.addOnSuccessListener { uri: Uri? ->
            //Load the image into an ImageView using a library like Picasso or Glide
            Picasso.get().load(uri).into(holder.itemBinding.imageViewtl)
        }


        val date = currentItem.Date.toLong() * 1000L
        val dateFormat = SimpleDateFormat("MM-dd-yyyy")
        holder.itemBinding.dateviewtl.text = dateFormat.format(date)
        holder.itemBinding.statusviewtl.text = currentItem.Assessment
        holder.itemBinding.timeviewtl.text = currentItem.Time
        holder.itemBinding.Trainno.text = currentItem.TrainNo
        holder.itemBinding.cartno.text = currentItem.CartNo

    }

    fun gettimepic(img: String): StorageReference {

        return storageRef.child("images/${img}")
    }

    fun setData(newData: List<timelinephoto>?) {
        if (newData != null) {
            dataList = newData
        }
        notifyDataSetChanged()
    }


}
