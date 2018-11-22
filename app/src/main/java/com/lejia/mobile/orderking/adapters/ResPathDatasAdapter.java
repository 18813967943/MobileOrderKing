package com.lejia.mobile.orderking.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.classes.NormalDatas;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.utils.FetchLocal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2018/11/17 11:19
 * TODO: 服务器资源数据列表对应的加载细化适配器
 */
public class ResPathDatasAdapter extends BaseAdapter {

    private Context mContext;
    private String cachePath;
    private ArrayList<ResUrlNodeXml.ResPath> resPathArrayList;

    public ResPathDatasAdapter(Context context, String cachePath, ArrayList<ResUrlNodeXml.ResPath> resPathArrayList) {
        this.mContext = context;
        this.cachePath = cachePath;
        this.resPathArrayList = new ArrayList<>();
        this.resPathArrayList.addAll(resPathArrayList);
    }

    /**
     * 此适配器只可持续增加数据
     *
     * @param resPathArrayList
     */
    public void add(ArrayList<ResUrlNodeXml.ResPath> resPathArrayList) {
        if (resPathArrayList == null || resPathArrayList.size() == 0)
            return;
        this.resPathArrayList.addAll(resPathArrayList);
        notifyDataSetChanged();
    }

    /**
     * 获取当前总数据列表
     */
    public ArrayList<ResUrlNodeXml.ResPath> getResPathArrayList() {
        return resPathArrayList;
    }

    /**
     * 清空数据
     */
    public void clear() {
        this.resPathArrayList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return resPathArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return (position >= 0 && position < resPathArrayList.size()) ? resPathArrayList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder = null;
        try {
            if (v == null) {
                v = View.inflate(mContext, R.layout.res_item, null);
                holder = new ViewHolder();
                holder.icon = v.findViewById(R.id.icon);
                holder.code = v.findViewById(R.id.code);
                holder.position = position;
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }
            final ViewHolder mHolder = holder;
            final ResUrlNodeXml.ResPath resPath = resPathArrayList.get(position);
            mHolder.resPath = resPath;
            // 请求数据
            final String filePath = cachePath + "BI-" + resPath.fileName;
            final int finalPosition = position;
            File file = new File(filePath);
            if (file.exists()) {
                new FetchLocal(filePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                    @Override
                    public void pullCompleted(String contents) {
                        loadDatas(mHolder, contents, filePath, finalPosition);
                    }
                });
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("url", resPath.path);
                params.put("type", "2");
                KosapRequest request = new KosapRequest(mContext, "DownloadMaterialDetailBuffer", params, new OnKosapResponseListener() {
                    @Override
                    public void response(String result, boolean error) {
                        new FetchLocal(filePath).push(result);
                        loadDatas(mHolder, result, filePath, finalPosition);
                    }

                    @Override
                    public void useLocal() {
                    }
                });
                request.request();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return v;
    }

    /**
     * 加载数据对象
     *
     * @param holder
     * @param datas
     */
    private void loadDatas(final ViewHolder holder, String datas, String cachePath, int position) {
        try {
            // 释放之前资源数据
            if (holder.normalDatas != null) {
                holder.normalDatas.release();
                holder.normalDatas = null;
            }
            // 重新加载刷新数据显示
            NormalDatas normalDatas = new NormalDatas();
            holder.normalDatas = normalDatas;
            holder.normalDatas.cachePath = cachePath;
            normalDatas.load(datas, position, new NormalDatas.OnNormalDatasListener() {
                @Override
                public void onCompeleted(NormalDatas nd, Bitmap preview, int position) {
                    if (preview != null && !preview.isRecycled()) {
                        holder.icon.setImageBitmap(preview);
                        holder.code.setText(nd.name);
                    }
                }
            });
            holder.resPath.normalDatas = normalDatas;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/17 11:51
     * TODO: 维持数据对象
     */
    public class ViewHolder {
        public ImageView icon;
        public TextView code;
        public NormalDatas normalDatas;
        public ResUrlNodeXml.ResPath resPath;
        public int position;
    }

}
