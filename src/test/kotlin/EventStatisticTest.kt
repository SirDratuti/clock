import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

internal class EventStatisticTest {

    private val nowSettable get() = normalClock.now().toSettableCLock()

    @Test
    fun `should correctly calculate rpm for 1 event`() {
        val clock = nowSettable
        val eventName = "event_1"

        EventsStatisticImpl(clock).apply {
            incEvent(eventName)
            assertEquals(
                expected = mapOf(
                    eventName to ONE_RPM,
                ),
                actual = getAllEventStatistics(),
            )
        }
    }

    @Test
    fun `should correctly calculate rpm for multiple events`() {
        val clock = nowSettable
        val amountOfEvents = 4
        val events = List(amountOfEvents) {
            "event_$it" to it + 1
        }

        EventsStatisticImpl(clock).apply {
            events.forEach { (name, amount) ->
                repeat(amount) {
                    incEvent(name)
                }
            }

            assertEquals(
                expected = buildMap {
                    events.forEach { (name, amount) ->
                        put(name, ONE_RPM * amount)
                    }
                },
                actual = getAllEventStatistics(),
            )
        }
    }

    @Test
    fun `should remove overdue statistic`() {
        val clock = nowSettable
        val eventName = "event_1"

        EventsStatisticImpl(clock).apply {
            incEvent(eventName)
            clock.move(2.hours.toJavaDuration())
            incEvent(eventName)

            assertEquals(
                expected = mapOf(
                    eventName to ONE_RPM,
                ),
                actual = getAllEventStatistics(),
            )
        }
    }

    @Test
    fun `should correctly get event statistic by name`() {
        val clock = nowSettable
        val amountOfEvents = 4
        val events = List(amountOfEvents) {
            "event_$it" to it + 1
        }

        val eventName = "event_2"
        val eventIndex = events.indexOfFirst { it.first == eventName }

        EventsStatisticImpl(clock).apply {
            events.forEach { (name, amount) ->
                repeat(amount) {
                    incEvent(name)
                }
            }

            assertEquals(
                expected = events[eventIndex].second * ONE_RPM,
                actual = getEventStatisticByName(eventName),
            )
        }
    }

    private companion object {
        private const val ONE_RPM = 1 / 60.0
    }
}