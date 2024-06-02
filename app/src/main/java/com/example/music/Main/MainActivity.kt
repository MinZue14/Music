package com.example.music.Main
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.music.ApiInterface
import com.example.music.MyData
import com.example.music.Adapter.TrackAdapter
import com.example.music.Admin.AdminLogin
import com.example.music.R
import com.example.music.databinding.ActivityMainBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var sharedPref: SharedPreferences
    lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

                R.id.navGenre -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navChart -> {
                    val intent = Intent(this, ChartActivity::class.java)
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

        searchView = findViewById(R.id.search)

        searchView.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            true
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

        // Gọi API để lấy dữ liệu bài hát
        val retrofitData = retrofitBuilder.getData("bts")

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(p0: Call<MyData?>, response: Response<MyData?>) {
                // Nếu cuộc gọi API thành công thì phương thức này được thực thi
                val dataList = response.body()?.data!!

// SLIDE QUẢNG CÁO
                val imageSlider = findViewById<ImageSlider>(R.id.imageSlider)
                val slideModels = ArrayList<SlideModel>()

                // Biến đếm số lượng bài hát đã thêm vào danh sách slideModels
                var itemCount = 0

                // Tạo SlideModel cho mỗi bài hát trong danh sách dữ liệu của bạn
                for (data in dataList) {
                    if (itemCount < 5) { // Chỉ thêm 5 bài hát vào danh sách
                        val slideModel = SlideModel(data.album.cover_big.toUri().toString())
                        slideModel.title = "Cùng tận hưởng âm nhạc của " + data.artist.name
                        slideModel.imageUrl = data.album.cover_big.toUri().toString()
                        slideModels.add(slideModel)
                        itemCount++
                    } else {
                        break // Thoát khỏi vòng lặp nếu đã thêm đủ 5 bài hát
                    }
                }

                // Đặt danh sách SlideModel vào ImageSlider
                imageSlider.setImageList(slideModels, ScaleTypes.FIT)

// LIST CA SĨ
                // Khởi tạo RecyclerView và Adapter
                val artistList = findViewById<RecyclerView>(R.id.artistList)
                val adapter = TrackAdapter.SlideAdapter(this@MainActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                artistList.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)

                // Thiết lập Adapter cho RecyclerView
                artistList.adapter = adapter

// LIST ALBUM
                // Khởi tạo RecyclerView và Adapter
                val albumList = findViewById<RecyclerView>(R.id.albumList)
                val adapter1 = TrackAdapter.AlbumAdapter(this@MainActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                val layoutManager = GridLayoutManager(this@MainActivity, 2, RecyclerView.VERTICAL, false)
                albumList.layoutManager = layoutManager

                // Thiết lập Adapter cho RecyclerView
                albumList.adapter = adapter1

// LIST NHẠC
                // Khởi tạo RecyclerView và Adapter
                val trackList = findViewById<RecyclerView>(R.id.trackList)
                val adapter2 = TrackAdapter.trackListAdapter(this@MainActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                trackList.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

                // Thiết lập Adapter cho RecyclerView
                trackList.adapter = adapter2

                Log.d("TAG", "onResponse: " + response.body())
            }

            override fun onFailure(p0: Call<MyData?>, t: Throwable) {
//                If the Api call is a failure then this method is executed
                Log.d("TAG", "onResponse: " + t.message)
            }
        })

        // Gọi API để lấy dữ liệu bài hát
        val retrofitData1 = retrofitBuilder.getData("blackpink")

        retrofitData1.enqueue(object : Callback<MyData?> {
            override fun onResponse(p0: Call<MyData?>, p1: Response<MyData?>) {
                // Nếu cuộc gọi API thành công thì phương thức này được thực thi
                val dataList = p1.body()?.data!!
// LIST ALBUM
                // Khởi tạo RecyclerView và Adapter
                val albumList1 = findViewById<RecyclerView>(R.id.albumList1)
                val adapter1 = TrackAdapter.AlbumAdapter(this@MainActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                val layoutManager = GridLayoutManager(this@MainActivity, 2, RecyclerView.VERTICAL, false)
                albumList1.layoutManager = layoutManager

                // Thiết lập Adapter cho RecyclerView
                albumList1.adapter = adapter1

// LIST NHẠC
                // Khởi tạo RecyclerView và Adapter
                val trackList1 = findViewById<RecyclerView>(R.id.trackList1)
                val adapter2 = TrackAdapter.trackListAdapter(this@MainActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                trackList1.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)

                // Thiết lập Adapter cho RecyclerView
                trackList1.adapter = adapter2

                Log.d("TAG", "onResponse: " + p1.body())
            }


            override fun onFailure(p0: Call<MyData?>, p1: Throwable) {
//                If the Api call is a failure then this method is executed
                Log.d("TAG", "onResponse: " + p1.message)
            }

        })

        }


}