package br.com.ume.libs.logging.gcp

class JsonLogBuilder {
    companion object {
        fun build(obj: Any): String = CustomSerializer.serialize(obj)
    }
}