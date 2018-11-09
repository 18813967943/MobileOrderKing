package com.lejia.mobile.orderking.hk3d.datas_3d.tools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author by HEKE
 *
 * @time 2018/11/7 10:56
 * TODO: 楼层信息记录对象
 */
public class CellsRecord {

    /**
     * 总楼层数，默认为1层
     */
    public static int totalCells = 1;

    /**
     * 当前编辑楼层
     */
    public static int current_edit_cell = 1;

    /**
     * 楼层每层信息
     */
    public static HashMap<Integer, Cell> cellsHashMap = new HashMap<>();

    static {
        Cell firstCell = new Cell(1, 280);
        cellsHashMap.put(1, firstCell);
        firstCell.calculateCellBegainHeight();
    }

    /**
     * 设置楼层信息
     *
     * @param key
     * @param cell
     * @param forceReplace
     */
    public static void put(int key, Cell cell, boolean forceReplace) {
        if (cell == null)
            return;
        if (cellsHashMap.containsKey(key) && !forceReplace)
            return;
        cellsHashMap.put(key, cell);
        cell.calculateCellBegainHeight();
    }

    /**
     * 获取指定楼层数据对象
     *
     * @param key
     */
    public static Cell get(int key) {
        if (cellsHashMap.size() == 0 || !cellsHashMap.containsKey(key))
            return null;
        return cellsHashMap.get(key);
    }

    /**
     * 获取指定楼层的起始高度
     *
     * @param atCell
     * @return
     */
    public static int getAtCellsBegainHeight(int atCell) {
        if (cellsHashMap.size() == 0)
            return 0;
        int totalCellsHeight = 0;
        Iterator<Map.Entry<Integer, Cell>> iterator = cellsHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Cell> entry = iterator.next();
            int key = entry.getKey();
            if (key < atCell) {
                Cell cell = entry.getValue();
                totalCellsHeight += cell.cellHeight;
            }
        }
        return totalCellsHeight;
    }

    /**
     * 获取当前所有楼层的总高度
     */
    public static int getTotalCellsHeight() {
        if (cellsHashMap.size() == 0)
            return 0;
        int totalCellsHeight = 0;
        Iterator<Map.Entry<Integer, Cell>> iterator = cellsHashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Cell> entry = iterator.next();
            Cell cell = entry.getValue();
            totalCellsHeight += cell.cellHeight;
        }
        return totalCellsHeight;
    }

}
