package com.example.cleanrickandmorty.presentation.fragment

import android.content.Context
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CharacterAdapter(requireContext(), object : CharacterAdapter.OnClickListener {
            override fun onClick(id: Int) {
                // Здесь можно открыть DetailActivity как в CharactersFragment
            }
        })

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterState.collect { state ->
                    if (state is UIState.Success) {
                        // Считываем ID лайкнутых персонажей
                        val favoriteIds = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()

                        // Оставляем только тех, чьи ID есть в списке избранного
                        val favoriteCharacters = state.data.results.filter {
                            favoriteIds.contains(it.id.toString())
                        }
                        adapter.submitList(favoriteCharacters)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}