package com.example.android.politicalpreparedness.election.repo

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import retrofit2.Response

class VoterInfoRepository private constructor(
    private val localDataSource: ElectionDao,
    private val remoteDataSource: CivicsApiService
) : VoterInfoRepoInterface {

    companion object {
        private var instance: VoterInfoRepoInterface? = null
        fun getInstance(localDataSource: ElectionDao, remoteDataSource: CivicsApiService): VoterInfoRepoInterface {
            return instance ?: VoterInfoRepository(localDataSource, remoteDataSource)
        }
    }

    override suspend fun getElectionById(id: Int): Election? {
        return localDataSource.getElectionBy(id)
    }

    override suspend fun getVoterInfo(address: String, electionId: Int): Response<VoterInfoResponse> {
        return remoteDataSource.getVoterInfo(address, electionId)
    }

    override suspend fun deleteLocalElection(election: Election) {
        localDataSource.deleteElection(election)
    }

    override suspend fun insertLocalElections(vararg election: Election) {
        localDataSource.insert(*election)
    }
}
