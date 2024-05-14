package com.example.music.Admin

import com.example.music.Adapter.UserAdapter
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.music.Database.DatabaseUsers
import com.example.music.R
import com.example.music.User.Users
import com.example.music.databinding.ActivityAdminUserBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView

class AdminUserActivity : AppCompatActivity() {
    lateinit var binding: ActivityAdminUserBinding
    lateinit var listUser: ArrayList<Users>
    lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdminUserBinding.inflate(layoutInflater)
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
                    val intent = Intent(this, AdminActivity::class.java)
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
        val dbHelper = DatabaseUsers(this)
        // Truy vấn cơ sở dữ liệu để lấy danh sách người dùng
        listUser = dbHelper.getAllUsers()

        // Khởi tạo adapter và gán adapter cho ListView
        userAdapter = UserAdapter(this, listUser)
        binding.listviewUser.adapter = userAdapter

        // Xử lý sự kiện thêm người dùng mới
        binding.btnAdd.setOnClickListener {
            val AdminUsername = binding.edtAdminUsername.text.toString()
            val AdminUsermail = binding.edtAdminUsermail.text.toString()
            val AdminUserpass = binding.edtAdminUserpass.text.toString()

            if (TextUtils.isEmpty(AdminUsername) || TextUtils.isEmpty(AdminUsermail) || TextUtils.isEmpty(
                    AdminUserpass
                )
            ) {
                showResultDialog("Vui lòng điền đầy đủ thông tin người dùng!")
            } else {
                val dbHelper = DatabaseUsers(this)
                val isInserted = dbHelper.insert(AdminUsername, AdminUsermail, AdminUserpass)
                dbHelper.close()

                if (isInserted) {
                    // Nếu insert thành công
                    binding.edtAdminUsername.setText("")
                    binding.edtAdminUsermail.setText("")
                    binding.edtAdminUserpass.setText("")
                    userAdapter.notifyDataSetChanged()
                    showResultDialog("Thêm người dùng thành công!")
                }
            }
        }


        // Xử lý sự kiện xóa người dùng
        binding.listviewUser.setOnItemLongClickListener { parent, view, position, id ->
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Xác nhận xóa")
            alertDialog.setMessage("Bạn muốn xóa hay không ?")
            alertDialog.setPositiveButton("Có") { dialog, which ->
                val user = listUser[position] // Lấy người dùng từ danh sách nguồn
                val dbHelper =
                    DatabaseUsers(this) // Khởi tạo đối tượng DatabaseUsers
                val isDeleted = dbHelper.deleteUser(user) // Gọi phương thức deleteUser từ DatabaseUsers để xóa dữ liệu

                if (isDeleted) {
                    // Xóa người dùng từ danh sách nguồn và cập nhật ListView
                    listUser.removeAt(position)
                    userAdapter.notifyDataSetChanged()
                    showResultDialog("Xóa dữ liệu thành công!")
                } else {
                    showResultDialog("Xóa dữ liệu không thành công!")
                }
            }

            alertDialog.setNegativeButton("Không") { dialog, which ->
                showResultDialog("Xóa dữ liệu không thành công!")
            }

            alertDialog.create().show()
            return@setOnItemLongClickListener false
        }

        binding.btnSearch.setOnClickListener {
            showSearchDialog()
        }
    }

    // Xử lý sự kiện hiện ra 1 dialog search thông tin
    private fun showSearchDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search, null)
        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Tìm kiếm")
            .setPositiveButton("Tìm") { dialog, which ->
                val edtUserIdSearch = dialogView.findViewById<EditText>(R.id.edtUserIdSearch)
                val edtUsernameSearch = dialogView.findViewById<EditText>(R.id.edtUsernameSearch)
                val edtUserMailSearch = dialogView.findViewById<EditText>(R.id.edtUserMailSearch)
                val edtUserPassSearch = dialogView.findViewById<EditText>(R.id.edtUserPassSearch)

                val UserIdSearch = edtUserIdSearch.text.toString().trim()
                val UsernameSearch = edtUsernameSearch.text.toString().trim()
                val UserMailSearch = edtUserMailSearch.text.toString().trim()
                val UserPassSearch = edtUserPassSearch.text.toString().trim()

                searchUser(UserIdSearch, UsernameSearch, UserMailSearch, UserPassSearch)
            }
            .setNegativeButton("Hủy") { dialog, which ->
                dialog.dismiss()
            }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun searchUser(UserIdSearch: String, UsernameSearch: String, UserMailSearch: String, UserPassSearch: String) {
        val searchList = ArrayList<Users>()

        for (user in listUser) {
            if (user.userID.contains(UserIdSearch, ignoreCase = true) &&
                user.username.contains(UsernameSearch, ignoreCase = true) &&
                user.email.contains(UserMailSearch, ignoreCase = true) &&
                user.password.contains(UserPassSearch, ignoreCase = true)
            ) {
                searchList.add(user)
            }
        }

        userAdapter = UserAdapter(this, searchList)
        binding.listviewUser.adapter = userAdapter
        Toast.makeText(applicationContext, "Tìm thấy ${searchList.size} kết quả", Toast.LENGTH_SHORT).show()
//        showResultDialog("Tìm thấy ${searchList.size} kết quả")
    }

    private fun showResultDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                recreate() // Tạo lại hoạt động hiện tại
            }
            .setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }
}
