package com.example.cleanrickandmorty.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.cleanrickandmorty.R
import com.example.cleanrickandmorty.databinding.ActivityMainBinding
import com.example.cleanrickandmorty.presentation.fragment.CharactersFragment
import com.example.cleanrickandmorty.presentation.fragment.FavoritesFragment

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // По умолчанию открываем фрагмент "Персонажи"
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, CharactersFragment())
            }
        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_characters -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, CharactersFragment())
                    }
                    true
                }
                R.id.nav_favorites -> {
                    supportFragmentManager.commit {
                        replace(R.id.fragment_container, FavoritesFragment())
                    }
                    true
                }
                else -> false
            }
        }
    }
}