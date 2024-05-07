package com.example.music

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.music.databinding.ActivityAdminBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView

class AdminActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
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

    }
}