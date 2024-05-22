package edu.stanford.healthconnectonfhirdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import edu.stanford.healthconnectonfhirdemo.ui.theme.HealthConnectOnFHIRDemoTheme
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime

class MainActivity : ComponentActivity() {
   private val healthConnectManager = HealthConnectManager(this)
   private var permissionsGranted = mutableStateOf<Boolean>(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getWritePermission(StepsRecord::class)
        )

        val requestPermissionsActivityContract =
            PermissionController.createRequestPermissionResultContract()

        val requestPermissions =
            registerForActivityResult(requestPermissionsActivityContract) { granted ->
                this.permissionsGranted.value = granted.containsAll(permissions)
            }

        enableEdgeToEdge()
        setContent {
            HealthConnectOnFHIRDemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        healthConnectManager,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(healthConnectManager: HealthConnectManager, modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                healthConnectManager.writeStepsRecord(1000)
            }
        }) {
            Text(text = "Write Steps Record")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                val today = ZonedDateTime.now().toLocalDate()
                val startOfDay = today.atStartOfDay(ZoneId.systemDefault()).toInstant()
                val endOfDay = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                val records = healthConnectManager.getStepsRecords(startOfDay, endOfDay)

                // TODO: Convert to FHIR
            }
        }) {
            Text(text = "Get Today's Steps Records")
        }
    }
}