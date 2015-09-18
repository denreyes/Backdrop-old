package io.denreyes.backdrop.Spotlight;

import java.io.Serializable;

/**
 * Created by DJ on 8/28/2015.
 */
public class SpotlightModel implements Serializable {
    public String title;
    public String mixer;
    public String img_url;
    public String id;


    public SpotlightModel() {

    }

    public SpotlightModel(SpotlightModel d) {
        this.setId(d.id);
        this.setTitle(d.title);
        this.setMixer(d.mixer);
        this.setImage(d.img_url);
    }

    public SpotlightModel(String id, String title, String mixer, String img_url) {
        this.id = id;
        this.title = title;
        this.mixer = mixer;
        this.img_url = img_url;
    }

    public void setId(String id) {
        this.id = id;
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
