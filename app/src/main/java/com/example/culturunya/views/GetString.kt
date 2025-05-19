package com.example.culturunya.views

import android.content.Context
import java.util.*

fun getString(context: Context, id: Int, langCode: String): String {
    val config = context.resources.configuration
    val locale = Locale(langCode)
    val localizedContext = context.createConfigurationContext(config.apply { setLocale(locale) })
    return localizedContext.resources.getString(id)
}