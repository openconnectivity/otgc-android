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

package org.openconnectivity.otgc.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.openconnectivity.otgc.R;

import timber.log.Timber;

public class UiUtils {

    private UiUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void parseThrowableToToast(Activity activity, Throwable throwable) {
        /*if (throwable instanceof OcException) {
            if (((OcException) throwable).getErrorCode().equals(ErrorCode.UNAUTHORIZED_REQ)) {
                activity.runOnUiThread(() -> Toast.makeText(activity, R.string.client_unauthorized_request, Toast.LENGTH_SHORT).show());
            } else {
                Timber.e(throwable);
            }
        } else {
            Timber.e(throwable);
        }*/
    }

    public static AlertDialog createProgressDialog(final Context context,
                                                   final String message) {
        View progressView = View.inflate(context, R.layout.dialog_progress, null);
        TextView progressMessage = progressView.findViewById(R.id.progress_message);
        progressMessage.setText(message);

        AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AppTheme))
                .setView(progressView)
                .create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        return alertDialog;
    }
}
