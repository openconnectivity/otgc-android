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

package org.openconnectivity.otgc.common.presentation.viewmodel;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * a generic class that describes a data with a status
 */
public class Response<T> {
    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

    private Response(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Response<T> success(@NonNull T data) {
        return new Response<>(Status.SUCCESS, data, null);
    }

    public static <T> Response<T> error(String msg, @Nullable T data) {
        return new Response<>(Status.ERROR, data, msg);
    }

    public static <T> Response<T> error(Throwable t) {
        return Response.error(t.getLocalizedMessage(), null);
    }

    public static <T> Response<T> loading(@Nullable T data) {
        return new Response<>(Status.LOADING, data, null);
    }

    public static <T> Response<T> loading() {
        return Response.loading(null);
    }

    public static <T> Response<T> complete(@Nullable T data) {
        return new Response<>(Status.COMPLETE, data, null);
    }

    public static <T> Response<T> complete() {
        return Response.complete(null);
    }
}
