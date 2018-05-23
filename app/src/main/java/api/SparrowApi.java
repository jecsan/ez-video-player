package api;

import api.models.ApiDrawing;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface SparrowApi {

    @GET("sample_analysis")
    Call<ApiDrawing> getFrames();
}