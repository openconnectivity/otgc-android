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

package org.openconnectivity.otgc.view.accesscontrol;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAce;
import org.openconnectivity.otgc.viewmodel.AccessControlViewModel;
import org.openconnectivity.otgc.utils.view.EmptyRecyclerView;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.di.Injectable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccessControlActivity extends AppCompatActivity implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_ocf_acls) EmptyRecyclerView mRecyclerView;
    @BindView(R.id.floating_button_acl_scan) FloatingActionButton mFloatingActionButton;

    @OnClick(R.id.floating_button_acl_scan)
    protected void onAddPressed() {
        Intent intent = new Intent(this, AceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("device", mDevice);
        startActivity(intent);
    }

    private AccessControlViewModel mViewModel;
    private AccessControlAdapter mAdapter;

    private Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_control);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        Intent intent = getIntent();
        mDevice = (Device)intent.getSerializableExtra("device");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.clearItems();
        mViewModel.retrieveAcl(mDevice);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);
                if (dy > 0 && mFloatingActionButton.getVisibility() == View.VISIBLE) {
                    mFloatingActionButton.hide();
                } else if (dy < 0 && mFloatingActionButton.getVisibility() != View.VISIBLE) {
                    mFloatingActionButton.show();
                }
            }
        });

        mAdapter = new AccessControlAdapter(this);
        AccessControlAdapter.setOnDeleteClickListener((position, v) ->
            mViewModel.deleteAce(mDevice, mAdapter.mDataset.get(position).getAceid())
        );

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AccessControlViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getResourceOwner().observe(this, this::processResourceOwner);
        mViewModel.getAce().observe(this, this::processAce);
        mViewModel.getDeletedAceId().observe(this, this::processDeletedAceId);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((AccessControlViewModel.Error)error.getType()) {
            case RETRIEVE:
                errorId = R.string.access_control_error_retrieve;
                break;
            case DB_ACCESS:
                errorId = R.string.client_cannot_retrieve_device_name;
            case DELETE:
                break;
        }

        if (errorId != 0) {
            Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void processResourceOwner(@NonNull String rownerUuid) {
        // TODO:
    }

    private void processAce(@NonNull OcAce ace) {
        mAdapter.addItem(ace);
    }

    private void processDeletedAceId(@NonNull Long aceId) {
        mAdapter.deleteItemById(aceId);
    }
}
