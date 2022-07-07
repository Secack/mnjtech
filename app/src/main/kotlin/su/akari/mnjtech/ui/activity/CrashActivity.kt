package su.akari.mnjtech.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.akari.mnjtech.ui.component.LazyListScaffold

class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val stackTrace = intent.getStringExtra("stackTrace") ?: ""

        setContent {
            MaterialTheme {
                LazyListScaffold(
                    title = {
                        Text(text = "崩溃了...")
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { finish() }
                        ) {
                            Icon(Icons.Outlined.Close, null)
                        }
                    }
                ) { padding, state ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .padding(padding),
                        state = state
                    ) {
                        items(stackTrace.split("\n")) {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}