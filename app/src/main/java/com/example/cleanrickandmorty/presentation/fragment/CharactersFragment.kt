package com.example.cleanrickandmorty.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleanrickandmorty.databinding.FragmentCharactersBinding
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.presentation.activity.DetailActivity
import com.example.cleanrickandmorty.presentation.activity.MainViewModel
import com.example.cleanrickandmorty.presentation.adapter.CharacterAdapter
import com.example.cleanrickandmorty.util.UIState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CharactersFragment : Fragment() {

    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: CharacterAdapter

    private var fullList: List<Character.Result> = emptyList()
    private val prefs by lazy { requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCharacter()

        adapter = CharacterAdapter(
            context = requireContext(),
            isFavoriteScreen = false, // Главный экран
            onClickListener = object : CharacterAdapter.OnClickListener {
                override fun onClick(id: Int) {
                    val intent = Intent(requireContext(), DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_ID, id)
                    }
                    startActivity(intent)
                }
            },
            onFavoriteClickListener = { character ->
                val currentIds = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
                val characterId = character.id.toString()

                // Если уже есть в избранном — удаляем, если нет — добавляем
                if (currentIds.contains(characterId)) {
                    currentIds.remove(characterId)
                    Toast.makeText(requireContext(), "${character.name} удален из избранного", Toast.LENGTH_SHORT).show()
                } else {
                    currentIds.add(characterId)
                    Toast.makeText(requireContext(), "${character.name} добавлен в избранное!", Toast.LENGTH_SHORT).show()
                }
                prefs.edit().putStringSet("favorite_ids", currentIds).apply()
            }
        )

        binding.characterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.characterRecyclerView.adapter = adapter

        observeCharacters()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText ?: ""
                adapter.currentQuery = query
                val filteredList = fullList.filter { it.name.contains(query, ignoreCase = true) }
                adapter.submitList(filteredList)
                return true
            }
        })
    }

    private fun observeCharacters() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.characterState.collect { state ->
                    when (state) {
                        is UIState.Success -> {
                            fullList = state.data.results
                            val query = binding.searchView.query.toString()
                            adapter.currentQuery = query
                            val filteredList = fullList.filter { it.name.contains(query, ignoreCase = true) }
                            adapter.submitList(filteredList)
                        }
                        is UIState.Error -> Log.e("CharactersFragment", state.message)
                        is UIState.Loading -> Log.e("CharactersFragment", "Loading")
                        else -> {}
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