package com.wyk.wechattomap.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wyk.wechattomap.R;
import com.wyk.wechattomap.base.BaseDialog;

/**
 * @author wyk
 */
public class OpenGPSDialog extends BaseDialog {

    private TextView tv_dialog_title;
    private TextView tv_dialog_confirmg;
    private TextView tv_dialog_cancel;

    public interface OnOpenGPSListener {
        void cancelGPS();
        void confirmOpenGPS();
    }

    private OnOpenGPSListener mOnOpenGPSListener;
    public void setOnOpenGPSListener(OnOpenGPSListener onOpenGPSListener) {
        this.mOnOpenGPSListener = onOpenGPSListener;
    }

    public OpenGPSDialog(Context context) {
        super(context);
        initDate();
        initListener();
    }

    @Override
    public void initView() {
        Window window = getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_common);

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;

        tv_dialog_title = (TextView) findViewById(R.id.tv_dialog_title);
        tv_dialog_confirmg = (TextView) findViewById(R.id.tv_dialog_confirmg);
        tv_dialog_cancel = (TextView) findViewById(R.id.tv_dialog_cancel);
    }

    public void initDate() {
        tv_dialog_title.setText(mContext.getString(R.string.usercenter_selete_address_gps_close));
        tv_dialog_confirmg.setText(mContext.getString(R.string.button_confirm));
        tv_dialog_cancel.setText(mContext.getString(R.string.button_cancel));
    }

    public void initListener() {
        tv_dialog_confirmg.setOnClickListener(this);
        tv_dialog_cancel.setOnClickListener(this);
    }

    @Override
    public void onDialogClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_confirmg:
                if(mOnOpenGPSListener !=null){
                    mOnOpenGPSListener.confirmOpenGPS();
                    dismiss();
                }
                break;
            case R.id.tv_dialog_cancel:
                if(mOnOpenGPSListener !=null){
                    mOnOpenGPSListener.cancelGPS();
                    dismiss();
                }
                break;
        }
    }
}