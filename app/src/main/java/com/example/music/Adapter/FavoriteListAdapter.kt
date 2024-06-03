package com.example.music.Adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.music.ApiInterface
import com.example.music.Data
import com.example.music.Database.DatabaseUserList
import com.example.music.MyData
import com.example.music.R
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FavoriteListAdapter(var context: Activity, var dataList: List<Data>, var favoriteTrackIDs: List<Long>) :
    RecyclerView.Adapter<FavoriteListAdapter.FavoriteListViewHolder>() {

    var apiInterface = Retrofit.Builder()
        .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    var favoriteTracks: MutableList<Data> = mutableListOf()

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        val databaseUserList = DatabaseUserList(context)
        val userID = getUserIdAfterLogin() // Hàm này để lấy userID sau khi đăng nhập

        if (userID != null) {
            // Lấy danh sách các ID bài hát yêu thích từ cơ sở dữ liệu SQLite
            val favoriteTrackIDs = databaseUserList.getUserFavoriteTracks(userID)

            // Nếu danh sách ID bài hát yêu thích không rỗng
            if (favoriteTrackIDs.isNotEmpty()) {
                val trackIDsString = favoriteTrackIDs.joinToString(",") // Chuyển danh sách ID bài hát thành chuỗi

                // Gọi API để lấy thông tin chi tiết của các bài hát yêu thích
                apiInterface.getData(trackIDsString).enqueue(object : Callback<MyData?> {
                    override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
                        if (response.isSuccessful && response.body() != null) {
                            favoriteTracks.addAll(response.body()!!.data)
                            notifyDataSetChanged() // Cập nhật RecyclerView khi có dữ liệu mới
                        } else {
                            Log.e("FavoriteListAdapter", "API response is not successful")
                        }
                    }

                    override fun onFailure(call: Call<MyData?>, t: Throwable) {
                        Log.e("FavoriteListAdapter", "API call failed", t)
                    }
                })
            }
        }
    }

    private fun getUserIdAfterLogin(): Long {
        val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        return sharedPref.getLong("userID", -1) // Trả về userID từ SharedPreferences, mặc định là -1 nếu không tìm thấy
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteListViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.layout_list_favorite, parent, false)
        return FavoriteListViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size

    }

    override fun onBindViewHolder(holder: FavoriteListViewHolder, position: Int) {
        val currentData = favoriteTracks[position]

        holder.trackName.text = currentData.title // Lấy tiêu đề của bài hát từ danh sách data
        holder.trackArtist.text = currentData.artist.name // Lấy tên nghệ sĩ từ danh sách data
        Picasso.get().load(currentData.album.cover_small).into(holder.trackImage) // Load hình ảnh bìa nhỏ từ danh sách data
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
