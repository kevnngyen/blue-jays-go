package com.app.blue_jays_go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
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
                    TeamDetails(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

/**
 * Shows selected team info inside the Box:
 * - asks ViewModel to load "tor"
 * - observes UI state (loading/error/data)
 * - renders logo, name, record,
 */
@Composable
fun TeamDetails(modifier: Modifier = Modifier) {
    val vm: SportsViewModel = koinViewModel()
    val team by vm.team.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        Dropdown()
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
                    Text("${team!!.abbreviation} • W-L: ${team!!.wins}-${team!!.losses}")}
                }
            }
        }
    }

@Composable
fun Dropdown() {

    val vm: SportsViewModel = koinViewModel()
    vm.loadTeamsAbr()
    val teamsAbr by vm.teamsAbrv.collectAsState()

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a string value to store the selected city
    val mSelectedTeam = vm.selectedTeam.collectAsState()

    var mTextFieldSize by remember { mutableStateOf(Size.Zero) }

    // Up Icon when expanded and down icon when collapsed
    val icon = if (mExpanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {

        // Create an Outlined Text Field
        // with icon and not expanded
        OutlinedTextField(
            value = mSelectedTeam.value,
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // This value is used to assign to
                    // the DropDown the same width
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = { Text("MLB Teams") },
            trailingIcon = {
                Icon(icon, "contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )

        // Create a drop-down menu with list of cities,
        // when clicked, set the Text Field text as the city selected
        DropdownMenu(
            expanded = mExpanded,
            onDismissRequest = { mExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { mTextFieldSize.width.toDp() })
                .height(200.dp)
        ) {
            teamsAbr.forEach { team ->
                DropdownMenuItem(
                    text = { Text(text = team) },
                    onClick = {
                        vm.changeSelectedTeam(team)
                        mExpanded = false
                    }
                )
            }
        }
    }
}
