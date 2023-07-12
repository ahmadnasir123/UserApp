package com.sirdev.userapp

// Import statements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sirdev.userapp.api.ApiService
import com.sirdev.userapp.data.ApiResponse
import com.sirdev.userapp.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var sentenceInput: EditText

    private lateinit var selectedUserName: TextView

    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userList: RecyclerView

    private var currentPage = 1
    private val perPage = 10
    private var isLoading = false

    private val apiBaseUrl = "https://reqres.in/api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameInput = findViewById(R.id.nameInput)
        sentenceInput = findViewById(R.id.sentenceInput)

        val checkButton: Button = findViewById(R.id.checkButton)
        checkButton.setOnClickListener {
            checkPalindrome()
        }

        val nextButton: Button = findViewById(R.id.nextButton)
        nextButton.setOnClickListener {
            goToSecondScreen()
        }
    }

    private fun checkPalindrome() {
        val name = nameInput.text.toString()
        val sentence = sentenceInput.text.toString().replace(" ", "")

        val isPalindrome = sentence == sentence.reversed()
        val message = if (isPalindrome) "is palindrome" else "not palindrome"

        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()

        dialogBuilder.show()
    }

    private fun goToSecondScreen() {
        setContentView(R.layout.activity_second_screen)

        val welcomeText: TextView = findViewById(R.id.welcomeText)
        selectedUserName = findViewById(R.id.selectedUserName)
        val chooseUserButton: Button = findViewById(R.id.chooseUserButton)

        welcomeText.text = "Welcome, ${nameInput.text.toString()}!"

        chooseUserButton.setOnClickListener {
            goToThirdScreen()
        }
    }

    private fun goToThirdScreen() {
        setContentView(R.layout.activity_third_screen)

        userList = findViewById(R.id.userList)
        userList.layoutManager = LinearLayoutManager(this)
        userListAdapter = UserListAdapter()

        userList.adapter = userListAdapter

        fetchUsers()

        userList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    currentPage++
                    fetchUsers()
                }
            }
        })
    }

    private fun fetchUsers() {
        isLoading = true

        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        val call = apiService.getUsers(currentPage, perPage)

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val userListResponse = response.body()?.data

                    if (userListResponse != null) {
                        userListAdapter.addUsers(userListResponse)
                    }

                    isLoading = false
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                isLoading = false
                // Handle failure
            }
        })
    }

    private inner class UserListAdapter : RecyclerView.Adapter<UserViewHolder>() {
        private val userList: MutableList<User> = mutableListOf()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_user_item, parent, false)
            return UserViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = userList[position]
            holder.bind(user)
            holder.itemView.setOnClickListener {
                selectedUserName.text = user.firstName
            }
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        fun addUsers(users: List<User>) {
            userList.addAll(users)
            notifyDataSetChanged()
        }
    }

    private inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userName: TextView = itemView.findViewById(R.id.userName)

        fun bind(user: User) {
            userName.text = user.firstName
        }
    }
}
