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
package org.openconnectivity.otgc.view.client;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.client.DynamicUiElement;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.domain.model.resource.virtual.d.OcDeviceInfo;
import org.openconnectivity.otgc.domain.model.resource.virtual.p.OcPlatformInfo;
import org.openconnectivity.otgc.viewmodel.GenericClientViewModel;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class GenericClientActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingAndroidInjector;

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.navigation_drawer) DrawerLayout mDrawerLayout;
    @BindView(R.id.right_drawer) RecyclerView mRecyclerView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private GenericClientViewModel mViewModel;

    private DeviceInfoAdapter mAdapter;

    private Device mDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_client);

        ButterKnife.bind(this);
        initViews();
        initViewModel();

        // Get the intent that started this activity and extract the string
        Intent intent = getIntent();
        mDevice = (Device)intent.getSerializableExtra("device");

        mViewModel.loadDeviceName(mDevice.getDeviceId());
        mViewModel.loadDeviceInfo(mDevice);
        mViewModel.loadPlatformInfo(mDevice);
        mViewModel.introspect(mDevice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generic_client, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buttonInfo:
                onInfoPressed();
                break;
            default:
                onBackPressed();
                break;
        }

        return true;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return mDispatchingAndroidInjector;
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new DeviceInfoAdapter(getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GenericClientViewModel.class);
        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getDeviceName().observe(this, this::processDeviceName);
        mViewModel.getDeviceInfo().observe(this, this::processDeviceInfo);
        mViewModel.getPlatformInfo().observe(this, this::processPlatformInfo);
        mViewModel.getResources().observe(this, this::processResources);
        mViewModel.getIntrospection().observe(this, this::processIntrospection);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO:
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((GenericClientViewModel.Error)error.getType()) {
            case DEVICE_NAME:
                errorId = R.string.client_cannot_retrieve_device_name;
                break;
            case DEVICE_INFO:
                errorId = R.string.client_cannot_retrieve_device_info;
                break;
            case PLATFORM_INFO:
                errorId = R.string.client_cannot_retrieve_platform_info;
                break;
            case INTROSPECTION:
                mViewModel.findResources(mDevice);
                break;
            case FIND_RESOURCES:
                errorId = R.string.client_cannot_introspect_device;
                break;
        }

        if (errorId != 0) {
            Toast.makeText(getApplicationContext(), errorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void processDeviceName(String deviceName) {
        mToolbar.setTitle(deviceName);
    }

    private void processDeviceInfo(OcDeviceInfo deviceInfo) {
        if (mToolbar.getTitle() == null || mToolbar.getTitle().toString().isEmpty()) {
            mToolbar.setTitle(deviceInfo.getName().isEmpty() ? getString(R.string.client_title_no_name) : deviceInfo.getName());
        }

        mAdapter.setDeviceInfo(deviceInfo);
    }

    private void processPlatformInfo(OcPlatformInfo platformInfo) {
        mAdapter.setPlatformInfo(platformInfo);
    }

    private void processResources(List<SerializableResource> resources) {
        if (!resources.isEmpty()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            for (SerializableResource resource : resources) {
                Fragment fragment = new ResourceFragment();
                Bundle args = new Bundle();
                args.putSerializable("device", mDevice);
                args.putSerializable("resource", resource);
                fragment.setArguments(args);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } else {
            // TODO:
        }
    }

    private void processIntrospection(List<DynamicUiElement> uiElements) {
        if (!uiElements.isEmpty()) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            for (DynamicUiElement uiElement : uiElements) {
                SerializableResource resource = new SerializableResource();
                resource.setUri(uiElement.getPath());
                resource.setPropertiesAccess(uiElement.getProperties());
                resource.setResourceTypes(uiElement.getResourceTypes());
                resource.setResourceInterfaces(uiElement.getInterfaces());

                Fragment fragment = new ResourceFragment();
                Bundle args = new Bundle();
                args.putSerializable("device", mDevice);
                args.putSerializable("resource", resource);
                fragment.setArguments(args);
                fragmentTransaction.add(R.id.fragment_container, fragment);
            }
            fragmentTransaction.commit();
        } else {
            mViewModel.findResources(mDevice);
        }
    }

    private void onInfoPressed() {
        mDrawerLayout.openDrawer(GravityCompat.END);
    }
}
