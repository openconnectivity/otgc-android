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

package org.openconnectivity.otgc.view.client;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.DeviceListViewHolder> {
    private Map<String, String> mDataset = new LinkedHashMap<>();
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class DeviceListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView mTitle;
        private TextView mContent;
        private DeviceListViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.info_title);
            mContent = itemView.findViewById(R.id.info_content);
        }
    }

    DeviceInfoAdapter(Context ctx) {
        mContext = ctx;

        String[] deviceInfo = ctx.getResources().getStringArray(R.array.device_info);
        for (String info : deviceInfo) {
            mDataset.put(info, null);
        }

        String[] platformInfo = ctx.getResources().getStringArray(R.array.platform_info);
        for (String info : platformInfo) {
            mDataset.put(info, null);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public DeviceInfoAdapter.DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_info, parent, false);

        return new DeviceInfoAdapter.DeviceListViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull DeviceInfoAdapter.DeviceListViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTitle.setText((new ArrayList<>(mDataset.keySet())).get(position));
        holder.mContent.setText((new ArrayList<>(mDataset.values())).get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        }
        return 0;
    }

    public void setDeviceInfo(OcDeviceInfo deviceInfo) {
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_name),
                deviceInfo.getName());
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_spec_version_url),
                deviceInfo.getSpecVersionUrl());
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_id),
                deviceInfo.getDeviceId());
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_data_model),
                deviceInfo.getDataModel());
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_piid),
                deviceInfo.getPiid());
        /*mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_descriptions),
                deviceInfo.getLocalizedDescriptions());*/
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_sw_version),
                deviceInfo.getSoftwareVersion());
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_manufacturer_name),
                deviceInfo.getManufacturerName());
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_device_model_number),
                deviceInfo.getModelNumber());
        this.notifyDataSetChanged();
    }

    public void setPlatformInfo(OcPlatformInfo platformInfo) {
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_id),
                platformInfo.getPlatformId()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_name),
                platformInfo.getManufacturerName()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_url),
                platformInfo.getManufacturerUrl()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_model_no),
                platformInfo.getManufacturerModelNumber()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_date),
                platformInfo.getManufacturedDate()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_platform_version),
                platformInfo.getManufacturerPlatformVersion()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_os_version),
                platformInfo.getManufacturerOsVersion()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_hw_version),
                platformInfo.getManufacturerHwVersion()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_fw_version),
                platformInfo.getManufacturerFwVersion()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_support_url),
                platformInfo.getManufacturerSupportUrl()
        );
        mDataset.put(
                mContext.getResources().getString(R.string.client_drawer_platform_man_system_time),
                platformInfo.getManufacturerSystemTime()
        );
        this.notifyDataSetChanged();
    }
}
