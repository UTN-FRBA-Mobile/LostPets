package com.utn.lostpets.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.utn.lostpets.databinding.ItemPublicacionBinding

class PublicationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPublicacionBinding.bind(view)

    fun bind(foto: String) {
        Picasso.get().load(foto).into(binding.idPublicacion)
    }
}