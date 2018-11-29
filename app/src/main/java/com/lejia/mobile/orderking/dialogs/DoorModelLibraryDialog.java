package com.lejia.mobile.orderking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.adapters.MineSchemeAdapter;
import com.lejia.mobile.orderking.bases.OrderKingApplication;
import com.lejia.mobile.orderking.classes.ResUrlNodeXml;
import com.lejia.mobile.orderking.hk3d.datas_2d.ServiceButtJoint.bridge.SchemeXmlParseToShowViews;
import com.lejia.mobile.orderking.https.KosapRequest;
import com.lejia.mobile.orderking.https.OnKosapResponseListener;
import com.lejia.mobile.orderking.httpsResult.classes.User;
import com.lejia.mobile.orderking.utils.FetchLocal;
import com.lejia.mobile.orderking.utils.FileUtils;
import com.lejia.mobile.orderking.utils.PagesTool;
import com.lejia.mobile.orderking.utils.TextUtils;
import com.lejia.mobile.orderking.widgets.ScrollerGridView;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Author by HEKE
 *
 * @time 2018/11/26 11:46
 * TODO: 用户保存库，包括保存方案、基础户型、样板间。
 */
public class DoorModelLibraryDialog extends Dialog {

    // 用户编辑的户型方案
    public static final int FLAG_USER_SAVE = 0;
    // 基础户型库
    public static final int FLAG_BASE_HOUSE = 1;
    // 样板间
    public static final int FLAG_PROTOTYPE = 2;

    private int flag = -1;
    private User mUser;

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.mineSchemes)
    TextView mineSchemes;
    @BindView(R.id.baseHouseLibrary)
    TextView baseHouseLibrary;
    @BindView(R.id.prototypeRooms)
    TextView prototypeRooms;
    @BindView(R.id.schemesGrid)
    ScrollerGridView schemesGrid;

    public DoorModelLibraryDialog(@NonNull Context context) {
        super(context, R.style.transparentDiag);
    }

    private void init() {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = getContext().getResources().getDisplayMetrics().widthPixels;
        layoutParams.height = getContext().getResources().getDisplayMetrics().heightPixels;
        layoutParams.alpha = 1.0f;
        window.setAttributes(layoutParams);
        mUser = ((OrderKingApplication) getContext().getApplicationContext()).mUser;
        // 加载默认显示数据
        setFlag(FLAG_USER_SAVE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_door_model_library);
        ButterKnife.bind(this);
        init();
    }

    @OnClick({R.id.back, R.id.mineSchemes, R.id.baseHouseLibrary, R.id.prototypeRooms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                dismiss();
                break;
            case R.id.mineSchemes:
                setFlag(FLAG_USER_SAVE);
                break;
            case R.id.baseHouseLibrary:
                setFlag(FLAG_BASE_HOUSE);
                break;
            case R.id.prototypeRooms:
                setFlag(FLAG_PROTOTYPE);
                break;
        }
    }

    /**
     * 设置加载显示类型
     *
     * @param flag
     */
    public void setFlag(int flag) {
        if (this.flag == flag)
            return;
        this.flag = flag;
        // 我的
        switch (flag) {
            case FLAG_USER_SAVE:
                loadMine();
                break;
        }
    }

    /************************************
     *  我的编辑保存方案
     * **********************************/

    private String mineSchemesCachPath; // 存储文件目录

    private ArrayList<ResUrlNodeXml.ResPath> mineResPathsList; // 自身设计保存的方案导航数据列表

    private PagesTool<ResUrlNodeXml.ResPath> pathPagesTool; // 分页工具

    private MineSchemeAdapter mineSchemeAdapter; // 数据适配器

    // 加载我的数据
    private void loadMine() {
        mineSchemesCachPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Yi3D/Libs/DoorModel/";
        final String cacheFilePath = mineSchemesCachPath + "mine.xml";
        FileUtils.createFile(cacheFilePath);
        HashMap<String, String> params = new HashMap<>();
        params.put("code", "" + mUser.getToken());
        KosapRequest request = new KosapRequest(getContext(), "GetUserFileNamesXML", params, new OnKosapResponseListener() {
            @Override
            public void response(String result, boolean error) {
                if (!TextUtils.isTextEmpty(result)) {
                    new FetchLocal(cacheFilePath).push(result);
                    // 解析导航数据
                    mineResPathsList = createResPaths(result);
                    showMineSchemes();
                }
            }

            @Override
            public void useLocal() {
                new FetchLocal(cacheFilePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                    @Override
                    public void pullCompleted(String contents) {
                        // 解析导航数据
                        mineResPathsList = createResPaths(contents);
                        showMineSchemes();
                    }
                });
            }
        });
        request.request();
    }

    // 解析xml数据列表
    private ArrayList<ResUrlNodeXml.ResPath> createResPaths(String xml) {
        ArrayList<ResUrlNodeXml.ResPath> resPathArrayList = new ArrayList<>();
        try {
            SAXReader saxReader = new SAXReader();
            ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
            Document document = saxReader.read(bais);
            Element element = document.getRootElement();
            List<Element> elementList = element.elements();
            if (elementList != null) {
                for (Element e : elementList) {
                    String nodeName = e.getName().toLowerCase();
                    if (nodeName.equals("file")) {
                        ResUrlNodeXml.ResPath resPath = new ResUrlNodeXml.ResPath(e.attributeValue("path"));
                        resPathArrayList.add(resPath);
                    }
                }
            }
            bais.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resPathArrayList;
    }

    /**
     * 加载显示我的数据列表
     */
    private void showMineSchemes() {
        pathPagesTool = new PagesTool<>(mineResPathsList, 12);
        pathPagesTool.setOnPagesToolChangeListener(new PagesTool.OnPagesToolChangeListener<ResUrlNodeXml.ResPath>() {
            @Override
            public void previous(ArrayList<ResUrlNodeXml.ResPath> list, PagesTool.Desc desc) {
            }

            @Override
            public void getPage(ArrayList<ResUrlNodeXml.ResPath> list, PagesTool.Desc desc) {
                if (mineSchemeAdapter == null) {
                    desc.used = true;
                    ArrayList<ResUrlNodeXml.ResPath> resPathArrayList = new ArrayList<>();
                    resPathArrayList.addAll(list);
                    mineSchemeAdapter = new MineSchemeAdapter(getContext(), resPathArrayList);
                }
            }

            @Override
            public void next(ArrayList<ResUrlNodeXml.ResPath> list, PagesTool.Desc desc) {
                if (!desc.used) {
                    mineSchemeAdapter.add(list);
                    desc.used = true;
                }
            }
        });
        // 加载第一页数据
        pathPagesTool.getPageList(0);
        schemesGrid.setSelector(R.drawable.grid_selector);
        schemesGrid.setAdapter(mineSchemeAdapter);
        schemesGrid.setOnItemClickListener(onItemClickListener);
    }

    /**
     * 加载点击的保存方案详细数据
     *
     * @param position
     */
    private void fetchSchemesRealXmlsData(int position) {
        if (mineSchemeAdapter == null)
            return;
        ResUrlNodeXml.ResPath resPath = (ResUrlNodeXml.ResPath) mineSchemeAdapter.getItem(position);
        final String xmlCachePath = mineSchemesCachPath + resPath.name + ".xml";
        File localFile = new File(xmlCachePath);
        // 从本地读取
        if (localFile.exists()) {
            new FetchLocal(xmlCachePath).pullAsync(new FetchLocal.OnAsyncPullListener() {
                @Override
                public void pullCompleted(String xml) {
                    xmlThenCreate(xml);
                }
            });

        } else {
            HashMap<String, String> params = new HashMap<>();
            params.put("filename", resPath.path);
            KosapRequest fetchXml = new KosapRequest(getContext(), "DownloadUserFileContentsXML", params, new OnKosapResponseListener() {
                @Override
                public void response(String result, boolean error) {
                    if (!error) {
                        new FetchLocal(xmlCachePath).push(result);
                        xmlThenCreate(result);
                    }
                }

                @Override
                public void useLocal() {
                }
            });
            fetchXml.request();
        }
    }

    /**
     * 解析指定方案数据，返回加载显示方案
     *
     * @param xml
     */
    private void xmlThenCreate(String xml) {
        new SchemeXmlParseToShowViews(getContext(), xml);
        dismiss();
    }

    /**
     * 我的保存方案单击事件
     */
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 我的
            if (flag == FLAG_USER_SAVE) {
                fetchSchemesRealXmlsData(position);
            }
        }
    };

}
