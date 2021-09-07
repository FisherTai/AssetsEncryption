package gw.com.assetsencryption.model;


import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import gw.com.assetsencryption.AppMain;
import gw.com.assetsencryption.BuildConfig;
import gw.com.assetsencryption.encrypt.AESCrypto;


/**
 * 有些專案會將配置檔作為Json檔放在Asset內，
 * 此工具類讀取Asset下的Json檔案。
 * <p>
 * 初始化時依APP版本抓取對應的Json檔
 * <p>
 * json檔來源按業務需求可能為:
 * 1.包內Assets預設
 * 2.本地暫存路徑
 * 3.從遠端獲取
 * <p>
 * 優先程度為 3 -> 2 -> 1
 */

public class ConfigLoader {
    private static final String TAG = "ConfigLoader";

    @SuppressWarnings("FieldCanBeLocal")
    private final String FILE_NAME = "my_json.json";


    private JSONObject mConfigObject;
    private String urlJsonStr;
    private static ConfigLoader mInstance = null;

    private ConfigLoader() {
        try {
            reset();
        } catch (JSONException e) {
            e.printStackTrace();
            mConfigObject = new JSONObject();
        }
    }

    public static ConfigLoader instance() {
        if (null == mInstance) {
            synchronized (ConfigLoader.class) {
                if (mInstance == null) {
                    mInstance = new ConfigLoader();
                }
            }
        }
        return mInstance;
    }


    public String getFileName() {
        return FILE_NAME;
    }

    private void reset() throws JSONException {
        urlJsonStr = getFunConfig(getFileName());
        printJson(urlJsonStr);
        mConfigObject = new JSONObject(urlJsonStr);
        new Handler().postDelayed(() -> RemoteSource.getInstance().call(),2000);
    }

    /**
     * 讀取本地暫存路徑下的文件
     */
    private String getFunConfig(String mFileName) {
        String fileContent = CacheUtil.readFile(mFileName + BuildConfig.VERSION_CODE).trim();
        fileContent = AESCrypto.decrypt(fileContent,AESCrypto.PASSWORD_01);
        if (!isJsonData(fileContent)) {
            fileContent = getFromAssets(mFileName);
        }

        return fileContent;
    }


    /**
     * 讀取本地assets路徑下的文件
     */
    private String getFromAssets(String fileName) {
        StringBuilder sb = new StringBuilder();
        InputStreamReader inputReader = null;
        BufferedReader bufReader = null;
        try {
            inputReader = new InputStreamReader(AppMain.application.getResources().getAssets().open(fileName));
            bufReader = new BufferedReader(inputReader);
            String line;
            while ((line = bufReader.readLine()) != null)
                sb.append(line);
            inputReader.close();
            inputReader = null;
            bufReader.close();
            bufReader = null;
            return sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "getFromAssets: ", e);
        } finally {
            try {
                if (null != inputReader) {
                    inputReader.close();
                }
                if (null != bufReader) {
                    bufReader.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sb.toString();
    }

    private boolean isJsonData(String content) {
        try {
            new JSONObject(content);
            return true;
        } catch (JSONException e) {
            Log.e(TAG, "isJsonData: " + content + "is JSONException");
            return false;
        }
    }


    /**
     * 印出Json
     */
    public static void printJson(String json) {
        String TAG = "PrintJson";
        if (TextUtils.isEmpty(json)) {
            Log.e(TAG, "Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(2);
                Log.d(TAG, "printJson: " + message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(2);
                Log.d(TAG, "printJson: " + message);
                return;
            }
            Log.e(TAG, "Invalid Json");
        } catch (JSONException e) {
            Log.e(TAG, "Invalid Json");
        }
    }

    public void reLoad(JSONObject json) {
        if (json != null) {
            mConfigObject = json;
            urlJsonStr = json.toString();
        }
    }

}

