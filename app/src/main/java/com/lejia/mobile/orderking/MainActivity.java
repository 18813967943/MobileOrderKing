package com.lejia.mobile.orderking;

import android.app.Activity;
import android.os.Bundle;

import com.lejia.mobile.orderking.hk3d.classes.AuxiliaryLine;
import com.lejia.mobile.orderking.hk3d.classes.Line;
import com.lejia.mobile.orderking.hk3d.classes.Point;

import java.util.ArrayList;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test();
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
