package com.example.project6

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PokeAdapter(private val pokeList: MutableList<Pokemon>) : RecyclerView.Adapter<PokeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pokeImages: ImageView = view.findViewById(R.id.pokeImage)
        val pokemonNames: TextView = view.findViewById(R.id.pokeName)
//        val pokemonAbilities: TextView = view.findViewById(R.id.pokeAbilities)
       val pokemonTypes: TextView = view.findViewById(R.id.pokeTypes)
    }
        fun updateList(newList: MutableList<Pokemon>) {
        this.pokeList.clear() // Clear the current list
        this.pokeList.addAll(newList) // Add all the items from the new list
        notifyDataSetChanged() // Notify the RecyclerView to refresh
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokeAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pokemon, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokeAdapter.ViewHolder, position: Int) {
        val pokemon = pokeList[position]

        holder.pokeImages.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Pokemon at position $position clicked", Toast.LENGTH_SHORT).show()
        }

        Glide.with(holder.itemView)
            .load(pokemon.imageUrl)
            .centerCrop()
            .into(holder.pokeImages)

        holder.pokemonNames.text = pokemon.name
//        holder.pokemonAbilities.text = "Abilities: ${pokemon.abilities.joinToString(", ")}"
        holder.pokemonTypes.text = "Types: ${pokemon.types.joinToString(", ")}"
    }

    override fun getItemCount(): Int {
        return pokeList.size
    }

}
