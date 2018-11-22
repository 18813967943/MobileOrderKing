package com.lejia.mobile.orderking.httpsResult.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Author by HEKE
 *
 * @time 2017/6/20 11:34
 * TODO: 乐家服务数据节点对象
 */
public class LJNodes implements Parcelable {

    /**
     * 标签
     */
    public String label;

    /**
     * 编号
     */
    public String id;

    /**
     * 子件层次
     */
    public ArrayList<LJNodes> childNodesList;

    /**
     * 节点对应一级目录对象
     */
    public Element element;

    private void init() {
        if (childNodesList == null)
            childNodesList = new ArrayList<>();
    }

    public LJNodes() {
        super();
        init();
    }

    public LJNodes(String label, String id) {
        this.label = label;
        this.id = id;
        init();
    }

    public LJNodes(String label, String id, ArrayList<LJNodes> childNodesList) {
        this.label = label;
        this.id = id;
        this.childNodesList = childNodesList;
        init();
    }

    protected LJNodes(Parcel in) {
        label = in.readString();
        id = in.readString();
        childNodesList = in.createTypedArrayList(LJNodes.CREATOR);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return label;
    }

    public void setName(String label) {
        this.label = label;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<LJNodes> getChildNodesList() {
        return childNodesList;
    }

    public void setChildNodesList(ArrayList<LJNodes> childNodesList) {
        this.childNodesList = childNodesList;
    }

    public Element getElement() {
        return element;
    }

    /**
     * 设置层级元素，根据层级元素获取子元素
     */
    public void setElement(Element element) {
        this.element = element;
        // 获取自己元素
        if (this.element != null) {
            NodeList childList = this.element.getChildNodes();
            if (childList != null) {
                for (int i = 0; i < childList.getLength(); i++) {
                    if (childList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element ele = (Element) childList.item(i);
                        if (ele.hasAttributes()) {
                            String label = ele.getAttribute("label");
                            String id = ele.getAttribute("id");
                            LJNodes nodes = new LJNodes(label, id);
                            nodes.setElement(ele);
                            childNodesList.add(nodes);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(id);
        dest.writeTypedList(childNodesList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LJNodes> CREATOR = new Creator<LJNodes>() {
        @Override
        public LJNodes createFromParcel(Parcel in) {
            return new LJNodes(in);
        }

        @Override
        public LJNodes[] newArray(int size) {
            return new LJNodes[size];
        }
    };

    /**
     * 复制
     */
    public LJNodes copy() {
        LJNodes copy = new LJNodes(label, id);
        if (childNodesList != null) {
            ArrayList<LJNodes> copyChildList = new ArrayList<>();
            for (int i = 0; i < childNodesList.size(); i++) {
                copyChildList.add(childNodesList.get(i).copy());
            }
        }
        return copy;
    }

    @Override
    public String toString() {
        return label + "|" + id + "|" + childNodesList;
    }
}
