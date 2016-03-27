package com.xinqi.ihandwh.SearchBook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.xinqi.ihandwh.R;


/**
 * Created by presisco on 2015/11/23.
 */
public class BookSearchOptionBox extends Activity{
    private static final Integer MAX_HISTORY_COUNT=10;
    private AutoCompleteTextView mSearchKeyInput;
    ArrayAdapter<String> mSearchHistoryAdapter;
    BookSearchHistoryHelper mHistoryHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_book_content_page);
        //mSearchKeyInput=(AutoCompleteTextView)findViewById(R.id.searchKeyInput);
        Button searchName=(Button)findViewById(R.id.searchByBookNameBtn);
        Button searchAuthor=(Button)findViewById(R.id.searchByAuthorBtn);
        searchName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SearchBookConst.SEARCH_OPTION_RESULT_OK, prepareForReturn(SearchBookConst.SEARCH_TYPE_BOOKNAME));
                finish();
            }
        });
        searchAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(SearchBookConst.SEARCH_OPTION_RESULT_OK, prepareForReturn(SearchBookConst.SEARCH_TYPE_AUTHOR));
                finish();
            }
        });
        initAutoComplete(mSearchKeyInput);
    }

    protected Intent prepareForReturn(String type)
    {
        Intent result=new Intent();
        result.putExtra(SearchBookConst.SEARCH_BOOK_KEY, mSearchKeyInput.getText().toString().trim());
        result.putExtra(SearchBookConst.SEARCH_TYPE, type);
        return result;
    }
    private void initAutoComplete(final AutoCompleteTextView autoCompleteTextView) {
        mHistoryHelper =new BookSearchHistoryHelper(this,null,1);

        mSearchHistoryAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, mHistoryHelper.querySearchHistory(MAX_HISTORY_COUNT));
        autoCompleteTextView.setAdapter(mSearchHistoryAdapter);
        autoCompleteTextView
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        AutoCompleteTextView view = (AutoCompleteTextView) v;
                        if (hasFocus) {
                            view.showDropDown();
                        }
                    }
                });
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AutoCompleteTextView) v).showDropDown();
            }
        });
    }

}
