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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.viewmodel.AceViewModel;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.di.Injectable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AceActivity extends AppCompatActivity implements Injectable {

    @Inject ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.layout_subject_uuid) View mUuidView;
    @BindView(R.id.layout_subject_role) View mRoleView;
    @BindView(R.id.layout_subject_conn_type) View mConnTypeView;
    @BindView(R.id.edit_text_subject_uuid) EditText mSubjectUuid;
    @BindView(R.id.edit_text_subject_role_id) EditText mSubjectRoleId;
    @BindView(R.id.edit_text_subject_role_authority) EditText mSubjectRoleAuthority;
    @BindView(R.id.checkbox_ace_permission_create) CheckBox mCheckBoxCreate;
    @BindView(R.id.checkbox_ace_permission_retrieve) CheckBox mCheckBoxRetrieve;
    @BindView(R.id.checkbox_ace_permission_update) CheckBox mCheckBoxUpdate;
    @BindView(R.id.checkbox_ace_permission_delete) CheckBox mCheckBoxDelete;
    @BindView(R.id.checkbox_ace_permission_notify) CheckBox mCheckBoxNotify;
    @BindView(R.id.radio_subject_conn_type_auth) RadioButton mConnTypeAuthCrypt;
    @BindView(R.id.list_resources) ListView mResources;

    @OnClick({ R.id.radio_subject_uuid, R.id.radio_subject_role, R.id.radio_subject_conn_type })
    public void onRadioButtonClicked(RadioButton rb) {
        // Is the button now checked?
        boolean checked = rb.isChecked();

        // Check which radio button was clicked
        switch (rb.getId()) {
            case R.id.radio_subject_uuid:
                if (checked) {
                    mUuidView.setVisibility(View.VISIBLE);
                    mRoleView.setVisibility(View.GONE);
                    mConnTypeView.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_subject_role:
                if (checked) {
                    mUuidView.setVisibility(View.GONE);
                    mRoleView.setVisibility(View.VISIBLE);
                    mConnTypeView.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_subject_conn_type:
                if (checked) {
                    mUuidView.setVisibility(View.GONE);
                    mRoleView.setVisibility(View.GONE);
                    mConnTypeView.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @OnClick(R.id.floating_button_ace_save)
    protected void onSavePressed() {
        if (mDevice != null) {
            SparseBooleanArray checked = mResources.getCheckedItemPositions();
            List<String> resources = new ArrayList<>();
            for (int i = 0; i < checked.size(); i++) {
                // Item position in adapter
                int position = checked.keyAt(i);
                // Add sport if it is checked i.e.) == TRUE!
                if (checked.valueAt(i)) {
                    resources.add(mAdapter.getItem(position));
                }
            }
            if (resources.size() == 0) {
                Toast.makeText(this, R.string.access_control_ace_error_select_resource, Toast.LENGTH_SHORT).show();
            } else {
                // TODO: ViewModel.create or ViewModel.update
                if (mUuidView.getVisibility() == View.VISIBLE) {
                    mViewModel.createAce(
                            mDevice,
                            mSubjectUuid.getText().toString(),
                            calculatePermission(),
                            resources
                    );
                } else if (mRoleView.getVisibility() == View.VISIBLE) {
                    mViewModel.createAce(
                            mDevice,
                            mSubjectRoleId.getText().toString(),
                            mSubjectRoleAuthority.getText().toString(),
                            calculatePermission(),
                            resources
                    );
                } else {
                    mViewModel.createAce(
                            mDevice,
                            mConnTypeAuthCrypt.isChecked(),
                            calculatePermission(),
                            resources
                    );
                }
            }
        }
    }

    private AceViewModel mViewModel;

    private Device mDevice;

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ace);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        Intent intent = getIntent();
        mDevice = (Device)intent.getSerializableExtra("device");

        mViewModel.retrieveResources(mDevice);
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

        mResources.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice);
        mResources.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(AceViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getResources().observe(this, this::processResources);
        mViewModel.getSuccess().observe(this, this::processSuccess);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((AceViewModel.Error)error.getType()) {
            case CREATE:
                errorId = R.string.access_control_ace_error_create;
                break;
            case UPDATE:
                errorId = R.string.access_control_ace_error_update;
                break;
        }

        Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
    }

    private void processResources(@NonNull List<String> resources) {
        mAdapter.addAll(resources);
    }

    private void processSuccess(@NonNull Boolean operationSucceeded) {
        if (operationSucceeded) {
            finish();
        } else {
            // TODO
        }
    }

    private int calculatePermission() {
        int permission = 0;
        if (mCheckBoxCreate.isChecked()) {
            permission += 1;
        }
        if (mCheckBoxRetrieve.isChecked()) {
            permission += 2;
        }
        if (mCheckBoxUpdate.isChecked()) {
            permission += 4;
        }
        if (mCheckBoxDelete.isChecked()) {
            permission += 8;
        }
        if (mCheckBoxNotify.isChecked()) {
            permission += 16;
        }

        return permission;
    }
}
