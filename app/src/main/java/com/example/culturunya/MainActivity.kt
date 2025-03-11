package com.example.culturunya

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.culturunya.ui.events.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referència al BottomNavigationView
        val bottomNavView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Quan l'usuari seleccioni un ítem del menú...
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_events -> {
                    replaceFragment(EventsFragment())
                    true
                }
                R.id.navigation_quiz -> {
                    replaceFragment(QuizFragment())
                    true
                }
                R.id.navigation_leaderboard -> {
                    replaceFragment(LeaderboardFragment())
                    true
                }
                R.id.navigation_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }

        // Carregar per defecte la pantalla d'Events
        bottomNavView.selectedItemId = R.id.navigation_events
    }

    private fun replaceFragment(fragment: SettingsFragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}

private fun Any.replace(fragmentContainer: Int, fragment: SettingsFragment): FragmentTransaction {

}
