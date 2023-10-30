package com.example.project6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class MainActivity : AppCompatActivity() {

    private lateinit var pokeList: MutableList<Pokemon>
    private lateinit var rvPoke: RecyclerView
    private lateinit var adapter: PokeAdapter
    private lateinit var displayedPokeList: MutableList<Pokemon>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPoke = findViewById(R.id.recyclerView)
        pokeList = mutableListOf()

        displayedPokeList = pokeList.toMutableList()

       adapter= PokeAdapter(displayedPokeList)
        rvPoke.adapter = adapter
        rvPoke.layoutManager = GridLayoutManager(this,2)

        // Setup the SearchView here
        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPokemonList(newText.orEmpty())
                return true
            }
        })

        fetchPokemon()
    }

    private fun filterPokemonList(query: String) {
        val filteredList = if (query.isEmpty()) {
            pokeList.toMutableList()
        } else {
            pokeList.filter { pokemon ->
                pokemon.name.contains(query, ignoreCase = true)
            }.toMutableList()
        }
        displayedPokeList.clear()
        displayedPokeList.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }

    private fun fetchPokemon() {
        val client = AsyncHttpClient()

        client["https://pokeapi.co/api/v2/pokemon?limit=251&offset=0", object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON?) {
                val results = json?.jsonObject?.getJSONArray("results")

                for (i in 0 until (results?.length() ?: 0)) {
                    val pokemonUrl = results?.getJSONObject(i)?.getString("url")
                    fetchPokemonImage(pokemonUrl)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String?, throwable: Throwable?) {
                Log.e("APIResponse", "Failure fetching Pokémon list: $errorResponse")
            }
        }]
    }

    private fun fetchPokemonImage(pokemonUrl: String?) {
        val client = AsyncHttpClient()

        client[pokemonUrl, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON?) {
                val imageUrl = json?.jsonObject?.getJSONObject("sprites")?.getString("front_default")
                val pokemonName = json?.jsonObject?.getString("name")
                // Extracting abilities
                val abilitiesList = mutableListOf<String>()
                val abilitiesJsonArray = json?.jsonObject?.getJSONArray("abilities")
                for (i in 0 until (abilitiesJsonArray?.length() ?: 0)) {
                    val abilityName = abilitiesJsonArray?.getJSONObject(i)?.getJSONObject("ability")?.getString("name")
                    abilityName?.let { abilitiesList.add(it) }
                }

                // Extracting types
                val typesList = mutableListOf<String>()
                val typesJsonArray = json?.jsonObject?.getJSONArray("types")
                for (i in 0 until (typesJsonArray?.length() ?: 0)) {
                    val typeName = typesJsonArray?.getJSONObject(i)?.getJSONObject("type")?.getString("name")
                    typeName?.let { typesList.add(it) }
                }

                if (imageUrl != null && pokemonName != null) {
                    val pokemon = Pokemon(imageUrl, pokemonName.capitalize(), abilitiesList, typesList)
                    pokeList.add(pokemon)
                    displayedPokeList.add(pokemon)
                    adapter.notifyItemInserted(displayedPokeList.size - 1)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String?, throwable: Throwable?) {
                Log.e("APIResponse", "Failure fetching Pokémon details: $errorResponse")
            }
        }]
    }
}
