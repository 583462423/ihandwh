package com.xinqi.ihandwh.Adapters;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.xinqi.ihandwh.Model.BookRouteInfo;
import com.xinqi.ihandwh.R;
import com.xinqi.ihandwh.SearchBook.BookRouteHistoryHelper;
import com.xinqi.ihandwh.SearchBook.SeeMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by syd on 2015/11/22.
 */
public class MyBookRootListViewAdapter extends BaseAdapter {
    private String bookname,bookcode,bookdetails,bookpos;
    private Context context;
    private List<BookRouteInfo> bookRouteInfos;
    private Intent intent;
    private TextView nonorputetv;
    private  BookRouteHistoryHelper bookRouteHistoryHelper;
    private int pos;

    public MyBookRootListViewAdapter(Context context,TextView noneroute){
        this.context=context;
        this.nonorputetv=noneroute;
        bookRouteInfos=new ArrayList<>();
        intent=new Intent(context, SeeMap.class);
        bookRouteHistoryHelper=new BookRouteHistoryHelper(context);
        bookRouteInfos=bookRouteHistoryHelper.quryBookRoute();
        if (bookRouteInfos.size()==0){
            nonorputetv.setVisibility(View.VISIBLE);
        }else {
            nonorputetv.setVisibility(View.GONE);
        }
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return bookRouteInfos.size();
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
        return bookRouteInfos.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        //无法进行加载优化处理了
        convertView=LayoutInflater.from(context).inflate(R.layout.bookroute_item, null);
        final TextView bookinfo=(TextView) convertView.findViewById(R.id.bookinfo);
        Button btnseemap= (Button) convertView.findViewById(R.id.seeroute);
        Button btnDelete= (Button) convertView.findViewById(R.id.btndeleteroute);
        btnseemap.setTag(position);
        btnDelete.setTag(position);
        bookinfo.setText(bookRouteInfos.get(position).getBookname() + "\n" + bookRouteInfos.get(position).getBookcode() + "\n" + bookRouteInfos.get(position).getBookdetails());
        btnseemap.setText(bookRouteInfos.get(position).getBookpos());
        btnseemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                bookname=bookRouteInfos.get(pos).getBookname();
                bookcode=bookRouteInfos.get(pos).getBookcode();
//                bookdetails=bookRouteInfos.get(position).getBookdetails();
                bookpos=bookRouteInfos.get(pos).getBookpos();
                intent.putExtra("floor", bookpos);
                intent.putExtra("bookcode", bookcode);
                intent.putExtra("bookname", bookname);
                context.startActivity(intent);
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pos = (int) v.getTag();
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setTitle("提醒");
                builder.setMessage("确定删除收藏路线？");
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bookname = bookRouteInfos.get(pos).getBookname();
                        bookcode = bookRouteInfos.get(pos).getBookcode();
//                bookdetails=bookRouteInfos.get(position).getBookdetails();
                        //bookpos = bookRouteInfos.get(pos).getBookpos();
                /*intent.putExtra("floor", bookpos);
                intent.putExtra("bookcode", bookcode);
                intent.putExtra("bookname", bookname);
                context.startActivity(intent);*/
                        boolean b = bookRouteHistoryHelper.deleteBookRoute(bookcode);
                        if (b){
                            Toast.makeText(context,"删除成功",Toast.LENGTH_SHORT).show();
                            bookRouteInfos=bookRouteHistoryHelper.quryBookRoute();
                            if (bookRouteInfos.size()==0){
                                nonorputetv.setVisibility(View.VISIBLE);
                            }else {
                                nonorputetv.setVisibility(View.GONE);
                            }
                            notifyDataSetInvalidated();
                        }    
                    }
                });
                builder.create();
                builder.show();
               
            }
        });

        return convertView;
    }
    private class ViewHolder{
        private TextView bookinfo;
        private Button btnseemap;
        private Button delete;
        public Button getBtnseemap() {
            return btnseemap;
        }

        public TextView getBookinfo() {
            return bookinfo;
        }

        public void setBookinfo(TextView bookinfo) {
            this.bookinfo = bookinfo;
        }

        public void setBtnseemap(Button btnseemap) {
            this.btnseemap = btnseemap;
        }

        public Button getDelete() {
            return delete;
        }

        public void setDelete(Button delete) {
            this.delete = delete;
        }
    }
}
