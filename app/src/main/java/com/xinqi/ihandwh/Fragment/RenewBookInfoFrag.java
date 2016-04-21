package com.xinqi.ihandwh.Fragment;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xinqi.ihandwh.HttpService.OkHttpTools;
import com.xinqi.ihandwh.Model.RenewBookInfo;
import com.xinqi.ihandwh.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class RenewBookInfoFrag extends Fragment {

    RecyclerView renewRecycleView;
    String cookie;
    String htmlInfo; //返回的Html的信息，待解析
    ProgressBar loading;
    public RenewBookInfoFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_renew_book_info, container, false);
        renewRecycleView = (RecyclerView) view.findViewById(R.id.renew_book_recycle);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        //调用多线程访问借书目录，在这之前要得到当前cookie
        cookie = getActivity().getSharedPreferences("is_login", Context.MODE_PRIVATE).getString("cookie",null);
        Call call = OkHttpTools.getBookCall(new OkHttpClient(),cookie);
        new getBook().execute(call);
        return view;
    }

    class getBook extends AsyncTask<Call,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Call... params) {
            Call call = params[0];
            try {
                htmlInfo = call.execute().body().string();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("111111111111","33333333333333333");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //结束后就开始解析Html，解析完成后，将数据添加到list中，并为RecycleView设置Adapter
            lists = new ArrayList<RenewBookInfo>();
            analysisHtml(htmlInfo);
            //测试开始
//            lists.add(new RenewBookInfo("TS12310","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12312","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12313","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12314","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12315","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12316","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12317","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12318","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12319","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12310","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12315","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
//            lists.add(new RenewBookInfo("TS12319","check","这本书的作者是谁你猜啊","就不告诉你作者","2014-2-8","2015-8-9","1","在那金碧辉煌的故宫啊"));
            //测试结束
            renewRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
            renewRecycleView.setItemAnimator(new DefaultItemAnimator());
            renewRecycleView.setAdapter(new BookAdapter());
            Log.i("lists的size",""+lists.size());
            loading.setVisibility(View.GONE);
            super.onPostExecute(aVoid);
        }
    }

    private void analysisHtml(String responceHtml)
    {
        Document doc = Jsoup.parse(responceHtml);
        Elements table = doc.select("table.table_line");
        Elements tr = table.select("tr");

        for(int i=1;i<tr.size();i++)
        {
            //对每条数据进行遍历
            RenewBookInfo renewBookInfo = new RenewBookInfo();
            Elements td = tr.get(i).select("td");
            String barcode = td.get(0).text();
            String bookNameAndAuther = td.get(1).text();
            String getDate = td.get(2).text();
            String shouldReturnDate = td.get(3).text();
            String cnt = td.get(4).text();
            String collectAddress = td.get(5).text();
            String renewInfo = td.get(7).select("input").toString();
            String renewInofs[] = renewInfo.split("\"");
            renewInfo = renewInofs[9];
            String tempInofs[] = renewInfo.split("'");
            String check = tempInofs[3];

            renewBookInfo.setBarcode(barcode);
            renewBookInfo.setBooknameAndAuther(bookNameAndAuther);
            renewBookInfo.setGetDate(getDate);
            renewBookInfo.setShouldReturnDate(shouldReturnDate);
            renewBookInfo.setCnt(cnt);
            renewBookInfo.setCollecAddress(collectAddress);
            renewBookInfo.setCheck(check);
            lists.add(renewBookInfo);
        }
    }




    List<RenewBookInfo> lists;





    class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder>
    {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.renew_book_item_layout,parent,false));
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.bookName.setText(lists.get(position).getBooknameAndAuther());
            holder.shouldReturnDate.setText("应还日期:"+lists.get(position).getShouldReturnDate());
            holder.barcode.setText("条码号:"+lists.get(position).getBarcode());
            holder.getDate.setText("借阅日期:"+lists.get(position).getGetDate());
            holder.address.setText("馆藏地:"+lists.get(position).getCollecAddress());
            holder.renew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //续借线程走起
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RenewBookInfo info = lists.get(position);

                    Call call = OkHttpTools.getRenewCall(okHttpClient,
                            getActivity().getSharedPreferences("is_login",Context.MODE_PRIVATE).getString("cookie",null),
                            info.getBarcode(),
                            info.getCheck(),
                            getActivity().getSharedPreferences("is_login",Context.MODE_PRIVATE).getString("captcha",null));

                    new AsyncTask<Call,Void,Void>(){
                        String responce;
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            holder.renewLoading.setVisibility(View.VISIBLE);
                            holder.renew.setEnabled(false);
                        }

                        @Override
                        protected Void doInBackground(Call... params) {
                            Call call = params[0];
                            try {
                                responce = call.execute().body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            holder.renewLoading.setVisibility(View.GONE);
                            holder.renew.setEnabled(true);
                            CharSequence responceHtml = Html.fromHtml(responce);

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("提示：")
                                    .setMessage(responceHtml)
                                    .setPositiveButton("知道了",null)
                                    .create().show();
                            super.onPostExecute(aVoid);
                        }
                    }.execute(call);
                }
            });
            holder.clickLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.hidenLayout.getVisibility()==View.GONE)
                    {
                        holder.hidenLayout.setVisibility(View.VISIBLE);
                        ValueAnimator animator = ValueAnimator.ofFloat(0f,1f);
                        animator.setDuration(300);
                        animator.setInterpolator(new FastOutSlowInInterpolator());
                        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                holder.hidenLayout.setScaleY((Float) animation.getAnimatedValue());
                            }
                        });
                        animator.start();
                    }else
                    {
                        holder.hidenLayout.setVisibility(View.GONE);

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return lists.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView bookName;
            TextView shouldReturnDate;
            TextView barcode;
            TextView getDate;
            TextView address;
            Button renew;
            RelativeLayout clickLayout;
            RelativeLayout hidenLayout;
            ProgressBar renewLoading;
            public MyViewHolder(View itemView) {
                super(itemView);
                bookName = (TextView) itemView.findViewById(R.id.book_name);
                shouldReturnDate = (TextView) itemView.findViewById(R.id.should_return_date);
                barcode = (TextView) itemView.findViewById(R.id.barcode);
                getDate = (TextView) itemView.findViewById(R.id.get_date);
                address = (TextView) itemView.findViewById(R.id.address);
                renew = (Button) itemView.findViewById(R.id.renew);
                clickLayout = (RelativeLayout) itemView.findViewById(R.id.can_click_layout);
                hidenLayout = (RelativeLayout) itemView.findViewById(R.id.hiden_layout);
                renewLoading = (ProgressBar) itemView.findViewById(R.id.renew_loading);
            }
        }
    }
}
