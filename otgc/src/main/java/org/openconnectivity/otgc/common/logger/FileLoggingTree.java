/* ******************************************************************
 *
 * Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 * ******************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ******************************************************************/
package org.openconnectivity.otgc.common.logger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    private static final String TAG = FileLoggingTree.class.getSimpleName();

    private static final SparseArray<String> LOG_LEVELS;
    static {
        LOG_LEVELS = new SparseArray<>();
        LOG_LEVELS.put(Log.VERBOSE, "VERBOSE");
        LOG_LEVELS.put(Log.DEBUG, "DEBUG");
        LOG_LEVELS.put(Log.INFO, "INFO");
        LOG_LEVELS.put(Log.WARN, "WARN");
        LOG_LEVELS.put(Log.ERROR, "ERROR");
    }

    private Context mContext;

    public FileLoggingTree(Context context) {
        mContext = context;
    }

    @Override
    protected void log(int priority, String tag, @NonNull String message, Throwable t) {
        super.log(priority, tag, message, t);

        String fileNameTimeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String logTimeStamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS", Locale.getDefault()).format(new Date());

        String fileName = fileNameTimeStamp + ".html";

        File file = new File(mContext.getExternalFilesDir(null), fileName);

        try {
            file.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error while logging into file: " + e);
            return;
        }

        if (file.exists()) {
            try (OutputStream fileOutputStream =
                    new FileOutputStream(file, true)) {
                String strPriority;
                switch (priority) {
                    case Log.ERROR:
                        strPriority = "<font color=\"red\">" + LOG_LEVELS.get(priority) + "</font>";
                        break;
                    case Log.WARN:
                        strPriority = "<font color=\"yellow\">" + LOG_LEVELS.get(priority) + "</font>";
                        break;
                    default:
                        strPriority = LOG_LEVELS.get(priority);
                        break;
                }
                fileOutputStream.write(
                        ("<p style=\"background:lightgray;\"><strong style=\"background:lightblue;\">&nbsp&nbsp"
                                + logTimeStamp + " :&nbsp&nbsp</strong>&nbsp&nbsp"
                                + strPriority + "&nbsp&nbsp"
                                + tag + "&nbsp&nbsp"
                                + message + "</p>")
                                .getBytes());
            } catch (Exception e) {
                Log.e(TAG, "Error while logging into file: " + e);
            }
        }
    }
}
