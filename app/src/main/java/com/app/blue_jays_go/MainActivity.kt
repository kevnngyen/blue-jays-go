package com.app.blue_jays_go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.blue_jays_go.presentation.ViewModel.SportsViewModel
import com.app.blue_jays_go.presentation.ui.theme.BlueJaysgoTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlueJaysgoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Shows Toronto Blue Jays info inside the Box:
 * - asks ViewModel to load "tor"
 * - observes UI state (loading/error/data)
 * - renders logo, name, record, next game
 */
@Preview(showBackground = true)
@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val vm: SportsViewModel = koinViewModel()
    val team by vm.team.collectAsState()

    // fire once
    LaunchedEffect(Unit) { vm.loadTeam("tor") }

    Box(modifier = modifier.fillMaxSize()) {
        when {
            team == null -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            else -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = team!!.logoUrl,
                        contentDescription = "${team!!.name} logo",
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(team!!.name, style = MaterialTheme.typography.titleLarge)
                    Text("${team!!.abbreviation} • W-L: ${team!!.wins}-${team!!.losses}")
                    team!!.nextGame?.let { Text("Next: $it") }
                }
            }
        }
    }



}


@Composable
fun GreetingPreview() {
    BlueJaysgoTheme {
        // Preview a skeleton
        Box(Modifier.fillMaxSize()) {
            Text("Blue Jays (preview)", Modifier.align(Alignment.Center))
        }
    }
}
