import com.ncorti.ktfmt.gradle.KtfmtExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ktfmt) apply false
}

buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}

subprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")
    configure<KtfmtExtension> {
        kotlinLangStyle()
        maxWidth = 120
    }
}
