package com.example.cleanrickandmorty.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleanrickandmorty.databinding.FragmentFavoritesBinding
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.presentation.activity.DetailActivity
import com.example.cleanrickandmorty.presentation.activity.MainViewModel
import com.example.cleanrickandmorty.presentation.adapter.CharacterAdapter
import com.example.cleanrickandmorty.util.UIState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: CharacterAdapter
    private val prefs by lazy { requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE) }

    private var allCharacters: List<Character.Result> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // КРИТИЧЕСКИЙ НАДДОСТАТОК ИСПРАВЛЕН: Запускаем получение персонажей для этого фрагмента
        viewModel.getCharacter()

        adapter = CharacterAdapter(
            context = requireContext(),
            isFavoriteScreen = true, // Экран избранного (сердца всегда красные)
            onClickListener = object : CharacterAdapter.OnClickListener {
                override fun onClick(id: Int) {
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_ID, id)
                    }
                    startActivity(intent)
                }
            },
            onFavoriteClickListener = { character ->
                // Удаляем из SharedPreferences при нажатии на сердце
                val currentIds = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
                currentIds.remove(character.id.toString())
                prefs.edit().putStringSet("favorite_ids", currentIds).apply()

                // Мгновенно перерисовываем список, и персонаж исчезает с экрана
                updateFavoritesList(allCharacters)
            }
        )

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterState.collect { state ->
                    if (state is UIState.Success) {
                        allCharacters = state.data.results
                        updateFavoritesList(allCharacters)
                    }
                }
            }
        }
    }

    private fun updateFavoritesList(characters: List<Character.Result>) {
        val favoriteIds = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        val favoriteCharacters = characters.filter {
            favoriteIds.contains(it.id.toString())
        }
        adapter.submitList(favoriteCharacters)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}