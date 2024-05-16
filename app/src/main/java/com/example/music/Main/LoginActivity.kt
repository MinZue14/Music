package com.example.music.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.music.Database.DatabaseUsers
import com.example.music.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var databaseUsers: DatabaseUsers
    var resultDialog: AlertDialog? = null
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseUsers = DatabaseUsers(this)
        //Lưu dữ liệu người dùng vào SharedPreferences
        sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)


        binding.loginBtn.setOnClickListener {
            val loginName = binding.loginName.text.toString()
            val loginPass = binding.loginPass.text.toString()

            if (loginName.isEmpty() || loginPass.isEmpty()) {
                showErrorDialog("Vui lòng nhập dữ liệu!")
            } else {
                val user = databaseUsers.checkPass(loginName, loginPass)
                if (user != null) {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)

                    // Lưu thông tin người dùng vào SharedPreferences
                    with(sharedPreferences.edit()) {
                        putString("userID", user.getUserID())
                        putString("username", user.getUsername())
                        putString("email", user.getEmail())
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
                binding.loginPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                // Ẩn mật khẩu
                binding.loginPass.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.loginToSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
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
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        resultDialog = builder.create()
        resultDialog?.show()
    }
}
