package com.example.toczek.wrumwrum.Utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toczek.wrumwrum.R;
import com.example.toczek.wrumwrum.Utils.models.NavItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Toczek on 2016-12-10.
 */

public class NavigationAdapter extends ArrayAdapter<NavItem> {
    public NavigationAdapter(Context context, ArrayList<NavItem> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        NavItem navItem = getItem(position);
        ViewHolder holder;
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        holder.mNavIconIv.setImageResource(navItem.getNavIcon());
        holder.mNavTitle.setText(navItem.getNavTitle());
        // Return the completed view to render on screen
        return convertView;
    }
    public static class ViewHolder {
        @BindView(R.id.nav_icon)
        ImageView mNavIconIv;
        @BindView(R.id.nav_title)
        TextView mNavTitle;

        private int mReleaseLtvHeight;

        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
