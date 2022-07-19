package com.adamfoerster.happyplaces

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.adamfoerster.happyplaces.activities.AddActivity
import com.adamfoerster.happyplaces.database.PlaceDAO
import com.adamfoerster.happyplaces.database.PlaceEntity
import com.adamfoerster.happyplaces.databinding.LayoutPlaceItemBinding
import com.adamfoerster.happyplaces.utils.Utils
import org.json.JSONObject

class PlaceListAdapter(private var context: Context, private var dataSet: Array<PlaceEntity>) :
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

    fun notifyEditItem(activity: Activity, position: Int, requestCode: Int) {
        val intent = Intent(context, AddActivity::class.java)
        intent.putExtra(Utils.EDIT_PLACE_EXTRA, dataSet[position].id)
        Log.d("bleh", dataSet[position].toString())
        activity.startActivityForResult(intent, requestCode)
        notifyItemChanged(position)
    }

    fun notifyRemoveItem(dao: PlaceDAO, position: Int) {
        dao.delete(dataSet[position])
        Toast.makeText(context, "Place no longer happy", Toast.LENGTH_SHORT).show()
        notifyItemRemoved(position)
        dataSet = dao.getAll().toTypedArray()
        notifyDataSetChanged()
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