package com.lejia.mobile.orderking.hk3d.classes;

import com.lejia.mobile.orderking.hk3d.datas.House;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2018/8/11 14:47
 * TODO: 未闭合房间与其他房间相交情况数据对象
 */
public class UncloseCheckResult {

    public House interHouse; // 相交房间
    public Point interPoint; // 相交点
    public int sideIndex = -1; // 相交端点编号，默认为非端点相交,0为起点，1为终点
    public boolean isSidePoint; // 是否端点
    public boolean isInterHouseSidePoint; // 是否是相交房间的端点
    public boolean isInterHouseSidePointBegain; // 是否是相交房间的起始第一个点

    public UncloseCheckResult() {
        super();
    }

    public UncloseCheckResult(House interHouse, Point interPoint, int sideIndex, boolean isSidePoint) {
        this.interHouse = interHouse;
        this.interPoint = interPoint;
        this.sideIndex = sideIndex;
        this.isSidePoint = isSidePoint;
        checkInterWithHouseSidePointDirectionR();
    }

    /**
     * 检测相交点与相交房间的端点顺序关系
     */
    private void checkInterWithHouseSidePointDirectionR() {
        if (interPoint == null || interHouse == null)
            return;
        PointList interCenterList = interHouse.centerPointList;
        if (interCenterList.size() > 0) {
            isInterHouseSidePointBegain = interPoint.equals(interCenterList.getIndexAt(0));
            isInterHouseSidePoint = isInterHouseSidePointBegain || interPoint.equals(interCenterList.getIndexAt(interCenterList.size() - 1));
        }
    }


    /**
     * 检测相交点是否也在另一个区域内
     *
     * @param uncloseCheckResult
     * @return
     */
    public boolean isUncloseCheckInterAlsoOn(UncloseCheckResult uncloseCheckResult) {
        if (interPoint == null || uncloseCheckResult == null)
            return false;
        PointList ceterList = uncloseCheckResult.interHouse.centerPointList;
        ArrayList<Line> linesList = null;
        if (uncloseCheckResult.interHouse.isWallClosed) {
            linesList = ceterList.toLineList();
        } else {
            linesList = ceterList.toNotClosedLineList();
        }
        for (Line line : linesList) {
            if (line.getAdsorbPoint(interPoint.x, interPoint.y, 1.0d) != null) {
                return true;
            }
        }
        return false;
    }

}
