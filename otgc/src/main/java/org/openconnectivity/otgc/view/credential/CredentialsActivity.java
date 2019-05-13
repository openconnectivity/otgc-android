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

package org.openconnectivity.otgc.view.credential;

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
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.utils.view.EmptyRecyclerView;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.viewmodel.CredentialsViewModel;
import org.openconnectivity.otgc.utils.di.Injectable;

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
        intent.putExtra("device", mDevice);
        startActivity(intent);
    }

    private CredentialsViewModel mViewModel;
    private CredentialAdapter mAdapter;

    private Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        Intent intent = getIntent();
        mDevice = (Device) intent.getSerializableExtra("device");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.retrieveCredentials(mDevice);
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
            mViewModel.deleteCredential(mDevice, mAdapter.mDataset.get(position).getCredid())
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
            case DB_ERROR:
                errorId = R.string.cannot_access_to_db;
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

    private void processCredential(@NonNull OcCredential credential) {
        mAdapter.addItem(credential);
    }

    private void processDeletedCredId(@NonNull Long credId) {
        mAdapter.deleteItemById(credId);
    }
}
