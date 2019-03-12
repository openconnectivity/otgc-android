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

package org.openconnectivity.otgc.client.presentation.view;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.iotivity.base.OcException;
import org.iotivity.base.OcRepresentation;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.client.domain.model.DynamicUiElement;
import org.openconnectivity.otgc.client.domain.model.DynamicUiProperty;
import org.openconnectivity.otgc.client.presentation.viewmodel.ResourceViewModel;
import org.openconnectivity.otgc.common.constant.OcfResourceProperties;
import org.openconnectivity.otgc.common.presentation.viewmodel.ViewModelError;
import org.openconnectivity.otgc.di.Injectable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntrospectedFragment extends Fragment implements Injectable {

    @BindView(R.id.expand_button) ImageButton mExpandButton;
    @BindView(R.id.device_resource_name) TextView mResourceName;
    @BindView(R.id.device_resource_observation) Switch mResourceObservation;
    @BindView(R.id.introspected_layout) GridLayout mLayout;

    @Inject ViewModelProvider.Factory mViewModelFactory;

    private ResourceViewModel mViewModel;

    private Map<String, View> mViews;

    private String mDeviceId;
    private DynamicUiElement mUiInfo;
    private boolean mExpanded = false;
    private boolean mObserving = false;
    private boolean mRetrieved = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mDeviceId = args.getString("deviceId");
        mUiInfo = (DynamicUiElement) args.getSerializable("uiInfo");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resource, container, false);
        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mUiInfo.getSupportedOperations().contains("get") && mObserving) {
            mViewModel.cancelObserveRequest(mUiInfo.getPath());
        }
    }

    private void initViews() {
        mViews = new HashMap<>();

        mLayout.setRowCount(mUiInfo.getProperties().size());

        mExpandButton.setOnClickListener(v -> {
            mExpanded = !mExpanded;

            if (getContext() != null) {
                mExpandButton.setImageDrawable(
                        ContextCompat.getDrawable(getContext(),
                                mExpanded ? R.drawable.ic_expand_less_black_36dp : R.drawable.ic_expand_more_black_36dp)
                );
            }

            mLayout.setVisibility(mExpanded ? View.VISIBLE : View.GONE);

            if (mUiInfo.getSupportedOperations().contains("get")) {
                mResourceObservation.setVisibility(mExpanded ? View.VISIBLE : View.INVISIBLE);

                if (mExpanded && !mRetrieved) {
                    mViewModel.getRequest(mDeviceId, mUiInfo.getPath(), mUiInfo.getResourceTypes(), mUiInfo.getInterfaces());
                    mRetrieved = true;
                }
            }
        });

        if (mUiInfo.getSupportedOperations().contains("get")) {
            mResourceObservation.setOnCheckedChangeListener((v, isChecked) -> {
                mObserving = isChecked;
                if (isChecked) {
                    mViewModel.observeRequest(mDeviceId, mUiInfo.getPath(), mUiInfo.getResourceTypes(), mUiInfo.getInterfaces());
                } else {
                    mViewModel.cancelObserveRequest(mUiInfo.getPath());
                }
            });
        } else {
            mResourceObservation.setVisibility(View.GONE);
        }

        for (DynamicUiProperty property : mUiInfo.getProperties()) {
            if (!property.getName().equals(OcfResourceProperties.NAME)) {
                View view = null;
                if (property.getType().equals("boolean")) {
                    view = generateBooleanRepresentation(property);
                } else if (property.getType().equals("string") || property.getType().equals("number")) {
                    view = isViewEnabled(property) ? new EditText(getActivity()) : new TextView(getActivity());
                }

                if (view != null) {
                    mViews.put(property.getName(), view);
                    TextView title = new TextView(getContext());
                    title.setText(property.getName());
                    mLayout.addView(title);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins((int) (8 * getResources().getDisplayMetrics().density), 0, 0, 0);
                    view.setLayoutParams(params);
                    mLayout.addView(view);
                }
            }
        }

        mResourceName.setText(mUiInfo.getPath());
    }

    private Switch generateBooleanRepresentation(DynamicUiProperty property) {
        Switch s = new Switch(getActivity());
        s.setEnabled(isViewEnabled(property));
        if (s.isEnabled()) {
            s.setOnCheckedChangeListener((v, isChecked) -> {
                OcRepresentation rep = new OcRepresentation();
                try {
                    rep.setValue(property.getName(), isChecked);
                } catch (OcException e) {

                }
                mViewModel.postRequest(mDeviceId, mUiInfo.getPath(), mUiInfo.getResourceTypes(), mUiInfo.getInterfaces(),
                        rep);
            });
        }

        return s;
    }

    private boolean isViewEnabled(DynamicUiProperty property) {
        return !property.isReadOnly()
                && (mUiInfo.getSupportedOperations().contains("post")
                        || mUiInfo.getSupportedOperations().contains("put"));
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(ResourceViewModel.class);
        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getResponse().observe(this, this::processResponse);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO:
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((ResourceViewModel.Error) error.getType()) {
            case GET:
                errorId = R.string.client_fragment_get_request_failed;
                break;
            case POST:
                errorId = R.string.client_fragment_post_request_failed;
                break;
            case PUT:
                errorId = R.string.client_fragment_put_request_failed;
                break;
        }

        Toast.makeText(getActivity(), errorId, Toast.LENGTH_SHORT).show();
    }

    private void processResponse(@NonNull OcRepresentation response) {
        Map<String, Object> values = response.getValues();

        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (entry.getKey().equals(OcfResourceProperties.NAME)) {
                String name = (String) entry.getValue();
                if (name != null && !name.isEmpty()) {
                    mResourceName.setText(name);
                }
            } else {
                DynamicUiProperty property = null;
                for (DynamicUiProperty p : mUiInfo.getProperties()) {
                    if (p.getName().equals(entry.getKey())) {
                        property = p;
                        break;
                    }
                }

                if (property != null) {
                    switch (property.getType()) {
                        case "boolean":
                            Switch s = (Switch) mViews.get(entry.getKey());
                            Boolean isChecked = (Boolean) entry.getValue();
                            s.setChecked(isChecked != null ? isChecked : false);
                            break;
                        case "string":
                            String text = (String) entry.getValue();
                            if (!isViewEnabled(property)) {
                                TextView tv = (TextView) mViews.get(entry.getKey());
                                tv.setText(text != null ? text : "");
                            } else {
                                EditText et = (EditText) mViews.get(entry.getKey());
                                et.setText(text != null ? text : "");
                            }
                            break;
                        case "number":
                            Double number = (Double) entry.getValue();
                            NumberFormat numberFormat = new DecimalFormat("0.0");
                            if (!isViewEnabled(property)) {
                                TextView tv = (TextView) mViews.get(entry.getKey());
                                tv.setText(number != null ? numberFormat.format(number) : "0");
                            } else {
                                EditText et = (EditText) mViews.get(entry.getKey());
                                et.setText(number != null ? numberFormat.format(number) : "0");
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
