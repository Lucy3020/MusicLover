package com.example.musiclovers.Models;

public class Artist {
    private String _id;
    private String artistName;
    private String description;
    private String artistImg;
    private boolean liked;

    public boolean isLiked() {
        return liked;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArtistImg() {
        return artistImg;
    }

    public void setArtistImg(String artistImg) {
        this.artistImg = artistImg;
    }

    public Artist(String _id, String artistName, String description, String artistImg, boolean loved) {
        this._id = _id;
        this.artistName = artistName;
        this.description = description;
        this.artistImg = artistImg;
        //this.loved = loved;
    }
}
