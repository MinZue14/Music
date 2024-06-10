package com.example.music.Main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.music.Admin.AdminLogin
import com.example.music.ApiInterface
import com.example.music.Data
import com.example.music.Database.DatabaseUserList
import com.example.music.R
import com.example.music.databinding.ActivityMusicBinding
import com.example.music.databinding.HeaderMenuBinding
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MusicActivity : AppCompatActivity() {
    lateinit var binding: ActivityMusicBinding
    lateinit var sharedPref: SharedPreferences
    lateinit var retrofitBuilder: ApiInterface

    private var currentTrackId: Long = 0L
    private var isFavorite: Boolean = false
    lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var isRepeat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
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
        retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        // Lấy ID của bài hát từ intent
        val trackId = intent.getStringExtra("trackId")
        Log.d("TAG", "Track ID from intent: $trackId") // Debug trackId

        if (!trackId.isNullOrEmpty()) { // Kiểm tra xem trackId có null hoặc rỗng không
            // Chuyển trackId từ String sang Long
            val trackIdLong = trackId.toLong()

            // Gọi API để lấy dữ liệu chi tiết của bài hát dựa trên trackId
            val retrofitData = retrofitBuilder.getTrackDetail(trackIdLong)

            retrofitData.enqueue(object : Callback<Data?> {
                override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                    // Nếu cuộc gọi API thành công thì phương thức này được thực thi
                    val data = response.body()
                    if (data != null) {

                // Hiển thị giao diện phát nhạc từ dữ liệu API
                        binding.trackName.text = data.title
                        binding.artistName.text = data.artist.name
                        currentTrackId = data.id


                        Picasso.get().load(data.album.cover_medium).into(binding.trackImage)

                        // Format and set the total time
                        val minutes = data.duration / 60
                        val seconds = data.duration % 60
                        val formattedTime = String.format("%d:%02d", minutes, seconds)

                        binding.totalTime.text = formattedTime

                // Set up MediaPlayer
                        setupMediaPlayer(data.preview)

                // Lặp lại bài hát
                        binding.buttonRepeat.setOnClickListener {
                            RepeatMode()
                        }

                // Quay lại bài hát trước
                        binding.buttonBack.setOnClickListener {
                            playPreviousTrack()
                        }

                // Chuyển tới bài hát tiếp theo
                        binding.buttonNext.setOnClickListener {
                            playNextTrack()
                        }

                // Kiểm tra xem bài hát đã tồn tại trong danh sách yêu thích của người dùng hay chưa
                        isFavorite = checkIfFavorite(data.id.toLong())
                        updateFavoriteButton(isFavorite)

                // Yêu thích bài hát
                        binding.buttonFavorite.setOnClickListener {
                            val currentlyFavorite = toggleFavoriteStatus(currentTrackId)
                            updateFavoriteButton(currentlyFavorite)
                        }

                    } else {
                        Log.d("TAG", "Response body is null")
                    }
                }

                override fun onFailure(call: Call<Data?>, t: Throwable) {
                    // Nếu cuộc gọi API thất bại thì phương thức này được thực thi
                    Log.d("TAG", "onFailure: ${t.message}")
                }
            })
        } else {
            // Xử lý trường hợp trackId null hoặc rỗng
            Log.d("TAG", "trackId is null or empty")
        }

    // Sự kiện Play/Pause
        binding.buttonPlay.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                binding.buttonPlay.setImageResource(android.R.drawable.ic_media_play)
            } else {
                mediaPlayer.start()
                binding.buttonPlay.setImageResource(android.R.drawable.ic_media_pause)
            }
            isPlaying = !isPlaying

            val editor = sharedPref.edit()
            editor.putBoolean("isPlaying", isPlaying)
            editor.apply()
        }
    }

    /////////// PHÁT NHẠC Ở GIAO DIỆN NHẠC ///////////////////////
    private fun setupMediaPlayer(previewUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                binding.buttonPlay.isEnabled = true
                if (isPlaying) {
                    start()
                }
            }
            setOnCompletionListener {
                binding.buttonPlay.setImageResource(android.R.drawable.ic_media_play)
                updateSeekBar()
                this@MusicActivity.isPlaying = false
                val editor = sharedPref.edit()
                editor.putBoolean("isPlaying", false)
                editor.apply()
            }
        }
    }

    private fun updateSeekBar() {
        // Cập nhật SeekBar khi bài hát phát
        val seekBar = binding.seekBar
        seekBar.max = mediaPlayer?.duration ?: 0
        val currentTimeTextView = binding.currentTime // Giả sử bạn có một TextView cho thời gian hiện tại
        val totalTimeTextView = binding.totalTime // Giả sử bạn có một TextView cho tổng thời gian

        // Cập nhật tổng thời gian
        val totalDuration = mediaPlayer?.duration ?: 0
        val totalMinutes = totalDuration / 60000
        val totalSeconds = (totalDuration % 60000) / 1000
        totalTimeTextView.text = String.format("%02d:%02d", totalMinutes, totalSeconds)

        // Tạo một đối tượng Runnable để cập nhật tiến trình của SeekBar mỗi giây.
        val update = object : Runnable {
            override fun run() {
                seekBar.progress = mediaPlayer?.currentPosition ?: 0
                val currentDuration = mediaPlayer?.currentPosition ?: 0
                val currentMinutes = currentDuration / 60000
                val currentSeconds = (currentDuration % 60000) / 1000
                currentTimeTextView.text = String.format("%02d:%02d", currentMinutes, currentSeconds)
                seekBar.postDelayed(this, 1000)
            }
        }
        seekBar.post(update)

        // Lắng nghe thay đổi của seek bar
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    val currentMinutes = progress / 60000
                    val currentSeconds = (progress % 60000) / 1000
                    currentTimeTextView.text =
                        String.format("%02d:%02d", currentMinutes, currentSeconds)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Xử lý sự kiện khi bắt đầu kéo seek bar
                mediaPlayer?.pause() // Dừng phát nhạc
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Xử lý sự kiện khi dừng kéo seek bar
                mediaPlayer?.seekTo(seekBar?.progress ?: 0) // Chuyển đến vị trí tương ứng với seek bar
                mediaPlayer?.start() // Tiếp tục phát nhạc
            }
        })
    }

    private fun playPreviousTrack() {
        // Xử lý logic cho nút Back
        Log.d("TAG", "Back button clicked")

        // Giảm currentTrackId một đơn vị để lấy previousTrackId
        val previousTrackId = currentTrackId - 1

        if (isValidTrackId(previousTrackId)) {
            playTrackById(previousTrackId)
        } else {
            // Xử lý khi không có bài hát trước đó
            Log.d("TAG", "No previous track available")
            Toast.makeText(this, "Không có bài nào ở trước!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playNextTrack() {
        // Xử lý logic cho nút Next
        Log.d("TAG", "Next button clicked")

        // Tăng currentTrackId lên một đơn vị để lấy nextTrackId
        val nextTrackId = currentTrackId + 1

        if (isValidTrackId(nextTrackId)) {
            playTrackById(nextTrackId)
        } else {
            Log.d("TAG", "No next track available")
            Toast.makeText(this, "Không có bài tiếp theo!", Toast.LENGTH_SHORT).show()
        }
    }
    private fun isValidTrackId(trackId: Long): Boolean {
        // Giả sử bạn có danh sách các ID bài hát hợp lệ
        val validTrackIds = listOf<Long>(1, 2, 3, 4, 5) // Ví dụ danh sách các ID hợp lệ
        return validTrackIds.contains(trackId)
    }

    private fun playTrackById(trackId: Long) {
        // Gọi API để lấy thông tin chi tiết của bài hát dựa trên trackId
        val retrofitData = retrofitBuilder.getTrackDetail(trackId)

        retrofitData.enqueue(object : Callback<Data?> {
            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                // Nếu cuộc gọi API thành công thì phương thức này được thực thi
                val data = response.body()
                if (data != null) {
                    // Hiển thị giao diện phát nhạc từ dữ liệu API
                    binding.trackName.text = data.title
                    binding.artistName.text = data.artist.name
                    currentTrackId = data.id

                    Picasso.get().load(data.album.cover_medium).into(binding.trackImage)

                    // Format and set the total time
                    val minutes = data.duration / 60
                    val seconds = data.duration % 60
                    val formattedTime = String.format("%d:%02d", minutes, seconds)
                    binding.totalTime.text = formattedTime

                    // Set up MediaPlayer
                    setupMediaPlayer(data.preview)
                } else {
                    Log.d("TAG", "Response body is null")
                    Toast.makeText(this@MusicActivity, "Không có dữ liệu bài hát", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Data?>, t: Throwable) {
                // Nếu cuộc gọi API thất bại thì phương thức này được thực thi
                Log.d("TAG", "onFailure: ${t.message}")
                Toast.makeText(this@MusicActivity, "Lỗi load dữ liệu", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfFavorite(trackId: Long): Boolean {
        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIDString = sharedPref.getString("userID", null)
        val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

        if (userID != -1L) {
            // Nếu userID hợp lệ, kiểm tra xem bài hát có trong danh sách yêu thích của người dùng hay không
            val dbManager = DatabaseUserList(this)
            return dbManager.isTrackInFavorites(userID, trackId)
        }

        // Trả về false nếu không có thông tin về userID hoặc nếu userID không hợp lệ
        return false
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        // Cập nhật giao diện của nút yêu thích dựa trên trạng thái yêu thích
        Log.d("TAG", "Updating favorite button to: $isFavorite")
        if (isFavorite) {
            binding.buttonFavorite.setImageResource(R.drawable.baseline_favorite_red)
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.baseline_love_2)
        }
    }

    private fun toggleFavoriteStatus(trackId: Long): Boolean {
        // Lấy thông tin người dùng từ SharedPreferences
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIDString = sharedPref.getString("userID", null)
        val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

        if (userID != -1L) {
            val dbManager = DatabaseUserList(this)
            if (isFavorite) {
                // Nếu bài hát đang trong danh sách yêu thích, xóa bài hát khỏi danh sách yêu thích
                val result = dbManager.removeTrackFromFavorite(userID, trackId)
                if (result) {
                    Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    isFavorite = false
                } else {
                    Toast.makeText(this, "mmm", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Nếu bài hát chưa có trong danh sách yêu thích, thêm bài hát vào danh sách yêu thích
                val result = dbManager.addTrackToFavorite(userID, trackId)
                if (result) {
                    Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    isFavorite = true
                } else {
                    Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                }
            }
            updateFavoriteButton(isFavorite) // Cập nhật giao diện nút yêu thích sau khi thay đổi trạng thái
            return isFavorite
        } else {
            Toast.makeText(this, "Không thể xác định người dùng", Toast.LENGTH_SHORT).show()
            return isFavorite
        }
    }

    private fun RepeatMode() {
        // Xử lý logic cho nút Repeat
        Log.d("TAG", "Repeat button clicked")
        isRepeat = !isRepeat
        if (isRepeat) {
            binding.buttonRepeat.setImageResource(R.drawable.baseline_replay)
            Toast.makeText(this, "Lặp lại bài hát", Toast.LENGTH_SHORT).show()
        } else {
            binding.buttonRepeat.setImageResource(R.drawable.baseline_random)
            Toast.makeText(this, "Phát ngẫu nhiên bài hát", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}
