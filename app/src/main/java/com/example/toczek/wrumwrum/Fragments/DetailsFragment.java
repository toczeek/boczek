package com.example.toczek.wrumwrum.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.toczek.wrumwrum.R;
import com.example.toczek.wrumwrum.Utils.providers.Fragments.DetailsFragmentProvider;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Toczek on 2017-01-07.
 */

public class DetailsFragment extends Fragment implements ObdListener {
    private DetailsFragmentProvider mDetailsFragmentProvider;
    @BindView(R.id.details_fragment_air_temp)
    TextView mAirTempTv;
    @BindView(R.id.details_fragment_coolant_temp)
    TextView mCoolantTempTv;
    @BindView(R.id.details_fragment_oil_temp)
    TextView mOilTempTv;
    @BindView(R.id.details_fragment_consumption_rate)
    TextView mConsumptionRateTempTv;
    @BindView(R.id.details_fragment_barometric_pressure)
    TextView mBarometricPressureTv;
    @BindView(R.id.details_fragment_fuel_pressure)
    TextView mFuelPressureTv;
    @BindView(R.id.details_fragment_fuel_level)
    TextView mFuelLevelTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.details_fragement, container, false);
        ButterKnife.bind(view);
        mDetailsFragmentProvider = new DetailsFragmentProvider();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mDetailsFragmentProvider.setObdListener(this);
    }

    @Override
    public void wasUpdate() {
        mCoolantTempTv.setText(""+mDetailsFragmentProvider.getCoolantTemp());
        mBarometricPressureTv.setText(""+mDetailsFragmentProvider.getBarometricPressure());
        mConsumptionRateTempTv.setText(""+mDetailsFragmentProvider.getConsumptionRate());
        mFuelLevelTv.setText(""+mDetailsFragmentProvider.getFuelLevel());
        mFuelPressureTv.setText(""+mDetailsFragmentProvider.getFuelPressure());
        mOilTempTv.setText(""+mDetailsFragmentProvider.getOilTemp());

    }
}
