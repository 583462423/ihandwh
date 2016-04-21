package com.xinqi.ihandwh.Model;

/**
 * Created by Qking on 2016/4/14.
 */
public class RenewBookInfo {
    String barcode; //条码号
    String check;   //并不知道这是什么，但是网页上面嵌套的有就写成一样的吧
    String booknameAndAuther;          //书名
    String getDate;           //解书时间
    String shouldReturnDate;  //应还时间
    String cnt;               //续借量
    String collecAddress;     //馆藏地

    public RenewBookInfo(){}

    public RenewBookInfo(String barcode, String check, String booknameAndAuther, String getDate, String shouldReturnDate, String cnt, String collecAddress) {
        this.barcode = barcode;
        this.check = check;
        this.booknameAndAuther = booknameAndAuther;
        this.getDate = getDate;
        this.shouldReturnDate = shouldReturnDate;
        this.cnt = cnt;
        this.collecAddress = collecAddress;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCheck() {
        return check;
    }

    public void setCheck(String check) {
        this.check = check;
    }

    public String getBooknameAndAuther() {
        return booknameAndAuther;
    }

    public void setBooknameAndAuther(String booknameAndAuther) {
        this.booknameAndAuther = booknameAndAuther;
    }

    public String getGetDate() {
        return getDate;
    }

    public void setGetDate(String getDate) {
        this.getDate = getDate;
    }

    public String getShouldReturnDate() {
        return shouldReturnDate;
    }

    public void setShouldReturnDate(String shouldReturnDate) {
        this.shouldReturnDate = shouldReturnDate;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getCollecAddress() {
        return collecAddress;
    }

    public void setCollecAddress(String collecAddress) {
        this.collecAddress = collecAddress;
    }
}
