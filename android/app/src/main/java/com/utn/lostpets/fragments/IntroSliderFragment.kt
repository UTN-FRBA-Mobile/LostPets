package com.utn.lostpets.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.facebook.FacebookSdk
import com.utn.lostpets.R
import com.utn.lostpets.adapters.IntroSliderAdapter
import com.utn.lostpets.databinding.FragmentIntroSliderBinding

class IntroSliderFragment : Fragment() {

    private var _binding: FragmentIntroSliderBinding? = null
    private val binding get() = _binding!!
    private val fragmentList = ArrayList<Fragment>()
    private val res = FacebookSdk.getApplicationContext().getResources()
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIntroSliderBinding.inflate(inflater, container, false)
        email = arguments?.getString("email").toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = activity?.let { IntroSliderAdapter(it) }
        binding.vpIntroSlider.adapter = adapter

        fragmentList.addAll(listOf(
            Intro1Fragment(), Intro2Fragment(), Intro3Fragment()
        ))
        adapter?.setFragmentList(fragmentList)

        if (adapter != null) {
            binding.indicatorLayout.setIndicatorCount(adapter.itemCount)
        }
        binding.indicatorLayout.selectCurrentPosition(0)

        registerListeners()
    }

    private fun registerListeners() {
        binding.vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                binding.indicatorLayout.selectCurrentPosition(position)

                if (position < fragmentList.lastIndex) {
                    binding.tvSkip.visibility = View.VISIBLE
                    binding.tvNext.text = res.getString(R.string.next)
                } else {
                    binding.tvSkip.visibility = View.GONE
                    binding.tvNext.text = res.getString(R.string.start)
                }
            }
        })

        binding.tvSkip.setOnClickListener {
            val bundle = bundleOf()
            val action = R.id.action_introSliderFragment_to_mapsFragment
            findNavController().navigate(action, bundle)
        }

        binding.tvNext.setOnClickListener {
            val position = binding.vpIntroSlider.currentItem

            if (position < fragmentList.lastIndex) {
                binding.vpIntroSlider.currentItem = position + 1
            } else {
                /* Pasamos el mail del user al siguiente fragment */
                val bundle = bundleOf("email" to email)
                val action = R.id.action_introSliderFragment_to_mapsFragment
                findNavController().navigate(action, bundle)
            }
        }
    }
}
