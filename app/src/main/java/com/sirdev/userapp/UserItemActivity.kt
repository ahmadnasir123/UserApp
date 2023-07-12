package com.sirdev.userapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sirdev.userapp.data.User
import com.squareup.picasso.Picasso

class UserItemActivity : AppCompatActivity() {
    private lateinit var avatarImageView: ImageView
    private lateinit var userName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_item)

        avatarImageView = findViewById(R.id.avatarImageView)
        userName = findViewById(R.id.userName)

        val user = intent.getSerializableExtra("user") as User

        Picasso.get().load(user.avatar).into(avatarImageView)
        userName.text = user.firstName
    }
}

