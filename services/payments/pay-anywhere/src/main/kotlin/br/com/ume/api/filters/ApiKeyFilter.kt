package br.com.ume.api.filters

import br.com.ume.api.configs.AuthenticationConfiguration
import io.micronaut.core.async.publisher.Publishers
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse.status
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.filter.FilterPatternStyle
import io.micronaut.http.filter.HttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.reactivex.Flowable
import org.reactivestreams.Publisher

@Filter(
    patternStyle = FilterPatternStyle.REGEX,
    patterns = ["^(?!/pubsub).*"]
)
class ApiKeyFilter(
    private val config: AuthenticationConfiguration
): HttpServerFilter {
    override fun doFilter(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        val traceId = request.headers.get("X-API-KEY")
        if (traceId != config.apiKey)
            return Publishers.just(status<HttpStatus>(HttpStatus.UNAUTHORIZED));

        return Flowable.fromPublisher(chain.proceed(request))
    }
}