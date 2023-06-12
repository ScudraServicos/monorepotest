package br.com.ume.libs.logging.gcp

import com.google.cloud.logging.LoggingHandler
import java.util.logging.Logger
import java.util.logging.LogManager
import com.google.cloud.logging.LoggingHandler.LogTarget

class LoggerFactory {
    companion object {

        init {
            LogManager.getLogManager().reset()
        }

        private val handlerByTargetMap: Map<LogTarget, LoggingHandler> =
            enumValues<LogTarget>().associateBy(
                { it },
                { GcpStructuredLoggingHandler().apply { logTarget = it } }
            )

        fun buildStructuredLogger(loggerName: String, logTarget: LogTarget = LogTarget.STDOUT): Logger =
            Logger.getLogger(loggerName).apply {
                addHandler(handlerByTargetMap[logTarget])
            }
    }
}