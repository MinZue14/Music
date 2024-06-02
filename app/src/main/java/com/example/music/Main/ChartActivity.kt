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
import com.example.music.Admin.AdminLogin
import com.example.music.ApiInterface
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

//                val trackViews = dataList.map { it.title to it.rank }.toMap()
//
//                // Sort and take the top 5
//                val topTracks = trackViews.entries.sortedByDescending { it.value }.take(20)
//
//                // Prepare data for chart
//                val labels = topTracks.map { it.key }
//                val values = topTracks.map { it.value.toFloat() }
//
//                updateBarChart(barChart, labels, values)


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
        barDataSet.valueTextSize = 16f
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry): String {
                return labels[barEntry.x.toInt()]
            }
        }

        val barData = BarData(barDataSet)
        chart.data = barData
        chart.setFitBars(true)
        chart.animateY(2000)
        chart.invalidate()
    }
}


