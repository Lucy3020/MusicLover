package com.example.musiclovers.Services;

import com.example.musiclovers.Fragments.PlayMusicView.LyricsFragment;
import com.example.musiclovers.Models.Album;
import com.example.musiclovers.Models.Artist;
import com.example.musiclovers.Models.Genre;
import com.example.musiclovers.Models.Playlist;
import com.example.musiclovers.Models.Song;
import com.example.musiclovers.Models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
/**
 * DONE
 */
public interface PlaceHolder {
    // 👇 GET 👇
    @GET("songs")
    Call<List<Song>> getSongs();

    @GET("songs/{songId}")
    Call<Song> getSong (@Path("songId") String songId);

    @GET("albums")
    Call<List<Album>> getAlbums();

    @GET("albums/{albumId}")
    Call<Album> getAlbum (@Path("albumId") String albumId);

    @GET("albums/artist/{artistId}")
    Call<List<Album>> getAlbumsByArtist (@Path("artistId") String artistId);

    @GET("songs/album/{albumId}")
    Call<List<Song>> getSongsByAlbum (@Path("albumId") String albumId);

    @GET("songs/category/{category}")
    Call<List<Song>> getSongsByCategory (@Path("category") String category); //new-music * best-new-songs *

    @GET("albums/category/{category}")
    Call<List<Album>> getAlbumsByCategory (@Path("category") String category); //new-albums * hot-albums *

    @GET("songs/artist/{artistId}")
    Call<List<Song>> getSongsByArtist (@Path("artistId") String artistId);

    @GET("playlists/{playlistId}")
    Call<Playlist> getPlaylist (@Path("playlistId") String playlistId);

    @GET("playlists/songs/{playlistId}")
    Call<List<Song>> getSongsByPlaylist (@Path("playlistId") String playlistId);

    @GET("playlists/user/{userId}")
    Call<List<Playlist>> getPlaylistsByUser (@Path("userId") String userId);

    @GET("playlists/{userId}/{playlist_number}")
    Call<List<Playlist>> getPlaylistByUser_PlaylistNum(@Path("userId") String userId, @Path("playlist_number") int playlist_number);

    @GET("artists/user/{userId}")
    Call<List<Artist>> getArtistsByUser(
            @Path("userId") String userId
    );

    @GET("artists/{userId}/{artistId}")
    Call<Artist> getArtist(
            @Path("artistId") String artistId,
            @Path("userId") String userId
    );

    @GET("genres/{genreId}/{userId}")
    Call<List<Song>> getUserGenres(
            @Path("genreId") String genreId,
            @Path("userId") String userId

    );

    @GET("genres")
    Call<List<Genre>> getGenres();

    @GET("lyrics/{songId}")
    Call<LyricsFragment.LyricsResponse> getLyrics(@Path("songId") String songId);

    @GET("songs/search")
    Call<List<Song>> searchSongs (@Query("q") String q);

    @GET("albums/search")
    Call<List<Album>> searchAlbums (@Query("q") String q);

    @GET("artists/search")
    Call<List<Artist>> searchArtists (@Query("q") String q);

    // 👇 PATCH 👇

    // 👇 POST 👇
    @FormUrlEncoded
    @POST("users/")
    Call<User> register(
            @Field("userName") String userName,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("users/login")
    Call<User> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("playlists/")
    Call<Playlist> createPlaylist(
            @Field("playlistName") String playlistName,
            @Field("userId") String userId
    );

    @FormUrlEncoded
    @POST("playlists/{playlistId}/songs")
    Call<Void> addSongToPlaylist(
            @Path("playlistId") String playlistId,
            @Field("songId") String songId
    );

    @FormUrlEncoded
    @POST("songs/likes")
    Call<Void> likeSong(
            @Field("userId") String userId,
            @Field("songId") String songId
    );

    @FormUrlEncoded
    @POST("artists/likes")
    Call<Void> likeArtist(
            @Field("userId") String userId,
            @Field("artistId") String artistId
    );

    @FormUrlEncoded
    @POST("songs/recent")
    Call<Void> addSong2RecentList(
            @Field("userId") String userId,
            @Field("songId") String songId
    );

    // 👇 DELETE 👇
    @DELETE("playlists/{playlistId}/songs/{songId}")
    Call<Void> removeSongInPlaylist(
            @Path("playlistId") String playlistId,
            @Path("songId") String songId
    );

    @DELETE("playlists/{playlistId}")
    Call<Void> deletePlaylist(
            @Path("playlistId") String playlistId
    );
}
