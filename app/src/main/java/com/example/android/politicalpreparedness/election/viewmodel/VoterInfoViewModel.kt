package com.example.android.politicalpreparedness.election.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.election.repo.VoterInfoRepoInterface
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.UpcomingImageStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(private val voterInfoRepo: VoterInfoRepoInterface) : ViewModel() {

    private val _apiStatus = MutableLiveData<UpcomingImageStatus>()
    val apiStatus: LiveData<UpcomingImageStatus>
        get() = _apiStatus

    private val _election = MutableLiveData<Election>()
    val election: LiveData<Election>
        get() = _election

    private val _administrationBody = MutableLiveData<AdministrationBody>()
    val administrationBody: LiveData<AdministrationBody>
        get() = _administrationBody

    private val _url = MutableLiveData<String?>()
    val url: MutableLiveData<String?>
        get() = _url

    private val _isElectionSaved = MutableLiveData<Boolean>()
    val isElectionSaved: LiveData<Boolean>
        get() = _isElectionSaved

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()

    val correspondenceAddress = _voterInfo.value?.state?.first()?.electionAdministrationBody?.correspondenceAddress?.toFormattedString()

    fun getVoterInformation(electionId: Int, division: Division) {
        _apiStatus.value = UpcomingImageStatus.LOADING

        viewModelScope.launch {
            try {
                val savedElection = voterInfoRepo.getElectionById(electionId)
                _isElectionSaved.value = savedElection != null

                val address = "${division.state}, ${division.country}"
                val voterInfoResponse = voterInfoRepo.getVoterInfo(address, electionId)

                if (voterInfoResponse.isSuccessful) {
                    _voterInfo.value = voterInfoResponse.body()

                    Timber.d("voterInfoResponse, %s", voterInfoResponse)
                    _election.value = voterInfoResponse.body()?.election

                    Timber.d("AdministrationBody: %s", voterInfoResponse.body()?.state?.first()?.electionAdministrationBody)
                    _administrationBody.value = voterInfoResponse.body()?.state?.first()?.electionAdministrationBody

                    _apiStatus.value = UpcomingImageStatus.DONE
                } else {
                    Timber.e(voterInfoResponse.message())
                    _apiStatus.value = UpcomingImageStatus.ERROR
                }
            } catch (e: Exception) {
                Timber.e("Error when retrieving voter information")
                _apiStatus.value = UpcomingImageStatus.ERROR
            }
        }
    }

    fun navigateToUrl(url: String) {
        _url.value = url
    }

    fun navigateToUrlCompleted() {
        _url.value = null
    }

    fun toggleFollowElection() {
        viewModelScope.launch {
            _election.value?.let {
                if (_isElectionSaved.value == true) {
                    voterInfoRepo.deleteLocalElection(it)
                    _isElectionSaved.value = false
                } else {
                    voterInfoRepo.insertLocalElections(it)
                    _isElectionSaved.value = true
                }
            }
        }
    }

    fun visibility(): Int {
        return if (_voterInfo.value?.state?.first()?.electionAdministrationBody?.correspondenceAddress == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}
