package com.jpwolfso.privdnsqt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

public class PrivateDnsCommon {
    public static final String DNS_MODE_OFF = "off";
    public static final String DNS_MODE_AUTO = "opportunistic";
    public static final String DNS_MODE_ON = "hostname";

    private Context context;

    public PrivateDnsCommon(Context context) {
        this.context = context;
    }

    public String getNextMode(String currentMode) {
        final SharedPreferences toggleStates = context.getSharedPreferences("togglestates", Context.MODE_PRIVATE);

        final boolean toggleOff = toggleStates.getBoolean("toggle_off", true);
        final boolean toggleAuto = toggleStates.getBoolean("toggle_auto", true);
        final boolean toggleOn = toggleStates.getBoolean("toggle_on", true);

        String dnsprovider = Settings.Global.getString(context.getContentResolver(), "private_dns_specifier");
        String newState = currentMode;

        switch (currentMode.toLowerCase()) {
            case DNS_MODE_ON:
                if (toggleOff)
                    newState = DNS_MODE_OFF;
                else if (toggleAuto)
                    newState = DNS_MODE_AUTO;
                break;
            case DNS_MODE_OFF:
                if (toggleAuto)
                    newState = DNS_MODE_AUTO;
                else if (toggleOn)
                    newState = DNS_MODE_ON;
                break;
            case DNS_MODE_AUTO:
                if (dnsprovider != null && !dnsprovider.isEmpty()) {
                    if (toggleOn)
                        newState = DNS_MODE_ON;
                    else if (toggleOff)
                        newState = DNS_MODE_OFF;
                } else {
                    Toast.makeText(context, R.string.toast_no_dns, Toast.LENGTH_SHORT).show();
                    newState = DNS_MODE_OFF;
                }
                break;
        }
        return newState;
    }

    public boolean hasPermission() {
        return context.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != PackageManager.PERMISSION_DENIED;
    }
}
