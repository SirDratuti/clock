import java.time.Instant
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

internal interface EventsStatistic {
    fun incEvent(name: String)
    fun printStatistics()

    fun getEventStatisticByName(name: String): Double
    fun getAllEventStatistics(): Map<String, Double>
}

private typealias StatName = String
private typealias Statistics = MutableList<Instant>

internal class EventsStatisticImpl(
    private val clock: Clock,
) : EventsStatistic {
    private val statistics = mutableMapOf<StatName, Statistics>()

    override fun incEvent(name: String) {
        val stats = statistics.getOrPut(name) {
            mutableListOf()
        }
        statistics[name] = stats.apply {
            add(clock.now())
        }
    }

    override fun printStatistics() {
        println(
            getAllEventStatistics()
                .entries
                .joinToString(separator = "\n") { (key, value) ->
                    "rpm for $key is $value"
                }
        )
    }

    override fun getEventStatisticByName(name: String): Double {
        val currentTime = clock.now()
        return statistics
            .getOrDefault(name, emptyList())
            .count { it in currentTime.minus(DURATION)..currentTime }
            .div(60.0)
    }

    override fun getAllEventStatistics(): Map<String, Double> = statistics
        .mapValues { (name, _) -> getEventStatisticByName(name) }

    private companion object {
        private val DURATION = 1.hours.toJavaDuration()
    }
}