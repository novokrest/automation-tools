package org.onepro.code.generator.config

import org.onepro.code.generator.model.CheckConstructorParamsMode
import org.onepro.code.generator.model.CheckConstructorParamsMode.RequireNonNull

data class Config(
        val withPackage: Boolean = true,
        val checkConstructorParamsMode: CheckConstructorParamsMode = RequireNonNull,
        val withNonnull: Boolean = true,
        val builderMethodPrefix: String = "with",
        val isFieldPublic: Boolean = false,
        val isGetPropertyWithoutPrefix: Boolean = false,
        val doesUseClassNameAsFactoryMethod: Boolean = true,
        val isBuilderFactoryInsideModelClass: Boolean = false
)