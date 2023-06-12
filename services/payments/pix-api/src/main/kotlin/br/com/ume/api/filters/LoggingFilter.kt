package br.com.ume.api.filters

import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import org.slf4j.MDC

@Filter("/**")
class LoggingFilter: HttpServerFilter {
    override fun doFilter(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        val traceId = request.headers.get("X-TRACE-ID")
        if (traceId != null)
            MDC.put("traceId", traceId)

        return Flowable.fromPublisher(chain.proceed(request))
            .doAfterNext {
                MDC.clear()
            }
            .doFinally{
                MDC.clear()
            }
    }
}