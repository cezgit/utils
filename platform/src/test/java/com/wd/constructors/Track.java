package com.wd.constructors;

import java.time.LocalDate;

public class Track {

    private String artist;
    private String title;
    private LocalDate releaseDate;

    public Track() {
    }

    private Track(Builder builder) {
        this.artist = builder.artist;
        this.title = builder.title;
        this.releaseDate = builder.releaseDate;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public static class Builder {

        private String artist;
        private String title;
        private LocalDate releaseDate;

        public Builder() {}
        public Builder withArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withReleaseDate(LocalDate date) {
            this.releaseDate = date;
            return this;
        }

        public Track build() {
            return new Track(this);
        }
    }

    @Override
    public String toString() {
        return "Track{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
