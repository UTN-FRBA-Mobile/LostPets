package com.utn.lostpets.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.utn.lostpets.R
import com.utn.lostpets.dataclass.PublicationsResponse
import java.util.List

class PublicationsAdapter(val publications: kotlin.collections.List<PublicationsResponse>) :
    RecyclerView.Adapter<PublicationsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicationsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PublicationsViewHolder(layoutInflater.inflate(R.layout.item_publicacion, parent, false))
    }

    override fun onBindViewHolder(holder: PublicationsViewHolder, position: Int) {
        val foto = publications[position].foto
        holder.bind(foto)
    }

    override fun getItemCount(): Int = publications.size
}