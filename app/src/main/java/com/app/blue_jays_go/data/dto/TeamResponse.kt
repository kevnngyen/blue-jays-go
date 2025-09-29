import com.app.blue_jays_go.data.dto.LogoDto

// This data class is represents the information we get from the team response API
// https://site.api.espn.com/apis/site/v2/sports/baseball/mlb/teams/{team}
// e.g team = tor

data class TeamResponse(
    val team: TeamDetailDto
)

data class TeamDetailDto(
    val id: String,
    val displayName: String,
    val abbreviation: String,
    val logos: List<LogoDto>,
    val record: RecordDto?,
    val nextEvent: List<EventDto>
)

data class RecordDto(
    val items: List<RecordItemDto>
)

data class RecordItemDto(
    val summary: String
)

data class EventDto(
    val name: String,
    val date: String

)
