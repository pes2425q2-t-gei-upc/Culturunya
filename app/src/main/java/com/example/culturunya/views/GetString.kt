package com.example.culturunya.views

import android.content.Context
import java.util.*

/**
 * getString is a utility function that retrieves a string resource from the context
 * based on the provided language code.
 *
 * @param context The context from which to retrieve the string resource.
 * @param id The resource ID of the string to retrieve.
 * @param langCode The language code to use for localization (e.g., "en", "fr").
 * @return The localized string corresponding to the provided resource ID and language code.
 */
fun getString(context: Context, id: Int, langCode: String): String {
    val config = context.resources.configuration
    val locale = Locale(langCode)
    val localizedContext = context.createConfigurationContext(config.apply { setLocale(locale) })
    return localizedContext.resources.getString(id)
}