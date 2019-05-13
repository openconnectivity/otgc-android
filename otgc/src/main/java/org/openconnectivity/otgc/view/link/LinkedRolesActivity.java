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

package org.openconnectivity.otgc.view.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.view.EmptyRecyclerView;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.domain.model.devicelist.DeviceRole;
import org.openconnectivity.otgc.utils.di.Injectable;
import org.openconnectivity.otgc.viewmodel.LinkedRolesViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class LinkedRolesActivity extends AppCompatActivity implements Injectable {
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_ocf_roles) EmptyRecyclerView mRecyclerView;
    @BindView(R.id.floating_button_roles_add) FloatingActionButton mFloatingActionButton;

    @OnClick(R.id.floating_button_roles_add)
    protected void onAddPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(LinkedRolesActivity.this, R.style.AppTheme));
        alertDialog.setTitle(LinkedRolesActivity.this.getString(R.string.linked_roles_dialog_add_role_title));

        LinearLayout layout = new LinearLayout(LinkedRolesActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText etRoleIdInput = new EditText(LinkedRolesActivity.this);
        etRoleIdInput.setHint(R.string.linked_roles_dialog_role_id_hint);
        final EditText etRoleAuthorityInput = new EditText(LinkedRolesActivity.this);
        etRoleAuthorityInput.setHint(R.string.linked_roles_dialog_role_authority_hint);
        layout.addView(etRoleIdInput);
        layout.addView(etRoleAuthorityInput);
        layout.setPadding(50, 40, 50, 10);

        alertDialog.setView(layout);
        alertDialog.setPositiveButton(LinkedRolesActivity.this.getString(R.string.linked_roles_dialog_add_role_yes_option), (dialog, which) -> {
            dialog.dismiss();
            if (!etRoleIdInput.getText().toString().isEmpty()) {
                mViewModel.addLinkedRole(mDevice, mDeviceRole, etRoleIdInput.getText().toString(), etRoleAuthorityInput.getText().toString());
            } else {
                Timber.d("Fiil role ID input text");
            }
        });
        alertDialog.setNegativeButton(LinkedRolesActivity.this.getString(R.string.linked_roles_dialog_add_role_no_option), (dialog, which) -> dialog.dismiss()).show();
    }

    private LinkedRolesViewModel mViewModel;
    private LinkedRolesAdapter mAdapter;

    private Device mDevice;
    private DeviceRole mDeviceRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linked_roles);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        Intent intent = getIntent();
        mDevice = (Device) intent.getSerializableExtra("device");
        mDeviceRole = DeviceRole.valueOf(intent.getStringExtra("deviceRole"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.retrieveLinkedRoles(mDevice, mDeviceRole);
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

        mAdapter = new LinkedRolesAdapter(this);
        LinkedRolesAdapter.setOnDeleteClickListener((position, v) ->
            mViewModel.deleteLinkedRole(mDevice, mDeviceRole, mAdapter.mDataset.get(position))
        );

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LinkedRolesViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getLinkedRoles().observe(this, this::processLinkedRoles);
        mViewModel.getDeletedRoleId().observe(this, this::processDeletedRoleId);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((LinkedRolesViewModel.Error)error.getType()) {
            case RETRIEVE:
                errorId = R.string.linked_roles_error_retrieve;
                break;
            case DELETE:
            default:
                break;
        }

        if (errorId != 0) {
            Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void processLinkedRoles(@NonNull String role) {
        mAdapter.addItem(role);
    }

    private void processDeletedRoleId(@NonNull String roleId) {
        mAdapter.deleteItemById(roleId);
    }

}
