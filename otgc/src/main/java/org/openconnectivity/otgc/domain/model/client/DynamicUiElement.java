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

package org.openconnectivity.otgc.domain.model.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DynamicUiElement implements Serializable {

    private String path;
    private List<String> resourceTypes;
    private List<String> interfaces;
    private List<DynamicUiProperty> properties;
    private List<String> supportedOperations;

    public DynamicUiElement() {
        this.path = "";
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getResourceTypes() {
        if (resourceTypes == null) {
            resourceTypes = new ArrayList<>();
        }
        return resourceTypes;
    }

    public void setResourceTypes(List<String> resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public List<String> getInterfaces() {
        if (interfaces == null) {
            interfaces = new ArrayList<>();
        }
        return interfaces;
    }

    public void setInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public List<DynamicUiProperty> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return properties;
    }

    public void setProperties(List<DynamicUiProperty> properties) {
        this.properties = properties;
    }

    public List<String> getSupportedOperations() {
        if (supportedOperations == null) {
            supportedOperations = new ArrayList<>();
        }
        return supportedOperations;
    }

    public void setSupportedOperations(List<String> supportedOperations) {
        this.supportedOperations = supportedOperations;
    }
}
