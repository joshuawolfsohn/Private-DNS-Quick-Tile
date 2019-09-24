package com.jpwolfso.privdnsqt;

import android.content.pm.PackageManager;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class PrivateDnsTileService extends TileService {


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
        if (dnsmode.equalsIgnoreCase("off")) {
            tile.setState((Tile.STATE_INACTIVE));
            tile.setLabel("DNS off");
        } else if (dnsmode.equalsIgnoreCase("opportunistic")) {
            tile.setState((Tile.STATE_ACTIVE));
            tile.setLabel("DNS auto");
        } else if (dnsmode.equalsIgnoreCase("hostname")) {
            tile.setState(Tile.STATE_ACTIVE);
            String dnsname = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
            tile.setLabel(dnsname);
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

            if (dnsmode.equalsIgnoreCase("off")) {
                Settings.Global.putString(getContentResolver(), "private_dns_mode", "opportunistic");
                tile.setState((Tile.STATE_ACTIVE));
                tile.setLabel("DNS auto");
            } else if (dnsmode.equalsIgnoreCase("opportunistic")) {
                Settings.Global.putString(getContentResolver(), "private_dns_mode", "hostname");
                tile.setState(Tile.STATE_ACTIVE);
                String dnsname = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
                tile.setLabel(dnsname);
            } else if (dnsmode.equals("hostname")) {
                Settings.Global.putString(getContentResolver(), "private_dns_mode", "off");
                tile.setState(Tile.STATE_INACTIVE);
                tile.setLabel("DNS off");
            }
            tile.updateTile();

        } else if (!(hasPermission())){
            Toast.makeText(this, "WRITE_SECURE_SETTINGS permission not granted", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean hasPermission() {

        if (checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }


}
