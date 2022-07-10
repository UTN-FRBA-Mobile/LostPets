package com.utn.lostpets.adapters

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.squareup.picasso.Picasso
import com.utn.lostpets.R
import com.utn.lostpets.databinding.ItemPublicacionProfileBinding
import com.utn.lostpets.model.Publication
import com.utn.lostpets.utils.FechaCalculator


class PublicationsProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemPublicacionProfileBinding.bind(view)
    private val resources = getApplicationContext().getResources()
//    private val deleteButton: AppCompatButton = itemView.findViewById<AppCompatButton>(R.id.deleteButton)
//    private val editButton: AppCompatButton = itemView.findViewById<AppCompatButton>(R.id.editButton)

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(publication: Publication, posicion: Int) {
        binding.idDescripcion.text = publication.descripcion
        binding.idContacto.text = publication.contacto
        binding.idDistancia.text = resources.getString(R.string.distanceStart) + " $posicion " + resources.getString(R.string.distanceEnd)
        binding.idFecha.text = FechaCalculator.calcularDistancia(publication.fechaPublicacion)
        binding.editButton.setOnClickListener{

       }
        binding.deleteButton.setOnClickListener{

        }

        /* Insertamos como imagen el base64 */
        val decodedString: ByteArray = Base64.decode(publication.foto, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.idPublicacion.setImageBitmap(decodedByte)
    }


}
