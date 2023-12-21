package edu.arizona.cast.hannaarnold.glucosemonitor

import android.content.Context
import kotlinx.coroutines.flow.Flow
import androidx.room.Room
import edu.arizona.cast.hannaarnold.glucosemonitor.database.GlucoseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

private const val DATABASE_NAME = "glucose-database.db"

class GlucoseRepository private constructor(
    context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope
    ) {
        private val database: GlucoseDatabase = Room.databaseBuilder(
        context.applicationContext,
        GlucoseDatabase::class.java,
        DATABASE_NAME
    ).createFromAsset(DATABASE_NAME)
        .build()

    fun getGlucoses() : Flow<List<Glucose>> = database.glucoseDao().getGlucoses()


    suspend fun getGlucose(date: Date): Glucose? = database.glucoseDao().getGlucose(date)
    fun updateGlucose(glucose: Glucose) {
        coroutineScope.launch {
            database.glucoseDao().updateGlucose(glucose)
        }
    }

    suspend fun addGlucose(glucose: Glucose) {
        database.glucoseDao().addGlucose(glucose)
    }

    companion object {
        private var INSTANCE: GlucoseRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = GlucoseRepository(context)
            }
        }

        fun get(): GlucoseRepository {
            return INSTANCE
                ?: throw IllegalStateException("GlucoseRepository must be initialized")
        }
    }
}