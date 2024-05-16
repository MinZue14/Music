package com.example.music.Adapter

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.music.Data
import com.example.music.Database.DatabaseUserList
import com.example.music.R
import com.squareup.picasso.Picasso

class TrackAdapter(var context:Activity, var dataList: List<Data>)
    :RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.admin_list_track, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        populate the data into the view
        val currentData = dataList[position]

        holder.tvtrackID.text = "ID: " + currentData.id.toString()
        holder.tvtrackName.text = "Tên bài hát: " + currentData.title
        holder.tvtrackArtist.text = "Ca sĩ:" + currentData.artist.name
        holder.tvtrackView.text = "Lượt xem: " + currentData.rank.toString()

        Picasso.get().load(currentData.album.cover).into(holder.trackImage);

        val MediaPlayer = MediaPlayer.create(context, currentData.preview.toUri())

        holder.imageButtonPlay.setOnClickListener {
            MediaPlayer.start()
        }

        holder.imageButtonPause.setOnClickListener {
            MediaPlayer.stop()
        }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // create the view in case the layout manager fails to create view for the data
        val trackImage: ImageView

        val tvtrackID: TextView
        val tvtrackName: TextView
        val tvtrackArtist: TextView
        val tvtrackView: TextView

        val imageButtonPlay: ImageButton
        val imageButtonPause: ImageButton

        init {
            trackImage = itemView.findViewById(R.id.trackImage)
            tvtrackID = itemView.findViewById(R.id.tvtrackID)
            tvtrackName = itemView.findViewById(R.id.tvtrackName)
            tvtrackArtist = itemView.findViewById(R.id.tvtrackArtist)
            tvtrackView = itemView.findViewById(R.id.tvtrackView)
            imageButtonPlay = itemView.findViewById(R.id.imageButtonPlay)
            imageButtonPause = itemView.findViewById(R.id.imageButtonPause)
        }
    }

    /////////////// list avatar artist /////////////////
    class SlideAdapter(var context: Activity, var dataList: List<Data>) :
        RecyclerView.Adapter<SlideAdapter.SlideViewHolder>() {

        // Khởi tạo một HashSet để lưu trữ các ID của nghệ sĩ đã xuất hiện
        private val artistIds = HashSet<Int>()

        // Danh sách dữ liệu đã lọc, mỗi ca sĩ chỉ xuất hiện một lần
        private val filteredDataList = ArrayList<Data>()

        init {
            // Lọc dữ liệu khi khởi tạo Adapter
            filterData()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
            val itemView =
                LayoutInflater.from(context).inflate(R.layout.layout_list_avatar, parent, false)
            return SlideViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return filteredDataList.size
        }

        override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
            val currentData = filteredDataList[position]

            holder.artistName.text = currentData.artist.name

            Picasso.get().load(currentData.artist.picture_small.toUri()).into(holder.artistImage);

            holder.artistImage.setOnClickListener {
                // Do something when artist image is clicked
            }
        }

        // Phương thức này được sử dụng để lọc dữ liệu, chỉ lấy mỗi ca sĩ một lần
        private fun filterData() {
            for (data in dataList) {
                if (!artistIds.contains(data.artist.id)) {
                    filteredDataList.add(data)
                    artistIds.add(data.artist.id)
                }
            }
        }

        class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val artistImage: ImageView = itemView.findViewById(R.id.artistImage)
            val artistName: TextView = itemView.findViewById(R.id.artistName)
        }
    }

    /////////////// list album   /////////////////
    class AlbumAdapter(var context: Activity, var dataList: List<Data>) :
        RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
        // Khởi tạo một HashSet để lưu trữ các ID của album đã xuất hiện
        private val albumIds = HashSet<Int>()

        // Danh sách dữ liệu đã lọc, mỗi ca sĩ chỉ xuất hiện một lần
        private val filteredDataList = ArrayList<Data>()

        init {
            // Lọc dữ liệu khi khởi tạo Adapter
            filterData()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
            val itemView =
                LayoutInflater.from(context).inflate(R.layout.layout_list_album, parent, false)
            return AlbumViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return minOf(10, dataList.size)
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            val currentData = dataList[position]

            holder.albumName.text = currentData.album.title

            Picasso.get().load(currentData.album.cover_medium.toUri()).into(holder.albumImage);

            holder.albumName.setOnClickListener {
                // Do something when artist image is clicked
            }
        }
        // Phương thức này được sử dụng để lọc dữ liệu, chỉ lấy mỗi album 1 lần
        private fun filterData() {
            for (data in dataList) {
                if (!albumIds.contains(data.album.id)) {
                    filteredDataList.add(data)
                    albumIds.add(data.album.id)
                }
            }
        }

        class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val albumImage: ImageView = itemView.findViewById(R.id.albumImage)
            val albumName: TextView = itemView.findViewById(R.id.albumName)
        }
    }

    /////////////// list nhạc /////////////////
    class trackListAdapter(var context: Activity, var dataList: List<Data>) :
        RecyclerView.Adapter<trackListAdapter.trackListViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): trackListViewHolder {
            val itemView =
                LayoutInflater.from(context).inflate(R.layout.layout_list_track, parent, false)
            return trackListViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return minOf(10, dataList.size)

        }

        override fun onBindViewHolder(holder: trackListViewHolder, position: Int) {
            val currentData = dataList[position]

            holder.trackName.text = currentData.title
            holder.trackArtist.text = currentData.artist.name
            holder.trackView.text = currentData.rank.toString() + " views"

            Picasso.get().load(currentData.album.cover).into(holder.trackImage);

            // Biến để theo dõi trạng thái của buttonLove và buttonPlaylist
            var isLoveSelected = false
            var isPlaylistSelected = false

            // Thiết lập sự kiện click cho thêm Playlist
            holder.imageButtonPlaylist.setOnClickListener {
                if (!isPlaylistSelected) {
                    // Hiển thị Toast
                    Toast.makeText(context, "Đã thêm vào playlist", Toast.LENGTH_SHORT).show()

                    // Thay đổi hình ảnh của buttonPlaylist thành buttonAdd
                    holder.imageButtonPlaylist.setImageResource(R.drawable.baseline_playlist_add_check)

                    // Đánh dấu là đã được chọn
                    isPlaylistSelected = true
                } else {
                    // Đặt lại hình ảnh ban đầu của buttonPlaylist
                    holder.imageButtonPlaylist.setImageResource(R.drawable.baseline_genre)
                    Toast.makeText(context, "Hủy thêm vào playlist", Toast.LENGTH_SHORT).show()

                    // Đánh dấu là chưa được chọn
                    isPlaylistSelected = false
                }
            }

            // Thiết lập sự kiện click cho buttonLove
            holder.imageButtonLove.setOnClickListener {
                val dbManager = DatabaseUserList(context)

                // Lấy userID từ SharedPreferences
                val sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
                val userIDString = sharedPref.getString("userID", null)
                val userID = userIDString?.toLongOrNull() ?: -1L // Chuyển đổi thành Long hoặc gán mặc định là -1L nếu không thành công

                // Lấy trackID từ dữ liệu hiện tại trong RecyclerView
                val currentData = dataList[position]
                val trackID = currentData.id

                if (!isLoveSelected) {
                    // Thêm bài hát vào danh sách yêu thích của người dùng
                    val isSuccess = dbManager.addTrackToFavorite(userID, trackID)
                    if (isSuccess) {
                        Toast.makeText(context, "Bạn đã yêu thích bài hát này", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Không thể thêm bài hát vào danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    }

                    // Thay đổi hình ảnh của buttonLove thành btnLoveRed
                    holder.imageButtonLove.setImageResource(R.drawable.baseline_favorite_red)

                    // Đánh dấu là đã được chọn
                    isLoveSelected = true
                } else {
                    // Xóa bài hát khỏi danh sách yêu thích của người dùng
                    val isSuccess = dbManager.removeTrackFromFavorite(userID, trackID)
                    if (isSuccess) {
                        Toast.makeText(context, "Bạn hong yêu thích bài này", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Không thể xóa bài hát khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show()
                    }

                    // Đặt lại hình ảnh ban đầu của buttonLove
                    holder.imageButtonLove.setImageResource(R.drawable.baseline_favorite)

                    // Đánh dấu là chưa được chọn
                    isLoveSelected = false
                }
            }
        }

        class trackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
}