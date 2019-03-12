/*
 * *****************************************************************
 *
 *  Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 *  ******************************************************************
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  ******************************************************************
 */
package org.openconnectivity.otgc.devicelist.presentation.view;

import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.SortedList;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.devicelist.domain.model.Device;
import org.openconnectivity.otgc.devicelist.domain.model.DeviceType;
import org.openconnectivity.otgc.devicelist.domain.model.Role;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoxsListAdapter extends RecyclerView.Adapter<DoxsListAdapter.DoxsListViewHolder> {
    SortedList<Device> mDataset;
    private Context mContext;
    private SelectionTracker mSelectionTracker;
    private static MyClickListener sMyClickListener;
    private static MyMenuItemClickListener sMyMenuItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class DoxsListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, ViewHolderWithDetails {
        // each data item is just a string in this case
        @BindView(R.id.card_view) View mView;
        @BindView(R.id.line_device_type) View mLineDeviceType;
        @BindView(R.id.text_device_name) TextView mDeviceName;
        @BindView(R.id.text_device_uuid) TextView mDeviceUuid;
        @BindView(R.id.text_device_role) TextView mDeviceRole;
        @BindView(R.id.text_device_type) TextView mDeviceType;
        //@BindView(R.id.img_btn_bottom_right) ImageButton mImageButton;
        @BindView(R.id.img_btn_popup_menu) ImageButton mPopupButton;
        @BindView(R.id.img_btn_add_device) ImageButton mAddDeviceButton;
        @BindView(R.id.img_btn_generic_client) ImageButton mClientButton;

        private DoxsListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mAddDeviceButton.setOnClickListener(this);
            ((View)mAddDeviceButton.getParent()).post(() -> {
                Rect delegateArea = new Rect();
                ImageButton delegate = mAddDeviceButton;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 48;
                delegateArea.bottom += 48;
                delegateArea.left -= 48;
                delegateArea.bottom += 48;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);
                ((View)mAddDeviceButton.getParent()).setTouchDelegate(expandedArea);
            });

            mClientButton.setOnClickListener(this);
            ((View)mClientButton.getParent()).post(() -> {
                Rect delegateArea = new Rect();
                ImageButton delegate = mClientButton;
                delegate.getHitRect(delegateArea);
                delegateArea.top -= 48;
                delegateArea.bottom += 48;
                delegateArea.left -= 48;
                delegateArea.bottom += 48;
                TouchDelegate expandedArea = new TouchDelegate(delegateArea, delegate);
                ((View)mClientButton.getParent()).setTouchDelegate(expandedArea);
            });
        }

        @Override
        public void onClick(View v) {
            sMyClickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            return sMyMenuItemClickListener.onMenuItemClick(getAdapterPosition(), item);
        }

        @Override
        public ItemDetailsLookup.ItemDetails getItemDetails() {
            return new MyItemDetail(getAdapterPosition(), mDataset.get(getAdapterPosition()));
        }

        public final void bind(Device device, boolean isActive) {
            mView.setActivated(isActive);
        }
    }

    DoxsListAdapter(Context ctx) {
        mContext = ctx;

        mDataset = new SortedList<>(Device.class, new SortedList.Callback<Device>() {
            @Override
            public int compare(Device d1, Device d2) {
                int comparison = getDeviceName(d1).compareTo(getDeviceName(d2));
                if (comparison == 0) {
                    return d1.getDeviceId().compareTo(d2.getDeviceId());
                } else {
                    return comparison;
                }
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Device oldItem, Device newItem) {
                return oldItem.getDeviceId().equals(newItem.getDeviceId())
                        && oldItem.getType().equals(newItem.getType());
            }

            @Override
            public boolean areItemsTheSame(Device item1, Device item2) {
                return item1.getDeviceId().equals(item2.getDeviceId());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    public static void setOnItemClickListener(MyClickListener myClickListener) {
        sMyClickListener = myClickListener;
    }

    public static void setOnMenuItemClickListener(MyMenuItemClickListener myMenuItemClickListener) {
        sMyMenuItemClickListener = myMenuItemClickListener;
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.mSelectionTracker = selectionTracker;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public DoxsListAdapter.DoxsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device, parent, false);

        return new DoxsListViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull DoxsListViewHolder holder, int position) {
        // - get element from your dataset at this position
        Device device = mDataset.get(position);
        // - replace the contents of the view with that element
        if (device.getDeviceInfo() != null) {
            holder.mDeviceName.setText(
                    device.getDeviceInfo().getName().isEmpty() ?
                            mContext.getString(R.string.devices_cardview_unnamed_device) :
                            device.getDeviceInfo().getName()
            );

            if (!device.getDeviceInfo().getFormattedDeviceTypes().isEmpty()) {
                holder.mDeviceType.setText(
                        TextUtils.join(",", device.getDeviceInfo().getFormattedDeviceTypes())
                );
            } else {
                holder.mDeviceType.setText(mContext.getString(R.string.devices_cardview_no_device_types));
            }
        } else {
            holder.mDeviceName.setText(mContext.getString(R.string.devices_cardview_unnamed_device));
            holder.mDeviceType.setText(mContext.getString(R.string.devices_cardview_no_device_types));
        }

        if (device.getRole().equals(Role.CLIENT)) {
            holder.mDeviceRole.setText(mContext.getString(R.string.devices_cardview_role_client));
        } else if (device.getRole().equals(Role.SERVER)) {
            holder.mDeviceRole.setText(mContext.getString(R.string.devices_cardview_role_server));
        } else {
            holder.mDeviceRole.setText(mContext.getString(R.string.devices_cardview_role_unknown));
        }

        holder.mDeviceUuid.setText(device.getDeviceId());

        holder.mPopupButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mContext, holder.mPopupButton);
            popupMenu.inflate(R.menu.menu_owned_devices);
            popupMenu.setOnMenuItemClickListener(holder);

            if (device.getType().equals(DeviceType.OWNED_BY_OTHER)) {
                popupMenu.getMenu().findItem(R.id.menu_item_set_device_name).setVisible(false);
            }

            popupMenu.show();
        });

        int color = ContextCompat.getColor(mContext, R.color.OCF_BLACK);

        switch (device.getType()) {
            case UNOWNED:
                color = ContextCompat.getColor(mContext, R.color.ocf_light_blue);
                holder.mAddDeviceButton.setVisibility(View.VISIBLE);
                holder.mClientButton.setVisibility(View.GONE);
                holder.mPopupButton.setVisibility(View.GONE);
                break;
            case OWNED_BY_SELF:
                color = ContextCompat.getColor(mContext, R.color.ocf_green);
                holder.mAddDeviceButton.setVisibility(View.GONE);
                holder.mClientButton.setVisibility(View.VISIBLE);
                holder.mPopupButton.setVisibility(View.VISIBLE);
                break;
            case OWNED_BY_OTHER:
                color = ContextCompat.getColor(mContext, R.color.OCF_ORANGE);
                holder.mAddDeviceButton.setVisibility(View.GONE);
                holder.mClientButton.setVisibility(View.VISIBLE);
                holder.mPopupButton.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        holder.mLineDeviceType.setBackgroundColor(color);

        holder.bind(device, mSelectionTracker.isSelected(device));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset != null) {
            return mDataset.size();
        }
        return 0;
    }

    public void clearItems() {
        mDataset.beginBatchedUpdates();
        while (mDataset.size() > 0) {
            mDataset.removeItemAt(mDataset.size() -1);
        }
        mDataset.endBatchedUpdates();
    }

    public void addItem(Device item) {
        if (item != null) {
            mDataset.add(item);
        }
    }

    public int updateItem(int position, Device deviceToUpdate) {
        mDataset.updateItemAt(position, deviceToUpdate);
        return mDataset.indexOf(deviceToUpdate);
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public interface MyMenuItemClickListener {
        boolean onMenuItemClick(int position, MenuItem item);
    }

    private String getDeviceName(Device device) {
        return device.getDeviceInfo() != null && !device.getDeviceInfo().getName().isEmpty() ?
                device.getDeviceInfo().getName() : mContext.getString(R.string.devices_cardview_unnamed_device);
    }
}
