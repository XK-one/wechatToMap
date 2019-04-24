package com.wyk.wechattomap.listener;

import android.app.Activity;
import android.widget.Toast;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.wyk.wechattomap.R;
import com.wyk.wechattomap.entity.LocationGpsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wyk
 *
 */
public class MyGetSuggResultListener implements OnGetSuggestionResultListener {

    public interface OnGetSuggestionResListener {
        void getSuggestionRes(List<LocationGpsBean> res);

    }
    private OnGetSuggestionResListener mOnGetSuggestionResListener;
    private Activity mActivity;

    public MyGetSuggResultListener(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            Toast.makeText(mActivity,mActivity.getString(R.string.usercenter_search_no_result),Toast.LENGTH_SHORT).show();
            return;
        }
        List<LocationGpsBean> suggest = new ArrayList<>();
        for(SuggestionResult.SuggestionInfo info : res.getAllSuggestions()){
            if(info.key != null && info.pt != null){
                LocationGpsBean bean = new LocationGpsBean();
                bean.blackName = info.key;
                bean.address = info.city + info.district;
                bean.pt = info.pt;
                bean.uId = info.uid;
                suggest.add(bean);
            }
        }
        if(suggest.size()>0 && mOnGetSuggestionResListener != null){
            Toast.makeText(mActivity,mActivity.getString(R.string.finish_search_address),Toast.LENGTH_SHORT).show();
           mOnGetSuggestionResListener.getSuggestionRes(suggest);
        }
    }

    public void setOnGetSuggestionResListener(OnGetSuggestionResListener onGetSuggestionResListener){
        mOnGetSuggestionResListener = onGetSuggestionResListener;
    }
}
