package com.example.cleanrickandmorty.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.cleanrickandmorty.databinding.ActivityDetailBinding
import com.example.cleanrickandmorty.util.UIState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityDetailBinding.inflate(layoutInflater) }
    private val viewModel : DetailViewModel by viewModel()
    private val id by lazy { intent.getIntExtra("ID", 0) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.getCharacterById(id)
        initialize()
    }

    private fun initialize() {
        initializeObserver()
    }

    private fun initializeObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultState.collect { state ->
                    viewModel.resultState.collect { state ->
                        when (state) {
                            is UIState.Empty -> {

                            }
                            is UIState.Error -> {

                            }
                            is UIState.Loading -> {

                            }
                            is UIState.Success -> {
                                binding.name.text = state.data.name
                                Glide.with(binding.image.context)
                                    .load(state.data.image)
                                    .centerCrop()
                                    .placeholder(android.R.color.darker_gray)
                                    .into(binding.image)
                            }
                        }
                    }

                }
            }
        }

    }
}