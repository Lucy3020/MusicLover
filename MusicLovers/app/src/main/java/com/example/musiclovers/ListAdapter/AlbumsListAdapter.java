package com.example.musiclovers.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musiclovers.Services.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.Fragments.Album.AlbumDetail;
import com.example.musiclovers.Models.Album;

import java.util.ArrayList;

/**DONE
 *
 */
public class AlbumsListAdapter extends RecyclerView.Adapter<AlbumsListAdapter.ViewHolder> {

    private int layoutHolder, tvName, tvArtist, ivImage;
    private ArrayList<Album> albumItems;
    private Context context;
    private ViewModel viewModel;

    public AlbumsListAdapter(int layoutHolder, int tvName, int tvArtist, int ivImage, ArrayList<Album> albumItems, Context context) {
        this.layoutHolder = layoutHolder;
        this.tvName = tvName;
        this.tvArtist = tvArtist;
        this.ivImage = ivImage;
        this.albumItems = albumItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutHolder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album currentAlbum = albumItems.get(position);
        String base_url = "http://10.0.2.2:3000/";
        new DownloadImageTask(holder.image).execute(base_url + currentAlbum.getAlbumImg());
        holder.artistName.setText(currentAlbum.getArtistName());
        holder.albumName.setText(currentAlbum.getAlbumName());
        holder.image.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel = new ViewModelProvider((MainActivity) context).get(ViewModel.class);
                viewModel.select(currentAlbum);
                ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new AlbumDetail())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView albumName;
        TextView artistName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumName = itemView.findViewById(tvName);
            artistName = itemView.findViewById(tvArtist);
            image = itemView.findViewById(ivImage);
        }
    }
}
