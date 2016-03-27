package com.xinqi.ihandwh.Model;

/**
 * Created by syd on 2015/11/17.
 */
public class BookListState {
    public int currPage;	//当前页数
    public int totalPage;	//总页数

    public BookListState(int currPage, int totalPage) {
        this.currPage = currPage;
        this.totalPage = totalPage;
    }

}
