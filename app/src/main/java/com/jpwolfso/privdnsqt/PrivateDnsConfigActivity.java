package com.jpwolfso.privdnsqt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

public class PrivateDnsConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_dns_config);

        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final SharedPreferences togglestates = getSharedPreferences("togglestates", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = togglestates.edit();

        final CheckBox checkoff = findViewById(R.id.check_off);
        final CheckBox checkauto = findViewById(R.id.check_auto);
        final CheckBox checkon = findViewById(R.id.check_on);

        final EditText texthostname = findViewById(R.id.text_hostname);

        final Button okbutton = findViewById(R.id.button_ok);

        if (togglestates.getBoolean("toggle_off", true)) {
            checkoff.setChecked(true);
        }

        if (togglestates.getBoolean("toggle_auto", true)) {
            checkauto.setChecked(true);
        }

        if (togglestates.getBoolean("toggle_on", true)) {
            checkon.setChecked(true);
            texthostname.setEnabled(true);
        } else {
            texthostname.setEnabled(false);

        }

        String dnsprovider = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
        if (dnsprovider != null) {
            texthostname.setText(dnsprovider);
        }

        checkoff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkoff.isChecked()) {
                    editor.putBoolean("toggle_off", true);
                } else {
                    editor.putBoolean("toggle_off", false);
                }
            }
        });

        checkauto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkauto.isChecked()) {
                    editor.putBoolean("toggle_auto", true);
                } else {
                    editor.putBoolean("toggle_auto", false);
                }
            }
        });

        checkon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkon.isChecked()) {
                    editor.putBoolean("toggle_on", true);
                    texthostname.setEnabled(true);
                } else {
                    editor.putBoolean("toggle_on", false);
                    texthostname.setEnabled(false);
                }
            }
        });

        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasPermission()) {
                    if (checkon.isChecked()) {
                        if (texthostname.getText().toString().isEmpty()) {
                            Toast.makeText(PrivateDnsConfigActivity.this, "DNS provider not configured", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Settings.Global.putString(getContentResolver(), "private_dns_specifier", texthostname.getText().toString());
                        }
                    }
                    editor.commit();
                    finish();
                } else {
                    Toast.makeText(PrivateDnsConfigActivity.this, getString(R.string.toast_permission), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_config);
        toolbar.showOverflowMenu();
        setActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_appinfo) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }   else if (id == R.id.action_fdroid) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://f-droid.org/en/packages/com.jpwolfso.privdnsqt/"));
                    startActivity(intent);
                }
                    return false;
            }
        });


    }

    public boolean hasPermission() {
        return checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != PackageManager.PERMISSION_DENIED;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_overflow,menu);
        return true;
    }

}
