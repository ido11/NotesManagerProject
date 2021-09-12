package com.example.notesmanager.Objects;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ToastManager {

    private static final int TEXT_SIZE = 30;
    private static final int X_OFFSET = 0;
    private static final int Y_OFFSET = 0;

    // This function makes a toast with a given parameters
    public static void makeCustomToast(Context context, String msg, int backGroundColor, int textColor){
        try {
            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            // Toast location
            toast.setGravity(Gravity.CENTER, X_OFFSET, Y_OFFSET);

            View view = toast.getView();
            // Toast background
            view.getBackground().setColorFilter(backGroundColor, PorterDuff.Mode.SRC_IN);

            //Gets the TextView from the Toast so it can be edited
            TextView text = view.findViewById(android.R.id.message);
            text.setTextColor(textColor);
            text.setTextSize(TEXT_SIZE);

            toast.show();
        }catch(Exception e){
            try{
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            }catch(Exception e1){

            }
        }
    }
}
