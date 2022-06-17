package com.utn.lostpets.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.utn.lostpets.databinding.ItemPublicacionBinding
import com.utn.lostpets.model.Publication

class PublicationsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPublicacionBinding.bind(view)

    fun bind(publication: Publication, posicion: Int) {
        binding.idDescripcion.text = publication.descripcion
        binding.idContacto.text = publication.contacto
        binding.idDistancia.text = "A $posicion kilómetros"
        binding.idFecha.text = publication.fechaPublicacion
        Picasso.get().load(publication.foto).into(binding.idPublicacion)
    }
}