package com.example.ajay_saba.mymessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        buRegister.setOnClickListener {
            performRegister()
        }

        buSelectPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type= "image/*"
            startActivityForResult(intent,0)
        }


        tvLoginPage.setOnClickListener{
            val intent = Intent(this,LoginPageActivity::class.java)
            startActivity(intent)
        }

    }

    var selectedPhotoUri : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==0 && resultCode== Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","Photo was selected")

            //URI represents the location of the file where it is present
             selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)

            ivSelectPhoto.setImageBitmap(bitmap)
            buSelectPhoto.alpha = 0f
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //setBackgroundDrawable is used to
            //buSelectPhoto.setBackgroundDrawable(bitmapDrawable)


        }
    }

    private fun performRegister(){
        val email = etEmail_Login.text.toString()
        val password = etPassword_Login.text.toString()

        if(email.isEmpty() || password.isEmpty())  {
            Toast.makeText(this,"Enter Email or password",Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity","Email : $email")
        Log.d("RegisterActivity","Password: $password")

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                else
                    Toast.makeText(this,"REGISTERED SUCCESSFULLY",Toast.LENGTH_SHORT).show()
                    Log.d("RegisterActivity","Created successfully with Uid: ${it.result.user.uid}")
                    uploadImage()
            }

            .addOnFailureListener {
                Log.d("RegisterActivity","Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to create user: ${it.message}",Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImage(){
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Successfully uploaded image ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity","FileLocation: $it")
                    saveDataToFirebase(it.toString())
                }
            }
    }

    private fun saveDataToFirebase(imageUrl: String){
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid,etUsername.text.toString(),imageUrl)

        ref.setValue(user).addOnSuccessListener{
            Log.d("RegisterActivity","Uploaded to Firebase Database")


        }
    }


}
class User(val uid:String, val username:String,val imageUrl: String)
