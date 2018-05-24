package api.models;

import com.squareup.moshi.Json;

import java.util.List;
import java.util.Map;

public class Body {

    @Json(name = "np")
    private List<Double> np = null;
    @Json(name = "hp")
    private List<Double> hp = null;
    @Json(name = "angles")

    private  Map<String,Double> angles = null;

    public Map<String,Double> getAngles() {
        return angles;
    }

    public void setAngles(Map<String,Double> np) {
        this.angles = np;
    }

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