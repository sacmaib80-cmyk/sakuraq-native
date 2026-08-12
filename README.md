# SakuraQ Native

**The same app rewritten as a native Android app, with unit tests.**

SakuraQ started as a web app wrapped for Android. This is the second implementation — same product,
written natively — mostly to find out how much of the design survived the move and how much of it
was really just web behaviour.

## What's interesting in here

- **Written twice, on purpose.** Rebuilding a shipped product on a different platform is where you
  learn which parts of it were actual design and which parts were accidents of the first stack.
- **Single source of truth in memory, persistence as a side effect.** State lives in one observable
  object that mutates synchronously; saving to disk happens after. The first version did it the
  other way round and screens rendered stale data for a frame.
- **Timers survive being killed.** The app stores an absolute end timestamp rather than a countdown,
  so force-closing it mid-session and reopening restores the running timer at the correct time.
- **22 unit tests covering the scoring rules**, including the abuse cases — repeated filler text,
  the same task name farmed all day, and the bonus thresholds.

## Stack

Kotlin · Jetpack Compose · DataStore · kotlinx.serialization

## Honest notes

Feature parity is intentionally partial: no auth, no cloud sync, no notifications. It exists to
prove the native path works, not to replace the shipped app.

<!-- ⚠️ ยังไม่มีภาพของตัวนี้ — adb ไม่เห็นเครื่องไหนเลย ต้องแคปจากมือถือเองแล้ววางที่ screens/ -->
