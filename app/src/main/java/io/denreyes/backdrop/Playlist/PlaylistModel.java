package io.denreyes.backdrop.Playlist;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by DJ on 8/29/2015.
 */
public class PlaylistModel implements Parcelable{
    public String title;
    public String artist;
    public String img_url;
    public String track_id;

    public PlaylistModel(String title, String artist, String img_url, String track_id) {
        this.title = title;
        this.artist = artist;
        this.img_url = img_url;
        this.track_id = track_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(artist);
        out.writeString(img_url);
        out.writeString(track_id);
    }

    private PlaylistModel(Parcel in){
        title=in.readString();
        artist=in.readString();
        img_url=in.readString();
        track_id=in.readString();
    }

    public static final Parcelable.Creator<PlaylistModel> CREATOR
            = new Parcelable.Creator<PlaylistModel>() {
        public PlaylistModel createFromParcel(Parcel in) {
            return new PlaylistModel(in);
        }

        public PlaylistModel[] newArray(int size) {
            return new PlaylistModel[size];
        }
    };

}
