package br.com.ume.api.logging

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.contrib.json.classic.JsonLayout

class LoggingLayout : JsonLayout() {
    val hostName = System.getenv("HOSTNAME") ?: ""

    init {
        this.isIncludeMDC = false
    }

    override fun addCustomDataToJsonMap(map: MutableMap<String, Any>, event: ILoggingEvent) {
        map["machineName"] = hostName
        map.putAll(event.mdcPropertyMap)
        super.addCustomDataToJsonMap(map, event)
    }
}