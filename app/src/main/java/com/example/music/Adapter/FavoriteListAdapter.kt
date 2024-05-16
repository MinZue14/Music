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

class FavoriteListAdapter(var context: Activity, var dataList: List<Data>) :
    RecyclerView.Adapter<FavoriteListAdapter.FavoriteListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.layout_list_favorite, parent, false)
        return FavoriteListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size

    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        val currentData = dataList[position]

        holder.trackName.text = currentData.title
        holder.trackArtist.text = currentData.artist.name

        Picasso.get().load(currentData.album.cover_small).into(holder.trackImage);

        // Lấy userID từ SharedPreferences
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val userIDString = sharedPref.getString("userID", null)
        val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

        // Lấy trackID từ dữ liệu hiện tại trong RecyclerView
        val trackID = currentData.id

        // Thiết lập sự kiện click cho buttonLove
        holder.imageButtonLove.setOnClickListener {
            val dbManager = DatabaseUserList(context)

            if (dbManager.isTrackInFavorites(userID, trackID)) {
                // Xóa bài hát khỏi danh sách yêu thích của người dùng
                val isSuccess = dbManager.removeTrackFromFavorite(userID, trackID)
                if (isSuccess) {
                    Toast.makeText(context, "Bạn hong yêu thích bài này", Toast.LENGTH_SHORT).show()
                    // Xóa bài hát khỏi danh sách hiển thị trên RecyclerView
                    dataList = dataList.filterNot { it.id == trackID }
                    notifyDataSetChanged() // Cập nhật lại RecyclerView
                } else {
                    Toast.makeText(context, "Không thể xóa bài hát khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class FavoriteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackImage: ImageView

        val trackName: TextView
        val trackArtist: TextView

        val imageButtonLove: ImageButton

        init {
            trackImage = itemView.findViewById(R.id.trackImage)
            trackName = itemView.findViewById(R.id.trackName)
            trackArtist = itemView.findViewById(R.id.trackArtist)
            imageButtonLove = itemView.findViewById(R.id.imageButtonLove)
        }
    }
}
