package br.com.ume.application.shared.utils

import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit

fun utcNow(): Timestamp = Timestamp.from(Instant.now().truncatedTo(ChronoUnit.MILLIS))