package br.com.ume.application.shared.logging.gcp

import com.google.cloud.MonitoredResource
import com.google.cloud.logging.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import org.slf4j.MDC
import java.time.Instant
import java.util.logging.Level
import java.util.logging.LogRecord

class GcpStructuredLoggingHandler(
    val log: String? = null,
    options: LoggingOptions? = null,
    monitoredResource: MonitoredResource? = null,
    enhancers: List<LoggingEnhancer>? = null,
    destination: LogDestinationName? = null
) : LoggingHandler(log, options, monitoredResource, enhancers, destination) {

    override fun logEntryFor(record: LogRecord): LogEntry.Builder {
        val msg = record.message

        val builder: LogEntry.Builder = if (msg.startsWith("{") && msg.endsWith("}")) {
            try {
                LogEntry.newBuilder(Payload.JsonPayload.of(toMap(msg)))

            } catch (exception: JsonSyntaxException) {
                LogEntry.newBuilder(Payload.StringPayload.of(msg))
            }
        } else {
            LogEntry.newBuilder(Payload.StringPayload.of(msg))
        }

        val baseLevel = if (level.equals(Level.ALL)) Level.FINEST else this.level
        if (baseLevel != record.level) {
            builder.addLabel("levelName", record.level.name)
                .addLabel("levelValue", record.level.intValue().toString())
        }

        getMdcMap()?.forEach {
            builder.addLabel(it.key, it.value)
        }

        return builder.setTimestamp(Instant.ofEpochMilli(record.millis))
                .setSeverity(severityFor(record.level))
    }

    private fun getMdcMap(): Map<String, String>? = MDC.getCopyOfContextMap()

    private fun toMap(jsonString: String): Map<String, Any> = GSON_MAPPER.fromJson(jsonString, object : TypeToken<HashMap<String, Any>>() {}.type)

    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
        val GSON_MAPPER: Gson = GsonBuilder().serializeNulls().setDateFormat(DATE_FORMAT).create()

        private fun severityFor(level: Level): Severity {
            if (level is LoggingLevel) {
                return level.severity
            }
            val value = level.intValue()
            return when {
                value <= Level.FINE.intValue() -> Severity.DEBUG
                value <= Level.INFO.intValue() -> Severity.INFO
                value <= Level.WARNING.intValue() -> Severity.WARNING
                value <= Level.SEVERE.intValue() -> Severity.ERROR
                value == Level.OFF.intValue() -> Severity.NONE
                else -> Severity.DEFAULT
            }
        }
    }
}