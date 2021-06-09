package com.capstoneproject.society.ui.personaluser.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ItemListNotificationsBinding
import com.capstoneproject.society.model.RequestAcceptedItem

class NotificationAdapter(val context: NotificationsFragment, private val mData: List<RequestAcceptedItem>, private val onItemClickCallback: OnItemClickCallback) : RecyclerView.Adapter<NotificationAdapter.NotificationVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.NotificationVH {
        val mView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_notifications, parent, false)
        return NotificationVH(mView)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.NotificationVH, position: Int) {
        val data = mData[position]

        Glide.with(holder.itemView.context)
                .load(data.donorImgUrl)
                .apply(RequestOptions().override(100, 100))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.tvImg)

        holder.tvDescription.text = context.getString(R.string.notif_description, data.donorName)
        holder.tvDatetime.text = data.date

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(mData[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = mData.size

    inner class NotificationVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListNotificationsBinding.bind(itemView)

        var tvImg = binding.itemProfile
        var tvDescription = binding.itemDescription
        var tvDatetime = binding.itemDatetime
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: RequestAcceptedItem)
    }
}