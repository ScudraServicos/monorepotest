package br.com.ume.api

import br.com.ume.application.shared.utils.CustomDeserializer
import br.com.ume.application.shared.utils.CustomSerializer

inline fun <reified T> transformObjectFromSerializer(obj: T): T {
    return CustomDeserializer.deserialize(CustomSerializer.serialize(obj), T::class.java)
}