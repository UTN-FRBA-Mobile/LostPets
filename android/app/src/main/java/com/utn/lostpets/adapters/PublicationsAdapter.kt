package com.utn.lostpets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utn.lostpets.R
import com.utn.lostpets.model.Publication

class PublicationsAdapter(val publications: kotlin.collections.List<Publication>) :
    RecyclerView.Adapter<PublicationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PublicationsViewHolder(layoutInflater.inflate(R.layout.item_publicacion, parent, false))
    }

    override fun onBindViewHolder(holder: PublicationsViewHolder, position: Int) {
        holder.bind(publications[position], position + 2)
    }

    override fun getItemCount(): Int = publications.size
}