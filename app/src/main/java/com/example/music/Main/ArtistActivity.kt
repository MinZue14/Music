package com.example.music.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Adapter.ArtistAdapter
import com.example.music.Adapter.TrackAdapter
import com.example.music.Admin.AdminLogin
import com.example.music.ApiInterface
import com.example.music.Artist
import com.example.music.Data
import com.example.music.MyData
import com.example.music.R
import com.example.music.databinding.ActivityArtistBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArtistActivity : AppCompatActivity() {
    lateinit var binding: ActivityArtistBinding
    lateinit var sharedPref: SharedPreferences
    private var dataList: List<Data> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtistBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//menu
        // Khai báo drawerLayout và navigationView
        val drawer = findViewById<DrawerLayout>(R.id.users)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Khởi tạo SharedPreferences
        sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)

        // Lấy thông tin người dùng từ SharedPreferences
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

//main
// Khởi tạo Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val artistID = intent.getStringExtra("artistID")
        Log.d("TAG", "Artist ID from intent: $artistID") // Debug artistID

        if (!artistID.isNullOrEmpty()) {
            val artistIDLong = artistID.toLong()
            val retrofitData = retrofitBuilder.getArtistDetail(artistIDLong)

            retrofitData.enqueue(object : Callback<Artist?> {
                override fun onResponse(call: Call<Artist?>, response: Response<Artist?>) {
                    if (response.isSuccessful) {
                        val artist = response.body()
                        if (artist != null) {
                            Log.d("TAG", "Artist Name: ${artist.name}")
                            Log.d("TAG", "Artist Link: ${artist.link}")
                        } else {
                            Log.d("TAG", "No artist data received")
                        }
                    } else {
                        Log.d("TAG", "Response not successful: ${response.code()}")
                        Log.d("TAG", "Error body: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Artist?>, t: Throwable) {
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } else {
            Log.d("TAG", "Artist ID is null or empty")
        }
    }
}