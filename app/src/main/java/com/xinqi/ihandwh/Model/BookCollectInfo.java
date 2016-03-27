package com.xinqi.ihandwh.Model;

/**
 * Created by syd on 2015/11/26.
 */
public class BookCollectInfo{
    public String marcno;
    public String name;
    public String code;
    public String detail;
    public String layer;
    public  int  total;
    public   int  rest;
    private String isclected;

    public String getIsclected() {
        return isclected;
    }

    public void setIsclected(String isclected) {
        this.isclected = isclected;
    }

    public int getRest() {
        return rest;
    }

    public int getTotal() {
        return total;
    }

    public String getCode() {
        return code;
    }

    public String getDetail() {
        return detail;
    }

    public String getLayer() {
        return layer;
    }

    public String getMarcno() {
        return marcno;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public void setMarcno(String marcno) {
        this.marcno = marcno;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRest(int rest) {
        this.rest = rest;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
