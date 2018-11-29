package com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.widget.ImageView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.utils.FetchLocal;
import com.lejia.mobile.orderking.utils.FileUtils;

import java.io.File;
import java.util.HashMap;

/**
 * Author by HEKE
 *
 * @time 2017/11/6 16:15
 * TODO: 我的保存方案数据加载异步线程
 */
public class MineSchemeLoadBuffer {

    private static final String SCHEME_CACHE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Libs/DoorModel/";

    private Context mContext;
    private ImageView preview;
    private ResUrlNodeXml.ResPath scheme;

    public MineSchemeLoadBuffer(Context context, ImageView preview, ResUrlNodeXml.ResPath scheme) {
        this.mContext = context;
        this.preview = preview;
        this.scheme = scheme;
        FileUtils.createDirectory(SCHEME_CACHE);
    }

    public void fetch() {
        if (preview == null || scheme == null)
            return;
        // 检测本地是否有此方案的缓存
        String path = scheme.path;
        String filename = scheme.name + ".sjpg";
        final String filePath = SCHEME_CACHE + filename;
        File file = new File(filePath);
        // 本地无缓存，从网络获取
        if (!file.exists()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("filename", path);
            KosapRequest fetch = new KosapRequest(mContext, "DownloadUserFilePreviewBufferString64", params, new OnKosapResponseListener() {
                @Override
                public void response(String result, boolean error) {
                    if (!error) {
                        // 设置显示
                        preview.setImageBitmap(base64ToBitmap(result));
                        // 缓存至本地
                        new FetchLocal(filePath).push(result);
                    } else {
                        preview.setImageResource(R.mipmap.unkonw_pic);
                    }
                }

                @Override
                public void useLocal() {
                    local(filePath);
                }
            });
            fetch.request();
        }
        // 本地具有缓存
        else {
            local(filePath);
        }
    }

    /**
     * 拉取本地数据
     *
     * @param filePath
     */
    private void local(String filePath) {
        new FetchLocal(filePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
            @Override
            public void pullCompleted(String contents) {
                if (contents != null) {
                    // 设置显示
                    preview.setImageBitmap(base64ToBitmap(contents));
                } else {
                    preview.setImageResource(R.mipmap.unkonw_pic);
                }
            }
        });
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
