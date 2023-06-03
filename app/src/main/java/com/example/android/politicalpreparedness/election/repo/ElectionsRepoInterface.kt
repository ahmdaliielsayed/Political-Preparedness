package com.example.android.politicalpreparedness.election.repo

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import retrofit2.Response

interface ElectionsRepoInterface {

    suspend fun getUpcomingElectionsFromServer(): Response<ElectionResponse>
    suspend fun getStoredElections(): List<Election>
}
