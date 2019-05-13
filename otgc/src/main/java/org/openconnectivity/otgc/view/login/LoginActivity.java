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
package org.openconnectivity.otgc.view.login;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.view.devicelist.DeviceListActivity;
import org.openconnectivity.otgc.utils.di.Injectable;
import org.openconnectivity.otgc.viewmodel.LoginViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class LoginActivity extends AppCompatActivity implements Injectable {
    static final ButterKnife.Setter<View, Boolean> ENABLED =
            (view, value, index) -> view.setEnabled(value);

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.edit_text_username) TextInputEditText etUserName;
    @BindView(R.id.edit_text_password) TextInputEditText etPassword;
    @BindView(R.id.button_login) Button btnActivate;

    private LoginViewModel mViewModel;

    private boolean isValidUsername = false;
    private boolean isValidPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        initViews();
        initViewModel();
    }

    @OnTextChanged(value = R.id.edit_text_username,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterUsernameInput(Editable editable) {
        isValidUsername = validateUsername(editable.toString());
        checkIfLoginEnabled();
    }

    @OnTextChanged(value = R.id.edit_text_password,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void afterPasswordInput(Editable editable) {
        isValidPassword = validatePassword(editable.toString());
        checkIfLoginEnabled();
    }

    @OnClick(R.id.button_login)
    public void onLoginPressed(View v) {
        signIn();
    }

    private void initViews() {
        checkIfLoginEnabled();
    }

    private void checkIfLoginEnabled() {
        ButterKnife.apply(btnActivate, ENABLED, isValidUsername && isValidPassword);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(LoginViewModel.class);
        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.isAuthenticated().observe(this, this::processAuthenticated);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO:
    }

    private void handleError(@NonNull ViewModelError error) {
        if (error.getType().equals(LoginViewModel.Error.AUTHENTICATE)) {
            Snackbar.make(btnActivate, R.string.login_error_authenticating, Snackbar.LENGTH_LONG)
                    .setAction(R.string.login_snackbar_action_try_again, view -> signIn()).show();
        }
    }

    private void processAuthenticated(@NonNull Boolean isAuthenticated) {
        if (isAuthenticated) {
            startActivity(new Intent(this, DeviceListActivity.class));
            finish();
        }
    }

    private void signIn() {
        mViewModel.authenticate(etUserName.getText().toString(),
                etPassword.getText().toString());
    }

    private boolean validateUsername(String username) {
        return !username.isEmpty();
    }

    private boolean validatePassword(String password) {
        return !password.isEmpty();
    }
}
