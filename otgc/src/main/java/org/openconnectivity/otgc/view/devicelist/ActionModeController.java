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

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceType;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceRole;
import org.openconnectivity.otgc.domain.usecase.link.RetrieveLinkedDevicesUseCase;

import java.util.Iterator;
import java.util.List;

import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.SelectionTracker;

public class ActionModeController implements ActionMode.Callback {
    private final Context mContext;
    private final SelectionTracker mSelectionTracker;
    private static MyMenuItemClickListener sMyMenuItemClickListener;

    private final RetrieveLinkedDevicesUseCase retrieveLinkedDevicesUseCase;

    public ActionModeController(Context context, SelectionTracker selectionTracker,
                                RetrieveLinkedDevicesUseCase retrieveLinkedDevicesUseCase) {
        this.mContext = context;
        this.mSelectionTracker = selectionTracker;
        this.retrieveLinkedDevicesUseCase = retrieveLinkedDevicesUseCase;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.menu_devices_selection, menu); // Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        MenuItem linkMenuItem = menu.findItem(R.id.action_pairwise);
        MenuItem unlinkMenuItem = menu.findItem(R.id.action_unlink);
        if (mSelectionTracker.hasSelection() && mSelectionTracker.getSelection().size() == 2) {
            Device server = null;
            Device client = null;
            Iterator<Device> deviceIterable = mSelectionTracker.getSelection().iterator();
            while (deviceIterable.hasNext()) {
                Device device = deviceIterable.next();
                if (device.getDeviceRole().equals(DeviceRole.SERVER) && device.getDeviceType().equals(DeviceType.OWNED_BY_SELF)) {
                    server = device;
                } else if (device.getDeviceRole().equals(DeviceRole.CLIENT) && device.getDeviceType().equals(DeviceType.OWNED_BY_SELF)) {
                    client = device;
                }
            }

            if (server != null && client != null) {
                String serverId = server.getDeviceId();
                final Device c = client;
                final Device s = server;

                List<String> linkClientDevices = retrieveLinkedDevicesUseCase.execute(client).blockingGet();

                if (linkClientDevices.contains(serverId)) {
                    unlinkMenuItem.setOnMenuItemClickListener(menuItem -> {
                        actionMode.finish();
                        return sMyMenuItemClickListener.onMenuItemClick(menuItem, c, s);
                    });
                    unlinkMenuItem.setVisible(true);
                } else {
                    linkMenuItem.setOnMenuItemClickListener(menuItem -> {
                        actionMode.finish();
                        return sMyMenuItemClickListener.onMenuItemClick(menuItem, c, s);
                    });
                    linkMenuItem.setVisible(true);
                }
            }
        } else {
            linkMenuItem.setVisible(false);
            unlinkMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        mSelectionTracker.clearSelection();
    }

    public static void setOnMenuItemClickListener(MyMenuItemClickListener myMenuItemClickListener) {
        sMyMenuItemClickListener = myMenuItemClickListener;
    }

    public interface MyMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem, Device client, Device server);
    }
}
