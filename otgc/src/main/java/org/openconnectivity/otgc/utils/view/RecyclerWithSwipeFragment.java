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
package org.openconnectivity.otgc.utils.view;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.utils.di.Injectable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class RecyclerWithSwipeFragment extends Fragment implements Injectable {

    @BindView(R.id.swipe_refresh_devices) SwipeRefreshLayout mSwipeToRefreshView;
    @BindView(R.id.recycler_ocf_devices)
    EmptyRecyclerView mRecyclerView;

    @Inject ViewModelProvider.Factory mViewModelFactory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recycler_with_swipe, container, false);
        ButterKnife.bind(this, rootView);

        initViews();

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
    }

    public abstract RecyclerView.Adapter getAdapter();

    public View getEmptyView() {
        return null;
    }

    public abstract void initViewModel();

    public abstract void onSwipeRefresh();

    public ViewModelProvider.Factory getViewModelFactory() {
        return mViewModelFactory;
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setEmptyView(getEmptyView());

        if (getContext() != null) {
            mSwipeToRefreshView.setColorSchemeColors(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }

        mSwipeToRefreshView.setOnRefreshListener(() -> {
            mSwipeToRefreshView.setRefreshing(false);
            onSwipeRefresh();
        });
    }
}
