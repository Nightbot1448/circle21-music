package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigri239.easymusic.databinding.ActivityAuthorsBinding

@Suppress("DEPRECATION")
class AuthorsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorsBinding.inflate(layoutInflater)
        val view = binding.root.also {
            setContentView(it)
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, HelpActivity::class.java)
        binding.backauth.setOnClickListener {
            binding.backauth.isClickable = false
            startActivity(intent)
        }
    }
}