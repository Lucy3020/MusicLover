package com.example.musiclovers.Fragments.PlayMusicView;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.musiclovers.MainActivity;
import com.example.musiclovers.Services.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.Models.Song;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class PlayMusic extends Fragment implements MainActivity.UpdateFragmentPlayMusic {
    String base_Url = "http://10.0.2.2:3000/";
    PlaceHolder placeHolder;
    ImageButton optionButton, btnPause_Start, btnNext, btnPrevious;
    TextView play_music_SongName, play_music_SingerName, play_music_SongEnd, play_music_SongStart;
    SeekBar songSeekBar, volumeSeekBar;
    ImageView play_music_SongImg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.play_music, container, false);
        play_music_SongName = v.findViewById(R.id.play_music_SongName);
        play_music_SingerName = v.findViewById(R.id.play_music_SingerName);
        play_music_SongImg = v.findViewById(R.id.play_music_SongImg);
        optionButton = v.findViewById(R.id.play_music_BtnOption);
        play_music_SongEnd = v.findViewById(R.id.play_music_SongEnd);
        play_music_SongStart = v.findViewById(R.id.play_music_SongStart);
        songSeekBar = v.findViewById(R.id.play_music_SeekbarSong);
        btnPause_Start = v.findViewById(R.id.play_music_BtnPlay);
        btnNext = v.findViewById(R.id.play_music_BtnForward);
        btnPrevious = v.findViewById(R.id.play_music_BtnBackward);
        volumeSeekBar = v.findViewById(R.id.play_music_SeekbarVolume);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initializeRetrofit();
        handlerBtnOption();
        btnPause_StartHandler();
        btnNext_PreviousHandler();
        volumeSeekBar();
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((MainActivity) getActivity()).mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }
    private void initializeRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_Url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolder = retrofit.create(PlaceHolder.class);
    }

    private void handlerBtnOption() {

        optionButton.setOnClickListener(new View.OnClickListener() {
            CountDownTimer countDownTimer1;
            @Override
            public void onClick(View view) {
                ArrayList<Song> songList = ((MainActivity) PlayMusic.this.getContext()).songList;
                int position = ((MainActivity) PlayMusic.this.getContext()).position;
                PopupMenu popup = new PopupMenu(PlayMusic.this.getContext(), view);
                popup.inflate(R.menu.play_music_option_menu);
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int time1 = 0;
                        switch (menuItem.getItemId()) {
                            case R.id.add_to_playlist_PlayMusic:
                                if (!songList.isEmpty()) {
                                    ((MainActivity) PlayMusic.this.getContext()).addSongToPlaylist(songList.get(position));
                                }
                                break;
                            case R.id.go_to_artist:
                                if (!songList.isEmpty()) {
                                    ((MainActivity) PlayMusic.this.getContext()).goToArtistDetail(songList.get(position));
                                    ((MainActivity) PlayMusic.this.getContext()).bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }
                                break;
                            case R.id.show_album:
                                if (!songList.isEmpty()) {
                                    ((MainActivity) PlayMusic.this.getContext()).goToAlbumDetail(songList.get(position));
                                    ((MainActivity) PlayMusic.this.getContext()).bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }
                                break;
                            case R.id.like:
                                //like song and add song to playlist [favorite songs]
                                if (!songList.isEmpty()) {
                                    ((MainActivity) PlayMusic.this.getContext()).loveMeOrNot(songList.get(position));
                                }
                                break;

                            case R.id.timer_15:
                                time1 = 15 * 60000;
                                break;
                            case R.id.timer_30:
                                time1 = 30 * 60000;
                                break;
                            case R.id.timer_45:
                                time1 = 45 * 60000;
                                break;
                            case R.id.timer_1:
                                time1 = 60 * 60000;
                                break;
                        }

                        if(time1 != 0){
                            if (countDownTimer1 != null) {
                                countDownTimer1.cancel();
                            }
                            if (time1 != 0) {
                                int finalTime = time1;
                                countDownTimer1 = new CountDownTimer(finalTime, 1000) {
                                    @Override
                                    public void onTick(long l) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        ((MainActivity)getContext()).onSongPause();
                                        ((MainActivity)getContext()).isRepeat = false;
                                    }
                                };
                                countDownTimer1.start();
                                sleepTimer(time1, countDownTimer1);
                            }
                        }
                        return true;
                    }
                });
            }
        });
    }

    void sleepTimer(int time, CountDownTimer countDownTimer){
        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext(), "notify_001");
        Intent ii = new Intent(getContext(), MainActivity.class)
                .setAction(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, ii, 0 | PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        Date date = new Date(new Date().getTime() + time);
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date);
        bigText.bigText("Music will turn off at "+ currentTime);
        bigText.setBigContentTitle("Sleep Timer is On 😪");
        bigText.setSummaryText("Delete this to Turn Sleep Timer Off");
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ((MainActivity)getContext()).isRepeat = false;
                countDownTimer.cancel();
                requireActivity().unregisterReceiver(this);
            }
        };
        Intent intent = new Intent("NOTIFICATION_DELETED_ACTION");
        PendingIntent pendingIntentDelete = PendingIntent.getBroadcast(getContext(), 0, intent, 0| PendingIntent.FLAG_IMMUTABLE);
        requireActivity().registerReceiver(receiver, new IntentFilter("NOTIFICATION_DELETED_ACTION"));

        mBuilder.setContentIntent(pendingIntent)
                .setDeleteIntent(pendingIntentDelete)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Your Title")
                .setContentText("Your text")
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(bigText);

        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(1, mBuilder.build());
        ((MainActivity)getContext()).isRepeat = true;
    }

    private void btnPause_StartHandler() {
        btnPause_Start.setOnClickListener(v -> {
            if (((MainActivity) getActivity()).mediaPlayer != null) {
                if (((MainActivity) getActivity()).mediaPlayer.isPlaying()) {
                    ((MainActivity) getActivity()).onSongPause();
                } else {
                    ((MainActivity) getActivity()).onSongPlay();
                }
            }
        });
    }

    private void btnNext_PreviousHandler(){
        btnPrevious.setOnClickListener(view -> {
            if(((MainActivity) getActivity()).mediaPlayer != null){
                ((MainActivity) getActivity()).onSongPrevious();
            }
        });
        btnNext.setOnClickListener(view -> {
            if(((MainActivity) getActivity()).mediaPlayer != null){
                ((MainActivity) getActivity()).onSongNext();
            }
        });
    }

    void volumeSeekBar(){
        int stream = AudioManager.STREAM_MUSIC;
        AudioManager audioManager = ((MainActivity) getContext()).audioManager;
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(stream));
        volumeSeekBar.setProgress(audioManager.getStreamVolume(stream));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);
            }

            public void onStartTrackingTouch(SeekBar bar) {}

            public void onStopTrackingTouch(SeekBar bar) {}
        });
    }

    @Override
    public void updateSong(String songName, String artistName) {
        play_music_SingerName.setText(artistName);
        play_music_SongName.setText(songName);
        play_music_SongImg.setImageBitmap(((MainActivity) getContext()).bmOnPlayingSong);
    }

    @Override
    public void updateBtnPlay(int btn) {
        btnPause_Start.setImageResource(btn);
    }

    @Override
    public void updateSongProgress(int progress) {
        songSeekBar.setProgress(progress);
    }

    @Override
    public void updateSongEnd(String time) {
        play_music_SongEnd.setText(time);
    }

    @Override
    public void updateSongStart(String time) {
        play_music_SongStart.setText(time);
    }

    @Override
    public void updateVolumeProgress(int progress) {
        volumeSeekBar.setProgress(progress);
    }

    @Override
    public void setMaxSongProgress(int max) {
        songSeekBar.setMax(max);
    }
}
