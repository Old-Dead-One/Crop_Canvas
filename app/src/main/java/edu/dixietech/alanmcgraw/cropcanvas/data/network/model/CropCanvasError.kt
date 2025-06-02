package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CropCanvasError(
    val status: Int,

    @SerialName("reason")
    override val message: String
): Exception()

class UnexpectedResponseException: Exception() {
    override val message = "Unexpected Response Form Server"
}