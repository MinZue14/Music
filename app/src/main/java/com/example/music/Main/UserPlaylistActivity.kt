package com.example.music.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Adapter.FavoriteListAdapter
import com.example.music.Adapter.PlaylistAdapter
import com.example.music.Admin.AdminLogin
import com.example.music.ApiInterface
import com.example.music.Data
import com.example.music.Database.DatabaseUserPlaylist
import com.example.music.R
import com.example.music.databinding.ActivityUserPlaylistBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserPlaylistActivity : AppCompatActivity() {
    lateinit var binding: ActivityUserPlaylistBinding
    lateinit var sharedPref: SharedPreferences
    lateinit var databaseUserPlaylist: DatabaseUserPlaylist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUserPlaylistBinding.inflate(layoutInflater)
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
                    val intent = Intent(this, UserPlaylistActivity::class.java)
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

//main
        // Khởi tạo databaseUserPlaylist
        databaseUserPlaylist = DatabaseUserPlaylist(this)

        // Lấy danh sách Playlist từ CSDL
        val PlaylistTracks = databaseUserPlaylist.getUserPlaylistTracks(userID)

        // Nếu danh sách yêu thích không rỗng
        if (PlaylistTracks.isNotEmpty()) {
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

            // Lặp qua danh sách các ID bài hát Playlist và gọi API để lấy thông tin chi tiết của từng bài hát
            for (trackID in PlaylistTracks) {
                retrofitBuilder.getTrackDetail(trackID).enqueue(object : Callback<Data> {
                    override fun onResponse(call: Call<Data>, response: Response<Data>) {
                        if (response.isSuccessful && response.body() != null) {
                            val trackDetail = response.body()
                            trackDetail?.let {
                                trackDetailsList.add(it)
                            }
                        }
                        completedCalls++
                        if (completedCalls == PlaylistTracks.size) {
                            // Nếu đã hoàn thành tất cả cuộc gọi API, hiển thị danh sách Playlist lên giao diện
                            val PlaylistList = findViewById<RecyclerView>(R.id.PlaylistList)
                            PlaylistList.layoutManager = LinearLayoutManager(this@UserPlaylistActivity)
                            val adapter = PlaylistAdapter(this@UserPlaylistActivity, trackDetailsList)
                            PlaylistList.adapter = adapter

                            // Thiết lập listener cho adapter
                            adapter.onItemClickListener = object : PlaylistAdapter.OnTrackClickListener {
                                override fun onItemClick(data: Data) {
                                    // Mở giao diện nhạc của bài hát được nhấp
                                    val intent = Intent(this@UserPlaylistActivity, MusicActivity::class.java)
                                    intent.putExtra("trackId", data.id.toString()
                                    ) // Truyền ID của bài hát qua intent
                                    startActivity(intent)
                                }
                            }

                        }
                    }

                    override fun onFailure(call: Call<Data>, t: Throwable) {
                        completedCalls++
                        if (completedCalls == PlaylistTracks.size) {
                            // Nếu đã hoàn thành tất cả cuộc gọi API (kể cả khi có lỗi xảy ra), hiển thị danh sách Playlist lên giao diện
                            val PlaylistList = findViewById<RecyclerView>(R.id.PlaylistList)
                            PlaylistList.layoutManager = LinearLayoutManager(this@UserPlaylistActivity)
                            val adapter = PlaylistAdapter(this@UserPlaylistActivity, trackDetailsList)
                            PlaylistList.adapter = adapter
                        }
                    }
                })
            }
        }
    }
}