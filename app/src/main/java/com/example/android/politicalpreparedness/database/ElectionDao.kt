package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg election: Election)

    @Query("SELECT * FROM election_table")
    suspend fun getStoredElections(): List<Election>

    @Query("SELECT * FROM election_table WHERE id = :id")
    suspend fun getElectionBy(id: Int): Election?

    @Delete
    suspend fun deleteElection(election: Election)

    @Query("DELETE FROM election_table")
    suspend fun clear()
}
