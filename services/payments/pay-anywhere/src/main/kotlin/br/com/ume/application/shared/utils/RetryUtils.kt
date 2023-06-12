package br.com.ume.application.shared.utils

// TODO(etevaldo.melo): Test this function when using another mock library.
fun <T> retryAndLog(
    trackingName: String,
    numberOfAttempts: Int = 1,
    params: Map<String, Any>?,
    f: () -> T,
): T? {
    val (result, error) = executeWithRetry(numberOfAttempts) {
        f()
    }

    if (error != null) {
        logError(trackingName, params?.plus("error" to error.stackTrace))
    } else {
        logInfo(trackingName, params)
    }
    return result
}

private fun <T> executeWithRetry(
    numberOfAttempts: Int = 1,
    f: () -> T
): Pair<T?, Exception?> {
    var response: T? = null
    var error: Exception? = null

    for (i in 1..numberOfAttempts) {
        try {
            response = f()
            error = null
            break
        } catch (ex: Exception) {
            error = ex
        }
    }
    return Pair(response, error)
}

private fun buildLog(logLevel: String, message: String, params: Map<String, Any>?): String {
    val builder = StringBuilder()

    builder.append("{\"timestamp\":\"${utcNow()}\",\"level\":\"${logLevel}\",\"message\":\"${message}\"")
    params?.forEach { builder.append(",\"${it.key}\":${CustomSerializer.serialize(it.value)}")}
    builder.append("}")
    return builder.toString()
}

private fun logError(message: String, params: Map<String, Any>?) {
    println(buildLog("ERROR", message, params))
}

private fun logInfo(message: String, params: Map<String, Any>?) {
    println(buildLog("INFO", message, params))
}