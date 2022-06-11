package com.utn.lostpets.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.utn.lostpets.MainActivity
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    /* Google sign in */
    private val GOOGLE_SIGN_IN = 100
    /* Facebook sign in */
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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

        /* Acción de ir a "Registrarse" */
        binding.signUpButtom.setOnClickListener {
            val action = R.id.action_loginFragment_to_registrationFragment
            findNavController().navigate(action)
        }

        /* Acción de "Acceder" */
        binding.loginButtom.setOnClickListener {
            if (binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty())
            /* Le pasamos mail y pass a Firebase que se encargará de autenticar al usuario */
                FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
        }

        /* Acción de "Acceder con Google" */
        binding.googleButton.setOnClickListener{
            val googleConf : GoogleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            /* Cliente autenticación Google */
            val googleClient : GoogleSignInClient = GoogleSignIn.getClient(requireContext(), googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        /* Acción de "Acceder con Facebook" */
        binding.facebookButtom.setOnClickListener{

            LoginManager.getInstance().logInWithReadPermissions(getActivity(), listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onSuccess(result: LoginResult?) {

                        result?.let {
                            val token = it.accessToken

                            val credential = FacebookAuthProvider.getCredential(token.token)

                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        showHome(it.result?.user?.email ?: "")
                                    } else {
                                        showAlert()
                                    }
                                }
                        }
                    }

                    override fun onCancel() {
                        TODO("Not yet implemented")
                    }

                    override fun onError(error: FacebookException?) {
                        showAlert()
                    }
                })
        }
    }

    /* Hace el intento de login con Google */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if(account != null) {

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "")
                            } else {
                                showAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                showAlert()
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
        findNavController().navigate(action, bundle)
    }
}
