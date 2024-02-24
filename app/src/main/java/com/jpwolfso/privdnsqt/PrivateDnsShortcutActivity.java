package com.jpwolfso.privdnsqt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class PrivateDnsShortcutActivity extends Activity {
    private static final String ACTION_ENABLE_DNS = "com.jpwolfso.privdnsqt.ACTION_ENABLE";
    private static final String ACTION_DISABLE_DNS = "com.jpwolfso.privdnsqt.ACTION_DISABLE";
    private static final String ACTION_TOGGLE_DNS = "com.jpwolfso.privdnsqt.ACTION_TOGGLE";

    private PrivateDnsCommon utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        utils = new PrivateDnsCommon(this);

        Intent intent = getIntent();
        if (intent != null) {
            if (!hasPermission()) {
                Toast.makeText(this, getString(R.string.toast_no_permission), Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            String dnsMode = Settings.Global.getString(getContentResolver(), "private_dns_mode");
            String dnsprovider = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
            String toastMsg = "";

            switch (intent.getAction()) {
                case ACTION_ENABLE_DNS:
                    if (dnsprovider != null && !dnsprovider.isEmpty()) {
                        dnsMode = PrivateDnsCommon.DNS_MODE_ON;
                    } else {
                        toastMsg = getString(R.string.toast_no_dns);
                    }
                    break;
                case ACTION_DISABLE_DNS:
                    dnsMode = PrivateDnsCommon.DNS_MODE_OFF;
                    break;
                case ACTION_TOGGLE_DNS:
                    dnsMode = utils.getNextMode(dnsMode);
                    break;
            }

            if (toastMsg.isEmpty()) {
                switch (dnsMode) {
                    case PrivateDnsCommon.DNS_MODE_OFF:
                        toastMsg = getString(R.string.qt_off);
                        break;
                    case PrivateDnsCommon.DNS_MODE_ON:
                        toastMsg = getString(R.string.dns_on) + ": " + dnsprovider;
                        break;
                    case PrivateDnsCommon.DNS_MODE_AUTO:
                        toastMsg = getString(R.string.qt_auto);
                        break;
                }
            }

            Toast.makeText(this, toastMsg, Toast.LENGTH_SHORT).show();
            Settings.Global.putString(getContentResolver(), "private_dns_mode", dnsMode);
        }

        finish();
    }

    public boolean hasPermission() {
        return checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != PackageManager.PERMISSION_DENIED;
    }

}
