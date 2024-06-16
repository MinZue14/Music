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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Adapter.TrackAdapter
import com.example.music.Admin.AdminLogin
import com.example.music.ApiInterface
import com.example.music.Data
import com.example.music.MyData
import com.example.music.R
import com.example.music.databinding.ActivitySearchBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity: AppCompatActivity() {
    lateinit var binding: ActivitySearchBinding
    lateinit var sharedPref: SharedPreferences
    lateinit var searchView: SearchView
//    lateinit var adapter : TrackAdapter.SlideAdapter
//    lateinit var adapter1 : TrackAdapter.AlbumAdapter
//    lateinit var adapter2 : TrackAdapter.trackListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
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
        searchView = findViewById(R.id.search_)
        // Khởi tạo Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        // Gọi API để lấy dữ liệu bài hát
        val retrofitData = retrofitBuilder.getData("bts")

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(p0: Call<MyData?>, response: Response<MyData?>) {
                // Nếu cuộc gọi API thành công thì phương thức này được thực thi
                val dataList = response.body()?.data!!

// LIST CA SĨ
                // Khởi tạo RecyclerView và Adapter
                val artistList = findViewById<RecyclerView>(R.id.artistList_)
                 val adapter = TrackAdapter.SlideAdapter(this@SearchActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                artistList.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)

                // Thiết lập Adapter cho RecyclerView
                artistList.adapter = adapter

                // Thiết lập listener cho adapter
                adapter.onItemClickListener = object : TrackAdapter.SlideAdapter.OnArtistClickListener{
                    override fun onItemClick(data: Data) {
                        // Mở giao diện nhạc của bài hát được nhấp
                        val intent = Intent(this@SearchActivity, ArtistActivity::class.java)
                        intent.putExtra("artistId", data.artist.id.toString()) // Truyền ID của bài hát qua intent
                        startActivity(intent)
                    }
                }
// LIST ALBUM
                // Khởi tạo RecyclerView và Adapter
                val albumList = findViewById<RecyclerView>(R.id.albumList_)
                val adapter1 = TrackAdapter.AlbumAdapter(this@SearchActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                val layoutManager = GridLayoutManager(this@SearchActivity, 2, RecyclerView.VERTICAL, false)
                albumList.layoutManager = layoutManager

                // Thiết lập Adapter cho RecyclerView
                albumList.adapter = adapter1

                // Thiết lập listener cho adapter
                adapter1.onItemClickListener = object : TrackAdapter.AlbumAdapter.OnAlbumClickListener {
                    override fun onItemClick(data: Data) {
                        // Mở giao diện nhạc của bài hát được nhấp
                        val intent = Intent(this@SearchActivity, AlbumActivity::class.java)
                        intent.putExtra("albumId", data.id.toString()
                        ) // Truyền ID của bài hát qua intent
                        startActivity(intent)
                    }
                }

// LIST NHẠC
                // Khởi tạo RecyclerView và Adapter
                val trackList = findViewById<RecyclerView>(R.id.trackList_)
                val adapter2 = TrackAdapter.trackListAdapter(this@SearchActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                trackList.layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)

                // Thiết lập Adapter cho RecyclerView
                trackList.adapter = adapter2

                // Thiết lập listener cho adapter
                adapter2.onItemClickListener = object : TrackAdapter.trackListAdapter.OnTrackClickListener {
                    override fun onItemClick(data: Data) {
                        // Mở giao diện nhạc của bài hát được nhấp
                        val intent = Intent(this@SearchActivity, MusicActivity::class.java)
                        intent.putExtra("trackId", data.id.toString()
                        ) // Truyền ID của bài hát qua intent
                        startActivity(intent)
                    }
                }

                Log.d("TAG", "onResponse: " + response.body())
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        // Implement search submit action if needed
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        // Filter data as user types in the SearchView
//                trackAdapter.filter.filter(newText)
//                        adapter.filterData()
                        adapter.filter.filter(newText)

                adapter1.filter.filter(newText)
                adapter2.filter.filter(newText)

                        return true
                    }
                })
            }

            override fun onFailure(p0: Call<MyData?>, t: Throwable) {
//                If the Api call is a failure then this method is executed
                Log.d("TAG", "onResponse: " + t.message)
            }
        })
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                // Implement search submit action if needed
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                // Filter data as user types in the SearchView
////                trackAdapter.filter.filter(newText)
//                adapter.filter.filter(newText)
////                adapter1.filter.filter(newText)
////                adapter2.filter.filter(newText)
//
//                return true
//            }
//        })
    }


}