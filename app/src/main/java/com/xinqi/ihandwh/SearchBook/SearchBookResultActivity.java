package com.xinqi.ihandwh.SearchBook;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xinqi.ihandwh.HttpService.SearchBookService.SearchBookService;
import com.xinqi.ihandwh.Model.BookInfo;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.xinqi.ihandwh.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by syd on 2015/11/18.
 */
public class SearchBookResultActivity extends AppCompatActivity implements SearchBookResultAdapter.OnUpdateDataInterface{
    private ActionBar actionBar;
    private Button mSecondarySearchBtn;
    private Button mCancel;
    SearchBookResultAdapter mSearchBookResultAdapter;
    private EditText mPrimarySearchEditText;
    private Spinner mSearchTypeSpinner;
    private ImageView mBackButtonImageView;
    private ArrayAdapter<String> mSearchTypeArrayAdapter;
    //结果页面状态
//    private BookListState bookListState;
    //搜索结果列表

    private String mPrimaryKeyWord;
    private String mPrimaryType;
    private String mSecondaryKeyWord;
    private String mSecondaryType;
    private Boolean mIsPrimarySearch=true;
    private Integer mCurPage=0;
    private Integer mTotalPage=Integer.MAX_VALUE;
    private Integer lastVisibileItem;
    private SearchView searchView;
    private boolean canrefresh=true;
    RecyclerView mSearchBookResultRecyclerView;
    private ProgressBar searchprogressbar,refreshpageprobar;
    private TextView searchtip;
    private TextView nomoretv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        setContentView(R.layout.activity_search_book_result);
        nomoretv= (TextView) findViewById(R.id.nomoretv);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.newsearchactionbar02);
        final Intent intent=getIntent();
        searchtip= (TextView) findViewById(R.id.searchbooktips);
        searchtip.setText("正在查找书籍");
        searchtip.setTextColor(getResources().getColor(R.color.my));
        searchtip.setVisibility(View.VISIBLE);
        searchprogressbar= (ProgressBar) findViewById(R.id.searchbooklistpro);
        searchprogressbar.setVisibility(View.VISIBLE);
        refreshpageprobar= (ProgressBar) findViewById(R.id.refreshpagepro);
        mPrimaryKeyWord = intent.getStringExtra(SearchBookConst.SEARCH_BOOK_KEY);
        mPrimaryType=intent.getStringExtra(SearchBookConst.SEARCH_TYPE);
//        View view01=LayoutInflater.from(this).inflate(R.layout.newsearchactionbar02,null);
        View view01=actionBar.getCustomView();
        searchView= (SearchView) view01.findViewById(R.id.searchview02);
        mCancel= (Button) view01.findViewById(R.id.newsearchbtncancel02);
        View view=LayoutInflater.from(this).inflate(R.layout.activity_search_book_result,null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();//清除焦点
            }
        });
//        actionBar.setTitle("查找书籍");
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        mPrimarySearchEditText=(EditText)findViewById(R.id.bookPrimarySearchEditText);
        searchView.setQueryHint(mPrimaryKeyWord);
        searchView.clearFocus();//清除焦点
        Log.i("bac", "上一次搜索" + mPrimaryKeyWord);
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    Intent intent1 = new Intent(SearchBookResultActivity.this, NewSearch.class);
                    startActivityForResult(intent1, SearchBookConst.SEARCH_BOOK_NEW_SEARCH_REQUEST_CODE);
                }
            }
        });
        mSearchBookResultRecyclerView = (RecyclerView) findViewById(R.id.bookSearchResultRecyclerView);
        mSearchBookResultRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
               boolean isSlidingtolast=false;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager linearLayoutManager= (LinearLayoutManager) recyclerView.getLayoutManager();
                //当不滚动时
                if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = linearLayoutManager.getItemCount();
                    // 判断是否滚动到底部，并且是向下滚动
                    if (lastVisibleItem == (totalItemCount -1) &&isSlidingtolast) {
                        //加载更多功能的代码
                        if (mCurPage == mTotalPage) {
                            refreshpageprobar.setVisibility(View.GONE);
                            nomoretv.setVisibility(View.VISIBLE);
                        } else {
                            if (canrefresh) {
                                onUpdate();
                                canrefresh = false;
                            }
                        }
                    }else {
                        nomoretv.setVisibility(View.GONE);
                    }

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy>0){
                    isSlidingtolast=true;
                }else {
                    isSlidingtolast=false;
                }
            }
        });
        mSecondarySearchBtn = (Button) findViewById(R.id.bookSecondarySearchButton2);
//        mSecondarySearchBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSecondaryKeyWord = mSecondarySearchEditText.getText().toString().trim();
//                switch(mSearchTypeSpinner.getSelectedItemPosition())
//                {
//                    case 0:mSecondaryType= SearchBookConst.SEARCH_TYPE_BOOKNAME;break;
//                    case 1:mSecondaryType= SearchBookConst.SEARCH_TYPE_AUTHOR;break;
//                    default:return;
//                }
//                mIsPrimarySearch=false;
//                mCurPage=1;
//                Toast.makeText(SearchBookResultActivity.this, "正在查找", Toast.LENGTH_SHORT).show();
//                new GetBookList().execute();
//            }
//        });
        mSecondarySearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchBookResultActivity.this);
                LayoutInflater li = SearchBookResultActivity.this.getLayoutInflater();
                final View view = li.inflate(R.layout.secondary_search_dialog, null);
                builder.setView(view)
                        .setPositiveButton("书名搜索", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mSecondaryType = SearchBookConst.SEARCH_TYPE_BOOKNAME;
                                mSecondaryKeyWord = ((EditText) view.findViewById(R.id.keyWordEditText))
                                        .getText().toString().trim();
                                mIsPrimarySearch = false;
                                mCurPage = 1;
//                                Toast.makeText(SearchBookResultActivity.this, "正在查找", Toast.LENGTH_SHORT).show();
                                searchprogressbar.setVisibility(View.VISIBLE);
                                searchtip.setText("正在查找书籍...");
                                searchtip.setVisibility(View.VISIBLE);
                                new GetBookList().execute();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("作者搜索", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSecondaryType = SearchBookConst.SEARCH_TYPE_AUTHOR;
                                mSecondaryKeyWord = ((EditText) view.findViewById(R.id.keyWordEditText))
                                        .getText().toString().trim();
                                mIsPrimarySearch = false;
                                mCurPage = 1;
                                searchtip.setText("正在查找书籍...");
                                searchtip.setVisibility(View.VISIBLE);
//                                Toast.makeText(SearchBookResultActivity.this, "正在查找", Toast.LENGTH_SHORT).show();
                                new GetBookList().execute();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
//        mBackButtonImageView = (ImageView) findViewById(R.id.SearchResultBackImageView);
//        mBackButtonImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        final String type_item[] = {getResources().getString(R.string.book_name),
//                                    getResources().getString(R.string.book_author)};
//        mSearchTypeArrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type_item);
//        mSearchTypeSpinner=(Spinner)findViewById(R.id.filtOptionSpinner);
//        mSearchTypeSpinner.setAdapter(mSearchTypeArrayAdapter);
//        mSearchTypeSpinner.setSelection(0, true);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final RecyclerView.LayoutManager mRecyclerViewLayoutManager = new GridLayoutManager(this, 1);
        mSearchBookResultRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mSearchBookResultAdapter = new SearchBookResultAdapter(new ArrayList<BookInfo>(), this, this);
        mSearchBookResultAdapter.hideFooter();
        mSearchBookResultRecyclerView.setAdapter(mSearchBookResultAdapter);
        // TODO: 2015/11/25
//        Toast.makeText(SearchBookResultActivity.this, "正在查找", Toast.LENGTH_SHORT).show();
        mCurPage=1;
        new GetBookList().execute();

        PushAgent.getInstance(this).onAppStart();
    }
    @Override
    public void onUpdate()
    {
        if (mCurPage==mTotalPage){
//            Toast.makeText(SearchBookResultActivity.this, "没有更多了！", Toast.LENGTH_SHORT).show();

        }else {
            mCurPage+=1;
            refreshpageprobar.setVisibility(View.VISIBLE);
            //Toast.makeText(SearchBookResultActivity.this, "正在加载下一页", Toast.LENGTH_SHORT).show();
            new GetBookList().execute();
        }
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

    private class GetBookList extends AsyncTask<String, Void, String> { // 加载检索结果
        /**
         * 根据获取到的HTML文件进行解析，获取图书信息，并且在RecyclerView中得到显示
         */

        public List<BookInfo> list;
        protected void onPostExecute(String result) {
            //mSearchBookResultRecyclerView.onRefreshComplete();
            if (result != null) {
                // 获取到图书信息，存储到book集合中

//                Log.d("SearchBookResult", "SearchBookResultAty count at this page:" + list.size());
                if (list != null) {

                    mTotalPage = SearchBookService.getListState(result).totalPage; // 获取页数信息
                    mCurPage = SearchBookService.getListState(result).currPage;
                    if (mCurPage == mTotalPage)
                        mSearchBookResultAdapter.hideFooter();
                    else
                        mSearchBookResultAdapter.showFooter();
//                    Log.d("SearchBookResult","Total Page:"+mTotalPage+" Current Page:"+mCurPage);

                    if (mCurPage != 1) {
                        mSearchBookResultAdapter.appendData(list);
                        handler.sendEmptyMessage(2);
                    } else {
                        // TODO: 2015/11/25
                        mSearchBookResultAdapter.updateDataSet(list);
                        handler.sendEmptyMessage(1);
                    }
                    mSearchBookResultAdapter.notifyDataSetChanged();
                    mSearchBookResultRecyclerView.setVisibility(View.VISIBLE);
                    Log.d("SearchBookResult","Result Count:" + mSearchBookResultAdapter.getItemCount());
                } else {

                   /* Toast.makeText(getApplicationContext(), "没有找到..",
                            Toast.LENGTH_LONG).show();*/
                    handler.sendEmptyMessage(0);
                }
            }
            super.onPostExecute(result);
            canrefresh=true;
        }
        /**
         * 获取搜索内容 返回解析到的HTML文件 arg0[0] title arg0[1] page
         */

        protected String doInBackground(String... arg0) {
            String result = "";
            if (mIsPrimarySearch)
                result = SearchBookService.getPage(mPrimaryKeyWord, mPrimaryType, mCurPage);
            else
                result = SearchBookService.queryTwice(mPrimaryKeyWord, mPrimaryType, mSecondaryKeyWord,
                        mSecondaryType, mCurPage);
            list = SearchBookService.getBookList(result,SearchBookResultActivity.this);
            mSearchBookResultAdapter.updateBookRouteInfo();
            return result;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if(requestCode==SearchBookConst.SEARCH_BOOK_NEW_SEARCH_REQUEST_CODE)
        {
            if(resultCode==SearchBookConst.SEARCH_BOOK_NEW_SEARCH_RESULT_OK) {
                mPrimaryType = data.getStringExtra(SearchBookConst.SEARCH_TYPE);
                mPrimaryKeyWord = data.getStringExtra(SearchBookConst.SEARCH_BOOK_KEY);
                mIsPrimarySearch = true;
                searchView.setQueryHint(mPrimaryKeyWord);
                mCurPage=1;
                searchtip.setVisibility(View.VISIBLE);
                searchprogressbar.setVisibility(View.VISIBLE);
                mSearchBookResultRecyclerView.setVisibility(View.INVISIBLE);
                new GetBookList().execute();
            }
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        searchView.clearFocus();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==1){
                searchprogressbar.setVisibility(View.GONE);
                searchtip.setVisibility(View.GONE);
            }else if (msg.what==2){
                refreshpageprobar.setVisibility(View.GONE);
            }else if (msg.what==0){
                searchprogressbar.setVisibility(View.GONE);
                searchtip.setText("没有找到相关书籍!");
                searchtip.setTextColor(getResources().getColor(R.color.red));
                searchtip.setVisibility(View.VISIBLE);
            }
        }
    };
    @Override
    public void onBackPressed() {
        finish();
    }

}
