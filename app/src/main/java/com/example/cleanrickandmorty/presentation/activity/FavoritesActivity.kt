package com.example.cleanrickandmorty.presentation.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleanrickandmorty.databinding.ActivityFavoritesBinding
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.presentation.adapter.CharacterAdapter
import com.example.cleanrickandmorty.util.UIState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesActivity : AppCompatActivity(), CharacterAdapter.OnClickListener {

    private val binding by lazy { ActivityFavoritesBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModel()

    private lateinit var adapter: CharacterAdapter
    private val prefs by lazy { getSharedPreferences("favorites", Context.MODE_PRIVATE) }

    private var allCharacters: List<Character.Result> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Запускаем загрузку данных для Activity
        viewModel.getCharacter()

        adapter = CharacterAdapter(
            context = this,
            isFavoriteScreen = true,
            onClickListener = this,
            onFavoriteClickListener = { character ->
                val currentIds = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
                currentIds.remove(character.id.toString())
                prefs.edit().putStringSet("favorite_ids", currentIds).apply()

                updateFavoritesList(allCharacters)
            }
        )
        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.favoritesRecyclerView.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {  //Загрузка в раздел "Избранные"
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterState.collect { state: UIState<Character> ->
                    if (state is UIState.Success) {         // ЕСЛИ ЗАГРУЗКА ДАННЫХ УСПЕШНА,ТО ОТОБРАЖАЕМ
                        allCharacters = state.data.results  // ОБНОВЛЕННЫХ ПЕРСОНАЖЕЙ
                        updateFavoritesList(allCharacters)
                    }
                }
            }
        }
    }

    private fun updateFavoritesList(characters: List<Character.Result>) { // ОБНОВЛЕНИЕ ИЗБРАННОГО ЛИСТА
        val favoriteIds = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        val favorites = characters.filter { favoriteIds.contains(it.id.toString()) }
        adapter.submitList(favorites)
    }

    override fun onClick(id: Int) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_ID, id)
        startActivity(intent)
    }
}