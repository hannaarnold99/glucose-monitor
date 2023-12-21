package edu.arizona.cast.hannaarnold.glucosemonitor.database

import androidx.room.*
import edu.arizona.cast.hannaarnold.glucosemonitor.Glucose
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface GlucoseDao {
    @Query("SELECT * FROM glucose")
    fun getGlucoses(): Flow<List<Glucose>>

    @Query("SELECT * FROM glucose WHERE date=(:date)")
    suspend fun getGlucose(date: Date): Glucose?

    @Update
    suspend fun updateGlucose(glucose: Glucose)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGlucose(glucose: Glucose)
}