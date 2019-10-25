/*
 * Copyright 2018 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
 *
 * ****************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *            http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openconnectivity.otgc.view.trustanchor;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;
import org.openconnectivity.otgc.utils.FilePath;
import org.openconnectivity.otgc.utils.di.Injectable;
import org.openconnectivity.otgc.utils.view.EmptyRecyclerView;
import org.openconnectivity.otgc.utils.viewmodel.ViewModelError;
import org.openconnectivity.otgc.viewmodel.TrustAnchorViewModel;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.encoders.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrustAnchorActivity extends AppCompatActivity implements Injectable {
    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_ocf_trustanchors) EmptyRecyclerView mRecyclerView;
    @BindView(R.id.floating_button_trustanchor_add) FloatingActionButton mFloatingActionButton;

    private static final int READ_REQUEST_CODE = 42;

    @OnClick(R.id.floating_button_trustanchor_add)
    protected void onAddPressed() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

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
                String path = FilePath.getPath(this, uri);
                mViewModel.addTrustAnchor(path);
            }
        }
    }

    private TrustAnchorViewModel mViewModel;
    private TrustAnchorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trust_anchor);

        ButterKnife.bind(this);
        initViews();
        initViewModel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.clearItems();
        mViewModel.retrieveTrustAnchors();
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

        mAdapter = new TrustAnchorAdapter(this);
        TrustAnchorAdapter.setOnClickListener(new TrustAnchorAdapter.ClickListener() {
            @Override
            public void onDeleteClick(int position, View v) {
                mViewModel.removeTrustAnchorByCredid(mAdapter.mDataset.get(position).getCredid());
            }

            @Override
            public void onInfoClick(int position, View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(TrustAnchorActivity.this, R.style.AppTheme));
                // set title
                alertDialogBuilder.setTitle("Trust Anchor - Information");
                // set dialog message
				
				if (mAdapter.mDataset.get(position).getPublicData().getDerData() != null) {
					alertDialogBuilder
                        .setMessage(showX509CertificateInformation(mAdapter.mDataset.get(position).getPublicData().getDerData()));
				} else if (mAdapter.mDataset.get(position).getPublicData().getPemData() != null) {
					String pem = mAdapter.mDataset.get(position).getPublicData().getPemData();
					String base64 = pem.replaceAll("\\s", "")
										.replaceAll("\\r\\n", "")
										.replace("-----BEGINCERTIFICATE-----", "")
										.replace("-----ENDCERTIFICATE-----", "");
					byte[] der = Base64.decode(base64.getBytes());
					alertDialogBuilder
                        .setMessage(showX509CertificateInformation(der));
				}
                
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(TrustAnchorViewModel.class);

        mViewModel.isProcessing().observe(this, this::handleProcessing);
        mViewModel.getError().observe(this, this::handleError);

        mViewModel.getCredential().observe(this, this::processCredential);
        mViewModel.getDeletedCredid().observe(this, this::processDeletedCredid);
    }

    private String showX509CertificateInformation(byte[] cert) {
        try (InputStream inputStream = new ByteArrayInputStream(cert)) {
            Security.addProvider(new BouncyCastleProvider());
            CertificateFactory factory = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
            X509Certificate caCert = (X509Certificate) factory.generateCertificate(inputStream);

            String pattern = "EEE, d MMM yyyy HH:mm:ss Z";
            SimpleDateFormat format = new SimpleDateFormat(pattern);

            String ret = "Subject\n\t" + caCert.getSubjectDN() + "\n" +
                    "Issuer\n\t" + caCert.getIssuerDN() + "\n" +
                    "Version " + caCert.getVersion() + "\n" +
                    "Serial Number\n" + caCert.getSerialNumber() + "\n" +
                    "Signature algorithm\n\t" + caCert.getSigAlgName() + "\n" +
                    "Validity\n" +
                    "\tNot Before\n" + format.format(caCert.getNotBefore()) + "\n" +
                    "\tNot After\n" + format.format(caCert.getNotAfter()) + "\n";


            return ret;
        } catch (Exception e) {
            // TODO:
        }

        return null;
    }

    private void handleProcessing(@NonNull Boolean isProcessing) {
        mProgressBar.setVisibility(isProcessing ? View.VISIBLE : View.GONE);
    }

    private void handleError(@NonNull ViewModelError error) {
        int errorId = -1;
        switch ((TrustAnchorViewModel.Error)error.getType()) {
            case ADD_ROOT_CERT:
                errorId = R.string.trust_anchor_create_error;
                break;
        }

        if (errorId != 0) {
            Toast.makeText(this, errorId, Toast.LENGTH_SHORT).show();
        }
    }

    private void processCredential(@NonNull OcCredential credential) {
        mAdapter.addItem(credential);
    }

    private void processDeletedCredid(@NonNull Long credid) {
        mAdapter.deleteItemById(credid);
    }
}
