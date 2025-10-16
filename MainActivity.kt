package com.example.pokeapi

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var imgSprite: ImageView
    private lateinit var txtName: TextView
    private lateinit var txtBaseExp: TextView
    private lateinit var btnRandom: Button
    private lateinit var btnSearch: Button
    private lateinit var inputName: EditText

    private val client = AsyncHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgSprite = findViewById(R.id.imgSprite)
        txtName = findViewById(R.id.txtName)
        txtBaseExp = findViewById(R.id.txtBaseExp)
        btnRandom = findViewById(R.id.btnRandom)
        btnSearch = findViewById(R.id.btnSearch)
        inputName = findViewById(R.id.inputName)

        // Initial load
        fetchPokemon(Random.nextInt(1, 152).toString())

        btnRandom.setOnClickListener {
            val id = Random.nextInt(1, 152).toString()
            fetchPokemon(id)
        }

        // Stretch: search by name
        btnSearch.setOnClickListener {
            val q = inputName.text.toString().trim().lowercase()
            if (q.isNotEmpty()) fetchPokemon(q)
        }
    }

    private fun fetchPokemon(idOrName: String) {
        val url = "https://pokeapi.co/api/v2/pokemon/$idOrName"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val obj = json.jsonObject
                val name = obj.getString("name")
                val baseExp = obj.getInt("base_experience")
                val sprites = obj.getJSONObject("sprites")
                val spriteUrl = sprites.optString("front_default", "")

                txtName.text = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                txtBaseExp.text = "Base Experience: $baseExp"

                if (spriteUrl.isNotEmpty()) {
                    Glide.with(this@MainActivity).load(spriteUrl).into(imgSprite)
                } else {
                    imgSprite.setImageDrawable(null)
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String?, throwable: Throwable?) {
                txtName.text = "Not found"
                txtBaseExp.text = ""
                imgSprite.setImageDrawable(null)
            }
        })
    }
