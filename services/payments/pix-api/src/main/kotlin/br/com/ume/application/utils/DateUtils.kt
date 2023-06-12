package br.com.ume.application.utils
import java.sql.Timestamp
import java.time.Instant

fun utcNow(): Timestamp = Timestamp.from(Instant.now())