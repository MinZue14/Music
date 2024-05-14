package com.example.music.User

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.music.Database.DatabaseUsers
import com.example.music.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var databaseUsers: DatabaseUsers
    var resultDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        databaseUsers = DatabaseUsers(this)

        binding.loginBtn.setOnClickListener {
            val loginName = binding.loginName.text.toString()
            val loginPass = binding.loginPass.text.toString()

            if (loginName.isEmpty() || loginPass.isEmpty()) {
                showErrorDialog("Vui lòng nhập dữ liệu!")
            } else {
                val user = databaseUsers.checkPass(loginName, loginPass)
                if (user != null) {
                    showResultDialog("Đăng nhập thành công!")
                    val intent = Intent(this, MainActivity::class.java)
                    //lưu dữ liệu acc
                    intent.putExtra("user_name", user.getUsername());
                    intent.putExtra("user_email", user.getEmail());

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
