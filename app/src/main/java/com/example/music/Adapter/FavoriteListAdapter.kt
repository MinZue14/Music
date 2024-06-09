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
import com.example.music.R
import com.squareup.picasso.Picasso

class FavoriteListAdapter(
    private val context: Activity,
    private val dataList: List<Data>
) : RecyclerView.Adapter<FavoriteListAdapter.FavoriteListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_list_favorite, parent, false)
        return FavoriteListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        val currentData = dataList[position] // Lấy dữ liệu của bài hát yêu thích hiện tại từ danh sách

        // Hiển thị thông tin chi tiết của bài hát trong RecyclerView
        holder.trackName.text = currentData.title // Hiển thị tiêu đề của bài hát
        holder.trackArtist.text = currentData.artist.name // Hiển thị tên nghệ sĩ

        Picasso.get().load(currentData.album.cover).into(holder.trackImage);

        // Thiết lập sự kiện click cho nút yêu thích
        holder.imageButtonLove.setOnClickListener {
            val dbManager = DatabaseUserList(context)

            // Lấy userID từ SharedPreferences
            val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
            val userIDString = sharedPref.getString("userID", null)
            val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

            // Kiểm tra xem user đã đăng nhập chưa
            if (userID != -1L) {
                val trackID = currentData.id
                val isSuccess = dbManager.removeTrackFromFavorite(userID, trackID)

                if (isSuccess) {
                    Toast.makeText(context, "Đã xóa bài hát khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    // Xóa bài hát khỏi danh sách hiện tại và cập nhật RecyclerView
                    val newList = dataList.toMutableList()
                    newList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                } else {
                    Toast.makeText(context, "Không thể xóa bài hát khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Vui lòng đăng nhập để thực hiện chức năng này", Toast.LENGTH_SHORT).show()
            }
        }
    }


    class FavoriteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackName: TextView = itemView.findViewById(R.id.trackName)
        val trackArtist: TextView = itemView.findViewById(R.id.trackArtist)
        val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
        val imageButtonLove: ImageButton = itemView.findViewById(R.id.imageButtonLove)

    }
}
