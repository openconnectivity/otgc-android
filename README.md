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

The OTGC Android App uses the binary distribution of the IoTivity-lite Android API.

IoTivity-lite Android API is compiled for a minimum SDK of API 21: Android 5.0 (Lollipop), so OTGC Android App shall use the same value.

To import the IoTivity-lite Android API Binary into the OTGC Android App project:

1. Go to &lt;iotivity-lite>/swig/iotivity-lite-java/libs.

2. Copy **iotivity-lite.jar** into &lt;otgc-android>/otgc/src/main/jniLibs.

3. Copy **libiotivity-lite-jni.so** into &lt;otgc-android>/otgc/src/main/jniLibs/&lt;target_arch>

where &lt;target_arch> is one of the following values: x86, x86_64, armeabi, armeabi-v7a, armeabi-v7a-hard or arm64-v8a.
    
Android Studio loads the library, including the contents of the library module. IoTivity-lite Android APIs can now be used in the OTGC Android App project.
  
## Build

### IoTivity-lite Android API

The steps required to build the binary of the IoTivity-lite Android API is shown below:

1. Change to the **swig** branch.
```
git checkout swig
```
2. Apply the patch of the OTGC in IoTivity-lite
```
git apply <otgc-android>/extlibs/patchs/remove_cred_by_credid.patch
```
3. Go to the **android** directory.
```
cd <iotivity-lite>/port/android
```
4. Execute the command to build the library.
```
make NDK_HOME=<ndk-directory> ANDROID_API=21 DEBUG=1 SECURE=1 IPV4=1 TCP=0 PKI=1 DYNAMIC=1 CLOUD=0 JAVA=1 IDD=1
```

Once built, the library can be found at:
```
<iotivity-lite>/swig/iotivity-lite-java/libs
```
  
## Testing
  
## Usage
  
## License

This library is released under Apache 2.0 license (http://www.apache.org/licenses/LICENSE-2.0.txt).

Please see the file 'LICENSE.md' in the root folder for further information.