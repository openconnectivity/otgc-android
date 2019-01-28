package org.openconnectivity.otgc.devicelist.presentation.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.content.Context;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.wlanscan.domain.model.WifiNetwork;

import java.util.ArrayList;

public class WifiAdapter extends ArrayAdapter<WifiNetwork> {

    public WifiAdapter(Context context, ArrayList<WifiNetwork> wifiList) {
        super(context, 0, wifiList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WifiNetwork wifi = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_wifi, parent, false);
        }

        TextView tvSsid = (TextView) convertView.findViewById(R.id.text_wifi_ssid);
        tvSsid.setText(wifi.getName());
        TextView tvSecurity = (TextView) convertView.findViewById(R.id.text_wifi_security);
        tvSecurity.setText(wifi.getSecurity());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        WifiNetwork wifi = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_wifi, parent, false);
        }

        TextView tvSsid = (TextView) convertView.findViewById(R.id.text_wifi_ssid);
        tvSsid.setText(wifi.getName());
        TextView tvSecurity = (TextView) convertView.findViewById(R.id.text_wifi_security);
        tvSecurity.setText(wifi.getSecurity());

        return convertView;
    }
}
