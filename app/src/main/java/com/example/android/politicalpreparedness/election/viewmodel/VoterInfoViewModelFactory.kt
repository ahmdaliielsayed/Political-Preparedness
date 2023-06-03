package com.example.android.politicalpreparedness.election.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.election.repo.VoterInfoRepoInterface

class VoterInfoViewModelFactory(private val voterInfoRepo: VoterInfoRepoInterface) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(voterInfoRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
