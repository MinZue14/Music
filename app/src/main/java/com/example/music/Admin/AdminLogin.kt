package com.example.music.Admin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.music.Database.DatabaseAdmin
import com.example.music.databinding.ActivityAdminLoginBinding

class AdminLogin : AppCompatActivity() {
    lateinit var binding: ActivityAdminLoginBinding
    lateinit var databaseAdmin: DatabaseAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseAdmin = DatabaseAdmin(this)

        binding.loginBtn.setOnClickListener {
            val loginName = binding.loginName.text.toString()
            val loginPass = binding.loginPass.text.toString()

            if (loginName.isEmpty() || loginPass.isEmpty()) {
                showErrorDialog("Vui lòng nhập dữ liệu!")
            } else {
                val admin = databaseAdmin.checkPass(loginName, loginPass)
                if (admin != null) {
                    showResultDialog("Đăng nhập thành công!", admin)
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

    private fun showResultDialog(message: String, admin: Admin) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        val dialog = builder.create()
        dialog.show()

        // Đóng Dialog sau 2 giây và chuyển trang
        Handler().postDelayed({
            dialog.dismiss()
            val intent = Intent(this, AdminActivity::class.java)

            //lưu dữ liệu acc
            intent.putExtra("admin_name", admin.getAdminName())
            startActivity(intent)
            finish()
        }, 2000) // Thời gian là 2 giây (có thể điều chỉnh thời gian tùy ý)
    }

    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
        val dialog = builder.create()
        dialog.show()
    }
}
