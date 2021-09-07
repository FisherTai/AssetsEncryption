package gw.com.assetsencryption;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import gw.com.assetsencryption.model.ConfigLoader;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.home_tv);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        ConfigLoader.instance();
    }
}