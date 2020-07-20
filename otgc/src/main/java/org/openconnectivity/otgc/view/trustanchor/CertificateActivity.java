package org.openconnectivity.otgc.view.trustanchor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.utils.di.Injectable;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.viewmodel.CertificateViewModel;

import java.io.InputStream;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CertificateActivity extends AppCompatActivity implements Injectable {
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.trust_anchor_select_end_entity_layout) LinearLayout selectEndEntityLayout;
    @BindView(R.id.spinner_select_end_entity_certificate) Spinner selectEndEntityCert;
    @BindView(R.id.button_select_certificate) Button selectCertificate;
    @BindView(R.id.trust_anchor_selected_certificate_text) TextView selectedCertificateText;
    @BindView(R.id.trust_anchor_select_key_layout) LinearLayout selectKeyLayout;
    @BindView(R.id.button_select_key) Button selectKey;
    @BindView(R.id.trust_anchor_selected_key_text) TextView selectKeyText;
    @BindView(R.id.radio_root_certificate) RadioButton rootRadioButton;
    @BindView(R.id.radio_intermediate_certificate) RadioButton intermediateRadioButton;
    @BindView(R.id.radio_end_entity_certificate) RadioButton endEntityRadioButton;

    @OnClick({ R.id.radio_root_certificate, R.id.radio_intermediate_certificate, R.id.radio_end_entity_certificate})
    public void onRadioButtonClicked(RadioButton rb) {
        // Is the button now checked?
        boolean checked = rb.isChecked();

        // Check which radio button was clicked
        switch (rb.getId()) {
            case R.id.radio_root_certificate:
                if (checked) {
                    selectEndEntityLayout.setVisibility(View.GONE);
                    selectKeyLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_intermediate_certificate:
                if (checked) {
                    selectEndEntityLayout.setVisibility(View.VISIBLE);
                    selectKeyLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.radio_end_entity_certificate:
                if (checked) {
                    selectEndEntityLayout.setVisibility(View.GONE);
                    selectKeyLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private static final int READ_REQUEST_CODE = 42;

    @OnClick({ R.id.button_select_certificate, R.id.button_select_key })
    public void onSelectFileClicked(Button button) {
        if (button.getId() == R.id.button_select_certificate) {
            isForCert = true;
            isForKey = false;
        } else if (button.getId() == R.id.button_select_key) {
            isForCert = false;
            isForKey = true;
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    private boolean isForCert = false;
    private boolean isForKey = false;
    private InputStream fileIs;
    private InputStream keyIs;

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    if (isForCert) {
                        fileIs = is;
                        isForCert = false;
                        selectedCertificateText.setText(uri.getPath());
                    } else if (isForKey) {
                        keyIs = is;
                        isForKey = false;
                        selectKeyText.setText(uri.getPath());
                    }
                } catch (Exception e) {
                    int errorId = R.string.trust_anchor_create_error;
                    Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    @OnClick(R.id.floating_button_cert_save)
    protected void onSavePressed() {
         if (rootRadioButton.isChecked()) {
            mViewModel.saveTrustAnchor(fileIs);
        } else if (intermediateRadioButton.isChecked()) {
            if (selectEndEntityCert.getSelectedItem() != null) {
                int index = selectEndEntityCert.getSelectedItemPosition();
                Integer credid = (Integer)selectEndEntityCert.getAdapter().getItem(index);
                mViewModel.saveIntermediateCertificate(credid, fileIs);
            }
        } else if (endEntityRadioButton.isChecked()) {
            mViewModel.saveEndEntityCertificate(fileIs, keyIs);
        }
    }

    private CertificateViewModel mViewModel;

    private ArrayList<Integer> mCertList;

    private ArrayAdapter<Integer> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        Intent intent = getIntent();
        mCertList = (ArrayList<Integer>) intent.getSerializableExtra("certs");

        ButterKnife.bind(this);
        initViews();
        initViewModel();
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

        selectedCertificateText.setText(R.string.trust_anchor_no_selected_certificate_text);
        selectKeyText.setText(R.string.trust_anchor_no_selected_key_text);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        if (mCertList != null) {
            mAdapter.addAll(mCertList);
        }
        selectEndEntityCert.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(CertificateViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getSuccess().observe(this, this::processSuccess);
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        // TODO
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = 0;
        switch ((CertificateViewModel.Error)error.getType()) {
            case ROOT_CERTIFICATE:
                errorId = R.string.trust_anchor_error_create_root_certificate;
                break;
            case INTERMEDIATE_CERTIFICATE:
                errorId = R.string.trust_anchor_error_create_intermediate_certificate;
                break;
            case END_ENTITY_CERTIFICATE:
                errorId = R.string.trust_anchor_error_create_end_entity_certificate;
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
