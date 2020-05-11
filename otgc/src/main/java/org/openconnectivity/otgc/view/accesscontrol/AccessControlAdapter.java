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

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.iotivity.OCAceSubjectType;
import org.openconnectivity.otgc.R;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAce;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAceResource;
import org.openconnectivity.otgc.domain.model.resource.secure.acl.OcAceSubjectType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccessControlAdapter extends RecyclerView.Adapter<AccessControlAdapter.AccessControlViewHolder> {
    SortedList<OcAce> mDataset;
    private Context mContext;
    private static DeleteClickListener sDeleteClickListener;

    public static class AccessControlViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.text_ace_id) TextView mAceId;
        @BindView(R.id.text_subject_id) TextView mSubjectId;
        @BindView(R.id.checkbox_permission_create) CheckBox mPermissionCreate;
        @BindView(R.id.checkbox_permission_retrieve) CheckBox mPermissionRetrieve;
        @BindView(R.id.checkbox_permission_update) CheckBox mPermissionUpdate;
        @BindView(R.id.checkbox_permission_delete) CheckBox mPermissionDelete;
        @BindView(R.id.checkbox_permission_notify) CheckBox mPermissionNotify;
        @BindView(R.id.linear_resources) LinearLayout mResources;
        @BindView(R.id.img_btn_delete_ace) ImageButton mDeleteButton;

        private AccessControlViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sDeleteClickListener.onDeleteClick(getAdapterPosition(), v);
        }
    }

    AccessControlAdapter(Context context) {
        mContext = context;

        mDataset = new SortedList<>(OcAce.class, new SortedList.Callback<OcAce>() {
            @Override
            public int compare(OcAce a1, OcAce a2) {
                return Long.compare(a1.getAceid(), a2.getAceid());
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(OcAce oldItem, OcAce newItem) {
                // TODO: Improve
                return oldItem.getAceid() == newItem.getAceid();
            }

            @Override
            public boolean areItemsTheSame(OcAce item1, OcAce item2) {
                return item1.getAceid() == item2.getAceid();
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
    public AccessControlAdapter.AccessControlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ace, parent, false);

        return new AccessControlViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessControlViewHolder holder, int position) {
        OcAce ace = mDataset.get(position);

        if (ace != null) {
            holder.mAceId.setText(String.format("%d", ace.getAceid()));
            String subject = "";
            if (OCAceSubjectType.valueOf(ace.getSubject().getType()) == OCAceSubjectType.OC_SUBJECT_UUID) {
                subject = mContext.getString(R.string.access_control_cardview_subject_uuid, ace.getSubject().getUuid());
            } else if (OCAceSubjectType.valueOf(ace.getSubject().getType()) == OCAceSubjectType.OC_SUBJECT_UUID) {
                String connType;
                switch (ace.getSubject().getConnType()) {
                    case "anon-clear":
                        connType = mContext.getString(R.string.access_control_cardview_subject_connection_type_anon);
                        break;
                    case "auth-crypt":
                        connType = mContext.getString(R.string.access_control_cardview_subject_connection_type_auth);
                        break;
                    default:
                        connType = ace.getSubject().getConnType();
                        break;
                }
                subject = mContext.getString(R.string.access_control_cardview_subject_connection_type, connType);
            } else if (OCAceSubjectType.valueOf(ace.getSubject().getType()) == OCAceSubjectType.OC_SUBJECT_ROLE) {
                    subject = mContext.getString(R.string.access_control_cardview_subject_role, ace.getSubject().getRoleId(), ace.getSubject().getAuthority());
            }
            holder.mSubjectId.setText(subject);
            holder.mPermissionCreate.setChecked((ace.getPermission() & 1) == 1);
            holder.mPermissionRetrieve.setChecked((ace.getPermission() & 2) == 2);
            holder.mPermissionUpdate.setChecked((ace.getPermission() & 4) == 4);
            holder.mPermissionDelete.setChecked((ace.getPermission() & 8) == 8);
            holder.mPermissionNotify.setChecked((ace.getPermission() & 16) == 16);

            if (ace.getResources() != null) {
                List<String> hrefList = new ArrayList<>();
                for (OcAceResource resource : ace.getResources()) {
                    hrefList.add(resource.getHref() != null ?
                            resource.getHref() : mContext.getString(R.string.access_control_ace_resources_all));
                }

                for (String href : hrefList) {
                    TextView tv = new TextView(mContext);
                    tv.setText(href);
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.font_size_notes));
                    holder.mResources.addView(tv);
                }
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

    public void addItem(OcAce item) {
        if (item != null) {
            mDataset.add(item);
        }
    }

    public void deleteItemById(long aceId) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).getAceid() == aceId) {
                mDataset.removeItemAt(i);
                break;
            }
        }
    }

    public interface DeleteClickListener {
        void onDeleteClick(int position, View v);
    }
}
