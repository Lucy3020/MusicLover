package com.example.musiclovers.ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.Services.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.R;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.Fragments.Artist.ArtistDetail;
import com.example.musiclovers.Models.Artist;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
/**
 * DONE
 */
public class ArtistsAdapter extends RecyclerView.Adapter<ArtistsAdapter.ViewHolder> {
    private int layoutHolder, tvName, CImage;
    private ArrayList<Artist> artistItems;
    private Context context;
    private ViewModel viewModel;

    public ArtistsAdapter(int layoutHolder, int tvName, int CImage, ArrayList<Artist> artistItems, Context context) {
        this.layoutHolder = layoutHolder;
        this.tvName = tvName;
        this.CImage = CImage;
        this.artistItems = artistItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutHolder, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistsAdapter.ViewHolder holder, int position) {
        Artist currentArtist = artistItems.get(position);
        String base_url = "http://10.0.2.2:3000/";
        new DownloadImageTask(holder.artistImg).execute(base_url+currentArtist.getArtistImg());
        holder.artistName.setText(currentArtist.getArtistName());
        holder.artistImg.setAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_transition_animation));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel = new ViewModelProvider((MainActivity) context).get(ViewModel.class);
                viewModel.select(currentArtist);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new ArtistDetail())
                        .addToBackStack(null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return artistItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView artistImg;
        TextView artistName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImg = itemView.findViewById(CImage);
            artistName = itemView.findViewById(tvName);
        }
    }
}
