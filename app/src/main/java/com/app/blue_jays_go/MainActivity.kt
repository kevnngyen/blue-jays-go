package com.app.blue_jays_go

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.app.blue_jays_go.presentation.ViewModel.SportsViewModel
import com.app.blue_jays_go.presentation.ui.theme.BlueJaysgoTheme
import org.koin.androidx.compose.koinViewModel

// Entry point Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // enable drawing edge-to-edge
        setContent {
            BlueJaysgoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Render TeamDetails composable with padding from Scaffold
                    TeamDetails(
                        modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
                    )
                }
            }
        }
    }
}

/**
 * Shows selected team info inside the Box:
 * - Gets SportsViewModel instance via Koin
 * - Observes team StateFlow
 * - Displays CircularProgress while loading
 * - Shows logo, team name, abbreviation, and record when loaded
 */
@Composable
fun TeamDetails(modifier: Modifier = Modifier) {
    val vm: SportsViewModel = koinViewModel()
    val team by vm.team.collectAsState() // observe team state

    Box(modifier = modifier.fillMaxSize().background(Color.DarkGray)) {
        // Horizontal scrollable bar of team logos
        HorizontalTeamBar()
//        Dropdown() // alternate dropdown selector

        when {
            team == null -> {
                // Show loading spinner while team is not loaded
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
            else -> {
                // Show team details
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Team logo
                    AsyncImage(
                        model = team!!.logoUrl,
                        contentDescription = "${team!!.name} logo",
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    // Team name + record
                    Text(team!!.name, style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text("${team!!.abbreviation} • W-L: ${team!!.wins}-${team!!.losses}", color = Color.White)
                }
            }
        }
    }
}

/**
 * Dropdown menu for selecting a team abbreviation:
 * - Uses OutlinedTextField with trailing expand/collapse icon
 * - Expands into DropdownMenu with all team abbreviations
 * - Updates ViewModel when new team selected
 */
@Composable
fun Dropdown() {
    val vm: SportsViewModel = koinViewModel()
    val teamsAbr by vm.teamsAbrv.collectAsState()
    var mExpanded by remember { mutableStateOf(false) } // is dropdown expanded?
    val mSelectedTeam = vm.selectedTeam.collectAsState() // current selection
    var mTextFieldSize by remember { mutableStateOf(Size.Zero) } // to match dropdown width

    // Change icon depending on expanded state
    val icon = if (mExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown

    Column(Modifier.padding(20.dp)) {
        OutlinedTextField(
            value = mSelectedTeam.value,
            onValueChange = { }, // read-only
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // capture text field width for dropdown
                    mTextFieldSize = coordinates.size.toSize()
                },
            label = { Text("MLB Teams") },
            trailingIcon = {
                // Toggle expanded state
                Icon(icon, "contentDescription",
                    Modifier.clickable { mExpanded = !mExpanded })
            }
        )

        // Dropdown list of teams
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
                        vm.changeSelectedTeam(team) // update selected team
                        mExpanded = false
                    }
                )
            }
        }
    }
}

/**
 * Horizontal scrollable team bar:
 * - Uses LazyRow to display team logos in a row
 * - Each logo is clickable and updates selected team in ViewModel
 * - Adds scaling animation when pressed
 */
@Preview
@Composable
fun HorizontalTeamBar() {
    val vm: SportsViewModel = koinViewModel()
    val teamsAbr by vm.teamsAbrv.collectAsState() // list of team abbreviations
    val listState = rememberLazyListState() // remember scroll state
    val logoDetails by vm.logos.collectAsState() // map of teamAbbr -> logo URL

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF5E5E5E)) // dark background strip
    ) {
        items(teamsAbr.size) { item ->
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            Box {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {

                    // Takes the team alternative color and dim it
                    val backgroundColor = Color(logoDetails[teamsAbr[item]]?.get("color")?.toColorInt() ?: 0xFF000000.toInt()).copy(alpha = 0.4f)

                    Box(
                        modifier = Modifier
                            // scale down when pressed
                            .graphicsLayer {
                                scaleX = if (isPressed) 0.85f else 1f
                                scaleY = if (isPressed) 0.85f else 1f
                            }
                            .background(
                                color = backgroundColor,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .border(4.dp, Color(0xFF424242), shape = RoundedCornerShape(50.dp))
                            .size(70.dp)
                            .clickable(
                                indication = null, // disable default ripple
                                interactionSource = interactionSource
                            ) {
                                // change team on click
                                vm.changeSelectedTeam(teamsAbr[item])
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        // team logo image
                        AsyncImage(
                            model = logoDetails[teamsAbr[item]]?.get("url"),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(0.7f)
                        )
                    }
                    // team abbreviation text
                    Text(
                        teamsAbr[item],
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
            }
        }
    }
}
