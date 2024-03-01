package com.example.pantomonitor.view


import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pantomonitor.databinding.AdapterexportBinding
import com.example.pantomonitor.viewmodel.timelinephoto
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat

class Adapterexport(private var list: List<timelinephoto>): RecyclerView.Adapter<Adapterexport.MyView>(){
     private val storage = FirebaseStorage.getInstance()
     private val storageRef: StorageReference = storage.reference



    class MyView(val itemBinding: AdapterexportBinding) :
        RecyclerView.ViewHolder(itemBinding.root

        ) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyView {
        return MyView(
            AdapterexportBinding.inflate(
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
        holder.itemBinding.imgViewex.text = pic.toString()
        val date = currentItem.Date.toLong() * 1000L
        val dateFormat = SimpleDateFormat("MM-dd-yyyy")
        holder.itemBinding.dateviewex.text = dateFormat.format(date)
        holder.itemBinding.statusviewex.text = currentItem.Assessment
        holder.itemBinding.timeviewex.text = currentItem.Time

    }
    private fun gettimepic(img: String): StorageReference {

        return storageRef.child("images/${img}")
    }


}
