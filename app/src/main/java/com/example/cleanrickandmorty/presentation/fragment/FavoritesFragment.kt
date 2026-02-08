package com.example.cleanrickandmorty.presentation.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cleanrickandmorty.databinding.FragmentFavoritesBinding
import com.example.cleanrickandmorty.domain.model.Character
import com.example.cleanrickandmorty.presentation.adapter.CharacterAdapter

class FavoritesFragment : Fragment() {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CharacterAdapter
    private val prefs by lazy { requireContext().getSharedPreferences("favorites", Context.MODE_PRIVATE) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CharacterAdapter(requireContext(), object : CharacterAdapter.OnClickListener {
            override fun onClick(id: Int) {}
        })

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRecyclerView.adapter = adapter

        loadFavorites()
    }

    private fun loadFavorites() {
        val favoriteIds = prefs.getStringSet("favorite_ids", setOf()) ?: setOf()
        // Здесь нужно получить данные персонажей по id
        // Для примера пока пустой список
        val favoriteCharacters: List<Character.Result> = emptyList()
        adapter.submitList(favoriteCharacters)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}