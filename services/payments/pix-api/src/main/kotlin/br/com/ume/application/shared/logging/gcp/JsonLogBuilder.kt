package br.com.ume.application.shared.logging.gcp

import br.com.ume.application.utils.CustomSerializer

class JsonLogBuilder {
    companion object {
        fun build(obj: Any): String = CustomSerializer.serialize(obj)
    }
}