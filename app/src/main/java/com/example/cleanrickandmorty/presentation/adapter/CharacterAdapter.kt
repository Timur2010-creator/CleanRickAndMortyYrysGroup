package com.example.cleanrickandmorty.presentation.adapter

import android.content.Context
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CharacterHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: CharacterHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(character: Character.Result) {
            Glide.with(binding.image.context)
                .load(character.image)
                .centerCrop()
                .placeholder(android.R.color.darker_gray)
                .into(binding.image)

            binding.name.text = character.name
            binding.status.text = character.status
            binding.species.text = character.species

            val colorRes = when (character.status) {
                "Alive" -> R.color.status_alive
                "Dead" -> R.color.status_dead
                else -> R.color.status_unknown
            }
            binding.statusIndicator.setColorFilter(ContextCompat.getColor(itemView.context, colorRes))

            // ================== Логика кнопки сердца ==================
            val favoriteIds = prefs.getStringSet("favorite_ids", mutableSetOf()) ?: mutableSetOf()
            val isFavorite = favoriteIds.contains(character.id.toString())
            updateFavoriteIcon(isFavorite)

            binding.favoriteButton.setOnClickListener {
                val currentlyFavorite = favoriteIds.contains(character.id.toString())
                if (currentlyFavorite) {
                    favoriteIds.remove(character.id.toString())
                } else {
                    favoriteIds.add(character.id.toString())
                }
                prefs.edit().putStringSet("favorite_ids", favoriteIds).apply()
                updateFavoriteIcon(!currentlyFavorite)
            }

            binding.root.setOnClickListener {
                onClickListener.onClick(character.id)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            val icon = if (isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_outline
            binding.favoriteButton.setImageResource(icon)
        }
    }

    interface OnClickListener {
        fun onClick(id: Int)
    }

    class DiffCallback : DiffUtil.ItemCallback<Character.Result>() {
        override fun areItemsTheSame(oldItem: Character.Result, newItem: Character.Result): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Character.Result, newItem: Character.Result): Boolean {
            return oldItem == newItem
        }
    }
}
