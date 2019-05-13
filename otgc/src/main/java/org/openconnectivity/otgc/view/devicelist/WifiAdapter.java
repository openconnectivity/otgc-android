/*
 *  *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  *****************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  *****************************************************************
 */

package org.openconnectivity.otgc.view.devicelist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.content.Context;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.WifiNetwork;

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
