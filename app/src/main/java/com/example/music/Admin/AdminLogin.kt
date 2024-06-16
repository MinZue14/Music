package com.example.music.Admin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.music.Database.DatabaseAdmin
import com.example.music.Main.MainActivity
import com.example.music.databinding.ActivityAdminLoginBinding

class AdminLogin : AppCompatActivity() {
    lateinit var binding: ActivityAdminLoginBinding
    lateinit var databaseAdmin: DatabaseAdmin
    lateinit var sharedPreferences: SharedPreferences
    var resultDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseAdmin = DatabaseAdmin(this)
        //Lưu dữ liệu người dùng vào SharedPreferences
        sharedPreferences = getSharedPreferences("admin_data", Context.MODE_PRIVATE)

        binding.loginBtn.setOnClickListener {
            val loginName = binding.loginName.text.toString()
            val loginPass = binding.loginPass.text.toString()

            if (loginName.isEmpty() || loginPass.isEmpty()) {
                showErrorDialog("Vui lòng nhập dữ liệu!")
            } else {
                val user = databaseAdmin.checkPass(loginName, loginPass)
                if (user != null) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminUserActivity::class.java)

                    // Lưu thông tin người dùng vào SharedPreferences
                    with(sharedPreferences.edit()) {
                        putString("adminName", user.getAdminName())
                        apply()
                    }
                    startActivity(intent)
                    finish()
                } else {
                    showErrorDialog("Đăng nhập thất bại!")
                }
            }
        }


        binding.showPasswordCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                // Hiển thị mật khẩu
                binding.loginPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            } else {
                // Ẩn mật khẩu
                binding.loginPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }


        binding.loginToSignup.setOnClickListener {
            val intent = Intent(this, AdminSignUp::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        // Kiểm tra và đóng dialog nếu đang mở
        resultDialog?.dismiss()
    }

    private fun showResultDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        resultDialog = builder.create()
        resultDialog?.show()
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        val dialog = builder.create()
        dialog.show()
    }
}
