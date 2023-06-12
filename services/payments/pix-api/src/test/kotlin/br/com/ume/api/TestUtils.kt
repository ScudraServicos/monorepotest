package br.com.ume.api

import br.com.ume.application.utils.CustomDeserializer
import br.com.ume.application.utils.CustomSerializer

inline fun <reified T> transformObjectFromSerializer(obj: T): T {
    return CustomDeserializer.deserialize(CustomSerializer.serialize(obj), T::class.java)
}