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
package org.openconnectivity.otgc.domain.model.devicelist;

import org.iotivity.OCEndpoint;
import org.iotivity.OCEndpointUtil;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;

import java.io.Serializable;
import java.util.List;

/**
 * Model class for unowned and owned devices
 */

public class Device implements Comparable<Device>, Serializable {
    public static final int NOTHING_PERMITS = 0;
    public static final int DOXS_PERMITS = 1;
    public static final int ACL_PERMITS = 2;
    public static final int CRED_PERMITS = 4;
    public static final int FULL_PERMITS = 8;

    private DeviceType deviceType;
    private DeviceRole role;
    private String deviceId;
    private transient OcDeviceInfo deviceInfo;
    private String ipv6SecureHost;
    private String ipv6TcpSecureHost;
    private String ipv6Host;
    private String ipv6TcpHost;
    private String ipv4SecureHost;
    private String ipv4TcpSecureHost;
    private String ipv4Host;
    private String ipv4TcpHost;
    private int permits;

    public Device() {}

    public Device(DeviceType type, String deviceId, OcDeviceInfo deviceInfo, OCEndpoint endpoints, int permits) {
        this.deviceType = type;
        this.role = DeviceRole.UNKNOWN;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.permits = permits;

        while(endpoints != null) {
            String endpointStr = OCEndpointUtil.toString(endpoints);

            if (endpointStr.contains(".")) {
                if (endpointStr.startsWith("coaps://")) {
                    ipv4SecureHost = endpointStr;
                } else if (endpointStr.startsWith("coaps+tcp://")) {
                    ipv4TcpSecureHost = endpointStr;
                } else if (endpointStr.startsWith("coap://")) {
                    ipv4Host = endpointStr;
                } else if (endpointStr.startsWith("coap+tcp://")) {
                    ipv4TcpHost = endpointStr;
                }
            } else {
                if (endpointStr.startsWith("coaps://")) {
                    ipv6SecureHost = endpointStr;
                } else if (endpointStr.startsWith("coaps+tcp://")) {
                    ipv6TcpSecureHost = endpointStr;
                } else if (endpointStr.startsWith("coap://")) {
                    ipv6Host = endpointStr;
                } else if (endpointStr.startsWith("coap+tcp://")) {
                    ipv6TcpHost = endpointStr;
                }
            }

            endpoints = endpoints.getNext();
        }
    }

    public Device(DeviceType type, String deviceId, OcDeviceInfo deviceInfo, List<String> endpoints, int permits) {
        this.deviceType = type;
        this.role = DeviceRole.UNKNOWN;
        this.deviceId = deviceId;
        this.deviceInfo = deviceInfo;
        this.permits = permits;

        for (String endpoint : endpoints) {
            if (endpoint.contains(".")) {
                if (endpoint.startsWith("coaps://")) {
                    ipv4SecureHost = endpoint;
                } else if (endpoint.startsWith("coaps+tcp://")) {
                    ipv4TcpSecureHost = endpoint;
                } else if (endpoint.startsWith("coap://")) {
                    ipv4Host = endpoint;
                } else if (endpoint.startsWith("coap+tcp://")) {
                    ipv4TcpHost = endpoint;
                }
            } else {
                if (endpoint.startsWith("coaps://")) {
                    ipv6SecureHost = endpoint;
                } else if (endpoint.startsWith("coaps+tcp://")) {
                    ipv6TcpSecureHost = endpoint;
                } else if (endpoint.startsWith("coap://")) {
                    ipv6Host = endpoint;
                } else if (endpoint.startsWith("coap+tcp://")) {
                    ipv6TcpHost = endpoint;
                }
            }
        }
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public DeviceRole getDeviceRole() {
        return this.role;
    }

    public void setDeviceRole(DeviceRole role) {
        this.role = role;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public OcDeviceInfo getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(OcDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getIpv6Host() {
        return this.ipv6Host;
    }

    public void setIpv6Host(String host) {
        this.ipv6Host = host;
    }

    public String getIpv6TcpHost() {
        return this.ipv6TcpHost;
    }

    public void setIpv6TcpHost(String host) {
        this.ipv6TcpHost = host;
    }

    public String getIpv6SecureHost() {
        return this.ipv6SecureHost;
    }

    public void setIpv6SecureHost(String host) {
        this.ipv6SecureHost = host;
    }

    public String getIpv6TcpSecureHost() {
        return this.ipv6TcpSecureHost;
    }

    public void setIpv6TcpSecureHost(String host) {
        this.ipv6TcpSecureHost = host;
    }

    public String getIpv4Host() {
        return this.ipv4Host;
    }

    public void setIpv4Host(String host) {
        this.ipv4Host = host;
    }

    public String getIpv4TcpHost() {
        return this.ipv4TcpHost;
    }

    public void setIpv4TcpHost(String host) {
        this.ipv4TcpHost = host;
    }

    public String getIpv4SecureHost() {
        return this.ipv4SecureHost;
    }

    public void setIpv4SecureHost(String host) {
        this.ipv4SecureHost = host;
    }

    public String getIpv4TcpSecureHost() {
        return this.ipv4SecureHost;
    }

    public void setIpv4TcpSecureHost(String host) {
        this.ipv4SecureHost = host;
    }

    public boolean equalsHosts(Device device) {
        if (this.getIpv6Host() != null && device.getIpv6Host() != null
                && !this.getIpv6Host().equals(device.getIpv6Host())) {
            return false;
        }

        if (this.getIpv6TcpHost() != null && device.getIpv6TcpHost() != null
                && !this.getIpv6TcpHost().equals(device.getIpv6TcpHost())) {
            return false;
        }

        if (this.getIpv6SecureHost() != null && device.getIpv6SecureHost() != null
                && !this.getIpv6SecureHost().equals(device.getIpv6SecureHost())) {
            return false;
        }

        if (this.getIpv6TcpSecureHost() != null && device.getIpv6TcpSecureHost() != null
                && !this.getIpv6TcpSecureHost().equals(device.getIpv6TcpSecureHost())) {
            return false;
        }

        if (this.getIpv4Host() != null && device.getIpv4Host() != null
                && !this.getIpv4Host().equals(device.getIpv4Host())) {
            return false;
        }

        if (this.getIpv4TcpHost() != null && device.getIpv4TcpHost() != null
                && !this.getIpv4TcpHost().equals(device.getIpv4TcpHost())) {
            return false;
        }

        if (this.getIpv4SecureHost() != null && device.getIpv4SecureHost() != null
                && !this.getIpv4SecureHost().equals(device.getIpv4SecureHost())) {
            return false;
        }

        if (this.getIpv4TcpSecureHost() != null && device.getIpv4TcpSecureHost() != null
                && !this.getIpv4TcpSecureHost().equals(device.getIpv4TcpSecureHost())) {
            return false;
        }

        return true;
    }

    public int getPermits() {
        return permits;
    }

    public void setPermits(int permits) {
        this.permits = permits;
    }

    public boolean hasACLpermit() {
        return (permits & ACL_PERMITS) == ACL_PERMITS;
    }

    public boolean hasDOXSpermit() {
        return (permits & DOXS_PERMITS) == DOXS_PERMITS;
    }

    public boolean hasCREDpermit() {
        return (permits & CRED_PERMITS) == CRED_PERMITS;
    }

    @Override
    public int compareTo(Device device) {
        int res;

        if (this.getDeviceType() ==  device.getDeviceType()) {     // Same types
            int nameComparision;
            if (this.getDeviceInfo().getName() == null && device.getDeviceInfo().getName() == null) {
                nameComparision = 0;
            } else if (this.getDeviceInfo().getName() == null) {
                nameComparision = -1;
            } else if (device.getDeviceInfo().getName() == null) {
                nameComparision = 1;
            } else {
                nameComparision = this.getDeviceInfo().getName().compareTo(device.getDeviceInfo().getName());
            }

            int uuidComparision = this.getDeviceId().compareTo(device.getDeviceId());

            // order by name or order by UUID if the names are equals
            res = (nameComparision == 0) ? uuidComparision : nameComparision;

        } else {    // Different types

            if (this.getDeviceType() == DeviceType.UNOWNED) {    // Is device1 unowned?
                res = -1;
            } else if (device.getDeviceType() == DeviceType.UNOWNED) {     // Is device2 unowned?
                res = 1;
            } else {
                int permissionComparision = this.getPermits() - device.getPermits();
                res = -1 * permissionComparision;
            }

        }

        return res;
    }

    @Override
    public boolean equals(Object device) {
        boolean same = false;

        if (device != null && device instanceof Device) {
            same = this.deviceId.equals(((Device)device).getDeviceId());
        }
        return same;
    }
}
