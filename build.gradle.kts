// Top-level build file
// Os plugins ficam declarados na raiz apenas com apply false.
// Hilt e KSP são aplicados juntos no módulo app.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.services) apply false
}
