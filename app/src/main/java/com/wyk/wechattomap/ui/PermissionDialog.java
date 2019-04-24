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
import com.wyk.wechattomap.utils.permission.OnPermisionListener;

/**
 * @author wyk
 * 权限弹框
 */
public class PermissionDialog extends BaseDialog {

    private TextView tv_dialog_title;
    private TextView tv_dialog_confirmg;
    private TextView tv_dialog_cancel;
    private int type=0;//O代表未勾选不再提醒，1代表已勾选不再提醒
    private String content;//对话框显示的内容

    private OnPermisionListener mOnPermisionListener;

    public void setOnPermisionListener(OnPermisionListener onPermisionListener) {
        mOnPermisionListener = onPermisionListener;
    }

    public PermissionDialog(Context context, int type, String content) {
        super(context);
        this.type=type;
        this.content=content;
        setCancelable(false);
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

        tv_dialog_title = findView(R.id.tv_dialog_title);
        tv_dialog_title.setGravity(Gravity.LEFT);
        tv_dialog_confirmg = findView(R.id.tv_dialog_confirmg);
        tv_dialog_cancel = findView(R.id.tv_dialog_cancel);
    }

    public void initDate() {
        tv_dialog_title.setText(content);
        if(type==0){
            tv_dialog_confirmg.setText(mContext.getString(R.string.button_confirm));
            tv_dialog_cancel.setText(mContext.getString(R.string.button_cancel));
        }else {
            tv_dialog_confirmg.setText(mContext.getString(R.string.button_goto_setting));
            tv_dialog_cancel.setText(mContext.getString(R.string.button_refuse));
        }
    }

    public void initListener() {
        tv_dialog_confirmg.setOnClickListener(this);
        tv_dialog_cancel.setOnClickListener(this);
    }

    @Override
    public void onDialogClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_confirmg:
                if(mOnPermisionListener !=null){
                    if(type==0)
                       mOnPermisionListener.onConfirm();
                    else
                       mOnPermisionListener.gotoSetting();
                    dismiss();
                }
                break;
            case R.id.tv_dialog_cancel:
                if(mOnPermisionListener !=null){
                    mOnPermisionListener.onCancel();
                    dismiss();
                }
                break;
        }
    }
}