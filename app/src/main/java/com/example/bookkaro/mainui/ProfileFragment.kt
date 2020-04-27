package com.example.bookkaro.mainui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookkaro.R
import com.example.bookkaro.databinding.FragmentProfileBinding
import com.example.bookkaro.helper.ProfileOption
import com.example.bookkaro.helper.ProfileOptionAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)

        val navController = Navigation.findNavController(requireActivity(), R.id.activity_main_nav_host_fragment)

        val options = arrayListOf(
                ProfileOption(R.drawable.ic_address, "Manage Address", View.OnClickListener { navController.navigate(R.id.action_profileFragment_to_manageAddressFragment) }),
                ProfileOption(R.drawable.bookings, "My Bookings", View.OnClickListener { navController.navigate(R.id.action_profileFragment_to_bookingsFragment) }),
                ProfileOption(R.drawable.ic_about, "About Book Karo", View.OnClickListener { navController.navigate(R.id.action_profileFragment_to_aboutFragment) }),
                ProfileOption(R.drawable.ic_help, "User Help", View.OnClickListener { navController.navigate(R.id.action_profileFragment_to_helpFragment) }),
                ProfileOption(R.drawable.ic_faq, "FAQ", View.OnClickListener { navController.navigate(R.id.action_profileFragment_to_FAQFragment) }),
                ProfileOption(R.drawable.ic_share, "Share Book Karo", View.OnClickListener {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "Sharing Book Karo")
                    startActivity(intent)
                }),
                ProfileOption(R.drawable.ic_rate, "Rate Book Karo", View.OnClickListener { TODO("get rating link") })
        )

        binding.profileOptionsRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ProfileOptionAdapter(options, context)
        }

        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser

        binding.profilePhoneText.text = user?.phoneNumber
        db.collection(getString(R.string.firebase_collection_user_data)).document(user!!.uid)
                .get()
                .addOnSuccessListener { document ->
                    binding.profileNameText.text = document.data?.get(getString(R.string.firebase_field_name)).toString()

                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileFragment", "Failed to fetch document")
                    //TODO: Notify user
                }

        binding.profileEditText.setOnClickListener {
            //TODO: Open edit details activity
        }
        return binding.root
    }

}