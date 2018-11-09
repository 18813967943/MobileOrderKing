package com.lejia.mobile.orderking.hk3d.datas_3d.tools;

/**
 * Author by HEKE
 *
 * @time 2018/11/7 11:00
 * TODO: 楼层对象
 */
public class Cell {

    /**
     * 所属楼层
     */
    public int cellIndex;

    /**
     * 楼层层高
     */
    public int cellHeight;

    /**
     * 楼层起始高度
     */
    public int cellBegainHeight;

    public Cell(int cellIndex, int cellHeight) {
        this.cellIndex = cellIndex;
        this.cellHeight = cellHeight;
    }

    /**
     * 计算楼层起始高度
     */
    public void calculateCellBegainHeight() {
        cellBegainHeight = CellsRecord.getAtCellsBegainHeight(cellIndex);
    }

}
