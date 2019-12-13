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

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.iotivity.OCRepresentation;
import org.iotivity.OCType;
import org.iotivity.OCValue;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.client.SerializableResource;
import org.openconnectivity.otgc.domain.model.devicelist.Device;
import org.openconnectivity.otgc.utils.constant.OcfResourceAttributeKey;
import org.openconnectivity.otgc.viewmodel.ResourceViewModel;
import org.openconnectivity.otgc.utils.constant.OcfInterface;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.utils.di.Injectable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResourceFragment extends Fragment implements Injectable {

    @BindView(R.id.expand_button) ImageButton mExpandButton;
    @BindView(R.id.device_resource_name) TextView mResourceName;
    @BindView(R.id.device_resource_observation) Switch mResourceObservation;
    @BindView(R.id.introspected_layout) GridLayout mLayout;

    @Inject ViewModelProvider.Factory mViewModelFactory;

    private ResourceViewModel mViewModel;

    private Map<String, View> mViews;
    private Map<String, Boolean> mViewsAccess;

    private Device mDevice;
    private SerializableResource mResource;
    private boolean mExpanded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mDevice = (Device) args.getSerializable("device");
        mResource = (SerializableResource) args.getSerializable("resource");
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

        if (mResource.isObservable()) {
            mViewModel.cancelObserveRequest(mResource);
        }
    }

    private void initViews() {
        mViews = new HashMap<>();
        mViewsAccess = new HashMap<>();

        mExpandButton.setOnClickListener(v -> {
            mExpanded = !mExpanded;

            if (getContext() != null) {
                mExpandButton.setImageDrawable(
                        ContextCompat.getDrawable(getContext(),
                                mExpanded ? R.drawable.ic_expand_less_black_36dp : R.drawable.ic_expand_more_black_36dp)
                );
            }
            mResourceObservation.setVisibility(mExpanded ? View.VISIBLE : View.INVISIBLE);
            mLayout.setVisibility(mExpanded ? View.VISIBLE : View.GONE);

            if (mExpanded && mViews.isEmpty()) {
                mViewModel.getRequest(mDevice, mResource);
            }
        });

        if (mResource.isObservable()) {
            mResourceObservation.setOnCheckedChangeListener((v, isChecked) -> {
                if (isChecked) {
                    mViewModel.observeRequest(mDevice, mResource);
                } else {
                    mViewModel.cancelObserveRequest(mResource);
                }
            });
        } else {
            mResourceObservation.setVisibility(View.GONE);
        }

        mResourceName.setText(mResource.getUri());
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
                for (View v : mViews.values()) {
                    v.setEnabled(false);
                }
                break;
            case PUT:
                errorId = R.string.client_fragment_put_request_failed;
                break;
            case DB:
                errorId = R.string.cannot_access_to_db;
                break;
            case OBSERVE:
                errorId = R.string.cannot_observe_resource;
                break;
            case CANCEL_OBSERVE:
                errorId = R.string.cannot_cancel_observe_resource;
                break;
        }

        Toast.makeText(getContext(), errorId, Toast.LENGTH_SHORT).show();
    }

    private void processResponse(@NonNull SerializableResource resource) {
        Map<String, Object> values = resource.getProperties();

        if (mViews.isEmpty()) {
            mLayout.setRowCount(values.size());
        }

        if (resource.getPropertiesAccess().isEmpty()) {
            createUI(resource, values);
        } else {
            createUIForIntrospection(resource, values);
        }
    }

    private void createUI(SerializableResource resource, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (mViews.containsKey(entry.getKey())) {
                if (entry.getValue() instanceof Boolean) {
                    ((Switch) mViews.get(entry.getKey())).setChecked((Boolean)entry.getValue());
                } else if (entry.getValue() instanceof Integer) {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    if (isViewEnabled(resource.getResourceInterfaces()) && mViews.get(entry.getKey()) instanceof EditText) {
                        ((EditText) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    } else {
                        ((TextView) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    }
                } else if (entry.getValue() instanceof Double) {
                    NumberFormat numberFormat = new DecimalFormat("0.0");
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        ((EditText) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    } else {
                        ((TextView) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    }
                } else if (entry.getValue() instanceof String) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        ((EditText) mViews.get(entry.getKey())).setText(entry.getValue().toString());
                    } else {
                        ((TextView) mViews.get(entry.getKey())).setText(entry.getValue().toString());
                    }
                } else if (entry.getValue() instanceof String[]) {
                    List<String> list = Arrays.asList((String[]) entry.getValue());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((Spinner) mViews.get(entry.getKey())).setAdapter(dataAdapter);
                } else if (entry.getValue() instanceof  int[]) {
                    LinearLayout layout = ((LinearLayout)mViews.get(entry.getKey()));
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    int i = 0;
                    for (int value : (int[])entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            ((EditText)layout.getChildAt(i)).setText(numberFormat.format(value));
                        } else {
                            ((TextView)layout.getChildAt(i)).setText(numberFormat.format(value));
                        }
                        i++;
                    }
                } else if (entry.getValue() instanceof  double[]) {
                    LinearLayout layout = ((LinearLayout)mViews.get(entry.getKey()));
                    NumberFormat numberFormat = new DecimalFormat("0.0");
                    int i = 0;
                    for (double value : (double[])entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            ((EditText)layout.getChildAt(i)).setText(numberFormat.format(value));
                        } else {
                            ((TextView)layout.getChildAt(i)).setText(numberFormat.format(value));
                        }
                        i++;
                    }
                } else if (entry.getValue() instanceof  boolean[]) {
                    LinearLayout layout = ((LinearLayout)mViews.get(entry.getKey()));
                    int i = 0;
                    for (boolean value : (boolean[])entry.getValue()) {
                        ((Switch)layout.getChildAt(i)).setChecked(value);
                        i++;
                    }
                }
            } else {
                View view = null;
                if (entry.getValue() instanceof Boolean) {
                    Switch s = new Switch(getContext());
                    //s.setText(entry.getKey());
                    s.setChecked((Boolean) entry.getValue());
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        s.setOnCheckedChangeListener((v, isChecked) -> {
                            OCValue value = new OCValue();
                            value.setBool(isChecked);

                            OCRepresentation rep = new OCRepresentation();
                            rep.setName(entry.getKey());
                            rep.setType(OCType.OC_REP_BOOL);
                            rep.setValue(value);

                            mViewModel.postRequest(mDevice, resource, rep, null);
                        });
                    } else {
                        s.setEnabled(false);
                    }

                    view = s;
                } else if (entry.getValue() instanceof Integer) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        EditText et = new EditText(getContext());
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        et.setText(numberFormat.format(entry.getValue()));
                        et.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                Integer number;

                                try {
                                    number = Integer.valueOf(et.getText().toString());
                                } catch (NumberFormatException e) {
                                    return;
                                }

                                OCValue value = new OCValue();
                                value.setInteger(number);

                                OCRepresentation rep = new OCRepresentation();
                                rep.setName(entry.getKey());
                                rep.setType(OCType.OC_REP_INT);
                                rep.setValue(value);

                                mViewModel.postRequest(mDevice, resource, rep, null);
                            }
                        });

                        view = et;
                    } else {
                        TextView tv = new TextView(getContext());
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        tv.setText(numberFormat.format(entry.getValue()));

                        view = tv;
                    }
                } else if (entry.getValue() instanceof Double) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        EditText et = new EditText(getContext());
                        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        NumberFormat numberFormat = new DecimalFormat("0.0");
                        et.setText(numberFormat.format(entry.getValue()));
                        et.setOnFocusChangeListener((v, hasFocus) ->  {
                            if (!hasFocus) {
                                Double number;

                                try {
                                    number = Double.valueOf(et.getText().toString());
                                } catch (NumberFormatException e) {
                                    return;
                                }

                                OCValue value = new OCValue();
                                value.setDouble(number);

                                OCRepresentation rep = new OCRepresentation();
                                rep.setName(entry.getKey());
                                rep.setType(OCType.OC_REP_DOUBLE);
                                rep.setValue(value);

                                mViewModel.postRequest(mDevice, resource, rep, null);
                            }
                        });

                        view = et;
                    } else {
                        TextView tv = new TextView(getContext());
                        NumberFormat numberFormat = new DecimalFormat("0.0");
                        tv.setText(numberFormat.format(entry.getValue()));

                        view = tv;
                    }
                } else if (entry.getValue() instanceof String) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        EditText et = new EditText(getContext());
                        et.setInputType(InputType.TYPE_CLASS_TEXT);
                        et.setText(String.valueOf(entry.getValue()));
                        et.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                OCValue value = new OCValue();
                                value.setString(et.getText().toString());

                                OCRepresentation rep = new OCRepresentation();
                                rep.setName(entry.getKey());
                                rep.setType(OCType.OC_REP_STRING);
                                rep.setValue(value);

                                mViewModel.postRequest(mDevice, resource, rep, null);
                            }
                        });

                        view = et;
                    } else {
                        TextView tv = new TextView(getContext());
                        tv.setText(String.valueOf(entry.getValue()));

                        view = tv;
                    }
                } else if (entry.getValue() instanceof String[]) {
                    List<String> list = Arrays.asList((String[]) entry.getValue());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to list view
                    Spinner spinner = new Spinner(getContext());
                    spinner.setAdapter(dataAdapter);

                    view = spinner;
                } else if (entry.getValue() instanceof int[]) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    for (int value : (int[]) entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            EditText et = new EditText(getContext());
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            et.setText(numberFormat.format(value));
                            et.setOnFocusChangeListener((v, hasFocus) -> {
                                if (!hasFocus) {
                                    int[] ret = new int[layout.getChildCount()];
                                    for (int i = 0; i < layout.getChildCount(); i++) {
                                        EditText tmp = (EditText) layout.getChildAt(i);
                                        Integer number;
                                        try {
                                            number = Integer.valueOf(tmp.getText().toString());
                                        } catch (NumberFormatException e) {
                                            return;
                                        }
                                        ret[i] = number;
                                    }

                                    OCRepresentation rep = new OCRepresentation();
                                    rep.setName(entry.getKey());
                                    rep.setType(OCType.OC_REP_INT_ARRAY);

                                    mViewModel.postRequest(mDevice, resource, rep, ret);
                                }
                            });

                            layout.addView(et);
                        } else {
                            TextView tv = new TextView(getContext());
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            tv.setPadding(0,0,10,0);
                            tv.setText(numberFormat.format(value));

                            layout.addView(tv);
                        }
                    }

                    view = layout;
                } else if (entry.getValue() instanceof double[]) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    for (double value : (double[]) entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            EditText et = new EditText(getContext());
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            et.setText(numberFormat.format(value));
                            et.setOnFocusChangeListener((v, hasFocus) -> {
                                if (!hasFocus) {
                                    double[] ret = new double[layout.getChildCount()];
                                    for (int i = 0; i < layout.getChildCount(); i++) {
                                        EditText tmp = (EditText) layout.getChildAt(i);
                                        Double number;
                                        try {
                                            number = Double.valueOf(tmp.getText().toString());
                                        } catch (NumberFormatException e) {
                                            return;
                                        }
                                        ret[i] = number;
                                    }

                                    OCRepresentation rep = new OCRepresentation();
                                    rep.setType(OCType.OC_REP_DOUBLE_ARRAY);
                                    rep.setName(entry.getKey());

                                    mViewModel.postRequest(mDevice, resource, rep, ret);
                                }
                            });

                            layout.addView(et);
                        } else {
                            TextView tv = new TextView(getContext());
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            tv.setText(numberFormat.format(value));

                            layout.addView(tv);
                        }
                    }

                    view = layout;
                } else if (entry.getValue() instanceof boolean[]) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    for (boolean value : (boolean[]) entry.getValue()) {
                        Switch s = new Switch(getContext());
                        //s.setText(entry.getKey());
                        s.setChecked(value);
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            s.setOnCheckedChangeListener((v, isChecked) -> {
                                boolean[] ret = new boolean[layout.getChildCount()];
                                for (int i = 0; i < layout.getChildCount(); i++) {
                                    Switch tmp = (Switch) layout.getChildAt(i);
                                    ret[i] = tmp.isChecked();
                                }

                                OCRepresentation rep = new OCRepresentation();
                                rep.setType(OCType.OC_REP_BOOL_ARRAY);
                                rep.setName(entry.getKey());

                                mViewModel.postRequest(mDevice, resource, rep, ret);
                            });
                        } else {
                            s.setEnabled(false);
                        }

                        layout.addView(s);
                    }

                    view = layout;
                }

                if (view != null) {
                    mViews.put(entry.getKey(), view);
                    TextView title = new TextView(getContext());
                    title.setText(entry.getKey());
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
    }

    private void createUIForIntrospection(SerializableResource resource, Map<String, Object> values) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (mViews.containsKey(entry.getKey())) {
                if (entry.getValue() instanceof Boolean) {
                    ((Switch) mViews.get(entry.getKey())).setChecked((Boolean)entry.getValue());
                } else if (entry.getValue() instanceof Integer) {
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    if (isViewEnabled(resource.getResourceInterfaces()) && mViews.get(entry.getKey()) instanceof EditText) {
                        ((EditText) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    } else {
                        ((TextView) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    }
                } else if (entry.getValue() instanceof Double) {
                    NumberFormat numberFormat = new DecimalFormat("0.0");
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        ((EditText) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    } else {
                        ((TextView) mViews.get(entry.getKey())).setText(numberFormat.format(entry.getValue()));
                    }
                } else if (entry.getValue() instanceof String) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        ((EditText) mViews.get(entry.getKey())).setText(entry.getValue().toString());
                    } else {
                        ((TextView) mViews.get(entry.getKey())).setText(entry.getValue().toString());
                    }
                } else if (entry.getValue() instanceof String[]) {
                    List<String> list = Arrays.asList((String[]) entry.getValue());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    ((Spinner) mViews.get(entry.getKey())).setAdapter(dataAdapter);
                } else if (entry.getValue() instanceof  int[]) {
                    LinearLayout layout = ((LinearLayout)mViews.get(entry.getKey()));
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    int i = 0;
                    for (int value : (int[])entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            ((EditText)layout.getChildAt(i)).setText(numberFormat.format(value));
                        } else {
                            ((TextView)layout.getChildAt(i)).setText(numberFormat.format(value));
                        }
                        i++;
                    }
                } else if (entry.getValue() instanceof  double[]) {
                    LinearLayout layout = ((LinearLayout)mViews.get(entry.getKey()));
                    NumberFormat numberFormat = new DecimalFormat("0.0");
                    int i = 0;
                    for (double value : (double[])entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            ((EditText)layout.getChildAt(i)).setText(numberFormat.format(value));
                        } else {
                            ((TextView)layout.getChildAt(i)).setText(numberFormat.format(value));
                        }
                        i++;
                    }
                } else if (entry.getValue() instanceof  boolean[]) {
                    LinearLayout layout = ((LinearLayout)mViews.get(entry.getKey()));
                    int i = 0;
                    for (boolean value : (boolean[])entry.getValue()) {
                        ((Switch)layout.getChildAt(i)).setChecked(value);
                        i++;
                    }
                }
            } else {
                View view = null;
                if (entry.getValue() instanceof Boolean) {
                    Switch s = new Switch(getContext());
                    //s.setText(entry.getKey());
                    s.setChecked((Boolean) entry.getValue());
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        s.setOnCheckedChangeListener((v, isChecked) -> {
                            sendResourceValues(resource);
                        });
                    } else {
                        s.setEnabled(false);
                    }

                    view = s;
                } else if (entry.getValue() instanceof Integer) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        EditText et = new EditText(getContext());
                        et.setInputType(InputType.TYPE_CLASS_NUMBER);
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        et.setText(numberFormat.format(entry.getValue()));
                        et.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                sendResourceValues(resource);
                            }
                        });

                        view = et;
                    } else {
                        TextView tv = new TextView(getContext());
                        NumberFormat numberFormat = NumberFormat.getInstance();
                        tv.setText(numberFormat.format(entry.getValue()));

                        view = tv;
                    }
                } else if (entry.getValue() instanceof Double) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        EditText et = new EditText(getContext());
                        et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        NumberFormat numberFormat = new DecimalFormat("0.0");
                        et.setText(numberFormat.format(entry.getValue()));
                        et.setOnFocusChangeListener((v, hasFocus) ->  {
                            if (!hasFocus) {
                                sendResourceValues(resource);
                            }
                        });

                        view = et;
                    } else {
                        TextView tv = new TextView(getContext());
                        NumberFormat numberFormat = new DecimalFormat("0.0");
                        tv.setText(numberFormat.format(entry.getValue()));

                        view = tv;
                    }
                } else if (entry.getValue() instanceof String) {
                    if (isViewEnabled(resource.getResourceInterfaces())) {
                        EditText et = new EditText(getContext());
                        et.setInputType(InputType.TYPE_CLASS_TEXT);
                        et.setText(String.valueOf(entry.getValue()));
                        et.setOnFocusChangeListener((v, hasFocus) -> {
                            if (!hasFocus) {
                                sendResourceValues(resource);
                            }
                        });

                        view = et;
                    } else {
                        TextView tv = new TextView(getContext());
                        tv.setText(String.valueOf(entry.getValue()));

                        view = tv;
                    }
                } else if (entry.getValue() instanceof String[]) {
                    List<String> list = Arrays.asList((String[]) entry.getValue());
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to list view
                    Spinner spinner = new Spinner(getContext());
                    spinner.setAdapter(dataAdapter);

                    view = spinner;
                } else if (entry.getValue() instanceof int[]) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    for (int value : (int[]) entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            EditText et = new EditText(getContext());
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            et.setText(numberFormat.format(value));
                            et.setOnFocusChangeListener((v, hasFocus) -> {
                                if (!hasFocus) {
                                    for (int i = 0; i < layout.getChildCount(); i++) {
                                        EditText tmp = (EditText) layout.getChildAt(i);
                                        String textValue = tmp.getText().toString();
                                        if (textValue.matches("-?\\d+")) {
                                            sendResourceValues(resource);
                                        }
                                    }
                                }
                            });

                            layout.addView(et);
                        } else {
                            TextView tv = new TextView(getContext());
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            tv.setPadding(0,0,10,0);
                            tv.setText(numberFormat.format(value));

                            layout.addView(tv);
                        }
                    }

                    view = layout;
                } else if (entry.getValue() instanceof double[]) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    for (double value : (double[]) entry.getValue()) {
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            EditText et = new EditText(getContext());
                            et.setInputType(InputType.TYPE_CLASS_NUMBER);
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            et.setText(numberFormat.format(value));
                            et.setOnFocusChangeListener((v, hasFocus) -> {
                                if (!hasFocus) {
                                    for (int i = 0; i < layout.getChildCount(); i++) {
                                        EditText tmp = (EditText) layout.getChildAt(i);
                                        String textValue = tmp.getText().toString();
                                        if (textValue.matches("-?\\d+\\.\\d+")){
                                            sendResourceValues(resource);
                                        }
                                    }
                                }
                            });

                            layout.addView(et);
                        } else {
                            TextView tv = new TextView(getContext());
                            NumberFormat numberFormat = NumberFormat.getInstance();
                            tv.setText(numberFormat.format(value));

                            layout.addView(tv);
                        }
                    }

                    view = layout;
                } else if (entry.getValue() instanceof boolean[]) {
                    LinearLayout layout = new LinearLayout(getContext());
                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    for (boolean value : (boolean[]) entry.getValue()) {
                        Switch s = new Switch(getContext());
                        s.setChecked(value);
                        if (isViewEnabled(resource.getResourceInterfaces())) {
                            s.setOnCheckedChangeListener((v, isChecked) -> {
                                sendResourceValues(resource);
                            });
                        } else {
                            s.setEnabled(false);
                        }

                        layout.addView(s);
                    }

                    view = layout;
                }


                if (view != null) {
                    if (resource.getPropertiesAccess().containsKey(entry.getKey())) {
                        mViewsAccess.put(entry.getKey(), resource.getPropertiesAccess().get(entry.getKey()));
                    } else {
                        mViewsAccess.put(entry.getKey(), false);
                    }

                    mViews.put(entry.getKey(), view);
                    TextView title = new TextView(getContext());
                    title.setText(entry.getKey());
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
    }

    private void sendResourceValues(SerializableResource resource) {
        Map<String, Object> values = new HashMap<>();

        for (String key : mViews.keySet()) {
            Object value = null;
            boolean readOnly = mViewsAccess.get(key);

            if (mViews.get(key) instanceof Switch) {
                value = ((Switch)mViews.get(key)).isChecked();
            } else if (mViews.get(key) instanceof EditText) {
                String tmp = ((EditText)mViews.get(key)).getText().toString();
                if (tmp.matches("-?\\d+")) {
                    value = Integer.valueOf(tmp);
                } else if (tmp.matches("-?\\d+\\.\\d+")) {
                    value = Double.valueOf(tmp);
                } else {
                    value = tmp;
                }
            } else if (mViews.get(key) instanceof TextView) {
                String tmp = ((TextView)mViews.get(key)).getText().toString();
                if (tmp.matches("-?\\d+")) {
                    value = Integer.valueOf(tmp);
                } else if (tmp.matches("-?\\d+\\.\\d+")) {
                    value = Double.valueOf(tmp);
                } else {
                    value = tmp;
                }
            } else if (mViews.get(key) instanceof LinearLayout) {
                value = new ArrayList<>();
                for (int i=0; i<((LinearLayout)mViews.get(key)).getChildCount(); i++) {
                    View child = ((LinearLayout)mViews.get(key)).getChildAt(i);
                    if (child instanceof EditText) {
                        String tmp = ((EditText)child).getText().toString();
                        if (tmp.matches("-?\\d+")) {
                            ((List)value).add(Integer.valueOf(tmp));
                        } else if (tmp.matches("-?\\d+\\.\\d+")) {
                            ((List)value).add(Double.valueOf(tmp));
                        } else {
                            ((List)value).add(tmp);
                        }
                    } else if (child instanceof TextView) {
                        String tmp = ((TextView)child).getText().toString();
                        if (tmp.matches("-?\\d+")) {
                            ((List)value).add(Integer.valueOf(tmp));
                        } else if (tmp.matches("-?\\d+\\.\\d+")) {
                            ((List)value).add(Double.valueOf(tmp));
                        } else {
                            ((List)value).add(tmp);
                        }
                    }
                }

            } else if (mViews.get(key) instanceof Spinner) {
                SpinnerAdapter adapter = ((Spinner)mViews.get(key)).getAdapter();
                List<String> tmp = new ArrayList<>();
                for (int i=0; i<adapter.getCount(); i++) {
                    tmp.add((String)adapter.getItem(i));
                }

                value = tmp;
            }

            if (!key.equals("Observe") && value != null && !readOnly
                    && !key.equals(OcfResourceAttributeKey.RESOURCE_TYPES_KEY)
                    && !key.equals(OcfResourceAttributeKey.INTERFACES_KEY)) {
                values.put(key, value);
            }
        }

        mViewModel.postRequest(mDevice, resource, values);
    }

    private boolean isViewEnabled(List<String> resourceInterfaces) {
        return resourceInterfaces.isEmpty()
                || resourceInterfaces.contains(OcfInterface.ACTUATOR)
                || resourceInterfaces.contains(OcfInterface.READ_WRITE);
    }
}
