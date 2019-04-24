package com.wyk.wechattomap.base;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.wyk.wechattomap.R;
import com.wyk.wechattomap.ui.PermissionDialog;
import com.wyk.wechattomap.utils.permission.OnPermisionListener;
import com.wyk.wechattomap.utils.permission.PermissionHandler;
import com.wyk.wechattomap.utils.permission.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyk
 * Activity的基类
 */
public abstract class BaseActivity extends FragmentActivity implements IUIOperation {

		public FragmentManager fragmentManager;				//Fragment管理器
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//if(!BuildConfig.DEBUG){
			//	getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
			//}
			// 去掉界面标题栏
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(getLayoutRes());
			fragmentManager = getSupportFragmentManager();


			View view =  findViewById(android.R.id.content);					// android.R.id.content系统的一个根布局
			// 查找一个布局中所有的按钮(Button或ImageButton)并设置点击事件
			findButtonToListener(view, this);

			initView();
			initListener();
			initData();

		}

		/** 查找子控件，可省强转 */
		public <T> T findView(int id) {
			T view = (T) findViewById(id);
			return view;
		}
		

		
		@Override
		public void onClick(View v) {
				onClick(v, v.getId());
		}


	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	//以备子类需要
	public void findButtonToListener(View view, View.OnClickListener listener) {
		if (view instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) view;
			int size = parent.getChildCount();
			for (int i = 0; i < size ; i ++) {
				View child = parent.getChildAt(i);
				if (child instanceof Button || child instanceof ImageButton || child instanceof ImageView) {
					// 设置按钮点击事件
					child.setOnClickListener(listener);
				} else if (child instanceof ViewGroup) {
					findButtonToListener(child, listener);
				}
			}
		}
	}

	private PermissionHandler mHandler;

	/**
	 * 请求权限
	 *
	 * @param permissions 权限列表
	 * @param handler     回调
	 */
	public void requestPermission(String[] permissions, PermissionHandler handler) {
		List<String> unGetPermissionList=new ArrayList<>();//统计未获得的权限
		for(String p:permissions){
			if(!PermissionUtils.hasSelfPermissions(this,p))
				unGetPermissionList.add(p);
		}
		if(unGetPermissionList.size()==0){
			handler.onGranted();
		}else {
			mHandler = handler;
			ActivityCompat.requestPermissions(this, unGetPermissionList.toArray(new String[unGetPermissionList.size()]), 1);
		}
	}


	/**
	 * 权限请求结果
	 *
	 * @param requestCode
	 * @param permissions
	 * @param grantResults
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull final String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (mHandler == null) return;

		if (PermissionUtils.verifyPermissions(grantResults)) {
			mHandler.onGranted();
		} else {
			if (!PermissionUtils.shouldShowRequestPermissionRationale(this, permissions)) {
				if (!mHandler.onNeverAsk()) {
					showNeverAskDialog();//展示权限选择“不再询问并拒绝”的对话框
				}
			} else {
				if(!mHandler.onDenied()){
					showDeniedDialog(permissions);//展示权限选择“拒绝”的对话框
				}
			}
		}
	}

	//展示权限选择“不再询问并拒绝”的对话框
	public void showNeverAskDialog(){
		if(mHandler!=null){
			String appName= getString(R.string.app);
			PermissionDialog permissionDialog=new PermissionDialog(this,1,"由于"+appName+"无法获取“"+mHandler.getPermissionName()+"”权限，不能正常工作，请开启权限后再使用。\n"
					+ "设置路径：设置->应用->"+appName+"->权限");
			permissionDialog.setOnPermisionListener(new OnPermisionListener() {
				@Override
				public void gotoSetting() {
					/**
					 * 以下方法为调用系统设置中关于本APP的应用详情页
					 */
					Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
					Uri uri = Uri.fromParts("package", getPackageName(), null);
					intent.setData(uri);
					startActivity(intent);
				}

				@Override
				public void onConfirm() {

				}

				@Override
				public void onCancel() {
					if(mHandler.isForce())
						finish();
				}
			});
			permissionDialog.show();
		}
	}

	//展示权限选择“拒绝”的对话框
	private void showDeniedDialog(final String[] permissions){
		if(mHandler!=null){
			String appName= getString(R.string.app_name);
			PermissionDialog permissionDialog=new PermissionDialog(this,0,"请允许获取“"+mHandler.getPermissionName()+"”权限，否则您将无法正常使用"+appName);
			permissionDialog.setOnPermisionListener(new OnPermisionListener() {
				@Override
				public void gotoSetting() {

				}

				@Override
				public void onConfirm() {
					requestPermission(permissions,mHandler);
				}

				@Override
				public void onCancel() {
					if(mHandler.isForce())
						finish();
				}
			});
			permissionDialog.show();
		}
	}

	public void showToast(String text) {
		Toast.makeText(this,text, Toast.LENGTH_LONG).show();
	}

}

