package com.example.music.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Data
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
    }

    class FavoriteListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trackName: TextView = itemView.findViewById(R.id.trackName)
        val trackArtist: TextView = itemView.findViewById(R.id.trackArtist)
        val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    }
}
