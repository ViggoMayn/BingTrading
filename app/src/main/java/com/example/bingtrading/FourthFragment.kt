package com.example.bingtrading

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bingtrading.databinding.FragmentFourthBinding
import com.example.bingtrading.models.ListedItemsViewModel
import com.example.bingtrading.models.MyAdapterProfile
import com.google.firebase.auth.FirebaseAuth

class FourthFragment : Fragment() {
    private var _binding: FragmentFourthBinding? = null
    private val binding get() = _binding!!
    private val listedItemsViewModel: ListedItemsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFourthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listedItemsViewModel.listedItemsLiveData.observe(viewLifecycleOwner) { items ->
            binding.recyclerViewProfile.visibility = if (items == null) View.GONE else View.VISIBLE
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val targetEmail = currentUser?.email

            val itemsToShow = items?.filter { it.sellerEmail == targetEmail}
            if (itemsToShow != null) {
                val adapter = MyAdapterProfile(itemsToShow) { position ->
                    val action =
                        FourthFragmentDirections.actionFourthFragmentToSecondFragment(position)
                    findNavController().navigate(action)
                }
                var columns = 1
                val currentOrientation = this.resources.configuration.orientation
                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    columns = 2
                } else if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    columns = 1
                }

                binding.recyclerViewProfile.layoutManager = GridLayoutManager(this.context, columns)

                binding.recyclerViewProfile.adapter = adapter

                adapter.setOnDeleteItemClickListener { item ->
                    listedItemsViewModel.delete(item.id)
                }
            }

            binding.swiperefresh.setOnRefreshListener {
                listedItemsViewModel.reload()
                binding.swiperefresh.isRefreshing = false
            }

            binding.homeButton.setOnClickListener {
                findNavController().navigate(R.id.action_fourthFragment_to_FirstFragment)
            }

            binding.createListingButton.setOnClickListener {
                findNavController().navigate(R.id.action_fourthFragment_to_fifthFragment)
            }

            binding.buttonLogout.setOnClickListener {
                findNavController().navigate(R.id.action_fourthFragment_to_thirdFragment)
            }
        }
    }
}