package com.winapp.svcatering.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.winapp.svcatering.model.TaxModel;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class SingleChoiceDialogFragment extends DialogFragment {

    int position = 0; //default selected position
    SweetAlertDialog pDialog;
    SessionManager session;
    HashMap<String, String> user;
    String companyCode;
    ArrayList<TaxModel> taxList;


    public SingleChoiceDialogFragment(ArrayList<TaxModel> taxList){
        this.taxList=taxList;
    }

    public interface SingleChoiceListener {
        void onPositiveButtonClicked(String[] list, int position);
        void onNegativeButtonClicked();
    }

    SingleChoiceListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SingleChoiceListener) context;
        } catch (Exception e) {
            throw new ClassCastException(getActivity().toString() + " SingleChoiceListener must implemented");
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        session=new SessionManager(getActivity());
        user = session.getUserDetails();
        companyCode = user.get(SessionManager.KEY_COMPANY_CODE);


        ArrayList<String> taxs=new ArrayList<>();
        if (taxList.size()>0){
            for (TaxModel model:taxList){
                taxs.add(model.getTaxName());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
       // String frnames[]=friendsnames.toArray(new String[friendsnames.size()]);
        final String[] list = taxs.toArray(new String[taxs.size()]);
        builder.setTitle("Select Tax")
                .setSingleChoiceItems(list, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        position = i;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onPositiveButtonClicked(list, position);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onNegativeButtonClicked();
                    }
                });

        return builder.create();
    }

}