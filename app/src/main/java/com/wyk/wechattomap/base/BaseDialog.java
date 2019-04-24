package com.wyk.wechattomap.base;

import android.app.Dialog;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

/**
 * Created by wyk on 2016/e/d.
 * 自定义Dialog的基类
 */
public abstract class BaseDialog extends Dialog implements View.OnClickListener{

    public Context mContext;

    public BaseDialog(Context context){
        super(context);
        this.mContext = context;
        initView();
    }

    public BaseDialog(Context context, int themeResId){
        super(context,themeResId);
        this.mContext = context;
        initView();
    }

    public abstract void initView();
    public abstract void onDialogClick(View v);

    /** 查找子控件，可省强转 */
    public <T> T findView(int id) {
        T view = (T) findViewById(id);
        return view;
    }

    public void showToast(String text) {
        Toast.makeText(mContext,text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        onDialogClick(v);
    }


}
