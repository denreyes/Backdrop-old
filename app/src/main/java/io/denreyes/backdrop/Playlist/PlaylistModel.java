package io.denreyes.backdrop.Playlist;

import java.io.Serializable;

/**
 * Created by DJ on 8/29/2015.
 */
public class PlaylistModel implements Serializable{
    public String title;
    public String artist;
    public String img_url;
    public String track_id;


    public PlaylistModel() {

    }

    public PlaylistModel(PlaylistModel d) {
        this.setTitle(d.title);
        this.setArtist(d.artist);
        this.setImage(d.img_url);
        this.setId(d.track_id);
    }

    public PlaylistModel(String title, String artist, String img_url, String track_id) {
        this.title = title;
        this.artist = artist;
        this.img_url = img_url;
        this.track_id = track_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setImage(String img_url) {
        this.img_url = img_url;
    }

    public void setId(String track_id) {
        this.track_id = track_id;
    }

}
