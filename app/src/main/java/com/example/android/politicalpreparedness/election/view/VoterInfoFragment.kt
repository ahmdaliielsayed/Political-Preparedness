package com.example.android.politicalpreparedness.election.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.repo.VoterInfoRepository
import com.example.android.politicalpreparedness.election.viewmodel.VoterInfoViewModel
import com.example.android.politicalpreparedness.election.viewmodel.VoterInfoViewModelFactory
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.utils.UpcomingImageStatus
import timber.log.Timber

class VoterInfoFragment : Fragment() {

    private val viewModel by viewModels<VoterInfoViewModel> {
        VoterInfoViewModelFactory(
            VoterInfoRepository.getInstance(
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
        val voterInfoFragmentArgs =
            VoterInfoFragmentArgs.fromBundle(
                requireArguments()
            )
        val electionId = voterInfoFragmentArgs.argElectionId
        val division = voterInfoFragmentArgs.argDivision

        Timber.d("electionId: %s", electionId)
        Timber.d("division: %s", division)

        val binding = FragmentVoterInfoBinding.inflate(layoutInflater)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            voterInfoViewModel = viewModel
        }

        viewModel.getVoterInformation(electionId, division)

        viewModel.url.observe(viewLifecycleOwner) { it ->
            it?.let {
                loadUrl(it)
                viewModel.navigateToUrlCompleted()
            }
        }

        viewModel.apiStatus.observe(viewLifecycleOwner) {
            it?.let {
                if (it == UpcomingImageStatus.ERROR) showRequestErrorDialog()
            }
        }

        return binding.root
    }

    private fun showRequestErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error))
            .setMessage(getString(R.string.voter_info_error_message))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                this.findNavController().popBackStack()
            }.show()
    }

    private fun loadUrl(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }
}
