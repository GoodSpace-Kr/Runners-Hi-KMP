package good.space.runnershi.user.domain

import good.space.runnershi.model.dto.running.RunCreateRequest
import good.space.runnershi.user.UserType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.Table
import kotlin.io.path.Path

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
abstract class User(
    var name: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Enumerated(EnumType.STRING)
    var userType: UserType,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var exp: Long = 0;

    var totalRunningDays: Long = 0

    var totalDistanceMeters: Double = 300.0

    var totalRunningHours: Double = 0.0

    var bestPace: Double = 0.0

    var longestDistanceMeters: Double = 0.0

    var averagePace: Double = 0.0


    @ElementCollection(targetClass = Achievement::class, fetch = FetchType.LAZY)
    @CollectionTable(
        name = "user_achievements", // 실제 생길 테이블 이름
        joinColumns = [JoinColumn(name = "user_id")]
    )
    @Enumerated(EnumType.STRING) // DB에 "CUMULATIVE_LV1" 문자열로 저장
    @Column(name = "achievement")
    val achievements: MutableSet<Achievement> = mutableSetOf()

    fun increaseExp(amount: Long) {
        this.exp += amount
    }

    fun updateRunningStats(request: RunCreateRequest) {
        val pace: Double = request.durationSeconds.toDouble() * (1000.0 / request.distanceMeters)
        val distanceMeters: Double = 0.0

        this.totalDistanceMeters += request.distanceMeters.toLong()

        this.totalRunningHours += request.durationSeconds / 60.0

        if (distanceMeters > this.longestDistanceMeters) {
            this.longestDistanceMeters = request.distanceMeters
        }

        if (this.bestPace == 0.0 || pace < this.bestPace) {
            this.bestPace = pace
        }

        averagePace = (averagePace + pace) / 2

        this.totalRunningDays += 1

        updateAchievement()
    }

    private fun updateAchievement() {
        for (achievement in Achievement.entries) {
            if (achievement.available(this)) {
                achievements.add(achievement)
            }
        }
    }
}
