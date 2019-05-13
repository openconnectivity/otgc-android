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

import androidx.annotation.NonNull;

import org.iotivity.OCRep;
import org.iotivity.OCRepresentation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SerializableResource implements Serializable {
    private String uri;
    private List<String> hosts;
    private List<String> types;
    private List<String> interfaces;
    Map<String, Object> properties = new HashMap<>();

    private boolean observing = false;
    private boolean observable = true;

    public SerializableResource(){}

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public @NonNull List<String> getHosts() {
        if (hosts == null) {
            this.hosts = new ArrayList<>();
        }

        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public @NonNull List<String> getResourceTypes() {
        if (types == null) {
            this.types = new ArrayList<>();
        }

        return types;
    }

    public void setResourceTypes(List<String> types) {
        this.types = types;
    }

    public @NonNull List<String> getResourceInterfaces() {
        if (interfaces == null) {
            this.interfaces = new ArrayList<>();
        }

        return interfaces;
    }

    public void setResourceInterfaces(List<String> interfaces) {
        this.interfaces = interfaces;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(OCRepresentation ocRepresentation) {
        while (ocRepresentation != null) {
            switch (ocRepresentation.getType()) {
                case OC_REP_BOOL:
                    properties.put(ocRepresentation.getName(), ocRepresentation.getValue().getBool());
                    break;
                case OC_REP_STRING:
                    properties.put(ocRepresentation.getName(), ocRepresentation.getValue().getString());
                    break;
                case OC_REP_STRING_ARRAY:
                    properties.put(ocRepresentation.getName(), OCRep.ocArrayToStringArray(ocRepresentation.getValue().getArray()));
                    break;
                case OC_REP_INT:
                    properties.put(ocRepresentation.getName(), (int)ocRepresentation.getValue().getInteger());
                    break;
                case OC_REP_INT_ARRAY:
                    properties.put(ocRepresentation.getName(), OCRep.ocArrayToLongArray(ocRepresentation.getValue().getArray()));
                    break;
                case OC_REP_DOUBLE:
                    properties.put(ocRepresentation.getName(), ocRepresentation.getValue().getDouble());
                    break;
                case OC_REP_DOUBLE_ARRAY:
                    properties.put(ocRepresentation.getName(), OCRep.ocArrayToDoubleArray(ocRepresentation.getValue().getArray()));
                    break;
                default:
                    break;
            }

            ocRepresentation = ocRepresentation.getNext();
        }
    }

    public boolean isObserving() {
        return this.observing;
    }

    public void setObserving(boolean observing) {
        this.observing = observing;
    }

    public boolean isObservable() {
        return observable;
    }

    public void setObservable(boolean observable) {
        this.observable = observable;
    }
}
