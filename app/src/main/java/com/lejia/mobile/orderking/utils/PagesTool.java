package com.lejia.mobile.orderking.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author by HEKE
 *
 * @time 2018/11/26 16:43
 * TODO: 分页工具，可使用内部数据分页处理，亦可以自己拿取分页数据自行处理
 */
public class PagesTool<T> {

    /**
     * 分页总数据列表
     */
    private ArrayList<T> list;

    private int pageCount; // 每页分页数量，输入数值无效时，默认为10个长度每页

    private int pageSize; // 页面总数

    private int currentPage; // 当前显示页

    private ArrayList<Desc> descsList; // 分页描述数据对象列表

    private OnPagesToolChangeListener onPagesToolChangeListener;

    public PagesTool(List<T> list, int pageCount) {
        if (list != null && list.size() == 0) {
            this.list = new ArrayList<>();
            for (T t : list) {
                this.list.add(t);
            }
        } else
            throw new UnsupportedOperationException("PagesTool creat error !");
        this.pageCount = pageCount;
        init();
    }

    public PagesTool(ArrayList<T> list, int pageCount) {
        this.list = list;
        this.pageCount = pageCount;
        init();
    }

    public PagesTool(T[] array, int pageCount) {
        if (array != null && array.length > 0) {
            this.list = new ArrayList<>();
            for (T t : array) {
                this.list.add(t);
            }
        } else
            throw new UnsupportedOperationException("PagesTool creat error !");
        this.pageCount = pageCount;
        init();
    }

    private void init() {
        if (list == null || list.size() == 0)
            return;
        if (pageCount <= 0)
            pageCount = 10;
        int size = list.size();
        int mod = size % pageCount;
        if (mod == 0) {
            pageSize = size / pageCount;
        } else {
            pageSize = size / pageCount + 1;
        }
        currentPage = 0;
        descsList = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            Desc desc = new Desc();
            desc.page = i;
            descsList.add(desc);
        }
    }

    // 绑定数据回调接口
    public void setOnPagesToolChangeListener(OnPagesToolChangeListener onPagesToolChangeListener) {
        this.onPagesToolChangeListener = onPagesToolChangeListener;
    }

    /**
     * 设置刷新列表数据
     *
     * @param list
     */
    public void setList(ArrayList<T> list) {
        this.list = list;
        init();
    }

    /**
     * 设置刷新列表数据
     *
     * @param array
     */
    public void setArray(T[] array) {
        if (array != null && array.length > 0) {
            this.list = new ArrayList<>();
            for (T t : array) {
                this.list.add(t);
            }
            init();
        }
    }

    /**
     * 获取分页每页数据数量大小
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     * 获取分页总数
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 获取当前页码索引
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 设置当前页脚，绑定接口情况下，自动返回设置页脚数据
     */
    public void setCurrentPage(int currentPage) {
        if (currentPage < 0 || currentPage >= pageSize)
            return;
        this.currentPage = currentPage;
        if (onPagesToolChangeListener != null)
            onPagesToolChangeListener.getPage(getPageList(currentPage), descsList.get(currentPage));
    }

    /**
     * 上一页索引
     */
    public int previousPage() {
        currentPage--;
        if (currentPage < 0)
            currentPage = 0;
        if (onPagesToolChangeListener != null)
            onPagesToolChangeListener.previous(getPageList(currentPage), descsList.get(currentPage));
        return currentPage;
    }

    /**
     * 下一页索引
     */
    public int nextPage() {
        currentPage++;
        if (currentPage >= list.size())
            currentPage = list.size() - 1;
        if (onPagesToolChangeListener != null)
            onPagesToolChangeListener.next(getPageList(currentPage), descsList.get(currentPage));
        return currentPage;
    }

    /**
     * 获取指定页索引对应的数据列表
     *
     * @param index 分页编号
     * @return 返回此页数据
     */
    public ArrayList<T> getPageList(int index) {
        if (list == null || list.size() == 0)
            return null;
        int bp = index * pageCount;
        int ep = (index == pageSize - 1) ? (pageSize - 1) * pageCount + list.size() % pageCount : bp + pageCount;
        ArrayList<T> pageList = new ArrayList<>();
        for (int i = bp; i < ep; i++) {
            pageList.add(list.get(i));
        }
        if (onPagesToolChangeListener != null)
            onPagesToolChangeListener.getPage(pageList, descsList.get(currentPage));
        return pageList;
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/26 17:24
     * TODO: 描述分页数据对象，用于用户处于某种特殊需求下需要标记，避免重复使用数据缓存等问题
     */
    public static class Desc {
        public int page; // 页脚
        public boolean used; // 是否已使用一次
    }

    /**
     * Author by HEKE
     *
     * @time 2018/11/26 17:21
     * TODO: 分页操作回调接口
     */
    public interface OnPagesToolChangeListener<T> {
        void previous(ArrayList<T> list, Desc desc);

        void getPage(ArrayList<T> list, Desc desc);

        void next(ArrayList<T> list, Desc desc);
    }

}
