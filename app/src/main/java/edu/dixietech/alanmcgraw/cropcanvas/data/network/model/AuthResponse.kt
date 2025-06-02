package edu.dixietech.alanmcgraw.cropcanvas.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val name: String,
    val token: String
)