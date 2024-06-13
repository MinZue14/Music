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

class PlaylistAdapter(
    private val context: Activity,
    private val dataList: List<Data>
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    // Khai báo một interface để xử lý sự kiện click
    interface OnTrackClickListener {
        fun onItemClick(data: Data)
    }

    // Biến để lưu trữ listener của sự kiện click
    var onItemClickListener: OnTrackClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_playlist, parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val currentData = dataList[position] // Lấy dữ liệu của bài hát playlist hiện tại từ danh sách

        // Hiển thị thông tin chi tiết của bài hát trong RecyclerView
        holder.trackName.text = currentData.title // Hiển thị tiêu đề của bài hát
        holder.trackArtist.text = currentData.artist.name // Hiển thị tên nghệ sĩ

        Picasso.get().load(currentData.album.cover).into(holder.trackImage);

        // Thiết lập sự kiện click cho mỗi mục trong danh sách
        holder.itemView.setOnClickListener {
            // Kiểm tra xem listener đã được thiết lập hay chưa
            onItemClickListener?.onItemClick(currentData)
        }

        // Thiết lập sự kiện click cho nút playlist
        holder.imageButtonPlaylist.setOnClickListener {
            val dbManager = DatabaseUserPlaylist(context)

            // Lấy userID từ SharedPreferences
            val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userIDString = sharedPref.getString("userID", null)
            val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

            // Kiểm tra xem user đã đăng nhập chưa
            if (userID != -1L) {
                val trackID = currentData.id
                val isSuccess = dbManager.removeTrackFromPlaylist(userID, trackID)

                if (isSuccess) {
                    Toast.makeText(context, "Đã xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                    // Xóa bài hát khỏi danh sách hiện tại và cập nhật RecyclerView
                    val newList = dataList.toMutableList()
                    newList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                } else {
                    Toast.makeText(context, "Không thể xóa bài hát khỏi playlist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Vui lòng đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show()
            }
        }
    }


    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackName: TextView = itemView.findViewById(R.id.trackName)
        val trackArtist: TextView = itemView.findViewById(R.id.trackArtist)
        val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
        val imageButtonPlaylist: ImageButton = itemView.findViewById(R.id.imageButtonPlaylist)

    }
}
