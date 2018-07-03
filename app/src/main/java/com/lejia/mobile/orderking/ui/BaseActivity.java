package com.lejia.mobile.orderking.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


import com.lejia.mobile.orderking.utils.MPermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/10/19.
 */
public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStack.getScreenManager().pushActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initViews();
    }

    public void setContentView(Activity act, int layoutResID) {
        setContentView(layoutResID);
//        StatusBarColorCompat.setContentViewWithStatusBarColorByColorPrimaryDark(act, layoutResID);
//        StatusBarColorCompat.setStatusBarColor(act, getResources().getColor(R.color.mainColor));
    }

    public void setContentView(Activity act, int layoutResID, int color) {
        setContentView(layoutResID);
//        StatusBarColorCompat.setContentViewWithStatusBarColorByColorPrimaryDark(act, layoutResID);
//        StatusBarColorCompat.setStatusBarColor(act, color);
    }

    @Override
    public final void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        setIntent(newIntent);
    }
    /**
     * edittext获取焦点并弹出键盘
     * @param editText
     */
    public static void getFocusAndOpenKeyBoard(final EditText editText){
        getFocus(editText);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                InputMethodManager inputManager = (InputMethodManager)editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }

        }, 100);
    }

    /**
     * edittext获取焦点
     * @param editText
     */
    public static void getFocus(EditText editText){
        editText.requestFocus();
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
    }

    protected abstract  void  initViews();

    /**
     * 提示信息
     *
     * @param layoutId
     */
    public void Toast(int layoutId) {
        Toast.makeText(this, getString(layoutId), Toast.LENGTH_SHORT).show();
    }


    /**
     * 提示信息
     *
     * @param values
     */
    public void Toast(String values) {
       Toast.makeText(this,values,Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示信息
     *
     * @param layoutId
     */
    public void Toast(int layoutId, int time) {
        Toast.makeText(this, getString(layoutId), time).show();
    }

    @Override
    protected void onDestroy() {
        ActivityStack.getScreenManager().popActivity(this);
        super.onDestroy();
    }

    public SharedPreferences getSharePreference(){

        return getSharedPreferences("information", Context.MODE_PRIVATE);
    }

    //map转换为json字符串
    public String hashMapToJson(HashMap map) {
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();

            string += "\"" + e.getKey() + "\":";
            string += "\"" + e.getValue() + "\",";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";


        return string;
    }

    public String hashMapToJsonString(HashMap map) {
        JSONObject json = new JSONObject();
        for (Iterator it = map.entrySet().iterator(); it.hasNext();) {
            Map.Entry e = (Map.Entry) it.next();
            try {
                json.put(e.getKey().toString(), e.getValue().toString());
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        return json.toString();
    }


    public String getAuthorCode(){
        return getSharePreference().getString("authorizationCode","");
    }

    public String getUserID(){
        return getSharePreference().getString("UID","");
    }

    public String getRulerUserID(){
        return getSharePreference().getString("measureUserID","");
    }

    public String getUserName(){
        return getSharePreference().getString("userName","");
    }

    public String getToken(){
        return getSharePreference().getString("TOKEN","");
    }

    public String getRulerToken(){
        return getSharePreference().getString("measureToken","");
    }

    public int getPositionID(){

       return getSharePreference().getInt("positionID",0);
    }


    public String getSignature(){
        return getSharePreference().getString("signature","");
    }

    public String getHeadImage(){
        return getSharePreference().getString("imageHead","");
    }

    public int getCompanyID(){

        return getSharePreference().getInt("companyID",0);
    }

    public String getPositionName(){
        return  getSharePreference().getString("positionName","");
    }
    public String getStoreName(){
        return getSharePreference().getString("storeName","");
    }

    public int getStoreID(){
        return getSharePreference().getInt("storeID",0);
    }

    public SharedPreferences.Editor getEditr(){
        return getSharePreference().edit();
    }

    public String getUserPhone(){
        return getSharePreference().getString("phone", "");
    }

    public void clearSharedPreference(){

        SharedPreferences.Editor editor = getEditr();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void cheakPermisssion(final Context context, String[] permissions) {

        MPermissionUtils.requestPermissionsResult(this, 1, permissions
                , new MPermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        if (onPMSelectListener != null) {
                            onPMSelectListener.onPMGranted();
                        }
                    }

                    @Override
                    public void onPermissionDenied() {
                        MPermissionUtils.showTipsDialog(context);
                    }
                });
    }

    public interface OnPMSelectListener {

        void onPMGranted();
    }

    private OnPMSelectListener onPMSelectListener;


    /**
     * 检查权限的总入口
     * @param context
     * @param permissions
     * @param onPMSelectListener
     */
    public void consumePM(Context context, String[] permissions, OnPMSelectListener onPMSelectListener) {

        this.onPMSelectListener = onPMSelectListener;
        cheakPermisssion(context,permissions);
    }

    public void closeBoard(Context mcontext) {
        InputMethodManager imm = (InputMethodManager) mcontext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(((Activity) mcontext).getWindow().getDecorView().getWindowToken(), 0);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }
}