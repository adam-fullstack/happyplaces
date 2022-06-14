package com.adamfoerster.happyplaces

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adamfoerster.happyplaces.database.PlaceEntity
import com.adamfoerster.happyplaces.databinding.LayoutPlaceItemBinding

class PlaceListAdapter(private var dataSet: Array<PlaceEntity>) :
    RecyclerView.Adapter<PlaceListAdapter.ViewHolder>() {

    var onItemClick: ((PlaceEntity) -> Unit)? = null

    inner class ViewHolder(binding: LayoutPlaceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
        val title = binding.title
        val location = binding.location
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet[adapterPosition])
            }
        }
    }

    fun setNewItems(items: ArrayList<PlaceEntity>) {
        this.dataSet = items.toTypedArray()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutPlaceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: PlaceEntity = dataSet[position]
        holder.image.setImageURI(Uri.parse(model.image))
        holder.title.text = model.title
        holder.location.text = model.location
    }

    override fun getItemCount() = dataSet.size
}