package com.xinqi.ihandwh.SearchBook;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.xinqi.ihandwh.Adapters.SearchHistoryListAdapter;
import com.umeng.analytics.MobclickAgent;
import com.xinqi.ihandwh.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by syd on 2015/11/24.
 */
public class SearchBookContentPage extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = SearchBookContentPage.class.getSimpleName();
    private static final Integer MAX_HISTORY_COUNT=10;
    private Button mSearchByAuthorBtn;
    private Button mSearchByBooknameBtn;
    private Button clearhis;
    private SearchView searchView;
    private ListView listView;
    private List<String> his;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SearchHistoryListAdapter searchHistoryListAdapter;
    private AutoCompleteTextView mSearchKeyInput;
    ArrayAdapter<String> mSearchHistoryAdapter;
    BookSearchHistoryHelper mHistoryHelper;

    public static Fragment newInstance() {
        SearchBookContentPage fragment = new SearchBookContentPage();
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//    }
    /**
     * Fragment初始化入口，用于绘制界面和初始化，代码在return view和View view中间插入
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.search_book_content_page, container, false);
        sharedPreferences=getActivity().getSharedPreferences(getResources().getString(R.string.app_name), Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        return view;
    }
    /**
     * 界面绘制完毕，在super.onViewCreated()后加入代码
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clearhis= (Button) view.findViewById(R.id.firstsearchbtnclearhis);
        his=new ArrayList<>();
        his.add(sharedPreferences.getString("searchhis001","没有搜索记录"));
        if (!sharedPreferences.getString("searchhis002","").equals(""))
            his.add(sharedPreferences.getString("searchhis002",""));
        if (!sharedPreferences.getString("searchhis003","").equals(""))
            his.add(sharedPreferences.getString("searchhis003",""));
        searchView= (SearchView) view.findViewById(R.id.firstsearchView);
        searchView.setQuery("",false);
        final RelativeLayout relativeLayout= (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.search_book_content_page,null);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN)
                    searchView.clearFocus();//释放焦点
                return false;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    Intent intent1 = new Intent(getActivity(), NewSearch.class);
                    getParentFragment().startActivityForResult(intent1, SearchBookConst.SEARCH_BOOK_NEW_SEARCH_REQUEST_CODE);
//                    getActivity().finish();
                }
            }
        });
        mSearchByAuthorBtn = (Button) view.findViewById(R.id.searchByAuthorBtn);
        mSearchByBooknameBtn = (Button) view.findViewById(R.id.searchByBookNameBtn);
        //mSearchKeyInput = (AutoCompleteTextView) view.findViewById(R.id.searchKeyInput);
        mSearchByBooknameBtn.setOnClickListener(this);
        mSearchByAuthorBtn.setOnClickListener(this);
        if (his.get(0).contains("没有搜索记录"))
            clearhis.setEnabled(false);
        clearhis.setOnClickListener(this);
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i("bac", "onResume");
        searchView.setQuery("", false);
        searchView.clearFocus();
        MobclickAgent.onPageStart("SearchBook"); //统计页面
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.searchByAuthorBtn:
                if (!TextUtils.isEmpty(searchView.getQuery())){
                Intent intent1 = new Intent(getActivity(), SearchBookResultActivity.class);
                intent1.putExtra(SearchBookConst.SEARCH_BOOK_KEY, searchView.getQuery().toString().trim());
                intent1.putExtra(SearchBookConst.SEARCH_TYPE, SearchBookConst.SEARCH_TYPE_AUTHOR);
                startActivity(intent1);
                //更新搜索记录
                editor.putString("searchhis003", sharedPreferences.getString("searchhis002", null));
                editor.putString("searchhis002", sharedPreferences.getString("searchhis001", null));
                editor.putString("searchhis001", searchView.getQuery().toString().trim());
                editor.commit();
            }else {
                    Toast.makeText(getActivity(),"请输入关键字！",Toast.LENGTH_SHORT).show();
                }
                // TODO: 2015/11/18  
                break;
            case R.id.searchByBookNameBtn:
                // TODO: 2015/11/18
                //saveHistory();
                if (!TextUtils.isEmpty(searchView.getQuery())) {
                    Intent intent = new Intent(getActivity(), SearchBookResultActivity.class);
                    intent.putExtra(SearchBookConst.SEARCH_BOOK_KEY, searchView.getQuery().toString().trim());
                    intent.putExtra(SearchBookConst.SEARCH_TYPE, SearchBookConst.SEARCH_TYPE_BOOKNAME);
                    startActivity(intent);
                    //更新搜索记录
                    editor.putString("searchhis003", sharedPreferences.getString("searchhis002", null));
                    editor.putString("searchhis002", sharedPreferences.getString("searchhis001", null));
                    editor.putString("searchhis001", searchView.getQuery().toString().trim());
                    editor.commit();
                }else {
                    Toast.makeText(getActivity(),"请输入关键字！",Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.firstsearchbtnclearhis://清楚历史纪录


                break;

        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SearchBook");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==SearchBookConst.SEARCH_BOOK_NEW_SEARCH_REQUEST_CODE)
        {
            if(resultCode==SearchBookConst.SEARCH_BOOK_NEW_SEARCH_RESULT_OK)
            {
                Intent intent=new Intent(getActivity(),SearchBookResultActivity.class);
                intent.putExtra(SearchBookConst.SEARCH_TYPE,data.getStringExtra(SearchBookConst.SEARCH_TYPE));
                intent.putExtra(SearchBookConst.SEARCH_BOOK_KEY,data.getStringExtra(SearchBookConst.SEARCH_BOOK_KEY));
                startActivity(intent);
            }
            else
            {

            }
        }
    }
}
