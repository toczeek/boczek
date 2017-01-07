package com.example.toczek.wrumwrum.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.toczek.wrumwrum.Fragments.DetailsFragment;
import com.example.toczek.wrumwrum.Fragments.ErrorFragment;
import com.example.toczek.wrumwrum.Fragments.LogbookFragment;
import com.example.toczek.wrumwrum.Fragments.MenuFragment;
import com.example.toczek.wrumwrum.R;
import com.example.toczek.wrumwrum.Utils.adapters.NavigationAdapter;
import com.example.toczek.wrumwrum.Utils.models.NavItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<NavItem> mNavItems;
    private FragmentManager mFragmentManager;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.left_drawer)
    ListView mDrawerList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mFragmentManager = getSupportFragmentManager();
        setupStartFragment();
        setupNavigationDrawer();
    }

    private void setupStartFragment(){
        MenuFragment menuFragment = new MenuFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, menuFragment);
        transaction.commit();
    }

    private void addFragmentToBackStack(Fragment fragment) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    private void setupNavigationDrawer(){
        mNavItems = new ArrayList<>();
        mNavItems.add(new NavItem(android.R.drawable.ic_menu_view, "Start", (Fragment) new MenuFragment()));
        mNavItems.add(new NavItem(android.R.drawable.ic_menu_delete, "Diagnostyka", (Fragment) new ErrorFragment()));
        mNavItems.add(new NavItem(android.R.drawable.ic_menu_camera, "Szczegółowe dane", (Fragment) new DetailsFragment()));
        mNavItems.add(new NavItem(android.R.drawable.ic_menu_help, "Logbook", (Fragment) new LogbookFragment()));

        mDrawerList.setAdapter(new NavigationAdapter(this, mNavItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if ( mNavItems.get(position).getFragment() !=null ) {
            addFragmentToBackStack(mNavItems.get(position).getFragment());
        }
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
