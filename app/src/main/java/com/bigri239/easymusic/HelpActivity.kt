package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bigri239.easymusic.databinding.ActivityHelpBinding

@Suppress("DEPRECATION")
class HelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpBinding.inflate(layoutInflater)
        val view = binding.root.also {
            setContentView(it)
        }
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, MainActivity::class.java)
        binding.backH.setOnClickListener {
            binding.backH.isClickable = false
            startActivity(intent)
        }

        val intent11 = Intent(this, AuthorsActivity::class.java)
        binding.authors.setOnClickListener {
            binding.authors.isClickable = false
            startActivity(intent11)
        }

        val intent12 = Intent(this, FaqActivity::class.java)
        binding.frequently.setOnClickListener {
            binding.frequently.isClickable = false
            startActivity(intent12)
        }

        val intent13 = Intent(this, TermsActivity::class.java)
        binding.terms.setOnClickListener {
            binding.terms.isClickable = false
            intent13.putExtra("isStart", "false")
            startActivity(intent13)
        }
    }
}