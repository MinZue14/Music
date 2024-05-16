package com.example.music.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Adapter.FavoriteListAdapter
import com.example.music.Adapter.TrackAdapter
import com.example.music.Admin.AdminLogin
import com.example.music.ApiInterface
import com.example.music.Database.DatabaseUserList
import com.example.music.MyData
import com.example.music.R
import com.example.music.databinding.ActivityUserLoveListBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserFavoriteListActivity : AppCompatActivity() {
    lateinit var binding:ActivityUserLoveListBinding
    lateinit var sharedPref: SharedPreferences
    lateinit var databaseUserList: DatabaseUserList // Khai báo biến databaseUserList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserLoveListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//menu
        // Khai báo drawerLayout và navigationView
        val drawer = findViewById<DrawerLayout>(R.id.users)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Khởi tạo SharedPreferences
        sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Lấy thông tin người dùng từ SharedPreferences
        val userIDString = sharedPref.getString("userID", null)
        val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công
        val username = sharedPref.getString("username", "") ?: ""
        val email = sharedPref.getString("email", "") ?: ""

        // Gán headerLayoutBinding cho navigationView menu
        val headerLayoutBinding = HeaderMenuBinding.bind(navigationView.getHeaderView(0))
        headerLayoutBinding.menuUsername.text = username
        headerLayoutBinding.menuUsermail.text = email

        // Thiết lập listener cho các mục menu trong navigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navHome -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navListLiked -> {
                    val intent = Intent(this, UserFavoriteListActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navGenre -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navChart -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navCountry -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_user_to_admin -> {
                    val intent = Intent(this, AdminLogin::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navSetting -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navLogOut -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    true
                }


                else -> false
            }
        }

        // Xử lý sự kiện khi người dùng nhấn nút mở Drawer
        binding.btnOpenDrawer.setOnClickListener {
            drawer.openDrawer(GravityCompat.START)
        }

//main
        // Khởi tạo Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        // Khởi tạo databaseUserList
        databaseUserList = DatabaseUserList(this) // Khởi tạo databaseUserList

        // Lấy danh sách các ID bài hát yêu thích từ SQLite
        val favoriteTrackIDs = databaseUserList.getUserFavoriteTracks(userID)

//       Gọi API để lấy dữ liệu cho danh sách các bài hát yêu thích
        val retrofitData = retrofitBuilder.getData(favoriteTrackIDs.joinToString(","))

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                if (response.isSuccessful && response.body() != null) {
                    val dataList = response.body()?.data

                    // Lọc dữ liệu để chỉ lấy các bài hát có trong danh sách yêu thích
                    val filteredDataList = dataList?.filter { data ->
                        favoriteTrackIDs.contains(data.id)
                    } ?: emptyList()

                    // Khởi tạo RecyclerView và Adapter
                    val favoriteList = findViewById<RecyclerView>(R.id.favoriteList)
                    val adapter = FavoriteListAdapter(this@UserFavoriteListActivity, filteredDataList)

                    // Thiết lập LayoutManager cho RecyclerView
                    favoriteList.layoutManager = LinearLayoutManager(this@UserFavoriteListActivity, LinearLayoutManager.VERTICAL, false)

                    // Thiết lập Adapter cho RecyclerView
                    favoriteList.adapter = adapter

                    Log.d("TAG", "Filtered Data: $filteredDataList")
                } else {
                    Log.d("TAG", "API Response not successful")
                }
            }

            override fun onFailure(call: Call<MyData?>, t: Throwable) {
                Log.d("TAG", "API Failure: ${t.message}")
            }
        })

    }
}