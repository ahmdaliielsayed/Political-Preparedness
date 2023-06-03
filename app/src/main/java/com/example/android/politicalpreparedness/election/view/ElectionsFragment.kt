package com.example.android.politicalpreparedness.election.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.repo.ElectionsRepository
import com.example.android.politicalpreparedness.election.view.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.view.adapter.ElectionListener
import com.example.android.politicalpreparedness.election.viewmodel.ElectionsViewModel
import com.example.android.politicalpreparedness.election.viewmodel.ElectionsViewModelFactory
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election

class ElectionsFragment : Fragment() {

    private val viewModel by viewModels<ElectionsViewModel> {
        ElectionsViewModelFactory(
            ElectionsRepository.getInstance(
                ElectionDatabase.getInstance(requireContext()).electionDao,
                CivicsApi.retrofitService
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentElectionBinding.inflate(inflater)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner

            electionsViewModel = viewModel

            recyclerUpcoming.adapter = ElectionListAdapter(
                ElectionListener {
                    viewModel.displayElectionInfo(it)
                }
            )

            recyclerSaved.adapter = ElectionListAdapter(
                ElectionListener {
                    viewModel.displayElectionInfo(it)
                }
            )
        }

        viewModel.navigateToElectionInfo.observe(viewLifecycleOwner) {
            if (null != it) {
                navigateToDetailFragment(it)
                viewModel.displayElectionInfoComplete()
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun navigateToDetailFragment(election: Election) {
        this.findNavController().navigate(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election.id,
                election.division
            )
        )
    }
}
