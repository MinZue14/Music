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
import com.example.music.Data
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
        val userID = userIDString?.toLongOrNull()
            ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công
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

                R.id.navPlaylist -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navChart -> {
                    val intent = Intent(this, ChartActivity::class.java)
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
        // Khởi tạo databaseUserList
        databaseUserList = DatabaseUserList(this)

//main
        // Khởi tạo databaseUserList
        databaseUserList = DatabaseUserList(this)

        // Lấy danh sách yêu thích từ CSDL
        val favoriteTracks = databaseUserList.getUserFavoriteTracks(userID)

        // Nếu danh sách yêu thích không rỗng
        if (favoriteTracks.isNotEmpty()) {
            // Gọi API để lấy thông tin chi tiết của các bài hát
            val retrofitBuilder = Retrofit.Builder()
                .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)

        // List để lưu thông tin chi tiết của các bài hát
            val trackDetailsList = mutableListOf<Data>()

        // Biến đếm số lượng cuộc gọi API đã hoàn thành
            var completedCalls = 0

        // Lặp qua danh sách các ID bài hát yêu thích và gọi API để lấy thông tin chi tiết của từng bài hát
            for (trackID in favoriteTracks) {
                retrofitBuilder.getTrackDetail(trackID).enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data>, response: Response<Data>) {
                        if (response.isSuccessful && response.body() != null) {
                            val trackDetail = response.body()
                            trackDetail?.let {
                                trackDetailsList.add(it)
                            }
                        }
                        completedCalls++
                        if (completedCalls == favoriteTracks.size) {
                            // Nếu đã hoàn thành tất cả cuộc gọi API, hiển thị danh sách yêu thích lên giao diện
                            val favoriteList = findViewById<RecyclerView>(R.id.favoriteList)
                            favoriteList.layoutManager =
                                LinearLayoutManager(this@UserFavoriteListActivity)
                            val adapter =
                                FavoriteListAdapter(this@UserFavoriteListActivity, trackDetailsList)
                            favoriteList.adapter = adapter
                        }
                    }

                    override fun onFailure(call: Call<Data>, t: Throwable) {
                        completedCalls++
                        if (completedCalls == favoriteTracks.size) {
                            // Nếu đã hoàn thành tất cả cuộc gọi API (kể cả khi có lỗi xảy ra), hiển thị danh sách yêu thích lên giao diện
                            val favoriteList = findViewById<RecyclerView>(R.id.favoriteList)
                            favoriteList.layoutManager =
                                LinearLayoutManager(this@UserFavoriteListActivity)
                            val adapter =
                                FavoriteListAdapter(this@UserFavoriteListActivity, trackDetailsList)
                            favoriteList.adapter = adapter
                        }
                    }
                })
            }
        }
    }
}