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

package org.openconnectivity.otgc.utils.constant;

import java.util.ArrayList;
import java.util.List;

public class OcfResourceType {

    private OcfResourceType() {
        throw new IllegalStateException("Constants class");
    }

    private static final String OIC_WK_PREFIX = "oic.wk.";
    public static final String DEVICE = OIC_WK_PREFIX + "d";
    public static final String DEVICE_CONF = OIC_WK_PREFIX + "con";
    public static final String PLATFORM = OIC_WK_PREFIX + "p";
    public static final String PLATFORM_CONF = OIC_WK_PREFIX + "con.p";
    public static final String RES = OIC_WK_PREFIX + "res";
    public static final String RESOURCE_DIRECTORY = OIC_WK_PREFIX + "rd";
    public static final String MAINTENANCE = OIC_WK_PREFIX + "mnt";
    public static final String INTROSPECTION = OIC_WK_PREFIX + "introspection";
    public static final String INTROSPECTION_PAYLOAD = OIC_WK_PREFIX + "introspection.payload";

    private static final String OIC_RT_PREFIX = "oic.r.";
    public static final String ICON = OIC_RT_PREFIX + "icon";
    public static final String DOXM = OIC_RT_PREFIX + "doxm";
    public static final String PSTAT = OIC_RT_PREFIX + "pstat";
    public static final String ACL2 = OIC_RT_PREFIX + "acl2";
    public static final String CRED = OIC_RT_PREFIX + "cred";
    public static final String CRL = OIC_RT_PREFIX + "crl";
    public static final String CSR = OIC_RT_PREFIX + "csr";
    public static final String ROLES = OIC_RT_PREFIX + "roles";
    public static final String SECURITY_PROFILES = OIC_RT_PREFIX + "sp";
    public static final String ACCELERATION_SENSOR = OIC_RT_PREFIX + "sensor.acceleration";
    public static final String ACTIVITY_COUNT_SENSOR = OIC_RT_PREFIX + "sensor.activity.count";
    public static final String AIR_QUALITY = OIC_RT_PREFIX + "airquality";
    public static final String AIR_QUALITY_COLLECTION = OIC_RT_PREFIX + "airqualitycollection";
    public static final String ALTIMETER = OIC_RT_PREFIX + "altimeter";
    public static final String ATMOSPHERIC_PRESSURE = OIC_RT_PREFIX + "sensor.atmosphericpressure";
    public static final String AIR_FLOW = OIC_RT_PREFIX + "airflow";
    public static final String AIR_FLOW_CONTROL = OIC_RT_PREFIX + "airflowcontrol";
    public static final String AUDIO = OIC_RT_PREFIX + "audio";
    public static final String AUTOFOCUS = OIC_RT_PREFIX + "autofocus";
    public static final String AUTOMATIC_DOCUMENT_FEEDER = OIC_RT_PREFIX + "automaticdocumentfeeder";
    public static final String AUTO_WHITE_BALANCE = OIC_RT_PREFIX + "autowhitebalance";
    public static final String BATTERY_ENERGY = OIC_RT_PREFIX + "energy.battery";
    public static final String BATTERY_MATERIAL = OIC_RT_PREFIX + "batterymaterial";
    public static final String BINARY_SWITCH = OIC_RT_PREFIX + "switch.binary";
    public static final String BREWING = OIC_RT_PREFIX + "brewing";
    public static final String BRIGHTNESS = OIC_RT_PREFIX + "light.brightness";
    public static final String BUTTON = OIC_RT_PREFIX + "button";
    public static final String CARBONDIOXIDE_SENSOR = OIC_RT_PREFIX + "sensor.carbondioxide";
    public static final String CARBONMONOXIDE_SENSOR = OIC_RT_PREFIX + "sensor.carbonmonoxide";
    public static final String CLOCK = OIC_RT_PREFIX + "clock";
    public static final String COLOUR_CHROMA = OIC_RT_PREFIX + "colour.chroma";
    public static final String COLOUR_HS = OIC_RT_PREFIX + "colour.hs";
    public static final String COLOUR_RGB = OIC_RT_PREFIX + "colour.rgb";
    public static final String COLOUR_SATURATION = OIC_RT_PREFIX + "colour.saturation";
    public static final String COLOUR_CSC = OIC_RT_PREFIX + "colour.csc";
    public static final String COLOUR_TEMPERATURE = OIC_RT_PREFIX + "colour.temperature";
    public static final String CONSUMABLE = OIC_RT_PREFIX + "consumable";
    public static final String CONSUMABLE_COLLECTION = OIC_RT_PREFIX + "consumablecollection";
    public static final String CONTACT_SENSOR = OIC_RT_PREFIX + "sensor.contact";
    public static final String DELAY_DEFROST = OIC_RT_PREFIX + "delaydefrost";
    public static final String DRLC_ENERGY = OIC_RT_PREFIX + "energy.drlc";
    public static final String DIMMING_LIGHT = OIC_RT_PREFIX + "light.dimming";
    public static final String DOOR = OIC_RT_PREFIX + "door";
    public static final String ECO_MODE = OIC_RT_PREFIX + "ecomode";
    public static final String VEHICLE_CONNECTOR = OIC_RT_PREFIX + "vehicle.connector";
    public static final String ENERGY_ELECTRICAL = OIC_RT_PREFIX + "energy.electrical";
    public static final String ENERGY_CONSUMPTION = OIC_RT_PREFIX + "energy.consumption";
    public static final String ENERGY_GENERATION = OIC_RT_PREFIX + "energy.generation";
    public static final String ENERGY_OVERLOAD = OIC_RT_PREFIX + "energy.overload";
    public static final String ENERGY_USAGE = OIC_RT_PREFIX + "energy.usage";
    public static final String FOAMING = OIC_RT_PREFIX + "foaming";
    public static final String SENSOR = OIC_RT_PREFIX + "sensor";
    public static final String GEOLOCATION_SENSOR = OIC_RT_PREFIX + "sensor.geolocation";
    public static final String GLASSBREAK_SENSOR = OIC_RT_PREFIX + "sensor.glassbreak";
    public static final String GRINDER = OIC_RT_PREFIX + "grinder";
    public static final String HEART_ZONE_SENSOR = OIC_RT_PREFIX + "sensor.heart.zone";
    public static final String HEATING_ZONE = OIC_RT_PREFIX + "sensor.heatingzone";
    public static final String HEATING_ZONE_COLLECTION = OIC_RT_PREFIX + "heatingzonecollection";
    public static final String HEIGHT = OIC_RT_PREFIX + "height";
    public static final String HUMIDITY = OIC_RT_PREFIX + "humidity";
    public static final String ICEMAKER = OIC_RT_PREFIX + "icemaker";
    public static final String ILLUMINANCE_SENSOR = OIC_RT_PREFIX + "sensor.illuminance";
    public static final String LIQUID_LEVEL = OIC_RT_PREFIX + "liquid.level";
    public static final String LOCK_STATUS = OIC_RT_PREFIX + "lock.status";
    public static final String LOCK_CODE = OIC_RT_PREFIX + "lock.code";
    public static final String MAGNETIC_FIELD_DIRECTION = OIC_RT_PREFIX + "sensor.magneticfielddirection";
    public static final String MEDIA = OIC_RT_PREFIX + "media";
    public static final String MEDIA_SOURCE = OIC_RT_PREFIX + "mediasource";
    public static final String MEDIA_SOURCE_LIST = OIC_RT_PREFIX + "mediasourcelist";
    public static final String MEDIA_INPUT = OIC_RT_PREFIX + "media.input";
    public static final String MEDIA_OUTPUT = OIC_RT_PREFIX + "media.output";
    public static final String MODE = OIC_RT_PREFIX + "mode";
    public static final String LINEAR_MOVEMENT = OIC_RT_PREFIX + "movement.linear";
    public static final String MOTION_SENSOR = OIC_RT_PREFIX + "sensor.motion";
    public static final String NIGHT_MODE = OIC_RT_PREFIX + "nightmode";
    public static final String OPEN_LEVEL = OIC_RT_PREFIX + "openlevel";
    public static final String OPERATIONAL_STATE = OIC_RT_PREFIX + "operational.state";
    public static final String PTZ = OIC_RT_PREFIX + "ptz";
    public static final String PRESENCE_SENSOR = OIC_RT_PREFIX + "sensor.presence";
    public static final String LIGHT_RAMP_TIME = OIC_RT_PREFIX + "light.ramptime";
    public static final String REFRIGERATION = OIC_RT_PREFIX + "refrigeration";
    public static final String SELECTABLE_LEVELS = OIC_RT_PREFIX + "selectablelevels";
    public static final String SIGNALS_STRENGTH = OIC_RT_PREFIX + "signalsstrength";
    public static final String SLEEP_SENSOR = OIC_RT_PREFIX + "sensor.sleep";
    public static final String SMOKE_SENSOR = OIC_RT_PREFIX + "sensor.smoke";
    public static final String SPEECH_TTS = OIC_RT_PREFIX + "speech.tts";
    public static final String TEMPERATURE = OIC_RT_PREFIX + "temperature";
    public static final String THREE_AXIS_SENSOR = OIC_RT_PREFIX + "sensor.threeaxis";
    public static final String TIME_PERIOD = OIC_RT_PREFIX + "time.period";
    public static final String TOUCH_SENSOR = OIC_RT_PREFIX + "sensor.touch";
    public static final String UV_RADIATION_SENSOR = OIC_RT_PREFIX + "sensor.radiation.uv";
    public static final String CONDITIONAL_VALUE = OIC_RT_PREFIX + "value.conditional";
    public static final String WATER_SENSOR = OIC_RT_PREFIX + "sensor.water";
    public static final String WEIGHT = OIC_RT_PREFIX + "weight";

    protected static final List<String> NON_VERTICAL_RESOURCE_TYPES;
    static {
        NON_VERTICAL_RESOURCE_TYPES = new ArrayList<>();

        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.DEVICE);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.DEVICE_CONF);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.PLATFORM);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.PLATFORM_CONF);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.RES);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.RESOURCE_DIRECTORY);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.MAINTENANCE);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.ICON);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.INTROSPECTION);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.INTROSPECTION_PAYLOAD);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.DOXM);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.PSTAT);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.ACL2);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.CRED);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.CRL);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.CSR);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.ROLES);
        NON_VERTICAL_RESOURCE_TYPES.add(OcfResourceType.SECURITY_PROFILES);
    }

    public static boolean isVerticalResourceType(String resourceType) {
        return !NON_VERTICAL_RESOURCE_TYPES.contains(resourceType);
    }
}
