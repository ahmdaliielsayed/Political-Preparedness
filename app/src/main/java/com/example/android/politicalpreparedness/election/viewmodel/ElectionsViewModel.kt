package com.example.android.politicalpreparedness.election.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.election.repo.ElectionsRepoInterface
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.utils.UpcomingImageStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class ElectionsViewModel(
    private val electionsRepository: ElectionsRepoInterface
) : ViewModel() {

    private val _upComingElections = MutableLiveData<List<Election>>()
    val upcomingElections: LiveData<List<Election>>
        get() = _upComingElections

    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections: LiveData<List<Election>>
        get() = _savedElections

    private val _upcomingStatus = MutableLiveData<UpcomingImageStatus>()
    val upcomingStatus: LiveData<UpcomingImageStatus>
        get() = _upcomingStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _navigateToElectionInfo = MutableLiveData<Election?>()
    val navigateToElectionInfo: MutableLiveData<Election?>
        get() = _navigateToElectionInfo

    init {
        getUpcomingElectionsFromServer()
        getStoredElections()
    }

    private fun getUpcomingElectionsFromServer() {
        _upcomingStatus.value = UpcomingImageStatus.LOADING
        viewModelScope.launch {
            try {
                val response = electionsRepository.getUpcomingElectionsFromServer()
                if (response.isSuccessful) {
                    _upcomingStatus.value = UpcomingImageStatus.DONE
                    _upComingElections.value = response.body()?.elections
                } else {
                    Timber.e("Error: %s", response.message())
                    _upcomingStatus.value = UpcomingImageStatus.ERROR
                    _upComingElections.value = ArrayList()
                }
            } catch (e: Exception) {
                Timber.e("Error: %s", e.localizedMessage)
                _upcomingStatus.value = UpcomingImageStatus.ERROR
                _upComingElections.value = ArrayList()
            }
        }
    }

    private fun getStoredElections() {
        _isLoading.value = true
        viewModelScope.launch {
            _savedElections.value = electionsRepository.getStoredElections()
            _isLoading.value = false
        }
    }

    fun displayElectionInfo(election: Election) {
        _navigateToElectionInfo.value = election
    }

    fun displayElectionInfoComplete() {
        _navigateToElectionInfo.value = null
    }

    fun refresh() {
        getStoredElections()
    }
}
