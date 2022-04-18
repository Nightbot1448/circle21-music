package com.bigri239.easymusic

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bigri239.easymusic.net.WebRequester
import kotlinx.android.synthetic.main.activity_terms.*
import java.io.File
import java.io.FileOutputStream

@Suppress("DEPRECATION")
class TermsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
    }

    override fun onStart() {
        super.onStart()
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val webRequester = WebRequester(this@TermsActivity)
        val path = filesDir
        val file = File(path, "terms.conf")
        if (intent.getStringExtra("isStart").toString() == "true") {
            if (file.exists()) {
                if (file.readText() == "1") {
                    val intent2 = Intent(this, MainActivity::class.java)
                    startActivity(intent2)
                }
            }
        }

        val intent1 = Intent(this, HelpActivity::class.java)
        if (file.exists()) backter.setOnClickListener {
            backter.isClickable = false
            startActivity(intent1)
        }

        val termsText = webRequester.getTextResource("terms")
        text_view.text = if (termsText != "0") termsText
        else {
            "Using the EasyMusic application (hereinafter referred to as the Application) is possible only if you agree to the following terms of use (hereinafter referred to as the Terms).\n\n" +
                "1. Since the Application is distributed free of charge, no claims to the quality of the services provided are considered. \n" +
                "2. The BiGri239 team consisting of Alexander Bigulov and Artem Grigorash (hereinafter referred to as the Developer) is not responsible for the safety of files and data in the account. In addition, the Developer reserves the right to delete the User's content or his account at any time without the possibility of recovery, if the User violates these Terms.\n" +
                "3. It is strictly forbidden to upload any materials to your account that violate the laws of the Russian Federation, in particular, it is forbidden to upload the names of sounds, files or user descriptions that violate the laws of the Russian Federation or discredit the honor, dignity or reputation of a third party. \n" +
                "4. When uploading information to the account, in particular, project files, the User implicitly consents to the free distribution of this information among the users of the application."
        }

        btnAccept.setOnClickListener {
            btnAccept.isClickable = false
            FileOutputStream(file).use {
                it.write("1".toByteArray())
            }
            Toast.makeText(this, "Thanks!", Toast.LENGTH_SHORT).show()
            if (intent.getStringExtra("isStart").toString() == "true") {
                val intent2 = Intent(this, MainActivity::class.java)
                startActivity(intent2)
            }
            else {
                val intent2 = Intent(this, HelpActivity::class.java)
                startActivity(intent2)
            }
        }

        btnDecline.setOnClickListener {
            Toast.makeText(this, "Use of the application is allowed only if you agree to the Terms of Use!", Toast.LENGTH_LONG).show()
            if (file.exists()) file.delete()
        }
    }
}