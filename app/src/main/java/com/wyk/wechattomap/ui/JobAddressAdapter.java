package com.wyk.wechattomap.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wyk.wechattomap.R;
import com.wyk.wechattomap.entity.LocationGpsBean;

import java.util.List;

/**
 * @author wyk
 * 展示定位信息的适配器
 */
public class JobAddressAdapter extends BaseAdapter {


    private Context mContext;
    private List<LocationGpsBean> searchResult;
    private int selectItem;

    public JobAddressAdapter(Context mContext, List<LocationGpsBean> searchResult) {
        this.mContext = mContext;
        this.searchResult = searchResult;
    }

    @Override
    public int getCount() {
        return searchResult==null ? 0:searchResult.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResult.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LocationGpsHolder holder = null;
        if(convertView==null){
           convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .item_usercenter_edit_add_address_name_item, parent, false);
            holder = new LocationGpsHolder();
            holder.blockName = (TextView) convertView.findViewById(R.id.tv_usercenter_edit_add_address_blockname);
            holder.address = (TextView) convertView.findViewById(R.id.tv_usercenter_edit_add_address_address);

            convertView.setTag(holder);

        }else{
            holder = (LocationGpsHolder) convertView.getTag();
        }

        if(searchResult.get(position).blackName == null){
            holder.blockName.setVisibility(View.GONE);
        }else{
            holder.blockName.setVisibility(View.VISIBLE);
        }

        if(selectItem==position){
            convertView.setBackgroundResource(R.color.item_line_cover);
        }else{
            convertView.setBackgroundResource(R.color.color_white);
        }
        holder.blockName.setText(searchResult.get(position).blackName);
        holder.address.setText(searchResult.get(position).address);

        return convertView;
    }

    public void selectPosition(int selectItem){
        this.selectItem = selectItem;
    }

    public void setAllData(List<LocationGpsBean> searchResult){
        this.searchResult = searchResult;
    }

}
