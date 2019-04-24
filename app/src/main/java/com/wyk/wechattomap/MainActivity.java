package com.wyk.wechattomap;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.provider.Settings;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapsdkplatform.comapi.location.CoordinateType;
import com.wyk.wechattomap.base.BaseActivity;
import com.wyk.wechattomap.constant.Constant;
import com.wyk.wechattomap.entity.LocationGpsBean;
import com.wyk.wechattomap.listener.MyGetSuggResultListener;
import com.wyk.wechattomap.ui.JobAddressAdapter;
import com.wyk.wechattomap.ui.OpenGPSDialog;
import com.wyk.wechattomap.utils.GlobalUtils;
import com.wyk.wechattomap.utils.permission.PermissionHandler;

import java.util.ArrayList;
import java.util.List;
/**
 * @author wyk
 * 发送定位
 */
public class MainActivity extends BaseActivity {


    public float radius = 160;                          //统一范围
    public LocationClient mLocationClient = null;

    private MapView mBaiduMapView;
    private BaiduMap mbaiduMap;
    private ListView mLvAddressNews;
    private EditText mEtInputSearch;

    public  String mCity;
    private Button mBtnCancel;
    private Button mBtnSave;
    private TextView mTvTitle;
    private ImageView mIvMapCenter;
    private List<LocationGpsBean> jobAddress = new ArrayList<>();
    private LocationGpsBean selectGpsBean;
    private JobAddressAdapter mAdapter;
    private Dialog loadingDialog;

    private double mCurrLatitude;
    private double mCurrLongitude;

    private GeoCoder mSearch;//地理编码

    private boolean isFirstLocation = false;

    private SuggestionSearch mSuggestionSearch;
    private int mShowGpsDialogTime = 2000;

    ContentObserver mGpsMonitor = new ContentObserver(null) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            boolean providerEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
    };
    private LocationManager mLocationManager;
    private OpenGPSDialog mOpenGPSDialog;
    private Runnable mRunnable;
    private boolean locationTag;//用于标记是否为刚刚判断权限后的定位失败（针对小米手机做的优化）

    @Override
    public int getLayoutRes() {

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.System.LOCATION_PROVIDERS_ALLOWED),
                        false, mGpsMonitor);

        return R.layout.usercenter_edit_seletejobaddress;
    }
    @Override
    public void initView() {
        mBaiduMapView = findView(R.id.mpv_usercenter_edit_selete_job_address_baidu);
        mbaiduMap = mBaiduMapView.getMap();

        mEtInputSearch = findView(R.id.et_usercenter_edit_selete_job_address_search_input);
        mLvAddressNews = findView(R.id.lv_usercenter_edit_selete_job_address_news);

        mBtnCancel = findView(R.id.btn1_header1_cancel);
        mBtnSave = findView(R.id.btn1_header1_save);
        mTvTitle = findView(R.id.tv_header1_center_title);
        mIvMapCenter = findView(R.id.iv_map_center);

        loadingDialog = GlobalUtils.getLoadingDialog(this,
                getString(R.string.web_im_sending_address),false);
    }
    private LatLng mapCenterLatLng;
    @Override
    public void initListener() {
        mEtInputSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);//设置键盘为搜索
        mEtInputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == 0 || actionId == 3) && event != null){
                    search();
                }
                return false;
            }
        });
        mbaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                Log.i(Constant.TAG, "onMapStatusChangeStart,触发的类型 == " + i);
            }
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }
            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if(isFirstLocation){
                    mapCenterLatLng = mapStatus.target;
                    getAddressData(mapCenterLatLng);

                }
            }
        });
        mbaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                switch(motionEvent.getAction()){
                    case MotionEvent.ACTION_UP:
                        mapCenterLatLng = mbaiduMap.getMapStatus().target;
                        getAddressData(mapCenterLatLng);
                        break;
                }
            }
        });

        mbaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //开启定位
               /*
                if(mLocationClient != null &&  !mLocationClient.isStarted()){
                    mLocationClient.start();
                    LogUtil.logi("地图加载完成");
                }*/

            }
        });
    }
    @Override
    public void initData() {
        mBtnCancel.setVisibility(View.VISIBLE);
        mBtnSave.setVisibility(View.VISIBLE);
        mTvTitle.setVisibility(View.VISIBLE);

        mTvTitle.setText(getString(R.string.baidu_location_address));
        mBtnCancel.setText(getString(R.string.button_cancel));
        mBtnSave.setText(getString(R.string.baidu_location_send));

        ColorStateList colorStateList = GlobalUtils.setBtnColorState(this);
        mBtnSave.setTextColor(colorStateList);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //检查定位权限
        PermissionHandler permissionHandler= new PermissionHandler() {
            @Override
            public void onGranted() {
                //检测GPS设置
                if(!GlobalUtils.isOpenGps(MainActivity.this)){
                    mOpenGPSDialog = new OpenGPSDialog(MainActivity.this);
                    mOpenGPSDialog.setOnOpenGPSListener(new OpenGPSDialog.OnOpenGPSListener() {
                        @Override
                        public void cancelGPS() {
                        }
                        @Override
                        public void confirmOpenGPS() {
                            // 跳到手机设置界面，用户自动设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivityForResult(intent, Constant.RESULT_OPEN_GPS);
                        }
                    });
                    mRunnable = new Runnable() {
                        @Override
                        public void run() {
                            mOpenGPSDialog.show();
                        }
                    };
                    if(mBaiduMapView != null)
                        mBaiduMapView.postDelayed(mRunnable, mShowGpsDialogTime);
                }
                locationTag=true;//表示刚刚进行了权限检测
                initLocation();
                beginLocation();

                if(xiaomiIsDenied && mLocationClient !=null && !mLocationClient.isStarted()){
                    xiaomiIsDenied = false;
                    mLocationClient.start();
                }
            }

            @Override
            public boolean onNeverAsk() {
                return super.onNeverAsk();
            }

            @Override
            public boolean onDenied() {
                xiaomiIsDenied = true;
                return super.onDenied();
            }
        };
        permissionHandler.setPermissionName(getString(R.string.permission_access_coarse_location));
        requestPermission(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, permissionHandler);
    }

    private boolean xiaomiIsDenied = false;

    private void initLocation() {
        // 打开定位图层,显示当前位置
        mbaiduMap.setMyLocationEnabled(true);

        BitmapDescriptor mapIndicator = BitmapDescriptorFactory.fromResource(R.drawable.yh_usercenter_map_indicator);
        MyLocationConfiguration config = new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING,     //NORMAL普通态  //COMPASS  // FOLLOWING跟随态，保持定位图标在地图中心
                false, // 是否显示方向
                mapIndicator);// 使用默认图标
        mbaiduMap.setMyLocationConfiguration(config);

        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(15);
        mbaiduMap.setMapStatus(mapStatusUpdate);

        // 创建定位对象
        mLocationClient = new LocationClient(this);
        // 设置监听器接收搜索结果
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
            @Override	// 定位到数据回调此方法
            public void onReceiveLocation(BDLocation location) {
                isFirstLocation = true;
                if (location == null) {
                    return;
                }

                if(jobAddress != null){
                    jobAddress.clear();
                }
                /*详细的地址信息
                mCurrAddrStr = location.getAddrStr();*/
                mCity = location.getCity(); 			// 城市
                mCurrLatitude = location.getLatitude(); 	// 纬度
                mCurrLongitude = location.getLongitude(); // 经度

                initSearch();
                refreshBaiduMap(mCurrLatitude,mCurrLongitude,radius);

                mapCenterLatLng = mbaiduMap.getMapStatus().target;
                getAddressData(mapCenterLatLng);
            }
        });

        if(mLocationClient != null &&  !mLocationClient.isStarted()){
            mLocationClient.start();
        }

    }

    //初始化原始点(大头针),从屏幕坐标反地理是有偏差的
   /*  mIvMapCenter.getLocationOnScreen(pointArr);
    LatLng latLng = projection.fromScreenLocation(point);*/

    private void beginLocation(){
        LocationClientOption option = new LocationClientOption();
        //高精度
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于5000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setCoorType(CoordinateType.BD09LL);//"bd09ll"
        option.setOpenGps(true);
        mLocationClient.setLocOption(option);

        // 隐藏logo
        View child = mBaiduMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){
            child.setVisibility(View.INVISIBLE);
        }
        //地图上比例尺
        mBaiduMapView.showScaleControl(true);

    }

    private boolean mIsCurrSearchAction = false;
    private void search() {
        GlobalUtils.hideKeyboard(mEtInputSearch);
        String searchInput = mEtInputSearch.getText().toString().trim();
        if(TextUtils.isEmpty(searchInput)){
            showToast(getString(R.string.search_input_content_empty));
            return ;
        }
        //下面做搜寻的操作
        if(mPoiSearch == null){
            initSearch();
        }
        // 发起搜索
        if(searchInput.contains("市")){
            mIsCurrSearchAction = true;
            String[] splitInputContent = searchInput.split("市");
            int length = splitInputContent.length;
            String city= "";
            String keyword= "";
            if(length==1){          //不能周边搜索,市的名称作为key
                keyword = splitInputContent[0];
            }
            if(length>=2){
                city = splitInputContent[0];
                keyword = splitInputContent[1];
            }
            mPoiSearch.searchInCity((new PoiCitySearchOption())
                    .city(city)
                    .keyword(keyword));
            return;
        }
        mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                .city(mCity!=null? mCity:"")            //在线搜索 == 关键词搜索
                .keyword(searchInput));

    }
    /** 兴趣点搜索对象，满足条件的搜索到的对象 */
    private PoiSearch mPoiSearch;
    private void initSearch() {
        // 创建搜索对象
        mPoiSearch = PoiSearch.newInstance();
        // 设置监听器
        mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
            @Override	// 接收搜索结果
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    if(!GlobalUtils.isOpenGps(MainActivity.this)){
                        showToast(getString(R.string.usercenter_selete_address_gps_close));
                    }else{
                        if(locationTag){//如果为刚刚进行权限检测后的定位失败，应该为“定位”权限未打开
                            showNeverAskDialog();
                        }else {
                            showToast(getString(R.string.usercenter_search_no_result));
                        }
                    }
                    locationTag=false;//复位
                    return ;
                }
                setBaseAdater(poiResult.getAllPoi());

            }
            @Override  // 详情数据
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                if (poiDetailResult == null || poiDetailResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    showToast(getString(R.string.usercenter_search_no_result));
                    return ;
                }
                if(mAdapter!=null){
                    int count = mAdapter.getCount();
                    for(int i=0;i<count;i++){
                        String adapterUid = ((LocationGpsBean) mAdapter.getItem(i)).getuId();
                        if(TextUtils.equals(poiDetailResult.getUid(),adapterUid)){
                            setSelectP(i);
                            break;
                        }
                    }
                }
            }
            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }
        });
        mSuggestionSearch = SuggestionSearch.newInstance();
        MyGetSuggResultListener myGetSuggResultListener = new MyGetSuggResultListener(this);
        myGetSuggResultListener.setOnGetSuggestionResListener(new MyGetSuggResultListener.OnGetSuggestionResListener() {
            @Override
            public void getSuggestionRes(List<LocationGpsBean> res) {                           //被回调的接口

                setArrayAdater(res);
            }
        });
        mSuggestionSearch.setOnGetSuggestionResultListener(myGetSuggResultListener);

    }

    private void refreshBaiduMap(double currLatitude,double currLongitude,float accuracy){
        MyLocationData datas = new MyLocationData
                .Builder()
                .latitude(currLatitude)   // 纬度
                .longitude(currLongitude)  // 经度
                .accuracy(accuracy) // 定位的精度
                .build();
        // 更新我的位置, 刷新界面显示
        mbaiduMap.setMyLocationData(datas);
    }
    private void moveToCenter() {
        isFirstLocation = true;
        LatLng latLng = new LatLng(mCurrLatitude, mCurrLongitude);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
        mbaiduMap.animateMapStatus(mapStatusUpdate);
    }
    private void setSelectP(int position){
        selectGpsBean = jobAddress.get(position);
        mAdapter.selectPosition(position);
        mAdapter.notifyDataSetChanged();
        mLvAddressNews.smoothScrollToPosition(position);
        searchFirstForCenter(selectGpsBean.getPt());
    }

    //显示周边搜索、城市搜索的结果
    private void setBaseAdater(List<PoiInfo> allPoi){
        jobAddress.clear();
        if(allPoi!=null && allPoi.size()>0) {
            if(mIsCurrSearchAction){
                searchFirstForCenter(allPoi.get(0).location);
            }
            for (PoiInfo info : allPoi) {
                LocationGpsBean bean = new LocationGpsBean();
                bean.blackName = info.name;
                bean.address = (TextUtils.isEmpty(info.address.trim())) ? info.name : info.address ;
                bean.uId = info.uid;
                bean.pt = info.location;
                jobAddress.add(bean);
            }
        }else{
            showToast(getString(R.string.usercenter_search_no_result));
            return;
        }
        mAdapter = new JobAddressAdapter(this, jobAddress);
        mLvAddressNews.setAdapter(mAdapter);
        mLvAddressNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectP(position);
            }
        });
        mAdapter.selectPosition(0);//默认第1项
        selectGpsBean = jobAddress.get(0);
        mIsCurrSearchAction = false;
    }
    //显示在线搜索的结果
    private void setArrayAdater(List<LocationGpsBean> res){
        mAdapter = new JobAddressAdapter(this, res);
        if(res!=null && res.size()>0){
            mAdapter.selectPosition(0);
            selectGpsBean = res.get(0);
            jobAddress = res;
            searchFirstForCenter(selectGpsBean.getPt());
        }
        mLvAddressNews.setAdapter(mAdapter);
        mLvAddressNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setSelectP(position);

            }
        });
    }
    //地图定位到 "搜索" 出的第一个点
    private void searchFirstForCenter(LatLng latlng){
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latlng);
        mbaiduMap.animateMapStatus(mapStatusUpdate);
    }

    @Override
    public void onClick(View view, int id) {
        switch(id){
            case R.id.iv_usercenter_edit_selete_job_address_search_icon:
                search();
                break;
            case R.id.btn1_header1_cancel:
                GlobalUtils.hideKeyboard(mEtInputSearch);
                finish();
                break;
            case R.id.btn1_header1_save:                //保存截图
                    if(selectGpsBean == null){
                        showToast(getString(R.string.web_im_get_address_failure));
                        break;
                    }

                    LatLng latlng = selectGpsBean.getPt();
                    loadingDialog.show();
                    BitmapDescriptor mapCenterPoint = BitmapDescriptorFactory.fromResource(R.drawable.location_origin_point);
                    OverlayOptions option = new MarkerOptions()
                            .position(new LatLng(latlng.latitude,latlng.longitude))
                            .icon(mapCenterPoint);
                    mbaiduMap.addOverlay(option);

                    mbaiduMap.setMyLocationEnabled(false);
                    mIvMapCenter.setVisibility(View.GONE);

                    //由于图片添加marker需要一定时间，才可以显示，故这里延时500ms
                    //如果不延时，截图上没有marker
                    mBaiduMapView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mbaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                                @Override
                                public void onSnapshotReady(Bitmap bitmap) {
                                    //上传截图
                                    //snapshot(bitmap);
                                    showToast(getString(R.string.alert_upload_img));
                                }
                            });
                        }
                    },500);

                break;
            case R.id.btn_edit_address_center:
                moveToCenter();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        mBaiduMapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        mBaiduMapView.onPause();
        super.onPause();
    }
    @Override
    public void onDestroy() {
        if(mLocationClient != null){
            mLocationClient.stop();     //关闭定位
        }
        mBaiduMapView.onDestroy();
        mBaiduMapView = null;

        if(mPoiSearch != null){
            mPoiSearch.destroy();
            mPoiSearch = null;
        }
        if(mSuggestionSearch != null){
            mSuggestionSearch.destroy();
        }
        if(loadingDialog != null) {
            loadingDialog.cancel();
            loadingDialog = null;
        }
        if(mOpenGPSDialog != null){
            mOpenGPSDialog.cancel();
            mOpenGPSDialog = null;
        }
        if(mSearch !=null){
            mSearch.destroy();
        }
        getContentResolver().unregisterContentObserver(mGpsMonitor);
        super.onDestroy();
    }


    @Override
    public void findButtonToListener(View view, View.OnClickListener listener) {            //note
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            int size = parent.getChildCount();
            for (int i = 0; i < size ; i ++) {
                View child = parent.getChildAt(i);
                if (child instanceof Button) {
                    // 设置Button点击事件
                    child.setOnClickListener(listener);
                } else if (child instanceof ViewGroup) {
                    findButtonToListener(child, listener);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constant.RESULT_OPEN_GPS){
            if(!GlobalUtils.isOpenGps(this)) {
               showToast(getString(R.string.result_gps_confirm_close));
                return;
            }
            if(mLocationClient != null && !mLocationClient.isStarted()){
                mLocationClient.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //获取周边的数据(周围的建筑物)
    private void getAddressData(LatLng latlng){
        coordinateToAddress(latlng);
    }
    //逆地理编码（即坐标转地址）
    private void coordinateToAddress(LatLng ptCenter){
        mSearch = GeoCoder.newInstance();
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            //获取地理编码结果
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有检索到结果

                };
            }
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                //获取反向地理编码结果
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    //没有找到检索结果
                }

                List<PoiInfo> poiList = result.getPoiList();
                PoiInfo poiInfo;
                if(isFirstLocation) {
                    isFirstLocation = false;
                    poiInfo = new PoiInfo();
                    poiInfo.address = result.getAddress();
                    poiInfo.location = result.getLocation();
                    poiList.add(0,poiInfo);
                }
                setBaseAdater(poiList);
            }
        };
        mSearch.setOnGetGeoCodeResultListener(listener);
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(ptCenter));

    }

}
