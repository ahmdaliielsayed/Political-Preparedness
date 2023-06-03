package com.example.android.politicalpreparedness.election

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.utils.CivicsApiStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class VoterInfoViewModel(private val dataSource: ElectionDao, private val apiService: CivicsApiService) : ViewModel() {

    private val _apiStatus = MutableLiveData<CivicsApiStatus>()
    val apiStatus: LiveData<CivicsApiStatus>
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
        _apiStatus.value = CivicsApiStatus.LOADING

        viewModelScope.launch {
            try {
                val savedElection = dataSource.getElection(electionId)
                _isElectionSaved.value = savedElection != null

                val address = "${division.state}, ${division.country}"
                val voterInfoResponse = apiService.getVoterInfo(address, electionId)

                _voterInfo.value = voterInfoResponse

                Timber.d("voterInfoResponse, %s", voterInfoResponse)
                _election.value = voterInfoResponse.election

                Timber.d("AdministrationBody: %s", voterInfoResponse.state?.first()?.electionAdministrationBody)
                _administrationBody.value = voterInfoResponse.state?.first()?.electionAdministrationBody

                _apiStatus.value = CivicsApiStatus.DONE
            } catch (e: Exception) {
                Timber.e("Error when retrieving voter information")
                _apiStatus.value = CivicsApiStatus.ERROR
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
                    dataSource.deleteElection(it)
                    _isElectionSaved.value = false
                } else {
                    dataSource.insertElection(it)
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
