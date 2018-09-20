package components;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.sapergis.parking.R;

import helperClasses.Helper;
import interfaces.PopUpInterface;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;


public class PopUp{
   private PopupWindow popupWindow;
   private int popUpX;
   private int popUpY;
   private int screenWidth;
   private View parentView;
   private Context context;

   public PopUp(View parentView, Context context){
            this.parentView = parentView;
            this.context = context;
            popupWindow = new PopupWindow();
            calculateScreenDimensions();
            setUpMarkPopUp();
            calculatePopUpPosition();
   }

    private void setUpMarkPopUp(){
        ImageView vehicleImg;
        ImageView currentLocationImg;
        final Activity mActivity = (Activity) context;
        final PopUpInterface popUpInterface = (PopUpInterface)mActivity;
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        try{

            View popupView = inflater.inflate(R.layout.popup_window_layout, null);
            vehicleImg = popupView.findViewById(R.id.vehicleMark);
            vehicleImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpInterface.popUpSelectionIs(Helper.GO_TO_VEHICLE);
                }
            });
            currentLocationImg = popupView.findViewById(R.id.currentLocationMark);
            currentLocationImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpInterface.popUpSelectionIs(Helper.GO_TO_CURRENT_LOCATION);
                }
            });
            popupWindow.setContentView(popupView);
            popupWindow.setHeight(200);
            popupWindow.setWidth(screenWidth/2);
            popupWindow.setOutsideTouchable(true);
            //popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.cast_album_art_placeholder));

        }catch (NullPointerException nex){
            Log.e(Helper.TAG, nex.getMessage());
        }

    }

    private void setUpViews(View view){



    }

    private void calculateScreenDimensions(){
        WindowManager wm = (WindowManager)context.getSystemService(WINDOW_SERVICE);
        try{
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }catch (NullPointerException nex){
            Log.e(Helper.TAG, nex.getMessage());
        }

    }

    private void calculatePopUpPosition(){
        int [] position = new int[2];
        parentView.getLocationOnScreen(position);
        popUpX = (screenWidth/2) - (popupWindow.getWidth()/2);//parentView.getWidth();
        popUpY = position[1] - (parentView.getHeight()/2) - popupWindow.getHeight();

    }

    public void show(){
        popupWindow.showAtLocation(parentView, Gravity.NO_GRAVITY, popUpX, popUpY);
    }

    public boolean isShowing(){
        return  popupWindow.isShowing();
    }

    public void dismiss(){
        popupWindow.dismiss();
    }
}
