package com.sirdev.userapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.sirdev.userapp.api.ApiService
import com.sirdev.userapp.data.ApiResponse
import com.sirdev.userapp.data.User
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ThirdScreenActivity : AppCompatActivity() {
    private lateinit var userListAdapter: UserListAdapter
    private lateinit var userList: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var currentPage = 1
    private val perPage = 10
    private var isLoading = false
    private var isLastPage = false

    private val apiBaseUrl = "https://reqres.in/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third_screen)

        userList = findViewById(R.id.userList)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        userList.layoutManager = LinearLayoutManager(this)
        userListAdapter = UserListAdapter()

        userList.adapter = userListAdapter

        fetchUsers()

        swipeRefreshLayout.setOnRefreshListener {
            refreshUsers()
        }

        userList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        currentPage++
                        fetchUsers()
                    }
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
                    val apiResponse = response.body()

                    if (apiResponse != null) {
                        val userList = apiResponse.data
                        userListAdapter.addUsers(userList)

                        if (userList.isEmpty()) {
                            isLastPage = true
                        }
                    }

                    isLoading = false
                    swipeRefreshLayout.isRefreshing = false
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                isLoading = false
                swipeRefreshLayout.isRefreshing = false
                // Handle failure
            }
        })


    }

    private fun refreshUsers() {
        currentPage = 1
        isLastPage = false
        userListAdapter.clearUsers()
        fetchUsers()
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
        }

        override fun getItemCount(): Int {
            return userList.size
        }

        fun addUsers(users: List<User>) {
            userList.addAll(users)
            notifyDataSetChanged()
        }

        fun clearUsers() {
            userList.clear()
            notifyDataSetChanged()
        }
    }

    private inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ImageView = itemView.findViewById(R.id.avatarImageView)
        private val firstNameTextView: TextView = itemView.findViewById(R.id.userName)

        fun bind(user: User) {
            Picasso.get().load(user.avatar).into(avatarImageView)
            firstNameTextView.text = user.firstName

            itemView.setOnClickListener {
                val selectedUserName = findViewById<TextView>(R.id.selectedUserName)
                selectedUserName.text = user.firstName
            }
        }
    }
}

