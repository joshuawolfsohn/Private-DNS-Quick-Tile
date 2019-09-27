package com.jpwolfso.privdnsqt;

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

        Tile tile = this.getQsTile();

        String dnsmode = Settings.Global.getString(getContentResolver(), "private_dns_mode");

        if (dnsmode.equalsIgnoreCase(DNS_MODE_OFF)) {
            tile.setState((Tile.STATE_INACTIVE));
            tile.setLabel(getString(R.string.qt_off));
            tile.setIcon(Icon.createWithResource(this,R.drawable.ic_dnsoff));
        } else if (dnsmode.equalsIgnoreCase(DNS_MODE_AUTO)) {
            tile.setState((Tile.STATE_ACTIVE));
            tile.setLabel(getString(R.string.qt_auto));
            tile.setIcon(Icon.createWithResource(this,R.drawable.ic_dnsauto));
        } else if (dnsmode.equalsIgnoreCase(DNS_MODE_ON)) {
            String dnsname = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
            if ((dnsname != null) && (!dnsname.isEmpty())) {
                tile.setState(Tile.STATE_ACTIVE);
                tile.setLabel(dnsname);
                tile.setIcon(Icon.createWithResource(this, R.drawable.ic_dnson));
            } else {
                Toast.makeText(this, "DNS provider not configured", Toast.LENGTH_SHORT).show();
            }
        }

        tile.updateTile();
    }

    public void onStopListening() {
        super.onStopListening();
    }

    public void onClick() {
        super.onClick();

        if (hasPermission()) {
            Tile tile = this.getQsTile();

            String dnsmode = Settings.Global.getString(getContentResolver(), "private_dns_mode");

            if (dnsmode.equalsIgnoreCase(DNS_MODE_OFF)) {
                Settings.Global.putString(getContentResolver(), "private_dns_mode", DNS_MODE_AUTO);
                tile.setState((Tile.STATE_ACTIVE));
                tile.setLabel(getString(R.string.qt_auto));
                tile.setIcon(Icon.createWithResource(this,R.drawable.ic_dnsauto));
            } else if (dnsmode.equalsIgnoreCase(DNS_MODE_AUTO)) {
                    String dnsname = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
                    if ((dnsname != null) && (!dnsname.isEmpty())) {
                        Settings.Global.putString(getContentResolver(), "private_dns_mode", DNS_MODE_ON);
                        tile.setState(Tile.STATE_ACTIVE);
                        tile.setLabel(dnsname);
                        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_dnson));
                    } else {
                        Toast.makeText(this, "DNS provider not configured", Toast.LENGTH_SHORT).show();
                        Settings.Global.putString(getContentResolver(), "private_dns_mode", DNS_MODE_OFF);
                        tile.setState((Tile.STATE_INACTIVE));
                        tile.setLabel(getString(R.string.qt_off));
                        tile.setIcon(Icon.createWithResource(this,R.drawable.ic_dnsoff));
                    }
            } else if (dnsmode.equals(DNS_MODE_ON)) {
                Settings.Global.putString(getContentResolver(), "private_dns_mode", DNS_MODE_OFF);
                tile.setState(Tile.STATE_INACTIVE);
                tile.setLabel(getString(R.string.qt_off));
                tile.setIcon(Icon.createWithResource(this,R.drawable.ic_dnsoff));
            }
            tile.updateTile();

        } else if (!(hasPermission())){
            Toast.makeText(this, getString(R.string.toast_permission), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasPermission() {

        return checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") != PackageManager.PERMISSION_DENIED;
    }


}
