package com.example.cleanrickandmorty.presentation.activity

import android.os.Bundle
import android.util.Log
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

    companion object {
        const val EXTRA_ID = "extra_id" //Константа для передачи id
    }

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val viewModel: DetailViewModel by viewModel()

    // Получаем id через константу
    private val id by lazy { intent.getIntExtra(EXTRA_ID, 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.d("DETAIL", "Received ID = $id")

        if (id == 0) {
            // Можно показать ошибку или finish()
            finish()
            return
        }

        viewModel.getCharacterById(id)
        initializeObservers()
    }

    private fun initializeObservers() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultState.collect { state ->
                    when (state) {
                        is UIState.Empty -> { /* loader */ }
                        is UIState.Loading -> { /* loader */ }
                        is UIState.Error -> { /* show error */ }
                        is UIState.Success -> bindCharacter(state.data)
                    }
                }
            }
        }
    }

    private fun bindCharacter(character: Character.Result) {
        binding.name.text = character.name
        Glide.with(binding.image.context)
            .load(character.image)
            .centerCrop()
            .placeholder(android.R.color.darker_gray)
            .into(binding.image)
        binding.location.text = character.location?.name ?: "Unknown location"
        binding.gender.text = character.gender
        binding.status.text = character.status
        setStatusIndicatorColor(character.status)
        binding.type.text = character.type
        binding.episode.text = character.episode
            .map { it.substringAfterLast("/") }
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