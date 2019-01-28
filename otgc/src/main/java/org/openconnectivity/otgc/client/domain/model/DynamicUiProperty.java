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

package org.openconnectivity.otgc.client.domain.model;

import java.io.Serializable;

public class DynamicUiProperty implements Serializable {

    private String mName;
    private String mType;
    private boolean mReadOnly;

    public DynamicUiProperty() {
        this("", "", true);
    }

    public DynamicUiProperty(String name, String type, boolean readOnly) {
        this.mName = name;
        this.mType = type;
        this.mReadOnly = readOnly;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        this.mType = type;
    }

    public boolean isReadOnly() {
        return mReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.mReadOnly = readOnly;
    }
}
