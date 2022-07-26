package com.example.musiclovers.Services;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.musiclovers.Models.Album;
import com.example.musiclovers.Models.Artist;
import com.example.musiclovers.Models.Genre;
import com.example.musiclovers.Models.Playlist;

/**
 * DONE
 */
public class ViewModel extends androidx.lifecycle.ViewModel {
    // TODO: Implement the ViewModel -> communicate between fragments
    //ALBUM______________
    private final MutableLiveData<Album> selectedAlbum = new MutableLiveData<Album>();
    public void select(Album album) {
        selectedAlbum.setValue(album);
    }
    public LiveData<Album> getSelectedAlbum() {
        return selectedAlbum;
    }

    //PLAYLIST______________
    private final MutableLiveData<Playlist> selectedPlaylist = new MutableLiveData<Playlist>();
    public void select(Playlist playlistItem) {
        selectedPlaylist.setValue(playlistItem);
    }
    public LiveData<Playlist> getSelectedPlaylist() {
        return selectedPlaylist;
    }

    //ARTIST______________
    private final MutableLiveData<Artist> selectedArtist = new MutableLiveData<Artist>();
    public void select(Artist artistItem) {
        selectedArtist.setValue(artistItem);
    }
    public LiveData<Artist> getSelectedArtist() {
        return selectedArtist;
    }

    //GENRE______________
    private final MutableLiveData<Genre> selectedGenre = new MutableLiveData<Genre>();
    public void select(Genre genreItem) {
        selectedGenre.setValue(genreItem);
    }
    public LiveData<Genre> getSelectedGenre() {
        return selectedGenre;
    }
}