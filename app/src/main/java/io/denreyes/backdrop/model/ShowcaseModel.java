package io.denreyes.backdrop.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DJ on 8/28/2015.
 */
public class ShowcaseModel implements Parcelable {
    public String title;
    public String mixer;
    public String img_url;
    public String id;

    public ShowcaseModel(String id, String title, String mixer, String img_url) {
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

    private ShowcaseModel(Parcel in){
        title=in.readString();
        mixer=in.readString();
        img_url=in.readString();
        id=in.readString();
    }

    public static final Parcelable.Creator<ShowcaseModel> CREATOR
            = new Parcelable.Creator<ShowcaseModel>() {
        public ShowcaseModel createFromParcel(Parcel in) {
            return new ShowcaseModel(in);
        }

        public ShowcaseModel[] newArray(int size) {
            return new ShowcaseModel[size];
        }
    };

}
