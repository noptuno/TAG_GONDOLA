package com.desarrollo.myapplication.fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import com.desarrollo.myapplication.R;

public class PorcentajeDescuento extends DialogFragment {


    //todo prueba de cambios

    public interface DiscountSelectionListener {
        void onDiscountSelected(int discount);
    }

    private DiscountSelectionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (DiscountSelectionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar DiscountSelectionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mListener = (DiscountSelectionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString() + " debe implementar DiscountSelectionListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Selecciona un Descuento")
                .setItems(R.array.discount_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int[] discountOptions = getResources().getIntArray(R.array.discount_values);
                        int selectedDiscount = discountOptions[which];
                        mListener.onDiscountSelected(selectedDiscount);
                        dismiss();
                    }
                });
        return builder.create();
    }
}