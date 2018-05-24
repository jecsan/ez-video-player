package api.models;

import com.squareup.moshi.Json;

import java.util.List;

public class Body {

    @Json(name = "np")
    private List<Double> np = null;
    @Json(name = "hp")
    private List<Double> hp = null;

    public List<Double> getNp() {
        return np;
    }

    public void setNp(List<Double> np) {
        this.np = np;
    }

    public List<Double> getHp() {
        return hp;
    }

    public void setHp(List<Double> hp) {
        this.hp = hp;
    }

}