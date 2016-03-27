package com.xinqi.ihandwh.Model;

/**
 * Created by presisco on 2015/11/16.
 */
public class FloorInfo {
    /**
     * 楼层名
     */
    public String layer;
    /**
     * 总座位
     */
    public int total;
    /**
     * 剩余座位
     */
    public int rest;

    public FloorInfo(String layer, int total, int rest) {
        this.layer = layer;
        this.total = total;
        this.rest = rest;
    }

    public FloorInfo() {
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public int getRest() {
        return rest;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getLayer() {
        return layer;
    }

}


