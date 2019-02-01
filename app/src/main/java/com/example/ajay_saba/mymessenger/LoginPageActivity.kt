package com.example.ajay_saba.mymessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_page.*

class LoginPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        buLogin.setOnClickListener {
            val email = etEmail_Login.text.toString()
            val password = etPassword_Login.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener


                    Toast.makeText(this,"LOGGED IN SUCCESSFULLY",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LatestMessagesActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Toast.makeText(this,"INCORRECT EMAIL/PASSWORD",Toast.LENGTH_SHORT).show()
                }
        }

        tvRegisterPage.setOnClickListener {
            finish()
        }


    }
}
