import java.time.Duration
import java.time.Instant

internal fun interface Clock {
    fun now(): Instant
}

internal val normalClock = Clock { Instant.now() }

internal data class SettableClock(var now: Instant) : Clock {
    override fun now(): Instant = now
}

internal fun Clock.move(duration: Duration) {
    if (this is SettableClock) {
        now += duration
    }
}

internal fun Instant.toSettableCLock() = SettableClock(this)
