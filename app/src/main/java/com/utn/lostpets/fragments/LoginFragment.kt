package com.utn.lostpets.fragments

import androidx.fragment.app.Fragment

class LoginFragment {

    interface LoginFragmentInteractionListener {
        fun showFragment(fragment: Fragment)
    }
}