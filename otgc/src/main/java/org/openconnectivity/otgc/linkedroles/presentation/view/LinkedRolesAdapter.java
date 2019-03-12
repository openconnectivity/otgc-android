/*
 * *****************************************************************
 *
 *  Copyright 2019 DEKRA Testing and Certification, S.A.U. All Rights Reserved.
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

package org.openconnectivity.otgc.linkedroles.presentation.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.openconnectivity.otgc.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LinkedRolesAdapter extends RecyclerView.Adapter<LinkedRolesAdapter.LinkedRolesViewHolder> {
    SortedList<String> mDataset;
    private static LinkedRolesAdapter.DeleteClickListener sDeleteClickListener;

    public static class LinkedRolesViewHolder  extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @BindView(R.id.text_role_id) TextView mRoleId;
        @BindView(R.id.img_btn_delete_role) ImageButton mDeleteButton;

        private LinkedRolesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mDeleteButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sDeleteClickListener.onDeleteClick(getAdapterPosition(), v);
        }
    }

    LinkedRolesAdapter(Context context) {
        mDataset = new SortedList<>(String.class, new SortedList.Callback<String>() {
            @Override
            public int compare(String a1, String a2) {
                return a1.compareTo(a2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(String oldItem, String newItem) {
                // TODO: Improve
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(String item1, String item2) {
                return item1.equals(item2);
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

    public static void setOnDeleteClickListener(LinkedRolesAdapter.DeleteClickListener deleteClickListener) {
        sDeleteClickListener = deleteClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public LinkedRolesAdapter.LinkedRolesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_role, parent, false);

        return new LinkedRolesAdapter.LinkedRolesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkedRolesViewHolder holder, int position) {
        String role = mDataset.get(position);

        if (role != null) {
            holder.mRoleId.setText(role);
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

    public void addItem(String item) {
        if (item != null) {
            mDataset.add(item);
        }
    }

    public void deleteItemById(String roleId) {
        for (int i = 0; i < mDataset.size(); i++) {
            if (mDataset.get(i).equals(roleId)) {
                mDataset.removeItemAt(i);
                break;
            }
        }
    }

    public interface DeleteClickListener {
        void onDeleteClick(int position, View v);
    }
}
