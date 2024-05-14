package com.example.music.Admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.music.API.ApiInterface
import com.example.music.API.MyData
import com.example.music.R
import com.example.music.databinding.ActivityAdminChartBinding
import com.example.music.databinding.HeaderMenuBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AdminChartActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminChartBinding
    lateinit var barChart:BarChart
    lateinit var pieChart:PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminChartBinding.inflate(layoutInflater)
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
        barChart = findViewById(R.id.barChart)
        pieChart = findViewById(R.id.pieChart)

        // Tạo HashMap để lưu tổng số lượt xem cho mỗi ca sĩ và mỗi album
        val artistViewsMap = HashMap<String, Int>() // Dùng tên ca sĩ làm khóa
        val albumViewsMap = HashMap<String, Int>() // Dùng tên album làm khóa


        // Khởi tạo Retrofit
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        // Gọi API để lấy dữ liệu bài hát
        val retrofitData = retrofitBuilder.getData("eminem")

        retrofitData.enqueue(object : Callback<MyData?> {
            override fun onResponse(p0: Call<MyData?>, response: Response<MyData?>) {
                // Lấy danh sách dữ liệu từ phản hồi
                val dataList = response.body()?.data!!

                for (data in dataList) {
                    val artist = data.artist.name
                    val album = data.album.title

                    // Tính tổng số lượt xem cho mỗi ca sĩ và mỗi album
                    artistViewsMap[artist] = artistViewsMap.getOrDefault(artist, 0) + data.rank
                    albumViewsMap[album] = albumViewsMap.getOrDefault(album, 0) + data.rank
                }

                // Chọn top ca sĩ và top album
                val topArtists = artistViewsMap.toList().sortedByDescending { (_, value) -> value }.take(5)
                val topAlbums = albumViewsMap.toList().sortedByDescending { (_, value) -> value }.take(5)

                // Cập nhật BarChart với top ca sĩ
                updateBarChart(barChart, topArtists.map { it.first }, topArtists.map { it.second.toFloat() })

                // Cập nhật PieChart với top album
                updatePieChart(pieChart, topAlbums.map { it.first }, topAlbums.map { it.second.toFloat() })

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

        val barDataSet = BarDataSet(barEntries, "Top Artists")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList() // Cấu hình màu sắc cho các phần tử trong BarChart
        barDataSet.valueTextSize = 10f // Kích thước của giá trị trên cột
        barDataSet.setValueTextSize(16f)
        barDataSet.valueFormatter = object : ValueFormatter() { // Định dạng giá trị trên cột
            override fun getBarLabel(barEntry: BarEntry?): String {
                return labels[barEntry?.x?.toInt() ?: 0] // Lấy tên ca sĩ tương ứng với cột
            }
        }

        val barData = BarData(barDataSet)
        chart.data = barData
        chart.setFitBars(true)
        chart.animateY(2000)
        chart.invalidate()
    }

    private fun updatePieChart(chart: PieChart, labels: List<String>, values: List<Float>) {
        val pieEntries = ArrayList<PieEntry>()
        for (i in labels.indices) {
            val pieEntry = PieEntry(values[i], labels[i])
            pieEntries.add(pieEntry)
        }

        val pieDataSet = PieDataSet(pieEntries, "Top Albums")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.asList() // Cấu hình màu sắc cho các phần tử trong PieChart
        pieDataSet.setValueTextSize(16f)

        val pieData = PieData(pieDataSet)
        chart.data = pieData
        chart.animateY(1000)
        chart.setCenterText("Top Album")
        chart.setExtraOffsets(2f, 2f, 2f, 2f) // Điều chỉnh khoảng cách giữa PieChart và viền ngoài
        chart.invalidate()
    }

}

