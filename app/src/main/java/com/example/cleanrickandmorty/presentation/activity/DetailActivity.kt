package com.example.cleanrickandmorty.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.cleanrickandmorty.R
import com.example.cleanrickandmorty.databinding.ActivityDetailBinding
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.util.UIState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val viewModel: DetailViewModel by viewModel()
    private val id by lazy { intent.getIntExtra("ID", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.getCharacterById(id)
        initializeObservers()
    }

    private fun initializeObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultState.collect { state ->
                    when (state) {
                        is UIState.Empty -> {
                            // Можно показать пустой экран или прогресс
                        }
                        is UIState.Error -> {
                            // Можно показать ошибку
                        }
                        is UIState.Loading -> {
                            // Можно показать loader
                        }
                        is UIState.Success -> {
                            bindCharacter(state.data)
                        }
                    }
                }
            }
        }
    }

    private fun bindCharacter(character: Character.Result) {
        binding.name.text = character.name   //имя
        Glide.with(binding.image.context) //картинка
            .load(character.image)
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .into(binding.image)
        binding.gender.text = character.gender
        binding.status.text = character.status
        setStatusIndicatorColor(character.status)
        binding.type.text = character.type
        binding.episode.text = character.episode
            .map { url -> url.substringAfterLast("/") }
            .joinToString(", ") { "Episode $it" }
    }

    private fun setStatusIndicatorColor(status: String?) {
        val colorRes = when (status) {
            "Alive" -> R.color.status_alive
            "Dead" -> R.color.status_dead
            else -> R.color.status_unknown
        }
        binding.statusIndicator.setColorFilter(
            ContextCompat.getColor(this, colorRes)
        )
    }
}
