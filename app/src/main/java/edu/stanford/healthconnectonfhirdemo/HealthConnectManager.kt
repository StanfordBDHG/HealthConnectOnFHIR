package edu.stanford.healthconnectonfhirdemo

import android.content.Context
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.mutableStateOf
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
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

    suspend fun hasAllPermissions(permissions: Set<String>): Boolean {
        return healthConnectClient?.permissionController?.getGrantedPermissions()?.containsAll(permissions)
            ?: false
    }

    fun requestPermissionsActivityContract(): ActivityResultContract<Set<String>, Set<String>> {
        return PermissionController.createRequestPermissionResultContract()
    }

    suspend fun getStepsRecords(startTime: Instant, endTime: Instant): List<StepsRecord>? {
        return healthConnectClient?.readRecords(
                ReadRecordsRequest(
                    StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )?.records
    }

    suspend fun writeStepsRecord(stepsCount: Long) {
        val now = Instant.now()
        val zoneOffset = java.time.ZoneId.systemDefault().rules.getOffset(now)

        val stepsRecord = StepsRecord(
            count = stepsCount,
            startTime = now,
            endTime = now,
            startZoneOffset = zoneOffset,
            endZoneOffset = zoneOffset
        )
        healthConnectClient?.insertRecords(listOf(stepsRecord))
    }
}