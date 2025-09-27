package com.app.blue_jays_go.presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.blue_jays_go.domain.model.Team
import com.app.blue_jays_go.domain.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SportsViewModel(
    private val repository: SportsRepository
) : ViewModel() {

    // Backing property: mutable state, only editable inside this ViewModel
    private val _team = MutableStateFlow<Team?>(null)

    // Publicly exposed as read-only so UI can observe
    val team: StateFlow<Team?> = _team

    /**
     * Loads team details (called from UI)
     * Uses coroutines to avoid blocking the main thread
     */
    fun loadTeam(teamSlug: String) {
        // viewModelScope is a CoroutineScope tied to the ViewModel’s lifecycle
        viewModelScope.launch {
            try {
                // 1. Suspend function call (runs off main thread)
                val result = repository.getTeamDetails(teamSlug)

                // 2. Update StateFlow with result
                //    UI observing this will recompose automatically
                _team.value = result
            } catch (e: Exception) {

                // If API/network fails, we could expose error state here
                e.printStackTrace()
            }
        }
    }
}
