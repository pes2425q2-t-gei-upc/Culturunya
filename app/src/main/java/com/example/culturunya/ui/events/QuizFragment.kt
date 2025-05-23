package com.example.culturunya.ui.events

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.culturunya.R

/**
 * @class QuizFragment
 * @brief Fragmento que muestra la pantalla de cuestionario (Quiz).
 */
class QuizFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz, container, false)
    }
}
