package com.example.music

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.music.databinding.ActivityAdminSignUpBinding
import com.example.music.databinding.ActivitySignUpBinding

class AdminSignUp : AppCompatActivity() {

    lateinit var binding: ActivityAdminSignUpBinding
    lateinit var databaseAdmin: DatabaseAdmin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseAdmin = DatabaseAdmin(this)

        binding.signupBtn.setOnClickListener {
            val signupName = binding.signupName.text.toString()
            val signupPass = binding.signupPass.text.toString()

            val confirmPassword = binding.signupConfirm.text.toString()

            if (signupName.isEmpty() || signupPass.isEmpty() || confirmPassword.isEmpty()) {
                showErrorDialog("Vui lòng nhập dữ liệu!")
            } else {
                if (signupPass == confirmPassword) {
                    val checkAminName = databaseAdmin.checkName(signupName)

                    if (!checkAminName) {

                        val insert = databaseAdmin.insert(signupName, signupPass)

                        if (insert) {
                            showResultDialog("Đăng ký thành công!")
                            startActivity(Intent(this, AdminLogin::class.java))
                        } else {
                            showErrorDialog("Đăng ký thất bại!")
                        }
                    }else {
                        showResultDialog("Tài khoản đã tồn tại! Xin hãy đăng nhập!")
                    }
                } else {
                    showErrorDialog("Mật khẩu không khớp!")
                }
            }
        }

        binding.showPasswordCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {

                // Hiển thị mật khẩu
                binding.signupPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.signupConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

            } else {
                // Ẩn mật khẩu
                binding.signupPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.signupConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }

        binding.signupToLogin.setOnClickListener {
            val intent = Intent(this, AdminLogin::class.java)
            startActivity(intent)
        }
        binding.adminToHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Hàm hiển thị dialog kết quả
    private fun showResultDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }

    // Hàm hiển thị dialog thông báo lỗi
    private fun showErrorDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.show()
    }
}
