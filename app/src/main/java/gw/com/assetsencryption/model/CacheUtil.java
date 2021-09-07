package gw.com.assetsencryption.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import gw.com.assetsencryption.AppMain;
import gw.com.assetsencryption.BuildConfig;
import gw.com.assetsencryption.encrypt.AESCrypto;

public class CacheUtil {
    private static final String TAG = "CacheUtil";
    private static final String mCachePath = AppMain.application.getFilesDir().getPath() + "/cache";

    public static final String PREF_CONFIG_VERSION = "ConfigVersion"; //配置文件本地保存的版本号


    /**
     * 讀取指定文件內容
     *
     * @param fileName File name
     */
    public static String readFile(String fileName) {
        File mFile = new File(mCachePath + "/" + fileName);
        Log.e(TAG, "readFile: "+ mFile.getPath());
        if (!mFile.exists()) {
            return "";
        }
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        try {
            fis = new FileInputStream(mFile);
            inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return "";
    }


    /**
     * 寫入到本地暫存
     */
    public static void writeFile(String fileName, String mContent) {
        File fileDir = new File(mCachePath);
        File mFile = new File(mCachePath + "/" + fileName);
        Log.d(TAG, "writeFile path:" + mFile.getPath());
        if (TextUtils.isEmpty(mContent)) {
            if (mFile.exists()) {
                mFile.delete();
            }
            return;
        }
        FileOutputStream fos = null;
        //加密後寫入
        mContent = AESCrypto.encrypt(mContent,AESCrypto.PASSWORD_01);
        try {
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            if (!mFile.exists()) {
                Log.d(TAG, "writeFile: create file");
                mFile.createNewFile();
            }
            if (mContent.length() > 1) {
                Log.d(TAG, "writeFile: " + mFile.getPath() + mFile.exists());
                fos = new FileOutputStream(mFile);
                fos.write(mContent.getBytes());
                fos.flush();// 刷新缓冲区
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static final SharedPreferences mSysPreferences = AppMain.application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    /**
     * 保存配置文件版本号设置
     */
    public static void saveConfigVersion(String appVersion, Integer version) {
        Integer version1 = (version == null) ? 0 : version;
        SharedPreferences.Editor editor = mSysPreferences.edit();
        editor.putInt(PREF_CONFIG_VERSION + appVersion, version1);
        editor.apply();
    }

    /**
     * 获取配置文件版本号设置
     */
    public static int getConfigVersion(String appVersion) {
        if (null != mSysPreferences) {
            return mSysPreferences.getInt(PREF_CONFIG_VERSION + appVersion, 0);
        }
        return 0;
    }

}
