package com.capstoneproject.society.ui.personaluser.home.requestlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ItemListRequestBinding
import com.capstoneproject.society.model.RequestItems

class RequestListAdapter(private val mData: List<RequestItems>, private val onItemClickCallback: OnItemClickCallback) : RecyclerView.Adapter<RequestListAdapter.RequestViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RequestListAdapter.RequestViewHolder {
        val mView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_list_request, parent, false)
        return RequestViewHolder(mView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RequestListAdapter.RequestViewHolder, position: Int) {
        val data = mData[position]

        Glide.with(holder.itemView.context)
            .load(data.profileimageurl)
            .apply(RequestOptions().override(55,55))
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(holder.itemImg)

        holder.itemName.text = data.name
        holder.itemBloodtype.text = data.bloodtyperequest
        holder.itemLoc.text = "${data.subdistrict}, ${data.city}"

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(mData[holder.adapterPosition]) }
    }

    override fun getItemCount(): Int = mData.size

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemListRequestBinding.bind(itemView)

        val itemName = binding.itemUserReqName
        val itemImg = binding.itemUserImg
        val itemBloodtype = binding.itemBloodType
        val itemLoc = binding.itemUserLoc
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: RequestItems)
    }
}