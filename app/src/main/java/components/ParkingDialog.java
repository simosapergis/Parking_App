package components;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.sapergis.parking.R;

import interfaces.ParkingDialogInterface;

public class ParkingDialog {
    Activity mActivity;

    public void show(final Activity activity , String txtMsg , String posBtnMsg , String negBtnMsg){
        mActivity = activity;
        final ParkingDialogInterface returnToActivity = (ParkingDialogInterface)mActivity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.parking_dialog_layout);

        TextView textView = (TextView)dialog.findViewById(R.id.text_dialog);
        textView.setText(txtMsg);

        Button positiveButton = (Button) dialog.findViewById(R.id.negativeBtn);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToActivity.releaseParkingLocation(true);
                dialog.dismiss();
            }
        });
        positiveButton.setText(posBtnMsg);
        Button negativeButton = (Button) dialog.findViewById(R.id.positiveBtn);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnToActivity.releaseParkingLocation(false);
                dialog.dismiss();
            }
        });
        negativeButton.setText(negBtnMsg);

        dialog.show();
    }


}
