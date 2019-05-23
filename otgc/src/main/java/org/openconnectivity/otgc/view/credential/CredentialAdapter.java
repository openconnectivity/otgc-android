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

package org.openconnectivity.otgc.view.credential;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.resource.secure.cred.OcCredential;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.CredentialViewHolder> {
    SortedList<OcCredential> mDataset;
    private Context mContext;
    private static DeleteClickListener sDeleteClickListener;

    public static class CredentialViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.text_cred_id) TextView mCredId;
        @BindView(R.id.text_cred_subject) TextView mCredSubject;
        @BindView(R.id.text_cred_type) TextView mCredType;
        @BindView(R.id.text_cred_role) TextView mCredRole;
        @BindView(R.id.text_cred_usage) TextView mCredUsage;
        @BindView(R.id.img_btn_delete_cred) ImageButton mDeleteButton;

        private CredentialViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sDeleteClickListener.onDeleteClick(getAdapterPosition(), v);
        }
    }

    CredentialAdapter(Context context) {
        mContext = context;

        mDataset = new SortedList<>(OcCredential.class, new SortedList.Callback<OcCredential>() {
            @Override
            public int compare(OcCredential c1, OcCredential c2) {
                return Long.compare(c1.getCredid(), c2.getCredid());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(OcCredential oldItem, OcCredential newItem) {
                // TODO: Improve
                return oldItem.getCredid() == newItem.getCredid();
            }

            @Override
            public boolean areItemsTheSame(OcCredential item1, OcCredential item2) {
                return item1.getCredid() == item2.getCredid();
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }
        });
    }

    public static void setOnDeleteClickListener(DeleteClickListener deleteClickListener) {
        sDeleteClickListener = deleteClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public CredentialAdapter.CredentialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cred, parent, false);

        return new CredentialViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CredentialViewHolder holder, int position) {
        OcCredential cred = mDataset.get(position);

        if (cred != null) {
            holder.mCredId.setText(String.format("%d", cred.getCredid()));
            holder.mCredSubject.setText(mContext.getString(R.string.credentials_cardview_subject_uuid, cred.getSubjectuuid()));
            holder.mCredType.setText(cred.getCredtype().toString());
            if (cred.getRoleid() != null) {
                holder.mCredRole.setVisibility(View.VISIBLE);
                holder.mCredRole.setText(mContext.getString(R.string.credentials_cardview_role,
                        cred.getRoleid().getRole(), cred.getRoleid().getAuthority()));
            } else {
                holder.mCredRole.setVisibility(View.GONE);
            }
            if (cred.getCredusage() != null) {
                holder.mCredUsage.setVisibility(View.VISIBLE);
                holder.mCredUsage.setText(cred.getCredusage().toString());
            } else {
                holder.mCredUsage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset != null ? mDataset.size() : 0;
    }

    public void clearItems() {
        mDataset.beginBatchedUpdates();
        while (mDataset.size() > 0) {
            mDataset.removeItemAt(mDataset.size() - 1);
        }
        mDataset.endBatchedUpdates();
    }

    public void addItem(OcCredential item) {
        if (item != null) {
            mDataset.add(item);
        }
    }

    public void deleteItemById(long credId) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getCredid() == credId) {
                mDataset.removeItemAt(i);
                break;
            }
        }
    }

    public interface DeleteClickListener {
        void onDeleteClick(int position, View v);
    }
}
