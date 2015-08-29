package io.denreyes.backdrop.model;

import java.io.Serializable;

/**
 * Created by DJ on 8/28/2015.
 */
public class SpotlightModel implements Serializable {
    public String title;
    public String mixer;
    public String img_url;


    public SpotlightModel() {

    }

    public SpotlightModel(SpotlightModel d) {
        this.setTitle(d.title);
        this.setMixer(d.mixer);
        this.setImage(d.img_url);
    }

    public SpotlightModel(String title, String mixer, String img_url) {
        this.title = title;
        this.mixer = mixer;
        this.img_url = img_url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMixer(String mixer) {
        this.mixer = mixer;
    }

    public void setImage(String img_url) {
        this.img_url = img_url;
    }

}
