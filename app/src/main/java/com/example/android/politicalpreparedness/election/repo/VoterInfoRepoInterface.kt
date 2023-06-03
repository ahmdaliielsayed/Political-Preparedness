package com.example.android.politicalpreparedness.election.repo

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import retrofit2.Response

interface VoterInfoRepoInterface {

    suspend fun getElectionById(id: Int): Election?
    suspend fun getVoterInfo(address: String, electionId: Int): Response<VoterInfoResponse>
    suspend fun deleteLocalElection(election: Election)
    suspend fun insertLocalElections(vararg election: Election)
}
