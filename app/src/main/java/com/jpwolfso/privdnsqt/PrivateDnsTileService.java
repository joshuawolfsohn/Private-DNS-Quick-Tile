package com.jpwolfso.privdnsqt;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class PrivateDnsTileService extends TileService {


    public static String DNS_MODE_OFF = "off";
    public static String DNS_MODE_AUTO = "opportunistic";
    public static String DNS_MODE_ON = "hostname";

    public void onTileAdded() {
        super.onTileAdded();

    }

    public void onTileRemoved() {
        super.onTileRemoved();
    }

    public void onStartListening() {
        super.onStartListening();

        String dnsmode = Settings.Global.getString(getContentResolver(), "private_dns_mode");
        Tile tile = this.getQsTile();

        if (dnsmode.equalsIgnoreCase(DNS_MODE_OFF)) {
            refreshTile(tile, Tile.STATE_INACTIVE, getString(R.string.qt_off), R.drawable.ic_dnsoff);
        } else if (dnsmode.equalsIgnoreCase(DNS_MODE_AUTO)) {
            refreshTile(tile, Tile.STATE_ACTIVE, getString(R.string.qt_auto), R.drawable.ic_dnsauto);
        } else if (dnsmode.equalsIgnoreCase(DNS_MODE_ON)) {
            String dnsprovider = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
            if ((dnsprovider != null)) {
                refreshTile(tile, Tile.STATE_ACTIVE, dnsprovider, R.drawable.ic_dnson);
            } else {
                Toast.makeText(this, "DNS provider not configured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStopListening() {
        super.onStopListening();
    }

    public void onClick() {
        super.onClick();

        final SharedPreferences togglestates = getSharedPreferences("togglestates", Context.MODE_PRIVATE);

        final Boolean toggleoff = togglestates.getBoolean("toggle_off", true);
        final Boolean toggleauto = togglestates.getBoolean("toggle_auto", true);
        final Boolean toggleon = togglestates.getBoolean("toggle_on", true);

        String dnsprovider = Settings.Global.getString(getContentResolver(), "private_dns_specifier");

        if (hasPermission()) {
            String dnsmode = Settings.Global.getString(getContentResolver(), "private_dns_mode");
            Tile tile = this.getQsTile();
            if (dnsmode.equalsIgnoreCase(DNS_MODE_OFF)) {
                if (toggleauto) {
                    changeTileState(tile, Tile.STATE_ACTIVE, getString(R.string.qt_auto), R.drawable.ic_dnsauto, DNS_MODE_AUTO);
                } else if (toggleon) {
                    changeTileState(tile, Tile.STATE_ACTIVE, dnsprovider, R.drawable.ic_dnson, DNS_MODE_ON);
                }
            } else if (dnsmode.equalsIgnoreCase(DNS_MODE_AUTO)) {
                if (dnsprovider != null) {
                    if (toggleon) {
                        changeTileState(tile, Tile.STATE_ACTIVE, dnsprovider, R.drawable.ic_dnson, DNS_MODE_ON);
                    } else if (toggleoff) {
                        changeTileState(tile, Tile.STATE_INACTIVE, getString(R.string.qt_off), R.drawable.ic_dnsoff, DNS_MODE_OFF);
                    }
                } else {
                    if (toggleoff) {
                        changeTileState(tile, Tile.STATE_INACTIVE, getString(R.string.qt_off), R.drawable.ic_dnsoff, DNS_MODE_OFF);
                    }
                }
            } else if (dnsmode.equals(DNS_MODE_ON)) {
                if (toggleoff) {
                    changeTileState(tile, Tile.STATE_INACTIVE, getString(R.string.qt_off), R.drawable.ic_dnsoff, DNS_MODE_OFF);
                } else if (toggleauto) {
                    changeTileState(tile, Tile.STATE_ACTIVE, getString(R.string.qt_auto), R.drawable.ic_dnsauto, DNS_MODE_AUTO);
                }
            }

        } else if (!(hasPermission())) {
            Toast.makeText(this, getString(R.string.toast_permission), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasPermission() {
        return checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != PackageManager.PERMISSION_DENIED;
    }

    public void changeTileState(Tile tile, int state, String label, int icon, String dnsmode) {
        tile.setLabel(label);
        tile.setState(state);
        tile.setIcon(Icon.createWithResource(this, icon));
        Settings.Global.putString(getContentResolver(), "private_dns_mode", dnsmode);
        tile.updateTile();
    }

    public void refreshTile(Tile tile, int state, String label, int icon) {
        tile.setState(state);
        tile.setLabel(label);
        tile.setIcon(Icon.createWithResource(this, icon));
        tile.updateTile();
    }


}
