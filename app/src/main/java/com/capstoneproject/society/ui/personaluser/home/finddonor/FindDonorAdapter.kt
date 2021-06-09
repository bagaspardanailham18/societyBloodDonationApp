package com.capstoneproject.society.ui.personaluser.home.finddonor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.capstoneproject.society.R
import com.capstoneproject.society.databinding.ItemRowOrganizationSupplyBinding
import com.capstoneproject.society.model.OrganizationSupplyItem

class FindDonorAdapter(private val mData: ArrayList<OrganizationSupplyItem>) : RecyclerView.Adapter<FindDonorAdapter.FindDonorVH>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(items: ArrayList<OrganizationSupplyItem>) {
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FindDonorAdapter.FindDonorVH {
        val mView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_organization_supply, parent, false)
        return FindDonorVH(mView)
    }

    override fun onBindViewHolder(holder: FindDonorAdapter.FindDonorVH, position: Int) {
        val data = mData[position]

        Glide.with(holder.itemView.context)
                .load(holder.itemView.context.resources.getDrawable(R.drawable.pmi))
                .apply(RequestOptions().override(100, 100))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.tvLogo)

        holder.tvName.text = data.organizationName
        holder.tvLocation.text = data.subDistrict

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(mData[holder.bindingAdapterPosition]) }
    }

    override fun getItemCount(): Int = mData.size

    inner class FindDonorVH(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val binding = ItemRowOrganizationSupplyBinding.bind(itemView)

        var tvName = binding.itemOrgSupplyName
        var tvLocation = binding.itemLocation
        var tvLogo = binding.itemLogo
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: OrganizationSupplyItem)
    }
}