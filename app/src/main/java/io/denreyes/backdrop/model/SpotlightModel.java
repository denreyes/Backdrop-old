package io.denreyes.backdrop.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 8/28/2015.
 */
public class SpotlightModel implements Parcelable {
    public String title;
    public String mixer;
    public String img_url;
    public String id;

    public SpotlightModel(String id, String title, String mixer, String img_url) {
        this.id = id;
        this.title = title;
        this.mixer = mixer;
        this.img_url = img_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(title);
        out.writeString(mixer);
        out.writeString(img_url);
        out.writeString(id);
    }

    private SpotlightModel(Parcel in){
        title=in.readString();
        mixer=in.readString();
        img_url=in.readString();
        id=in.readString();
    }

    public static final Parcelable.Creator<SpotlightModel> CREATOR
            = new Parcelable.Creator<SpotlightModel>() {
        public SpotlightModel createFromParcel(Parcel in) {
            return new SpotlightModel(in);
        }

        public SpotlightModel[] newArray(int size) {
            return new SpotlightModel[size];
        }
    };

}
