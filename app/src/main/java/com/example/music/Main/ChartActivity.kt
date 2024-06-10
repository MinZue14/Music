package com.example.music.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import com.example.music.databinding.ActivityChartBinding
import com.example.music.databinding.ActivityMusicBinding
import com.example.music.databinding.HeaderMenuBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChartActivity : AppCompatActivity() {
    lateinit var binding: ActivityChartBinding
    lateinit var sharedPref: SharedPreferences
    lateinit var searchView: SearchView
    lateinit var barChart: BarChart


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChartBinding.inflate(layoutInflater)
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

        barChart = findViewById(R.id.barChart)

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(p0: Call<MyData?>, response: Response<MyData?>) {
                // Nếu cuộc gọi API thành công thì phương thức này được thực thi
                val dataList = response.body()?.data!!

// CHART
                val trackViews = dataList.map { it.title to it.rank }.toMap()

                // Sort and take the top 5
                val topTracks = trackViews.entries.sortedByDescending { it.value }.take(20)

                // Prepare data for chart
                val labels = topTracks.map { it.key }
                val values = topTracks.map { it.value.toFloat() }

                updateBarChart(barChart, labels, values)

// LIST CA SĨ
                // Khởi tạo RecyclerView và Adapter
                val artistList = findViewById<RecyclerView>(R.id.artistList)
                val adapter = TrackAdapter.SlideAdapter(this@ChartActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                artistList.layoutManager = LinearLayoutManager(this@ChartActivity, LinearLayoutManager.HORIZONTAL, false)

                // Thiết lập Adapter cho RecyclerView
                artistList.adapter = adapter

                // Thiết lập listener cho adapter
                adapter.onItemClickListener = object : TrackAdapter.SlideAdapter.OnArtistClickListener{
                    override fun onItemClick(data: Data) {
                        // Mở giao diện nhạc của bài hát được nhấp
                        val intent = Intent(this@ChartActivity, ArtistActivity::class.java)
                        intent.putExtra("artistID", data.artist.id.toString()) // Truyền ID của bài hát qua intent
                        startActivity(intent)
                    }
                }
// LIST ALBUM
                // Khởi tạo RecyclerView và Adapter
                val albumList = findViewById<RecyclerView>(R.id.albumList)
                val adapter1 = TrackAdapter.AlbumAdapter(this@ChartActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                val layoutManager =
                    GridLayoutManager(this@ChartActivity, 2, RecyclerView.VERTICAL, false)
                albumList.layoutManager = layoutManager

                // Thiết lập Adapter cho RecyclerView
                albumList.adapter = adapter1

// LIST NHẠC
                // Khởi tạo RecyclerView và Adapter
                val trackList = findViewById<RecyclerView>(R.id.trackList)
                val adapter2 = TrackAdapter.trackListAdapter(this@ChartActivity, dataList)

                // Thiết lập LayoutManager cho RecyclerView
                trackList.layoutManager =
                    LinearLayoutManager(this@ChartActivity, LinearLayoutManager.VERTICAL, false)

                // Thiết lập Adapter cho RecyclerView
                trackList.adapter = adapter2

                // Thiết lập listener cho adapter
                adapter2.onItemClickListener = object : TrackAdapter.trackListAdapter.OnTrackClickListener {
                    override fun onItemClick(data: Data) {
                        // Mở giao diện nhạc của bài hát được nhấp
                        val intent = Intent(this@ChartActivity, MusicActivity::class.java)
                        intent.putExtra("trackId", data.id.toString()
                        ) // Truyền ID của bài hát qua intent
                        startActivity(intent)
                    }
                }

                Log.d("TAG", "onResponse: " + response.body())
            }

            override fun onFailure(p0: Call<MyData?>, t: Throwable) {
//                If the Api call is a failure then this method is executed
                Log.d("TAG", "onResponse: " + t.message)
            }
        })
    }

    private fun updateBarChart(chart: BarChart, labels: List<String>, values: List<Float>) {
        val barEntries = ArrayList<BarEntry>()
        for (i in labels.indices) {
            val barEntry = BarEntry(i.toFloat(), values[i])
            barEntries.add(barEntry)
        }

        val barDataSet = BarDataSet(barEntries, "Top Tracks")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        barDataSet.valueTextSize = 20f // thiết lập kích thước font chữ
        barDataSet.setDrawValues(true) // hiển thị dữ liệu bên trong chân cột

        val barData = BarData(barDataSet)
        chart.data = barData
        chart.setFitBars(true)
        chart.animateY(2000)
        chart.invalidate()

        // Tùy chỉnh định dạng cho nhãn giá trị cột
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                // Hiển thị tên bài hát và giá trị bên trong cột
                return "${barEntry.y.toInt()} - ${labels[barEntry.x.toInt()]}"
            }
        }

        // Đảm bảo nhãn giá trị nằm bên trong cột
        chart.setDrawValueAboveBar(false)
        barData.setValueTextSize(10f)
        barData.setValueTextColor(Color.BLACK)

        // Cấu hình trục x để hiển thị các chỉ số đúng cách
        chart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return labels[value.toInt()]
            }
        }

        // Đảm bảo nhãn trục x không bị cắt bớt
        chart.xAxis.granularity = 1f
        chart.xAxis.labelCount = labels.size

        // Cấu hình trục y
        chart.axisLeft.setDrawGridLines(false)
        chart.axisRight.setDrawGridLines(false)
        chart.axisRight.isEnabled = false
        chart.axisLeft.isEnabled = false

    }
}


