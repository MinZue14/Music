package com.example.music.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.music.ApiInterface
import com.example.music.MyData
import com.example.music.R
import com.example.music.Adapter.TrackAdapter
import com.example.music.databinding.ActivityAdminTrackBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminTrackActivity : AppCompatActivity() {
    lateinit var binding:ActivityAdminTrackBinding
    lateinit var myRecycleView: RecyclerView
    lateinit var searchView: SearchView
    lateinit var trackAdapter: TrackAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminTrackBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//menu
        // Khai báo drawerLayout và navigationView
        val drawer = findViewById<DrawerLayout>(R.id.admin)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        // Lấy dữ liệu người dùng từ Intent
        val adminName = intent.getStringExtra("admin_name")

        // Gán headerLayoutBinding cho navigationView menu
        val headerLayoutBinding = HeaderMenuBinding.bind(navigationView.getHeaderView(0))
        headerLayoutBinding.menuUsername.text = "Admin: " + adminName

        // Thiết lập listener cho các mục menu trong navigationView
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.admin_user -> {
                    val intent = Intent(this, AdminUserActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.admin_song -> {
                    val intent = Intent(this, AdminTrackActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.admin_chart -> {
                    val intent = Intent(this, AdminChartActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.admin_logout -> {
                    val intent = Intent(this, AdminLogin::class.java)
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
        myRecycleView = findViewById(R.id.RecycleView)
        searchView = findViewById(R.id.seachView)

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
//                If the Api call is a success then this method is executed
                val dataList = response.body()?.data!!

                trackAdapter = TrackAdapter(this@AdminTrackActivity, dataList)
                myRecycleView.adapter = trackAdapter
                myRecycleView.layoutManager = LinearLayoutManager(this@AdminTrackActivity)

                Log.d("TAG", "onResponse: " + response.body())
            }

            override fun onFailure(p0: Call<MyData?>, t: Throwable) {
//                If the Api call is a failure then this method is executed
                Log.d("TAG", "onResponse: " + t.message)
            }
        })

    }


}