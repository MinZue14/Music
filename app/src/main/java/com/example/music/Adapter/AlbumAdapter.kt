package com.example.music.Adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Data
import com.example.music.Database.DatabaseUserList
import com.example.music.Database.DatabaseUserPlaylist
import com.example.music.R
import com.squareup.picasso.Picasso

class AlbumAdapter (var context: Activity, var dataList: List<Data>) :
    RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>(){

    // Khai báo một interface để xử lý sự kiện click
    interface OnAlbumClickListener {
        fun onItemClick(data: Data)
    }

    // Biến để lưu trữ listener của sự kiện click
    var onItemClickListener: OnAlbumClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_list_track, parent, false)
        return AlbumViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val currentData = dataList[position]

        holder.trackName.text = currentData.title
        holder.trackArtist.text = currentData.artist.name

        Picasso.get().load(currentData.album.cover).into(holder.trackImage);

//giao diện nhạc
        // Thiết lập sự kiện click cho mỗi mục trong danh sách
        holder.itemView.setOnClickListener {
            // Kiểm tra xem listener đã được thiết lập hay chưa
            onItemClickListener?.onItemClick(currentData)
        }
//favorite
        // Kiểm tra xem bài hát đã tồn tại trong danh sách yêu thích của người dùng hay không
        val dbManager = DatabaseUserList(context)
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIDString = sharedPref.getString("userID", null)
        val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

        if (userID != -1L) {
            val trackID = currentData.id
            val isTrackInFavorites = dbManager.isTrackInFavorites(userID, trackID)

            if (isTrackInFavorites) {
                // Bài hát đã được yêu thích, hiển thị hình ảnh yêu thích đã chọn
                holder.imageButtonLove.setImageResource(R.drawable.baseline_favorite_red)
            } else {
                // Bài hát chưa được yêu thích, hiển thị hình ảnh yêu thích bình thường
                holder.imageButtonLove.setImageResource(R.drawable.baseline_favorite)
            }
        }

        // Thiết lập sự kiện click cho buttonLove
        holder.imageButtonLove.setOnClickListener {

            if (userID != -1L) {
                val currentData = dataList[position]
                val trackID = currentData.id

                // Kiểm tra xem bài hát đã tồn tại trong danh sách yêu thích của người dùng hay không
                val isTrackFavorited = isTrackInFavorites(userID, trackID)

                if (!isTrackFavorited) {
                    // Thêm bài hát vào danh sách yêu thích của người dùng
                    val dbManager = DatabaseUserList(context)
                    val isSuccess = dbManager.addTrackToFavorite(userID, trackID)
                    if (isSuccess) {
                        Toast.makeText(context, "Bạn đã yêu thích bài hát này", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Xỏa bài hát khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    }

                    // Thay đổi hình ảnh của buttonLove thành btnLoveRed
                    holder.imageButtonLove.setImageResource(R.drawable.baseline_favorite_red)

                } else {
                    // Xóa bài hát khỏi danh sách yêu thích của người dùng
                    val dbManager = DatabaseUserList(context)
                    val isSuccess = dbManager.removeTrackFromFavorite(userID, trackID)
                    if (isSuccess) {
                        Toast.makeText(context, "Bạn hong yêu thích bài này", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Xỏa bài hát khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    }

                    // Đặt lại hình ảnh ban đầu của buttonLove
                    holder.imageButtonLove.setImageResource(R.drawable.baseline_favorite)
                }
            } else {
                Toast.makeText(context, "Không thể xác định người dùng", Toast.LENGTH_SHORT).show()
            }
        }
//playlist
        // Kiểm tra xem bài hát đã tồn tại trong danh sách playlist của người dùng hay không
        val dbManager1 = DatabaseUserPlaylist(context)

        if (userID != -1L) {
            val trackID = currentData.id
            val isTrackInPlaylist = dbManager1.isTrackInPlaylist(userID, trackID)

            if (isTrackInPlaylist) {
                // Bài hát đã được thêm vào playlist, hiển thị hình ảnh playlist đã chọn
                holder.imageButtonPlaylist.setImageResource(R.drawable.baseline_playlist_add_check)
            } else {
                // Bài hát chưa được thm vào playlist, hiển thị hình ảnh playlist bình thường
                holder.imageButtonPlaylist.setImageResource(R.drawable.baseline_genre)
            }
        }

        // Thiết lập sự kiện click cho buttonPlaylist
        holder.imageButtonPlaylist.setOnClickListener {

            if (userID != -1L) {
                val currentData = dataList[position]
                val trackID = currentData.id

                // Kiểm tra xem bài hát đã tồn tại trong Playlist của người dùng hay không
                val isTrackPlaylist = isTrackInPlaylist(userID, trackID)

                if (!isTrackPlaylist) {
                    // Thêm bài hát vào Playlist của người dùng
                    val dbManager1 = DatabaseUserPlaylist(context)
                    val isSuccess = dbManager1.addTrackToPlaylist(userID, trackID)
                    if (isSuccess) {
                        Toast.makeText(context, "Bạn đã thêm bài hát này vào playlist", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                    }

                    // Thay đổi hình ảnh của imageButtonPlaylist thành baseline_playlist_add_check
                    holder.imageButtonPlaylist.setImageResource(R.drawable.baseline_playlist_add_check)

                } else {
                    // Xóa bài hát khỏi playlist của người dùng
                    val dbManager1 = DatabaseUserPlaylist(context)
                    val isSuccess = dbManager1.removeTrackFromPlaylist(userID, trackID)
                    if (isSuccess) {
                        Toast.makeText(context, "Bạn hong bỏ bài hát này vào playlist ", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                    }

                    // Đặt lại hình ảnh ban đầu của buttonLove
                    holder.imageButtonPlaylist.setImageResource(R.drawable.baseline_genre)
                }
            } else {
                Toast.makeText(context, "Không thể xác định người dùng", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isTrackInFavorites(userID: Long, trackID: Long): Boolean {
        val dbManager = DatabaseUserList(context)
        return dbManager.isTrackInFavorites(userID, trackID)
    }
    private fun isTrackInPlaylist(userID: Long, trackID: Long): Boolean {
        val dbManager1 = DatabaseUserPlaylist(context)
        return dbManager1.isTrackInPlaylist(userID, trackID)
    }

    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackImage: ImageView

        val trackName: TextView
        val trackArtist: TextView
        val trackView: TextView

        val imageButtonPlaylist: ImageButton
        val imageButtonLove: ImageButton

        init {
            trackImage = itemView.findViewById(R.id.trackImage)
            trackName = itemView.findViewById(R.id.trackName)
            trackArtist = itemView.findViewById(R.id.trackArtist)
            trackView = itemView.findViewById(R.id.trackView)
            imageButtonPlaylist = itemView.findViewById(R.id.imageButtonPlaylist)
            imageButtonLove = itemView.findViewById(R.id.imageButtonLove)
        }
    }
}