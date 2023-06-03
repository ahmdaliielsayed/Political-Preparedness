package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.*
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.CivicsApiStatus
import kotlinx.coroutines.launch
import timber.log.Timber

class RepresentativeViewModel(private val savedHandle: SavedStateHandle) : ViewModel() {

    private val _apiService = CivicsApi.retrofitService

    private val _apiStatus: MutableLiveData<CivicsApiStatus> = MutableLiveData()
    val apiStatus: LiveData<CivicsApiStatus>
        get() = _apiStatus

    private val _representatives = MutableLiveData<List<Representative>?>()
    val representatives: MutableLiveData<List<Representative>?>
        get() = _representatives

    private val _address = MutableLiveData<Address?>()
    val address: MutableLiveData<Address?>
        get() = _address

    private val _navigateToRepresentativeInfo = MutableLiveData<Representative?>()
    val navigateToRepresentativeInfo: MutableLiveData<Representative?>
        get() = _navigateToRepresentativeInfo

    init {
        _address.value = Address("", null, "", "", "")
        val list: List<Representative>? = savedHandle["representatives"]
        if (list != null) {
            _representatives.value = list
        }
    }

    fun getRepresentativesList(address: Address?) {
        _apiStatus.value = CivicsApiStatus.LOADING

        viewModelScope.launch {
            _representatives.value = arrayListOf()
            if (address != null) {
                try {
                    _address.value = address
                    val (offices, officials) = _apiService.getRepresentatives(_address.value?.toFormattedString()!!)
                    _representatives.value =
                        offices.flatMap { office -> office.getRepresentatives(officials) }
                    savedHandle["representatives"] = _representatives.value
                    _apiStatus.value = CivicsApiStatus.DONE
                } catch (e: Exception) {
                    Timber.e(
                        "Error: %s",
                        e.localizedMessage
                    )
                    _apiStatus.value = CivicsApiStatus.ERROR
                }
            }
        }
    }

    fun displayRepresentativeInfo(representative: Representative) {
        _navigateToRepresentativeInfo.value = representative
    }

    fun displayRepresentativeInfoComplete() {
        _navigateToRepresentativeInfo.value = null
    }

    fun getRepresentativesList() {
        Timber.d("address: %s", _address.value)
        getRepresentativesList(_address.value)
    }
}
