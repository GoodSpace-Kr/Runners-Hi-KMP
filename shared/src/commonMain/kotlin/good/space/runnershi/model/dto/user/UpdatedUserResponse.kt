package good.space.runnershi.model.dto.running

import kotlinx.serialization.Serializable

@Serializable
data class UpdatedUserResponse(
    val runId: Long,
    val userId: Long,
    val userExp: Long,
    val totalRunningDays: Long,
    val badges: List<String>,
    val newBadges: List<newBadgeInfo>
)

@Serializable
data class newBadgeInfo(
    val name: String,
    val exp: Long
)
