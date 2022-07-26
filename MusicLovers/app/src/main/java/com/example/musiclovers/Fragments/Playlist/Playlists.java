package com.example.musiclovers.Fragments.Playlist;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.musiclovers.MainActivity;
import com.example.musiclovers.Services.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.ListAdapter.PlaylistAdapter;
import com.example.musiclovers.Models.Playlist;
import com.example.musiclovers.SignInSignUp.SaveSharedPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
public class Playlists extends Fragment {

    public ArrayList<Playlist> playlistItems = new ArrayList<>();
    PlaceHolder placeHolder;
    PlaylistAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlists, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String base_Url = "http://10.0.2.2:3000/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
        Call<List<Playlist>> call = placeHolder.getPlaylistsByUser(SaveSharedPreference.getId(getContext()));
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(getContext(), "code: "+ response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                playlistItems = (ArrayList<Playlist>) response.body();
                ListView playlistListView = view.findViewById(R.id.fragment_playlists_ListView);
                adapter = new PlaylistAdapter(
                        R.layout.playlist_format,
                        R.id.playlist_format_Name,
                        R.id.playlist_format_NumSongs,
                        R.id.playlist_format_Image,
                        playlistItems,
                        getContext()
                );
                playlistListView.setAdapter(adapter);
                playlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        ViewModel viewModel = new ViewModelProvider((MainActivity)getContext()).get(ViewModel.class);
                        viewModel.select(playlistItems.get(i));
                        ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new PlaylistDetail())//
                                .addToBackStack(null)
                                .setReorderingAllowed(true)
                                .commit();
                    }
                });
                playlistListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Call<Void> call2Delete = placeHolder.deletePlaylist(playlistItems.get(i).get_id());
                        call2Delete.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()){
                                    playlistItems.remove(i);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(getContext(), "Playlist Deleted !", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getContext(), "Error 😥", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(getContext(), "Error 😥", Toast.LENGTH_LONG).show();
                            }
                        });
                        return true;
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        });
        FloatingActionButton btnCreateNewPlaylist = view.findViewById(R.id.fragment_playlists_CreateNewPlaylist);
        btnCreateNewPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_new_playlist);
                EditText etPlaylistName = dialog.findViewById(R.id.dialog_new_playlist_etPlaylistName);
                Button btnOke = dialog.findViewById(R.id.dialog_new_playlist_btnOke);
                Button btnCancel = dialog.findViewById(R.id.dialog_new_playlist_btnCancel);
                btnCancel.setOnClickListener(view1 -> dialog.dismiss());
                btnOke.setOnClickListener(view2 -> {
                    String playlistName = etPlaylistName.getText().toString();
                    if(playlistName.isEmpty()){
                        Toast.makeText(getContext(), "Invalid Name", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Call<Playlist> call = placeHolder.createPlaylist(playlistName, SaveSharedPreference.getId(getContext()));
                    call.enqueue(new Callback<Playlist>() {
                        @Override
                        public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                            if(response.isSuccessful()){
                                Playlist newPlaylist = (Playlist) response.body();
                                playlistItems.add(newPlaylist);
                                adapter.notifyDataSetChanged();
                            }else{
                                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Playlist> call, Throwable t) {
                            Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                        }
                    });
                    dialog.dismiss();
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
