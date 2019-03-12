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

package org.openconnectivity.otgc.logviewer.presentation.view;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Switch;

import org.openconnectivity.otgc.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogViewerActivity extends AppCompatActivity {

    @BindView(R.id.logviewer_iotivity_switch) Switch logSwitch;
    @BindView(R.id.logviewer_webview) WebView webView;

    private String iotivityLog = "";
    private String otgcLog = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logviewer);
        ButterKnife.bind(this);

        String fileNameTimeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        otgcLog = "file:///" + getExternalFilesDir(null).toString() + "/" + fileNameTimeStamp + ".html";
        iotivityLog = "file:///" + getExternalFilesDir(null).toString() + "/log/logcat" + fileNameTimeStamp + ".html";

        logSwitch.setOnClickListener(v -> loadWebView() );
        webView.loadUrl(iotivityLog);
    }

    private void loadWebView() {
        if (logSwitch.isChecked()) {
            webView.loadUrl(iotivityLog);
        } else {
            webView.loadUrl(otgcLog);
        }
    }
}
