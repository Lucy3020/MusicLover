package com.example.musiclovers.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.musiclovers.Services.DownloadImageTask;
import com.example.musiclovers.R;
import com.example.musiclovers.Models.Playlist;
import java.util.ArrayList;
/**
 * DONE
 */
public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private Context context;
    int layoutHolder, tvName, tvNumSongs, ivImage;

    public PlaylistAdapter(int layoutHolder, int tvName, int tvNumSongs, int ivImage, ArrayList<Playlist> playlistItems, Context context) {
        super(context, layoutHolder, playlistItems);
        this.context = context;
        this.tvName = tvName;
        this.tvNumSongs = tvNumSongs;
        this.ivImage = ivImage;
        this.layoutHolder = layoutHolder;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(layoutHolder, null, false);
        }
        String baseUrl = "http://10.0.2.2:3000/";
        Playlist playlist = getItem(position);
        TextView playlistName = convertView.findViewById(R.id.playlist_format_Name);
        TextView numSongs = convertView.findViewById(R.id.playlist_format_NumSongs);
        ImageView playlistImg = convertView.findViewById(R.id.playlist_format_Image);
        if(playlist != null){
            new DownloadImageTask(playlistImg).execute(baseUrl + playlist.getPlaylistImg());
            playlistName.setText(playlist.getPlaylistName());
            numSongs.setText(playlist.getNumSongs() + " Songs");
            playlistImg.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        }
        return convertView;
    }
}
