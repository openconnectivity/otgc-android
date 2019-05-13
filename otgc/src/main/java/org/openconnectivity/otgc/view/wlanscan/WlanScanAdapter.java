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

package org.openconnectivity.otgc.view.wlanscan;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.WifiNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WlanScanAdapter extends RecyclerView.Adapter<WlanScanAdapter.WlanScanViewHolder> {
    private List<WifiNetwork> mDataset = new ArrayList<>();
    private Context mContext;
    private static ScanResultClickListener sScanResultClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class WlanScanViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.text_wlan_ssid) TextView mWlanSsid;
        @BindView(R.id.text_wlan_security) TextView mWlanSecurity;
        @BindView(R.id.img_view_wifi_level) ImageView mWifiLevel;

        private WlanScanViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sScanResultClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    WlanScanAdapter(Context ctx) {
        mContext = ctx;
    }

    public static void setOnItemClickListener(ScanResultClickListener clickListener) {
        sScanResultClickListener = clickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public WlanScanAdapter.WlanScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wlan, parent, false);

        return new WlanScanViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull WlanScanViewHolder holder, int position) {
        // - get the element of the dataset at this position
        // - replace the contents of the view with that element
        holder.mWlanSsid.setText(mDataset.get(position).getName());
        holder.mWlanSecurity.setText(mDataset.get(position).getSecurity());

        Drawable levelDrawable;
        if (!mDataset.get(position).isSecured()) {
            switch (mDataset.get(position).getLevel()) {
                case 2:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_2_bar_24dp);
                    break;
                case 3:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_3_bar_24dp);
                    break;
                case 4:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_4_bar_24dp);
                    break;
                default:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_1_bar_24dp);
                    break;
            }
        } else {
            switch (mDataset.get(position).getLevel()) {
                case 2:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_2_bar_lock_24dp);
                    break;
                case 3:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_3_bar_lock_24dp);
                    break;
                case 4:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_4_bar_lock_24dp);
                    break;
                default:
                    levelDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_signal_wifi_1_bar_lock_24dp);
                    break;
            }
        }

        holder.mWifiLevel.setImageDrawable(levelDrawable);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        }
        return 0;
    }

    public WifiNetwork getItem(int position) {
        if (mDataset != null && mDataset.size() > position) {
            return mDataset.get(position);
        }
        return null;
    }

    public void updateDataset(List<WifiNetwork> items) {
        mDataset.clear();
        this.notifyDataSetChanged();
        for (WifiNetwork item : items) {
            mDataset.add(item);
            this.notifyItemInserted(mDataset.size() - 1);
        }
    }

    public interface ScanResultClickListener {
        void onItemClick(int position, View v);
    }
}
