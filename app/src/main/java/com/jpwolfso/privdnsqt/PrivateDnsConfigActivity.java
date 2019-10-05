package com.jpwolfso.privdnsqt;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class PrivateDnsConfigActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_dns_config);

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


    }

    public boolean hasPermission() {
        return checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != PackageManager.PERMISSION_DENIED;
    }

}
