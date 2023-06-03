package com.example.android.politicalpreparedness.election.repo

import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import retrofit2.Response

class ElectionsRepository private constructor(
    private val localDataSource: ElectionDao,
    private val remoteDataSource: CivicsApiService
) : ElectionsRepoInterface {

    companion object {
        private var instance: ElectionsRepoInterface? = null
        fun getInstance(localDataSource: ElectionDao, remoteDataSource: CivicsApiService): ElectionsRepoInterface {
            return instance ?: ElectionsRepository(localDataSource, remoteDataSource)
        }
    }

    override suspend fun getUpcomingElectionsFromServer(): Response<ElectionResponse> {
        return remoteDataSource.getElections()
    }

    override suspend fun getStoredElections(): List<Election> {
        return localDataSource.getStoredElections()
    }
}
