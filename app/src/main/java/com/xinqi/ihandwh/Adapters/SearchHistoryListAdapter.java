package com.xinqi.ihandwh.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

//import com.xinqi.ihandwh.R;
import com.xinqi.ihandwh.R;

import java.util.List;

/**
 * Created by syd on 2015/11/24.
 */
public class SearchHistoryListAdapter extends BaseAdapter {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;
    private List<String> hisotory;
    public SearchHistoryListAdapter(Context context,List<String> hisotory){
        this.context=context;
//        hisotory=new ArrayList<>();
        sharedPreferences=context.getSharedPreferences(context.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        this.hisotory=hisotory;
        editor=sharedPreferences.edit();
//        hisotory.add(sharedPreferences.getString("searchhis01",""));
//        hisotory.add(sharedPreferences.getString("searchhis02",""));
//        hisotory.add(sharedPreferences.getString("searchhis03",""));
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return hisotory.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return hisotory.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){

            viewHolder=new ViewHolder();
            convertView=LayoutInflater.from(context).inflate(R.layout.searchhistorylistitem, null);
            viewHolder.setSearchhisitemtv((TextView) convertView.findViewById(R.id.searchhistoryitemtv));
            convertView.setTag(viewHolder);

        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.getSearchhisitemtv().setText(hisotory.get(position));
        return convertView;
    }
    private class ViewHolder{
        private TextView searchhisitemtv;
       /* private Button btnseemap;
        private Button delete;*/

       /* public Button getBtnseemap() {
            return btnseemap;
        }*/

        public void setSearchhisitemtv(TextView searchhisitemtv) {
            this.searchhisitemtv = searchhisitemtv;
        }

        public TextView getSearchhisitemtv() {
            return searchhisitemtv;
        }
        /*  public void setBtnseemap(Button btnseemap) {
            this.btnseemap = btnseemap;
        }*/

       /* public Button getDelete() {
            return delete;
        }

        public void setDelete(Button delete) {
            this.delete = delete;
        }*/
    }
}
