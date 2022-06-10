package com.utn.lostpets.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentRegistrationBinding

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {

        /* Acción de ir a "Login" */
        binding.loginButtom.setOnClickListener {
            val action = R.id.action_registrationFragment_to_loginFragment
            findNavController().navigate(action)
        }

        /* Acción de "Crear cuenta" */
        binding.createAccountButtom.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty())
            /* Le pasamos mail y pass a Firebase que se encargará de autenticar al usuario */
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
        }
    }

    /* Mostramos alerta en caso de que haya fallado la autenticación */
    private fun showAlert() {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /* Redirigimos a pantalla principal en caso de login exitoso */
    private fun showHome(email: String) {
        val bundle = bundleOf("email" to email)
        val action = R.id.action_registrationFragment_to_mapsFragment
        findNavController().navigate(action, bundle)
    }
}