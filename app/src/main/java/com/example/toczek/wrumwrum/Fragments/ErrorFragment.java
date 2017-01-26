package com.example.toczek.wrumwrum.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.toczek.wrumwrum.R;
import com.example.toczek.wrumwrum.Utils.providers.Fragments.ErrorFragmentProvider;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Toczek on 2016-12-08.
 */

public class ErrorFragment extends Fragment implements ObdListener {
    private ErrorFragmentProvider mErrorFragmentProvider;
    @BindView(R.id.error_fragment_trouble_codes)
    TextView mTroubleCodesTv;
    @BindView(R.id.error_fragment_pending_trouble_codes)
    TextView mPendingTroubleCodesTv;
    @BindView(R.id.error_fragment_permanent_trouble_codes)
    TextView mPermanentTroubleCodesTv;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.error_fragment, container, false);
            ButterKnife.bind(this, view);
            mErrorFragmentProvider = new ErrorFragmentProvider();
            return view;
        }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        mErrorFragmentProvider.setObdListener(this);
    }

    @Override
    public void wasUpdate() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        });
    }
    private void getData() {
        mTroubleCodesTv.setText("Trouble Codes : "+mErrorFragmentProvider.getTroubleCodes());
        mPendingTroubleCodesTv.setText("Pending Trouble Codes : "+mErrorFragmentProvider.getPendingTroubleCodes());
        mPermanentTroubleCodesTv.setText("Trouble Codes : "+mErrorFragmentProvider.getPendingTroubleCodes());
    }
    }

