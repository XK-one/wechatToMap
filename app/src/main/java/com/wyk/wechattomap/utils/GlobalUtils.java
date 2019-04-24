package com.wyk.wechattomap.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wyk.wechattomap.R;

public class GlobalUtils {

    public static ColorStateList setBtnColorState(Context context){
        //头部标题点击字体样式
        int stateSelected = android.R.attr.state_selected;
        int[][] state = {{stateSelected}, {-stateSelected}};
        int selectColor = context.getResources().getColor(R.color.text_gray_01);
        int normalColor = context.getResources().getColor(R.color.white);
        int [] selectState = {selectColor, normalColor};
        ColorStateList colorStateList = new ColorStateList(state,selectState);
        return colorStateList;
    }

    public static Dialog getLoadingDialog(Activity activity, String tvLoadingStr, boolean
            isCanceledOnTouchOutside){
        Dialog dlg = new Dialog(activity, R.style.ActiviyDialogStyle);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View viewContent = LayoutInflater.from(activity)
                .inflate( R.layout.loading_progress_dialog, null, false);
        ProgressBar pb = viewContent.findViewById(R.id.iv_loading_progress_dialog_loadprogress);
        pb.setInterpolator(new AccelerateDecelerateInterpolator());
        pb.animate().setDuration(100);

        if(tvLoadingStr!=null && !TextUtils.isEmpty(tvLoadingStr)){
            TextView tvLoading = (TextView) viewContent.findViewById(R.id.more_data_msg);
            tvLoading.setText(tvLoadingStr);
        }

        dlg.setContentView(viewContent);
        dlg.setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        dlg.setCancelable(true);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

        Window window = dlg.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.3f;
        lp.width = dm.widthPixels * 20 / 20;
        window.setAttributes(lp);
        //dlg.show();
        return dlg;
    }

    /** 检测GPS定位是否开启*/
    public static boolean isOpenGps(Context context){
        LocationManager locationManager = (LocationManager)context.getSystemService(Context
                .LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /** 隐藏键盘*/
    public static void hideKeyboard(View view){
        Context context = view.getContext();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
