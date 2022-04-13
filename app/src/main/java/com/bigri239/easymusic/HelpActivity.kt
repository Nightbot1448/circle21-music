package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*

@Suppress("DEPRECATION")
class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, MainActivity::class.java)
        findViewById<TextView>(R.id.back_H).setOnClickListener {
            startActivity(intent)
            back_H.setOnClickListener {}
        }
        val intent11 = Intent(this, AuthorsActivity::class.java)
        findViewById<TextView>(R.id.authors).setOnClickListener {
            startActivity(intent11)
            authors.setOnClickListener {}
        }
        val intent12 = Intent(this, FaqActivity::class.java)
        findViewById<TextView>(R.id.frequently).setOnClickListener {
            startActivity(intent12)
            frequently.setOnClickListener {}
        }
        val intent13 = Intent(this, TermsActivity::class.java)
        findViewById<TextView>(R.id.terms).setOnClickListener {
            startActivity(intent13)
            terms.setOnClickListener {}
        }

    }
}