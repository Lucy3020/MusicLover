package com.example.musiclovers.Models;

public class Genre {
    private String _id;
    private String genreType;

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setGenreType(String genreType) {
        this.genreType = genreType;
    }

    public String get_id() {
        return _id;
    }

    public String getGenreType() {
        return genreType;
    }

    public Genre(String _id, String genreType) {
        this._id = _id;
        this.genreType = genreType;
    }
}
