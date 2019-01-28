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

package org.openconnectivity.otgc.credential.presentation.view;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.iotivity.base.OicSecCred;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.common.presentation.view.EmptyRecyclerView;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.credential.presentation.viewmodel.CredentialsViewModel;
import org.openconnectivity.otgc.di.Injectable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CredentialsActivity extends AppCompatActivity implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_ocf_creds) EmptyRecyclerView mRecyclerView;
    @BindView(R.id.floating_button_cred_add) FloatingActionButton mFloatingActionButton;

    @OnClick(R.id.floating_button_cred_add)
    protected void onAddPressed() {
        Intent intent = new Intent(this, CredActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("deviceId", mDeviceId);
        startActivity(intent);
    }

    private CredentialsViewModel mViewModel;
    private CredentialAdapter mAdapter;

    private String mDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        Intent intent = getIntent();
        mDeviceId = intent.getStringExtra("deviceId");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.retrieveCredentials(mDeviceId);
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

        mAdapter = new CredentialAdapter(this);
        CredentialAdapter.setOnDeleteClickListener((position, v) ->
            mViewModel.deleteCredential(mDeviceId, mAdapter.mDataset.get(position).getCredID())
        );

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CredentialsViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getResourceOwner().observe(this, this::processResourceOwner);
        mViewModel.getCredential().observe(this, this::processCredential);
        mViewModel.getDeletedCredId().observe(this, this::processDeletedCredId);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((CredentialsViewModel.Error) error.getType()) {
            case RETRIEVE_CREDS:
                errorId = R.string.credentials_error_retrieve;
                break;
            case DELETE:
                errorId = R.string.credentials_error_delete;
                break;
        }

        Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
    }

    private void processResourceOwner(@NonNull String rownerUuid) {
        // TODO:
    }

    private void processCredential(@NonNull OicSecCred credential) {
        mAdapter.addItem(credential);
    }

    private void processDeletedCredId(@NonNull Integer credId) {
        mAdapter.deleteItemById(credId);
    }
}
