package com.xinqi.ihandwh.Model;

/**
 * Created by syd on 2015/11/18.
 */
public class OrderInfo {
    /**
     * 预约楼层
     * */
     String room;
    /**
     * 预约座位号
     * */
     String seatNum;
    /**
     * 预约日期
     * */
     String date;
    /**
     * 最晚刷卡时间*/
    private Long confirmTime;
    /**
     * 是否已经正常取消*/
    String hascanceled;


    public Long getConfirmTime() {
        return confirmTime;
    }

    public void setHascanceled(String hascanceled) {
        this.hascanceled = hascanceled;
    }
    public String getHasCanceled() {
       return hascanceled;
    }
    public void setConfirmTime(Long confirmTime) {
        this.confirmTime = confirmTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public String getDate() {
        return date;
    }

    public String getRoom() {
        return room;
    }

    public String getSeatNum() {
        return seatNum;
    }

}
