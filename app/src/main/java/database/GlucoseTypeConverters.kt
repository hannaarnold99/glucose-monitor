package edu.arizona.cast.hannaarnold.glucosemonitor.database

import androidx.room.TypeConverter
import java.util.Date

class GlucoseTypeConverters {
    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long): Date {
        return Date(millisSinceEpoch)
    }
}