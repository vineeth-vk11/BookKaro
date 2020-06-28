package com.example.bookkaro.mainui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookkaro.R
import com.example.bookkaro.databinding.FragmentHomeBinding
import com.example.bookkaro.helper.home.AdsAdapter
import com.example.bookkaro.helper.home.CategoryAdapter
import com.example.bookkaro.helper.home.ServicesGroupAdapter


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModel = ViewModelProvider(this, HomeViewModelFactory(requireActivity().application)).get(HomeViewModel::class.java)

        viewModel.getCategories().observe(viewLifecycleOwner, Observer { categories ->
            if (!categories.isNullOrEmpty()) {
                binding.categoryRecyclerView.apply {
                    layoutManager = GridLayoutManager(requireContext(), 3)
                    adapter = CategoryAdapter(requireContext(), categories)
                }
            }
        })

        viewModel.getAds().observe(viewLifecycleOwner, Observer { ads ->
            if (!ads.isNullOrEmpty()) {
                binding.AdRecycler.apply {
                    layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    adapter = AdsAdapter(requireContext(), ads)
                }
            }
        })

        val navController = findNavController()
        viewModel.getServices().observe(viewLifecycleOwner, Observer { services ->
            if (!services.isNullOrEmpty()) {
                binding.servicesRecycler.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = ServicesGroupAdapter(requireContext(), services, navController)
                }
            }
        })

        binding.currentAddressText.setOnClickListener { findNavController().navigate(R.id.action_homeFragment_to_viewAddressFragment) }

        return binding.root
    }

}