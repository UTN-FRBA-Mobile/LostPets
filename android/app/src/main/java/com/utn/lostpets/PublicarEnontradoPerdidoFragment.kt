package com.utn.lostpets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.databinding.FragmentLoginBinding
import com.utn.lostpets.databinding.FragmentPublicarEnontradoPerdidoBinding


class PublicarEnontradoPerdidoFragment : Fragment() {

    private var _binding: FragmentPublicarEnontradoPerdidoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicarEnontradoPerdidoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        /* Se vuelve a la pantalla de "Login" en caso de cerrarse la sesión */

    }

}