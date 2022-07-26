package com.example.musiclovers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musiclovers.Fragments.Album.AlbumDetail;
import com.example.musiclovers.Fragments.Artist.ArtistDetail;
import com.example.musiclovers.Fragments.Main.Browse;
import com.example.musiclovers.Fragments.Main.Library;
import com.example.musiclovers.Fragments.Main.ListenNow;
import com.example.musiclovers.Fragments.PlayMusicView.LyricsFragment;
import com.example.musiclovers.Fragments.PlayMusicView.PlayMusic;
import com.example.musiclovers.Fragments.PlayMusicView.PlayingNext;
import com.example.musiclovers.Fragments.Main.Search;
import com.example.musiclovers.ListAdapter.PlaylistAdapter;
import com.example.musiclovers.Models.Album;
import com.example.musiclovers.Models.Artist;
import com.example.musiclovers.Models.Playlist;
import com.example.musiclovers.Models.Song;
import com.example.musiclovers.Services.DownloadImageTask;
import com.example.musiclovers.Services.PlaceHolder;
import com.example.musiclovers.Services.Playable;
import com.example.musiclovers.Services.ViewModel;
import com.example.musiclovers.Services.Notification.CreateNotification;
import com.example.musiclovers.SignInSignUp.SaveSharedPreference;
import com.example.musiclovers.SignInSignUp.loginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class MainActivity extends AppCompatActivity implements Playable {
    /* Declare */
    String base_Url = "http://10.0.2.2:3000/";
    public ArrayList<Song> songList = new ArrayList<>();
    public MediaPlayer mediaPlayer;
    public Handler handler = new Handler();
    PlaceHolder placeHolder;
    public int position = 0;
    boolean nextSong = false;
    BottomNavigationView bottomNavigationView;
    public BottomSheetBehavior bottomSheetBehavior;
    LinearLayout tab_song_container;
    NotificationManager manager;
    public AudioManager audioManager;
    public boolean isRepeat = false;
    public Bitmap bmOnPlayingSong;

    //interface - using to update fragment
    public static UpdateFragmentPlayMusic updateFragmentPlayMusic;
    public static UpdateFragmentPlayingNext updateFragmentPlayingNext;
    public static UpdateFragmentLyrics updateFragmentLyrics;
    //fragment song_tab
    TextView song_tab_SongName;
    ImageView song_tab_SongImg;
    ImageButton song_tab_btnPause_Start, song_tab_btnNext;

    //ViewPager - using to create view that slide left and right
    private ViewPager2 mPager;
    public static Fragment play_music, playingNext, lyrics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById();
        initializeRetrofit();
        setUpVolumeSeekBar();
        tabSong_playMusicFragment();
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListenNow()).commit();
        NavigationBar();
        createNotificationChannel();
        viewPagerInit();

        btnPause_StartHandler();
        btnNext_PreviousHandler();
    }

    @Override
    protected void onStart() {
        super.onStart();
        logout();
    }

    public void logout(){
        if(SaveSharedPreference.getUserName(this).isEmpty()) {
            Intent login = new Intent(this, loginActivity.class);
            startActivity(login);
            finish();
        }
    }

    @Override
    protected void onResume() {
        if(updateFragmentPlayMusic != null)
            updateFragmentPlayMusic.updateVolumeProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        super.onResume();
    }

    private void findViewById() {
        tab_song_container = findViewById(R.id.song_tab_container);
        song_tab_SongName = findViewById(R.id.song_tab_SongName);
        song_tab_SongImg = findViewById(R.id.song_tab_SongImg);
        song_tab_btnPause_Start = findViewById(R.id.img_btn_song_tab_Play);
        song_tab_btnNext = findViewById(R.id.img_btn_song_tab_Forward);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mPager = findViewById(R.id.pager);
    }

    private void initializeRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
    }

    private void viewPagerInit(){
        mPager = findViewById(R.id.pager);
        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(this);
        mPager.setAdapter(pagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setOffscreenPageLimit(3);
        View v2 =  mPager.getChildAt(0);
        if (v2 != null) {
            v2.setNestedScrollingEnabled(false);
        }

    }

    private static class ScreenSlidePagerAdapter extends FragmentStateAdapter {
        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int p) {
            if (p == 0) {
                playingNext = new PlayingNext();
                updateFragmentPlayingNext = (UpdateFragmentPlayingNext) playingNext;
                return playingNext;
            }
                if (p == 1) {
                play_music = new PlayMusic();
                updateFragmentPlayMusic = (UpdateFragmentPlayMusic) play_music;
                return play_music;
            }
                if (p == 2){
                lyrics =  new LyricsFragment();
                updateFragmentLyrics = (UpdateFragmentLyrics) lyrics;
                return lyrics;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    private void tabSong_playMusicFragment() {
        /* Show up play_music bottom sheet */
        tab_song_container.setOnClickListener(view -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
        bottomSheetBehavior = BottomSheetBehavior.from(mPager);
        /* Show & Hide song_tab */
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0.15) {
                    tab_song_container.setVisibility(View.GONE);
                } else if (slideOffset <= 0.15) {
                    tab_song_container.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void goToAlbumDetail(Song songItem) {
        Call<Album> call1 = placeHolder.getAlbum(songItem.getAlbumId());
        call1.enqueue(new Callback<Album>() {
            @Override
            public void onResponse(@NonNull Call<Album> call, @NonNull Response<Album> response) {
                if (response.isSuccessful()) {
                    Album album = response.body();
                    ViewModel viewModel = new ViewModelProvider(MainActivity.this).get(ViewModel.class);
                    viewModel.select(album);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new AlbumDetail())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Album> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void goToArtistDetail(Song songItem){
        String[] artistId = songItem.getArtistId();
        Call<Artist> call = placeHolder.getArtist(artistId[0], SaveSharedPreference.getId(this));
        call.enqueue(new Callback<Artist>() {
            @Override
            public void onResponse(@NonNull Call<Artist> call, @NonNull Response<Artist> response) {
                if(response.isSuccessful()){
                    Artist artist = response.body();
                    ViewModel viewModel = new ViewModelProvider(MainActivity.this).get(ViewModel.class);
                    viewModel.select(artist);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ArtistDetail())
                            .addToBackStack(null)
                            .setReorderingAllowed(true)
                            .commit();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Artist> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void getSongs(ArrayList<Song> songItems, int current) {
        songList.clear();
        position = 0;
        for(int i = current; i < songItems.size(); i++){
            songList.add(songItems.get(i));
        }
        updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
        prepareSongAndPlay();
    }

    public void loveMeOrNot(Song song) {
        Call<Void> call = placeHolder.likeSong(SaveSharedPreference.getId(this), song.get_id());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if(response.isSuccessful()){
                    boolean loved = response.code() == 201;
                    int ic_image;
                    String message;
                    if(loved){
                        ic_image = R.drawable.ic_fill_heart;
                        message = "Add to Favorite List";
                    }else{
                        ic_image = R.drawable.ic_unfill_heart;
                        message = "Remove from Favorite List";
                    }
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_custom, findViewById(R.id.toast_layout_root));
                    ImageView image = (ImageView) layout.findViewById(R.id.toast_image);
                    image.setImageResource(ic_image);
                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText(message);
                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }else{
                    Toast.makeText(getApplicationContext(), "code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), "code: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addSongToPlaylist(Song song){

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_playlists);
        ListView listView = dialog.findViewById(R.id.dialog_playlist_ListView);
        //get playlists
        Call<List<Playlist>> call = placeHolder.getPlaylistByUser_PlaylistNum(SaveSharedPreference.getId(this), 2);
        call.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                if(response.isSuccessful()){
                    ArrayList<Playlist> playlistItems = (ArrayList<Playlist>) response.body();
                    if(playlistItems.isEmpty()){
                        Toast.makeText(MainActivity.this, "No playlist to add !! \n Please create a playlist first", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }
                    PlaylistAdapter adapter = new PlaylistAdapter(
                            R.layout.playlist_format,
                            R.id.playlist_format_Name,
                            R.id.playlist_format_NumSongs,
                            R.id.playlist_format_Image,
                            playlistItems,
                            getApplicationContext()
                    );
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //add song to playlist
                            Call<Void> addSongToPlaylist = placeHolder.addSongToPlaylist(playlistItems.get(i).get_id(), song.get_id());
                            addSongToPlaylist.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(response.code() == 200){
                                        Toast.makeText(MainActivity.this, "Song added !!", Toast.LENGTH_LONG).show();
                                    }else if(response.code() == 201){
                                        Toast.makeText(MainActivity.this, "Song is Already in the Playlist !!", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Sorry \n Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(MainActivity.this, "Sorry \n Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Playlist>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Sorry \n Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void NavigationBar() {
        //load Listen Now when open app
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.Listen_Now_page:
                    selectedFragment = new ListenNow();
                    break;
                case R.id.Browse_page:
                    selectedFragment = new Browse();
                    break;
                case R.id.Library_page:
                    selectedFragment = new Library();
                    break;
                case R.id.Search_page:
                    selectedFragment = new Search();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        });
        bottomNavigationView.setOnItemReselectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.Listen_Now_page:
                    selectedFragment = new ListenNow();
                    break;
                case R.id.Browse_page:
                    selectedFragment = new Browse();
                    break;
                case R.id.Library_page:
                    selectedFragment = new Library();
                    break;
                case R.id.Search_page:
                    selectedFragment = new Search();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        });
    }

    private void btnPause_StartHandler() {
        song_tab_btnPause_Start.setOnClickListener(view -> {
            if(mediaPlayer != null){
                if (mediaPlayer.isPlaying()) {
                    onSongPause();
                } else {
                    onSongPlay();
                }
            }
        });
    }

    private void btnNext_PreviousHandler() {
        song_tab_btnNext.setOnClickListener(view -> {
            if(mediaPlayer!=null){
                onSongNext();
            }
        });
    }

    private void prepareSongAndPlay() {
        Song song = songList.get(position);
        song_tab_SongName.setText(song.getSongName() + " - " + song.getArtistName());
        song_tab_SongName.setSelected(true);
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_pause);

        if(mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }else {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            if(song.get_id() == null){ //offline
                mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(song.getSongSrc()));
                FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
                retriever.setDataSource(song.getSongImg());
                byte [] data = retriever.getEmbeddedPicture();
                if (data != null){
                    bmOnPlayingSong = BitmapFactory.decodeByteArray(data, 0, data.length);
                }else{ //.mp3 file doesn't have meta data like image, i.e
                    bmOnPlayingSong = BitmapFactory.decodeResource(getResources(), R.drawable.ic_music); //it not working
                }
                retriever.release();
                song_tab_SongImg.setImageBitmap(bmOnPlayingSong);
            }else{ //online
                mediaPlayer.setDataSource(base_Url + song.getSongSrc());
                mediaPlayer.prepare();
                bmOnPlayingSong = new DownloadImageTask(song_tab_SongImg).execute(base_Url + song.getSongImg()).get();
                updateFragmentLyrics.getData(song.get_id(), placeHolder, mediaPlayer);

            }
            updateFragmentPlayMusic.updateSong(song.getSongName(), song.getArtistName());
            mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                mediaPlayer.stop();
                nextSong = true;
            });
            mediaPlayer.start();
            recentlyPlayed(song.get_id());
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        timerSetting();
        updateSongSeekBar();
        new CreateNotification(this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_pause,
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());

        handler.postDelayed(playingOrder, 500);
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_pause);
    }

    void recentlyPlayed(String songId){
        Call<Void> call = placeHolder.addSong2RecentList(SaveSharedPreference.getId(this), songId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void songPlayback() { // finish playing row, move back to the begin.
        try { mediaPlayer.prepare(); } catch (IOException e) { e.printStackTrace(); }
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_play_music);
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_play_music);
        handler.removeCallbacks(update);
        updateFragmentPlayMusic.updateSongProgress(0);
        updateFragmentPlayMusic.updateSongStart(new SimpleDateFormat("mm:ss").format(0));
        new CreateNotification(this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_play_music,
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());
    }

    private void setUpVolumeSeekBar() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            updateFragmentPlayMusic.updateVolumeProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) + 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            updateFragmentPlayMusic.updateVolumeProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createNotificationChannel() {
        registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    CreateNotification.CHANNEL_ID_1,
                    CreateNotification.CHANNEL_NAME_1,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
            channel.enableLights(true);
            manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public Runnable update = new Runnable() { //update running time of the song that is playing, the seek is also call up to update in this function.
        @Override
        public void run() {
            updateFragmentPlayMusic.updateSongStart(new SimpleDateFormat("mm:ss").format(mediaPlayer.getCurrentPosition()));
            updateSongSeekBar();
        }
    };

    Runnable playingOrder = new Runnable() { //define
        @Override
        public void run() {
            if (nextSong) {
                position++;
                if(isRepeat){
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        handler.removeCallbacks(update);
                        updateFragmentPlayMusic.updateSongProgress(0);
                        updateFragmentPlayMusic.updateSongStart(new SimpleDateFormat("mm:ss").format(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    updateFragmentPlayingNext.updateDisplayNextSongsList(true, false, null);
                    if (position == (songList.size())) {
                        position = 0;
                        //nếu mảng bài hát chỉ gồm 1 phần tử thì chỉ cần prepare không cần tải lại
                        if (songList.size() == 1) {
                            songPlayback();
                        } else {
                            mediaPlayer.reset();
                            prepareSongAndPlay();
                            updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
                            updateFragmentPlayMusic.updateSongProgress(0);
                            onSongPause();
                        }
                    } else {
                        mediaPlayer.reset();
                        prepareSongAndPlay();
                    }
                }
                nextSong = false;
            } else {
                handler.postDelayed(this, 300);
            }
        }
    };

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");

            switch (action) {
                case CreateNotification.ACTION_PREVIOUS:
                    onSongPrevious();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mediaPlayer.isPlaying()) {
                        onSongPause();
                    } else {
                        onSongPlay();
                    }
                    break;
                case CreateNotification.ACTION_NEXT:
                    onSongNext();
                    break;
                case CreateNotification.ACTION_LIKE:
                    onLikeSong();
                    break;
            }
        }
    };

    @Override
    public void onSongPrevious() {
        if(position == 0 || mediaPlayer.getCurrentPosition() > 5000) {
            mediaPlayer.stop();
            try { mediaPlayer.prepare(); } catch (IOException e) { e.printStackTrace(); }
            mediaPlayer.start();
        }
        else {

            updateFragmentPlayingNext.updateDisplayNextSongsList(false, true, songList.get(position));
            position--;
            prepareSongAndPlay();
        }
    }

    @Override
    public void onSongPlay() {
        mediaPlayer.start();
        updateSongSeekBar();
        song_tab_btnPause_Start.setImageResource(R.drawable.ic_pause);
        updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_pause);
        new CreateNotification(MainActivity.this,
                songList.get(position).getSongName(),
                songList.get(position).getArtistName(),
                R.drawable.ic_pause, /* switch to pause */
                position,
                songList.size())
                .execute(base_Url + songList.get(position).getSongImg());
        updateSongSeekBar();
    }

    @Override
    public void onSongPause() {
        if(mediaPlayer!=null){
            handler.removeCallbacks(update);
            mediaPlayer.pause();
            song_tab_btnPause_Start.setImageResource(R.drawable.ic_play_music);
            updateFragmentPlayMusic.updateBtnPlay(R.drawable.ic_play_music);
            new CreateNotification(MainActivity.this,
                    songList.get(position).getSongName(),
                    songList.get(position).getArtistName(),
                    R.drawable.ic_play_music,
                    position,
                    songList.size())
                    .execute(base_Url + songList.get(position).getSongImg());
        }
    }

    @Override
    public void onSongNext() {
        if (position == songList.size() - 1) {
            position = 0;
            if(songList.size() == 1) {
                mediaPlayer.stop();
                songPlayback();
            }
            else {
                mediaPlayer.reset();
                prepareSongAndPlay();
                updateFragmentPlayingNext.initDisplayNextSongsList(songList, position);
                updateFragmentPlayMusic.updateSongProgress(0);
                onSongPause();
            }
        }
        else {
            updateFragmentPlayingNext.updateDisplayNextSongsList(true, false, null);
            position++;
            prepareSongAndPlay();
        }
    }

    @Override
    public void onLikeSong() {
        loveMeOrNot(songList.get(position));
    }

    private void timerSetting() {
        updateFragmentPlayMusic.updateSongEnd(new SimpleDateFormat("mm:ss").format(mediaPlayer.getDuration()));
        updateFragmentPlayMusic.setMaxSongProgress(mediaPlayer.getDuration());
    }

    private void updateSongSeekBar() {
        if (mediaPlayer.isPlaying()) {
            updateFragmentPlayMusic.updateSongProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(update,300);
        }
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            manager.cancelAll();
        }
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
        }
        unregisterReceiver(broadcastReceiver);
    }

    public interface UpdateFragmentPlayMusic{
        void updateSong(String songName, String artistName);
        void updateSongProgress(int progress);
        void updateBtnPlay(int btn);
        void updateSongEnd(String time);
        void updateSongStart(String time);
        void updateVolumeProgress(int progress);
        void setMaxSongProgress(int max);
    }

    public interface UpdateFragmentPlayingNext{
        void initDisplayNextSongsList(ArrayList<Song> songsList, int position);
        void updateDisplayNextSongsList(Boolean isNext, Boolean isPrevious, Song song);
    }

    public interface UpdateFragmentLyrics{
        void getData(String songId, PlaceHolder placeHolder, MediaPlayer mediaPlayer);
    }
}
