package br.com.ume.application.features.brcode.shared.brcodeInspected.helpers

import br.com.ume.application.features.brcode.shared.brcodeInspected.services.brcodeEnrichingService.dtos.BrcodePayloadDto
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.util.*

abstract class BrcodePayloadHelper {
    companion object {
        fun decodePayloadToken(token: String): BrcodePayloadDto? {
            return try {
                val chunks: List<String> = token.split(".")
                val decodedPayload = String(Base64.getUrlDecoder().decode(chunks[1]))

                Gson().fromJson(decodedPayload, BrcodePayloadDto::class.java)
            } catch(ex: IndexOutOfBoundsException) {
                null
            }
            catch (ex: JsonSyntaxException) {
                null
            }
        }
    }
}