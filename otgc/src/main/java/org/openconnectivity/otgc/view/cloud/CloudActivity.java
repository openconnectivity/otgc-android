package org.openconnectivity.otgc.view.cloud;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.iotivity.OCCloudStatusMask;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.resource.cloud.OcCloudConfiguration;
import org.openconnectivity.otgc.utils.di.Injectable;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.viewmodel.CloudViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CloudActivity extends AppCompatActivity implements Injectable {
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.cloud_label_status)
    TextView mLabelStatus;
    @BindView(R.id.cloud_text_auth_provider)
    EditText mTextAuthProvider;
    @BindView(R.id.cloud_text_url)
    EditText mTextUrl;
    @BindView(R.id.cloud_text_access_token)
    EditText mTextAccessToken;
    @BindView(R.id.cloud_text_uuid)
    EditText mTextUuid;
    @BindView(R.id.floating_button_cloud_add)
    FloatingActionButton mFloatingActionButton;

    @OnClick(R.id.floating_button_cloud_add)
    protected void onAddPressed() {
        // TODO: Retrieve configuration from view
        String authProvider = mTextAuthProvider.getText().toString();
        String cloudUrl = mTextUrl.getText().toString();
        String accessToken = mTextAccessToken.getText().toString();
        String cloudUuid = mTextUuid.getText().toString();

        mViewModel.provisionCloudConfiguration(authProvider, cloudUrl, accessToken, cloudUuid);
    }

    private CloudViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_cloud);

        ButterKnife.bind(this);
        initViews();
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.retrieveStatus();
        mViewModel.retrieveCloudConfiguration();
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
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CloudViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getStatus().observe(this, this::processStatus);
        mViewModel.getCloudConfiguration().observe(this, this::processCloudConfiguration);
        mViewModel.getSuccess().observe(this, this::processSuccess);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = -1;
        switch ((CloudViewModel.Error)error.getType()) {
            case RETRIEVE_STATUS:
                errorId = R.string.cloud_retrieve_status_error;
                break;
            case RETRIEVE_CONFIGURATION:
                errorId = R.string.cloud_retrieve_configuration_error;
                break;
            case PROVISION_CONFIGURATION:
                errorId = R.string.cloud_provision_configuration_error;
                break;
        }

        if (errorId != -1) {
            Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void processStatus(@NonNull Integer status) {
        // Set value of status
        switch(status) {
            case OCCloudStatusMask.OC_CLOUD_INITIALIZED:
                mLabelStatus.setText("Initialized");
                break;
            case OCCloudStatusMask.OC_CLOUD_REGISTERED:
                mLabelStatus.setText("Registered");
                break;
            case OCCloudStatusMask.OC_CLOUD_LOGGED_IN:
                mLabelStatus.setText("Logged in");
                break;
            case OCCloudStatusMask.OC_CLOUD_TOKEN_EXPIRY:
                mLabelStatus.setText("Token expiry");
                break;
            case OCCloudStatusMask.OC_CLOUD_REFRESHED_TOKEN:
                mLabelStatus.setText("Refresh token");
                break;
            case OCCloudStatusMask.OC_CLOUD_LOGGED_OUT:
                mLabelStatus.setText("Logged out");
                break;
            case OCCloudStatusMask.OC_CLOUD_FAILURE:
                mLabelStatus.setText("Failure");
                break;
            case OCCloudStatusMask.OC_CLOUD_DEREGISTERED:
                mLabelStatus.setText("Deregistered");
                break;
            default:
                mLabelStatus.setText("Unknown");
                break;
        }
    }

    private void processCloudConfiguration(@NonNull OcCloudConfiguration cloudConf) {
        mTextAuthProvider.setText(cloudConf.getAuthProvider());
        mTextUrl.setText(cloudConf.getCloudUrl());
        mTextAccessToken.setText(cloudConf.getAccessToken());
        mTextUuid.setText(cloudConf.getCloudUuid());
    }

    private void processSuccess(@NonNull Boolean success) {
        if (success) {
            Toast.makeText(this, R.string.cloud_store_config_success, Toast.LENGTH_SHORT).show();
        }
    }
}
