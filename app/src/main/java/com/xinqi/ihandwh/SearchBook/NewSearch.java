package com.xinqi.ihandwh.SearchBook;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import com.xinqi.ihandwh.Adapters.SearchHistoryListAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xinqi.ihandwh.R;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by syd on 2015/11/24.
 */
public class NewSearch extends AppCompatActivity implements View.OnClickListener {
    private ActionBar actionBar;
    private SearchView searchView;
    //    private ListAdapter listAdapter;
    private SearchHistoryListAdapter searchHistoryListAdapter;
    private ListView historylistView;//点击填写
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private List<String> his=new ArrayList<>();
    private Button clearhis,newsearchbtncancel,newsearchbyname,newsearchbyahtuor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar=getSupportActionBar();
        setContentView(R.layout.activity_new_booksearch);

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.newsearchactionbar);
        actionBar.show();
        newsearchbyname= (Button) findViewById(R.id.newsearchbyname);
        newsearchbyname.setOnClickListener(this);
        newsearchbyahtuor= (Button) findViewById(R.id.newsearchbyauthor);
        newsearchbyahtuor.setOnClickListener(this);
        newsearchbtncancel= (Button) actionBar.getCustomView().findViewById(R.id.newsearchbtncancel);
        newsearchbtncancel.setOnClickListener(this);
        searchView= (SearchView) actionBar.getCustomView().findViewById(R.id.searchview);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){//开始输入则让搜索历史消失
                    newsearchbyahtuor.setVisibility(View.VISIBLE);
                    newsearchbyname.setVisibility(View.VISIBLE);
                    historylistView.setVisibility(View.GONE);
                    clearhis.setVisibility(View.GONE);
                }else {
                    newsearchbyahtuor.setVisibility(View.GONE);
                    newsearchbyname.setVisibility(View.GONE);
                    his=null;
                    his=new ArrayList<String>();
                    his.add(sharedPreferences.getString("searchhis001","没有搜索记录"));
                    if (!sharedPreferences.getString("searchhis002","").equals(""))
                        his.add(sharedPreferences.getString("searchhis002",""));
                    if (!sharedPreferences.getString("searchhis003","").equals(""))
                        his.add(sharedPreferences.getString("searchhis003",""));
                    searchHistoryListAdapter=null;
                    searchHistoryListAdapter=new SearchHistoryListAdapter(NewSearch.this,his);
                    historylistView.setAdapter(searchHistoryListAdapter);
                    historylistView.setVisibility(View.VISIBLE);
                    clearhis.setVisibility(View.VISIBLE);
                    if (his.get(0).contains("没有搜索记录"))
                        clearhis.setEnabled(false);
                    else
                        clearhis.setEnabled(true);

                }
                return false;
            }
        });
        sharedPreferences=this.getSharedPreferences(this.getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        historylistView= (ListView) findViewById(R.id.newsearchhistorylistviewlistview);
        his.add(sharedPreferences.getString("searchhis001","没有搜索记录"));
        if (!sharedPreferences.getString("searchhis002","").equals(""))
            his.add(sharedPreferences.getString("searchhis002",""));
        if (!sharedPreferences.getString("searchhis003","").equals(""))
            his.add(sharedPreferences.getString("searchhis003",""));
        // listAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,his);
        searchHistoryListAdapter=new SearchHistoryListAdapter(this,his);

        historylistView.setAdapter(searchHistoryListAdapter);
        historylistView.setVisibility(View.VISIBLE);
        //点击填充
        historylistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!his.get(0).contains("没有搜索记录"))
                searchView.setQuery(his.get(position).toString(), false);
            }
        });
        clearhis= (Button) findViewById(R.id.newsearchbtnclearhis);
        if (his.get(0).contains("没有搜索记录"))
            clearhis.setEnabled(false);
        //清楚查找记录
        clearhis.setOnClickListener(this);

    }
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem search=menu.findItem(R.id.newsearch);
        search.collapseActionView();
        SearchView searchView= (SearchView) search.getActionView();
        //searchView.setIconifiedByDefault(false);
        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo=searchManager.getSearchableInfo(getComponentName());
        //searchView.setSearchableInfo(searchableInfo);
        return true;

    }*/

    //    @Override
   /* public boolean onCreateOptionsMenu(Menu menu ) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Configure the search info and add any event listeners
//        searchView.setIconifiedByDefault(false);
        searchView.onActionViewExpanded();
        return super.onCreateOptionsMenu(menu);
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newsearchbtnclearhis://清楚历史搜索记录
                //// TODO: 2016/2/28 添加确认
                Log.i("bac","清楚");
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("清空搜索记录？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editor.putString("searchhis001", null);
                        editor.putString("searchhis002", null);
                        editor.putString("searchhis003", null);
                        editor.commit();
                        historylistView.setVisibility(View.GONE);//没有则不显示
                        clearhis.setVisibility(View.GONE);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create();
                builder.show();
                /*editor.putString("searchhis001", null);
                editor.putString("searchhis002", null);
                editor.putString("searchhis003", null);
                editor.commit();
                historylistView.setVisibility(View.GONE);//没有则不显示
                clearhis.setVisibility(View.GONE);*/
                break;
            case R.id.newsearchbtncancel://取消新搜索
                setResult(SearchBookConst.SEARCH_BOOK_NEW_SEARCH_RESULT_CANCELD);
                finish();
                break;
            case R.id.newsearchbyname://按书名重新搜索
                Log.i("bac", "书名");
                // TODO: 2015/11/24
                Intent intent2 = new Intent();
                intent2.putExtra(SearchBookConst.SEARCH_BOOK_KEY, searchView.getQuery().toString().trim());
                intent2.putExtra(SearchBookConst.SEARCH_TYPE, SearchBookConst.SEARCH_TYPE_BOOKNAME);
                //结束掉之前的resultactivity
//                if (from==1)
//                    SearchBookResultActivity.getinstance().finish();
//                startActivity(intent);
                //更新搜索记录
                String leatest03 = sharedPreferences.getString("searchhis003", null);
                String leatest02 = sharedPreferences.getString("searchhis002", null);
                String leatest01 = sharedPreferences.getString("searchhis001", null);
                String s = searchView.getQuery().toString().trim();
                Log.i("-----------==------",s);
                if (!s.equals(leatest01) && !s.equals(leatest02) && !s.equals(leatest03)) {
                    editor.putString("searchhis003", sharedPreferences.getString("searchhis002", null));
                    editor.putString("searchhis002", sharedPreferences.getString("searchhis001", null));
                    editor.putString("searchhis001", s);
                    editor.commit();
                }

                setResult(SearchBookConst.SEARCH_BOOK_NEW_SEARCH_RESULT_OK, intent2);
                finish();
                break;
            case R.id.newsearchbyauthor://按作者重新搜索
                // 结束掉之前的resultactivity
//                if (from==1)
//                    SearchBookResultActivity.this.finish();
                Log.i("bac","作者");
                Intent intent3 = new Intent();
                intent3.putExtra(SearchBookConst.SEARCH_BOOK_KEY,searchView.getQuery().toString().trim());
                intent3.putExtra(SearchBookConst.SEARCH_TYPE, SearchBookConst.SEARCH_TYPE_AUTHOR);
//                startActivity(intent1);
                //更新搜索记录
                String leatest003= sharedPreferences.getString("searchhis003", null);
                String leatest002= sharedPreferences.getString("searchhis002", null);
                String leatest001= sharedPreferences.getString("searchhis001", null);
                String s1=searchView.getQuery().toString().trim();
                if (!s1.equals(leatest001)&&!s1.equals(leatest002)&&!s1.equals(leatest003)) {
                    editor.putString("searchhis003", sharedPreferences.getString("searchhis002", null));
                    editor.putString("searchhis002", sharedPreferences.getString("searchhis001", null));
                    editor.putString("searchhis001",s1);
                    editor.commit();
                }
                setResult(SearchBookConst.SEARCH_BOOK_NEW_SEARCH_RESULT_OK, intent3);
                finish();
                break;
        }

    }
    /* private void back() {

     }*/
   /* //获得后退键退出
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        Log.i("bac","ddddddddddddddddddd");
          if (keyCode == KeyEvent.KEYCODE_BACK&& event.getAction() == KeyEvent.ACTION_DOWN) {
              Log.i("bac","回到主页");
              back();
              return false;
             }else
         // return super.onKeyDown(keyCode, event);
        return false;
        }*/
    @Override
    public void onBackPressed() {
        setResult(SearchBookConst.SEARCH_BOOK_NEW_SEARCH_RESULT_CANCELD);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
