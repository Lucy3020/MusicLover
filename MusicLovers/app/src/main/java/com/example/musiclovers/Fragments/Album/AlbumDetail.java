package com.example.musiclovers.Fragments.Album;

import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musiclovers.Services.DownloadImageTask;
import com.example.musiclovers.MainActivity;
import com.example.musiclovers.Services.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.ListAdapter.SongsListAdapter;
import com.example.musiclovers.Models.Song;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class AlbumDetail extends Fragment {
    private RecyclerView mRecyclerView;
    private SongsListAdapter mAdapter;
    private PlaceHolder placeHolder;
    private ViewModel viewModel;
    private TextView artistName;
    private ExtendedFloatingActionButton btnPlayAll;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_album_detail, container, false);
        artistName = v.findViewById(R.id.fragment_album_detail_ArtistName);
        btnPlayAll = v.findViewById(R.id.fragment_album_detail_PLayBtn);
        collapsingToolbarLayout = v.findViewById(R.id.fragment_album_detail_CollapsingToolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        viewModel = new ViewModelProvider(getActivity()).get(ViewModel.class);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Passing data from Browse Fragment
        viewModel.getSelectedAlbum().observe(getViewLifecycleOwner(), album -> {
            CharSequence sequence = (album.getAlbumName());
            artistName.setText(album.getArtistName());
            collapsingToolbarLayout.setTitle(sequence);
            ImageView albumImg = view.findViewById(R.id.fragment_album_detail_AlbumImg);
            mRecyclerView = view.findViewById(R.id.fragment_album_detail_AlbumRecycleView);
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            String base_Url = "http://10.0.2.2:3000/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(base_Url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            placeHolder = retrofit.create(PlaceHolder.class);
            new DownloadImageTask(albumImg).execute(base_Url + album.getAlbumImg());
            //Loading Album Songs
            Call<List<Song>> call = placeHolder.getSongsByAlbum(album.get_id());
            call.enqueue(new Callback<List<Song>>() {
                @Override
                public void onResponse(@NonNull Call<List<Song>> call, @NonNull Response<List<Song>> response) {
                    if(!response.isSuccessful()) {
                        Toast.makeText(getContext(), "code: "+ response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<Song> songItems = (ArrayList<Song>) response.body();

                    mAdapter = new SongsListAdapter(
                            R.layout.song_format,
                            R.id.song_format_SongName,
                            R.id.song_format_ArtistName,
                            R.id.song_format_SongImg,
                            songItems,
                            3, /* add song to playing next & playlist AVAILABLE */
                            getContext());
                    mRecyclerView.setAdapter(mAdapter);

                    btnPlayAll.setOnClickListener(view1 -> ((MainActivity)getActivity()).getSongs(songItems, 0));
                }
                @Override
                public void onFailure(@NonNull Call<List<Song>> call, Throwable t) {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
