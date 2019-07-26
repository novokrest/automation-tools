package ru.onepro.code.generator.config

data class Config(
        val withNonnull: Boolean = true,
        val builderMethodPrefix: String = "with",
        val isFieldPublic: Boolean = false
)