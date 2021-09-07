package gw.com.assetsencryption.model;


import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import gw.com.assetsencryption.BuildConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

//TODO 模擬遠端來源
public class RemoteSource {
    private static final String TAG = "RemoteSource";
    private static RemoteSource mInstance;

    //測試用JSON格式資料
    private final String TEST_JSON_DATA = "{\n" +
            "  \"config_version\": \"4\",\n" +
            "  \"content\": \"{\\\"userId\\\": 1,\\\"id\\\": 1,\\\"title\\\": \\\"quidem molestiae enim\\\"}\"\n" +
            "}";

    private final HttpJSON httpJSON;

    private RemoteSource() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://jsonplaceholder.typicode.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpJSON = retrofit.create(HttpJSON.class);
    }

    public static RemoteSource getInstance() {
        if (mInstance == null) {
            mInstance = new RemoteSource();
        }
        return mInstance;
    }


    public interface HttpJSON {

        // 測試網站      https://jsonplaceholder.typicode.com/
        // GET網址      https://jsonplaceholder.typicode.com/albums/1
        // POST網址     https://jsonplaceholder.typicode.com/albums
        // 設置一個GET連線
        @GET("posts")
        Call<ResponseBody> getPosts();
    }


    public void call() {
        Call<ResponseBody> call = httpJSON.getPosts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String jsonStr = new String(response.body().bytes());
                    if (response.isSuccessful()){
                        handle(TEST_JSON_DATA); //測試
//                        handle(jsonStr);
                    }
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }


    private static void handle(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            int currentVersion = jsonObject.optInt("config_version"); //在API中可設計一個版本參數
            int configVersion = CacheUtil.getConfigVersion(BuildConfig.VERSION_NAME); //本地的版本

            String content = jsonObject.optString("content");
            if (!TextUtils.isEmpty(content) && currentVersion > configVersion) {
                JSONObject configJson = new JSONObject(content);
                CacheUtil.writeFile(ConfigLoader.instance().getFileName() + BuildConfig.VERSION_CODE, content);
                CacheUtil.saveConfigVersion(BuildConfig.VERSION_NAME, currentVersion);
                Log.i(TAG, "save config file success. version = " + currentVersion);
                ConfigLoader.instance().reLoad(configJson);
                return;
            }
            Log.i(TAG, "parse result failed .result = " + result);
        } catch (JSONException e) {
            Log.e(TAG, "handle :", e);
        }
    }

}
