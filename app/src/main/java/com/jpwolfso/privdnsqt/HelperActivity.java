package com.jpwolfso.privdnsqt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class HelperActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Go to 'More connection settings' then 'Private DNS' to configure.", Toast.LENGTH_LONG).show();
    }
}
