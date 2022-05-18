package com.utn.lostpets

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.databinding.ActivityMainBinding
import com.utn.lostpets.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {

        /* Acción de "Registrarse" */
        binding.signUpButtom.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty())
                /* Le pasamos mail y pass a Firebase que se encargará de autenticar al usuario */
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.text.toString(),binding.passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
        }

        /* Acción de "Acceder" */
        binding.loginButtom.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty())
            /* Le pasamos mail y pass a Firebase que se encargará de autenticar al usuario */
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEditText.text.toString(),binding.passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
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
        val action = R.id.action_loginFragment_to_mapsFragment
        findNavController().navigate(action,bundle)
    }
}