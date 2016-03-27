package com.xinqi.ihandwh.Model;

/**
 * Created by syd on 2015/11/17.
 */
public class BookInfo {
    public String marcno;
    public String name;
    public String code;
    public String detail;
    public String layer;
    public Integer total;
    public Integer rest;

    public BookInfo(String marcno,String name, String code, String detail,String layer,Integer total,Integer rest) {
        this.marcno=marcno;
        this.name = name;
        this.code = code;
        this.detail = detail;
        this.layer = layer;
        this.total=total;
        this.rest=rest;
    }

    public BookInfo()
    {

    }

    public Integer getRest() {
        return rest;
    }

    public Integer getTotal() {
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

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
