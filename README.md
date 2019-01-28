<!---
  ~ //******************************************************************
  ~ //
  ~ // Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
  ~ //
  ~ //******************************************************************
  ~ //
  ~ // Licensed under the Apache License, Version 2.0 (the "License");
  ~ // you may not use this file except in compliance with the License.
  ~ // You may obtain a copy of the License at
  ~ //
  ~ //      http://www.apache.org/licenses/LICENSE-2.0
  ~ //
  ~ // Unless required by applicable law or agreed to in writing, software
  ~ // distributed under the License is distributed on an "AS IS" BASIS,
  ~ // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ // See the License for the specific language governing permissions and
  ~ // limitations under the License.
  ~ //
  ~ //******************************************************************
  --->
# Onboarding Tool and Generic Client: Android App
  
## Overview

IoTivity uses the [Android Java Coding Standard](https://source.android.com/setup/code-style) for Java code. In consequence, OTGC Android App uses the same coding standard.
  
## Project Setup

The OTGC Android App uses the binary distribution of the [IoTivity Base Android API](#iotivity-base-android-api).

IoTivity Base Android API is compiled for a minimum SDK of API 21: Android 5.0 (Lollipop), so OTGC Android App shall use the same value.

To import the IoTivity Base Android API Binary into the OTGC Android App project:

1. From the File menu, select **New Module**.
2. In the **Choose Module Type** window, do the following:
  1. Select **Import .JAR or .AAR Package**.
  2. Click **Next**.
3. In the Create New Module window, click the **...** button to locate the .aar file.
4. Navigate to the <iotivity>/java/iotivity-android/base/build/outputs/aar directory and select the .aar file. In the example below, iotivity-base-armeabi-release.aar is selected.
5. Click **OK**. Then in the Create New Module window, click **Finish**.
6. In the Android Studio project view, right click the dependent module and select **Open Module Settings**.
7. In the Project Structure window, select the **Dependencies** tab.
8. Right click the **+** icon. From the dropdown, select **Module dependency**.
9. Select the iotivity-base-&lt;target_arch>-&lt;release> module from the list of available modules and click **OK**. In the example below, iotivity-base-armeabi-release.aar is selected.
10. In the Project Structure Window, click **OK**.
    
    Android Studio rebuilds the module, including the contents of the library module the next time the project or module is built. IoTivity Base Android APIs can now be used in the OTGC Android App project.
  
## Build

### IoTivity Base Android API

The binary distribution of the IoTivity Base Android API (a .aar file) can be built following the [Android build instructions](https://wiki.iotivity.org/android_build_instructions). Once built, it can be found at:

    <iotivity>/java/iotivity-android/build/outputs/aar/iotivity-base-<your arch>-<release mode>.aar

Currently, this binary cannot be built using Windows but Linux as host system. The command required to build the binary is shown below.

    scons TARGET_OS=android TARGET_ARCH=<target_arch>
    
where &lt;target_arch> is one of the following values: x86, x86_64, armeabi, armeabi-v7a, armeabi-v7a-hard or arm64-v8a.
  
## Testing
  
## Usage
  
## License

This library is released under Apache 2.0 license (http://www.apache.org/licenses/LICENSE-2.0.txt).

Please see the file 'LICENSE.md' in the root folder for further information.