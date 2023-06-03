package com.example.android.politicalpreparedness.election.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.election.repo.ElectionsRepoInterface

class ElectionsViewModelFactory(
    private val electionsRepository: ElectionsRepoInterface
) : ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ElectionsViewModel::class.java)) {
            return ElectionsViewModel(electionsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
