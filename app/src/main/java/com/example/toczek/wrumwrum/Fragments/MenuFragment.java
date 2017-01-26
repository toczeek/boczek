package com.example.toczek.wrumwrum.Fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.toczek.wrumwrum.R;
import com.example.toczek.wrumwrum.Utils.models.ValueItem;
import com.example.toczek.wrumwrum.Utils.providers.Fragments.MenuFragmentProvider;
import com.example.toczek.wrumwrum.Utils.providers.Obd.ObdListener;
import com.example.toczek.wrumwrum.Utils.views.VelocimeterView;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.AbsoluteLoadCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.OilTempCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Toczek on 2016-12-08.
 */

public class MenuFragment extends Fragment implements ObdListener {

    private MenuFragmentProvider mMenuFragmentProvider;
    final CharSequence[] itemNames = {
            "Prędkość", "Obroty", "Temperatura powietrza", "Temperatura płynu chłodniczego", "Temperatura oleju",
            "Spalanie", "Ciśnienie atmosferyczne", "Ciśnienie paliwa", "Poziom paliwa"
    };
    ValueItem selectedVelue1;
    ValueItem selectedVelue2;
    @BindView(R.id.button1)
    Button mButton;
    @BindView(R.id.rpmVelocimeter)
    VelocimeterView rpmVelocimeter;
    @BindView(R.id.speedVelocimeter)
    VelocimeterView speedVelocimeter;
    @OnLongClick(R.id.speedVelocimeter)
    public boolean selectFirstVelocimeter() {
        showSelectVolicmeterDialog(speedVelocimeter, selectedVelue1);
        return false;
    }
    @OnLongClick(R.id.rpmVelocimeter)
    public boolean selectSecondVelocimeter() {
        showSelectVolicmeterDialog(rpmVelocimeter, selectedVelue2);
        return false;
    }
    @OnClick(R.id.button1)
    public void onClickBtn1() {
        showHelpDialog();
    }
    @OnClick(R.id.button)
    public void OnClickBtn() {
        showBTDeviceDialog();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        ButterKnife.bind(this, view);
        mMenuFragmentProvider = new MenuFragmentProvider();
        selectedVelue1 = new ValueItem();
        selectedVelue2 = new ValueItem();
        selectedVelue1.setId(0);
        selectedVelue2.setId(1);
        return view;
    }
    private void showHelpDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogLayout = inflater.inflate(R.layout.dialog_help, null);
        dialog.setView(dialogLayout);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.show();
    }
    private void showSelectVolicmeterDialog(final VelocimeterView velocimeterView, final ValueItem valueItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Wybór wskaźnika");
        builder.setItems(itemNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemId) {
                valueItem.setId(itemId);
                mMenuFragmentProvider.optionCombiner(valueItem);
                velocimeterView.setMax(valueItem.getMaxValue());
                velocimeterView.setUnits(getContext(), valueItem.getUnit());
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void showBTDeviceDialog() {
        ArrayList deviceStrs = new ArrayList();
        final ArrayList devices = new ArrayList();

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        final Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());
            }
        }

        // show list
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());

        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.select_dialog_singlechoice,
                deviceStrs.toArray(new String[deviceStrs.size()]));

        alertDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                    int position = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String deviceAddress = devices.get(position).toString();
                    mMenuFragmentProvider.setupObdAndStart(deviceAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("OBDHACK", "ER: " + e);
                }
                //---------------------------------------------
            }
        });


        alertDialog.setTitle("Choose Bluetooth device");
        alertDialog.show();
    }

    @Override
    public void wasUpdate() {
        if (mMenuFragmentProvider != null) {
            mMenuFragmentProvider.optionCombiner(selectedVelue1);
            mMenuFragmentProvider.optionCombiner(selectedVelue2);
            speedVelocimeter.setValue(selectedVelue1.getValue(), false);
            rpmVelocimeter.setValue(selectedVelue2.getValue(), false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMenuFragmentProvider.setObdListener(this);
    }
}
