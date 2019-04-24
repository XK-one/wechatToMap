package com.wyk.wechattomap.base;

import android.view.View;
import android.view.View.OnClickListener;

/**
* 界面操作方法封装类
*/
public interface IUIOperation extends OnClickListener {

		/** 获取activity或者Fragment的布局文件 */
		int getLayoutRes();
		
		/** 查找子控件 */
		void initView();
		
		/** 初始化控件的监听器 */
		void initListener();
		
		/** 初始化数据 */
		void initData();
		
		/**
		 * 按钮的点击事件
		 * @param view	按钮
		 * @param id	按钮id
		 */
		void onClick(View view, int id);
}


