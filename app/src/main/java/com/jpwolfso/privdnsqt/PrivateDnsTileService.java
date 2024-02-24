package com.jpwolfso.privdnsqt;

import android.graphics.drawable.Icon;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class PrivateDnsTileService extends TileService {
    private PrivateDnsCommon utils;

    @Override
    public void onCreate() {
        utils = new PrivateDnsCommon(this);
    }

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

        if (dnsmode.equalsIgnoreCase(PrivateDnsCommon.DNS_MODE_OFF)) {
            refreshTile(tile, Tile.STATE_INACTIVE, getString(R.string.qt_off), R.drawable.ic_dnsoff);
        } else if (dnsmode.equalsIgnoreCase(PrivateDnsCommon.DNS_MODE_AUTO)) {
            refreshTile(tile, Tile.STATE_ACTIVE, getString(R.string.qt_auto), R.drawable.ic_dnsauto);
        } else if (dnsmode.equalsIgnoreCase(PrivateDnsCommon.DNS_MODE_ON)) {
            String dnsprovider = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
            if ((dnsprovider != null)) {
                refreshTile(tile, Tile.STATE_ACTIVE, dnsprovider, R.drawable.ic_dnson);
            } else {
                Toast.makeText(this, R.string.toast_no_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onStopListening() {
        super.onStopListening();
    }

    public void onClick() {
        super.onClick();

        String dnsprovider = Settings.Global.getString(getContentResolver(), "private_dns_specifier");
        String dnsMode = Settings.Global.getString(getContentResolver(), "private_dns_mode");
        String newMode = utils.getNextMode(dnsMode);
        Tile tile = this.getQsTile();

        if (utils.hasPermission()) {
            switch (newMode) {
                case PrivateDnsCommon.DNS_MODE_ON:
                    changeTileState(tile, Tile.STATE_ACTIVE, dnsprovider, R.drawable.ic_dnson, PrivateDnsCommon.DNS_MODE_ON);
                    break;
                case PrivateDnsCommon.DNS_MODE_OFF:
                    changeTileState(tile, Tile.STATE_INACTIVE, getString(R.string.qt_off), R.drawable.ic_dnsoff, PrivateDnsCommon.DNS_MODE_OFF);
                    break;
                case PrivateDnsCommon.DNS_MODE_AUTO:
                    changeTileState(tile, Tile.STATE_ACTIVE, getString(R.string.qt_auto), R.drawable.ic_dnsauto, PrivateDnsCommon.DNS_MODE_AUTO);
                    break;
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_no_permission), Toast.LENGTH_LONG).show();
        }
    }

    public void changeTileState(Tile tile, int state, String label, int icon, String dnsmode) {
        Settings.Global.putString(getContentResolver(), "private_dns_mode", dnsmode);
        refreshTile(tile, state, label, icon);
    }

    public void refreshTile(Tile tile, int state, String label, int icon) {
        tile.setState(state);
        tile.setLabel(label);
        tile.setIcon(Icon.createWithResource(this, icon));
        tile.updateTile();
    }
}
