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

import org.openconnectivity.otgc.domain.model.devicelist.Device;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.widget.SortedList;

public class MyItemKeyProvider extends ItemKeyProvider {
    private final SortedList<Device> itemList;

    public MyItemKeyProvider(int scope, SortedList<Device> itemList) {
        super(scope);
        this.itemList = itemList;
    }

    @Nullable
    @Override
    public Object getKey(int position) {
        return itemList.get(position);
    }

    @Override
    public int getPosition(@NonNull Object key) {
        return itemList.indexOf((Device)key);
    }
}
