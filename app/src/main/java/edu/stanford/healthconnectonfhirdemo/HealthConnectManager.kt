package edu.stanford.healthconnectonfhirdemo

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant

class HealthConnectManager(private val context: Context) {
    var healthConnectClient: HealthConnectClient? = null

    private var isAvailable = mutableStateOf(false)

    init {
        isAvailable.value = checkAvailabilityStatus()
        if (isAvailable.value) {
            healthConnectClient = HealthConnectClient.getOrCreate(context)
        }
    }

    private fun checkAvailabilityStatus(): Boolean {
        val availabilityStatus = HealthConnectClient.getSdkStatus(context)
        return availabilityStatus == HealthConnectClient.SDK_AVAILABLE
    }

    suspend fun getStepsRecords(startTime: Instant, endTime: Instant): List<StepsRecord>? {
        return healthConnectClient?.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )?.records
    }
}