package com.lejia.mobile.orderking.ui;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.hk3d.classes.AuxiliaryLine;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;
import com.lejia.mobile.orderking.utils.LogUtil;
import com.lejia.mobile.orderking.view.MenuBar;
import com.lejia.mobile.orderking.view.TitlesView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.getback)
    ImageButton getback;
    @BindView(R.id.forward)
    ImageButton forward;
    @BindView(R.id.jingzhun)
    ImageButton jingzhun;
    @BindView(R.id.title)
    TitlesView title;
    @BindView(R.id.zhouce)
    ImageButton zhouce;
    @BindView(R.id.threed)
    ImageButton threed;
    @BindView(R.id.more)
    ImageButton more;
    private final int ROOM = 0;
    private final int DOORWINDOW = 1;
    private final int TILES = 2;
    private final int DECORATE = 3;
    private MenuBar menuBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        test();
    }

    /**
     * 在获得焦点后自动全屏
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
//            setFullScreen();
        }
    }

    @Override
    protected void initViews() {
        title.setTitles(new String[]{"房间", "门窗", "铺砖", "布置"});
        title.setOnTitlesStatusListener(new TitlesView.OnTitlesStatusListener() {
            @Override
            public void onItemTitleClick(int position, String title, boolean isfromUser) {
                Toast(title);
                switch (position) {
                    case ROOM:   //房间
                        break;
                    case DOORWINDOW :   //门窗

                        break;
                    case TILES:   //铺砖
                        break;
                    case DECORATE:   //布置
                        break;

                }
            }
        });
    }


    @OnClick({R.id.getback, R.id.forward, R.id.jingzhun, R.id.zhouce, R.id.threed, R.id.more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.getback:
                break;
            case R.id.forward:
                break;
            case R.id.jingzhun:
                break;
            case R.id.zhouce:
                break;
            case R.id.threed:
                break;
            case R.id.more:
                String[] titles = new String[]{"新建", "打开", "保存", "另存", "量尺"};
                menuBar = new MenuBar(MainActivity.this, titles, (int) getResources().getDimension(R.dimen.menu_width)
                        , -1, MenuBar.HALF_ALPHA);
//                menuBar.setOnMenuBarChangedListener(onMenuBarChangedListener);
                menuBar.show();
                break;
        }

    }

    private void test() {
        ArrayList<Line> linesList = new ArrayList<>();
        Line line1 = new Line(new Point(100, 100), new Point(400, 100));
        line1.loadAuxiliaryArray();
        Line line2 = new Line(new Point(400, 100), new Point(400, 500));
        line2.loadAuxiliaryArray();
        Line line3 = new Line(new Point(400, 500), new Point(800, 200));
        line3.loadAuxiliaryArray();
        linesList.add(line1);
        linesList.add(line2);
        linesList.add(line3);

        // 求相交区域
        for (int i = 0; i < linesList.size(); i++) {
            Line now = linesList.get(i);
            Line next = null;
            if (i != linesList.size() - 1) {
                next = linesList.get(i + 1);
            }
            if (next != null) {
                ArrayList<AuxiliaryLine> nowAuxList = now.getAuxiliaryLineList();
                ArrayList<AuxiliaryLine> nextAuxList = next.getAuxiliaryLineList();
                AuxiliaryLine now0 = nowAuxList.get(0);
                AuxiliaryLine next0 = nextAuxList.get(0);
                Point intersectedPoint = now0.getAuxiliaryIntersectePoint(next0);
                System.out.println("####################### ");
                System.out.println("### now0 : " + now0);
                System.out.println("### next0 : " + next0);
                System.out.println("### intersectedPoint : " + intersectedPoint);
                System.out.println("####################### ");
            }
        }
    }


}
