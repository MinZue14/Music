package com.example.music

import android.app.Activity
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class TrackAdapter(var context:Activity, var dataList: List<Data>)
    :RecyclerView.Adapter<TrackAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.layout_listtrack, parent, false)
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

        holder.imageButtonPlay.setOnClickListener{
            MediaPlayer.start()
        }

        holder.imageButtonPause.setOnClickListener{
            MediaPlayer.stop()
        }

    }

    class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        // create the view in case the layout manager fails to create view for the data
        val trackImage: ImageView

        val tvtrackID:TextView
        val tvtrackName:TextView
        val tvtrackArtist:TextView
        val tvtrackView:TextView

        val imageButtonPlay:ImageButton
        val imageButtonPause:ImageButton

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

}