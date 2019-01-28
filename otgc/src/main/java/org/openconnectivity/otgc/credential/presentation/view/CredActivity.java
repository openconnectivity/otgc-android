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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.credential.presentation.viewmodel.CredViewModel;
import org.openconnectivity.otgc.di.Injectable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CredActivity extends AppCompatActivity implements Injectable {

    @Inject ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.layout_type_role) View mRoleView;
    @BindView(R.id.edit_text_type_role_id) EditText mTypeRoleId;
    @BindView(R.id.edit_text_type_role_authority) EditText mTypeRoleAuthority;

    @OnClick({ R.id.radio_type_identity, R.id.radio_type_role })
    public void onRadioButtonClicked(RadioButton rb) {
        // Is the button now checked?
        boolean checked = rb.isChecked();

        // Check which radio button was clicked
        switch (rb.getId()) {
            case R.id.radio_type_identity:
                if (checked) {
                    mRoleView.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_type_role:
                if (checked) {
                    mRoleView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @OnClick(R.id.floating_button_cred_save)
    protected void onSavePressed() {
        if (mDeviceId != null) {
            if (mRoleView.getVisibility() == View.VISIBLE) {
                mViewModel.provisionRoleCertificate(
                        mDeviceId,
                        mTypeRoleId.getText().toString(),
                        mTypeRoleAuthority.getText().toString());
            } else {
                mViewModel.provisionIdentityCertificate(mDeviceId);
            }
        }
    }

    private CredViewModel mViewModel;

    private String mDeviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cred);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        Intent intent = getIntent();
        mDeviceId = intent.getStringExtra("deviceId");
    }

    private void initViews() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CredViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getSuccess().observe(this, this::processSuccess);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((CredViewModel.Error)error.getType()) {
            case PROVISION_IDENTITY_CERT:
                errorId = R.string.credentials_cred_error_provision_identity;
                break;
            case PROVISION_ROLE_CERT:
                errorId = R.string.credentials_cred_error_provision_role;
                break;
        }

        Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
    }

    private void processSuccess(@NonNull Boolean operationSucceeded) {
        if (operationSucceeded) {
            finish();
        } else {
            // TODO
        }
    }
}
