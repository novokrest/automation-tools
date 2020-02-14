package ru.onepro.code.generator.config

import ru.onepro.code.generator.model.CheckConstructorParamsMode
import ru.onepro.code.generator.model.CheckConstructorParamsMode.RequireNonNull

data class Config(
        val checkConstructorParamsMode: CheckConstructorParamsMode = RequireNonNull,
        val withNonnull: Boolean = true,
        val builderMethodPrefix: String = "with",
        val isFieldPublic: Boolean = false
)