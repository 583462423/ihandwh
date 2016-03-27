package com.xinqi.ihandwh.OrderSeats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xinqi.ihandwh.R;

import java.util.List;

/**
 * Created by presisco on 2016/3/6.
 */
public class CustomSpinnerListAdaper extends BaseAdapter {
    List<String> mDataSet;
    int resId;
    Context mContext;

    public CustomSpinnerListAdaper(Context context,List<String> dataset) {
        super();
        mDataSet=dataset;
        mContext=context;
    }

    @Override
    public Object getItem(int position) {
        return mDataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=LayoutInflater.from(mContext).inflate(
                    R.layout.listitem_select_floor_list, null);
        }
        ((TextView)convertView.findViewById(R.id.textSeatTitle)).setText(mDataSet.get(position));
        return convertView;
    }

    public void updateDataset(List<String> newData){
        mDataSet=newData;
    }

    @Override
    public int getCount() {
        return mDataSet.size();
    }
}
