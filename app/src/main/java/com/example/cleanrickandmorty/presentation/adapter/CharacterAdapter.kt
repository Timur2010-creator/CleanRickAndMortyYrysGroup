package com.example.cleanrickandmorty.presentation.adapter

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cleanrickandmorty.R
import com.example.cleanrickandmorty.databinding.CharacterHolderBinding
import com.example.cleanrickandmorty.domain.model.Character

class CharacterAdapter(
    private val context: Context,
    private val onClickListener: OnClickListener
) : ListAdapter<Character.Result, CharacterAdapter.ViewHolder>(DiffCallback()) {

    private val prefs by lazy { context.getSharedPreferences("favorites", Context.MODE_PRIVATE) }

    var currentQuery: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CharacterHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), currentQuery)
    }

    inner class ViewHolder(private val binding: CharacterHolderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(character: Character.Result, query: String) {
            Glide.with(binding.image.context)
                .load(character.image)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(binding.image)

            // 🔹 Подсветка всех совпадений
            val spannable = SpannableString(character.name)
            if (query.isNotEmpty()) {
                val regex = Regex(Regex.escape(query), RegexOption.IGNORE_CASE)
                regex.findAll(character.name).forEach { match ->
                    spannable.setSpan(
                        ForegroundColorSpan(Color.GREEN),
                        match.range.first,
                        match.range.last + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            binding.name.text = spannable

            binding.status.text = character.status
            binding.species.text = character.species

            val colorRes = when (character.status) {
                "Alive" -> R.color.status_alive
                "Dead" -> R.color.status_dead
                else -> R.color.status_unknown
            }
            binding.statusIndicator.setColorFilter(ContextCompat.getColor(context, colorRes))

            val characterId = character.id.toString()
            val favoriteIds = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
            var isFavorite = favoriteIds.contains(characterId)
            updateFavoriteUI(isFavorite)

            binding.favoriteButton.setOnClickListener {
                val currentIds = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
                if (currentIds.contains(characterId)) {
                    currentIds.remove(characterId)
                    isFavorite = false
                } else {
                    currentIds.add(characterId)
                    isFavorite = true
                }
                prefs.edit().putStringSet("favorite_ids", currentIds).apply()
                updateFavoriteUI(isFavorite)
            }

            binding.root.setOnClickListener {
                onClickListener.onClick(character.id)
            }
        }

        private fun updateFavoriteUI(isFavorite: Boolean) {
            val iconRes = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            binding.favoriteButton.setImageResource(iconRes)
            if (isFavorite) {
                binding.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.status_dead))
            } else {
                binding.favoriteButton.clearColorFilter()
            }
        }
    }

    interface OnClickListener {
        fun onClick(id: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<Character.Result>() {
        override fun areItemsTheSame(oldItem: Character.Result, newItem: Character.Result) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Character.Result, newItem: Character.Result) = oldItem == newItem
    }
}