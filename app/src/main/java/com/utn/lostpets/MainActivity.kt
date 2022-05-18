package com.utn.lostpets

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.databinding.ActivityMainBinding
import com.utn.lostpets.fragments.LoginFragment

class MainActivity : AppCompatActivity(), LoginFragment.LoginFragmentInteractionListener {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Lleva automáticamente al layout pudiendo acceder a sus contenidos */
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
    }

    private fun setup() {

        title = "Autenticación"

        binding.signUpButtom.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty())
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.emailEditText.text.toString(),binding.passwordEditText.text.toString())
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            showAlert()
                        }
                    }
        }

        binding.loginButtom.setOnClickListener {
            if(binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty())
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

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String) {
        val homeIntent = Intent(this, HomeActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    override fun showFragment(fragment: Fragment) {
        /*supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()*/
    }
}