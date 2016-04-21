package com.xinqi.ihandwh.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.xinqi.ihandwh.LibraryActivity;
import com.xinqi.ihandwh.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends DialogFragment {


    TextView noticeInfo;
    Button confirm;
    CheckBox remember;
    Dialog dialog;
    String info = "     在您还没开始图书馆功能之前，先跟您说声抱歉，由于图书馆的续借与选座采用的不是一个系统，"
    +"导致您不得不在进入续借系统之前再次登录一遍，而该系统的密码与您选座系统的密码可能是不相同的，初始时是学号。\n\n"
            +"      而官方的系统，您登录的时候需要填写验证码，续借也需要填写验证码，所以我们的产品也迫不得已也需要您填写验证码"
            + "不过我们的产品对此进行了优化，即:只需要填写一次验证码即可，不过一定要准确无误哦"
            +"如果您对我们有更好的建议，那么请在个人中心的进行反馈中反馈您宝贵的建议，谢谢您的使用~\n\n";

    public NoticeFragment() {
        // Required empty public constructor
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View v = mInflater.inflate(R.layout.fragment_notice,null);
        confirm = (Button) v.findViewById(R.id.confirm);
        noticeInfo = (TextView) v.findViewById(R.id.notice_info);
        remember = (CheckBox) v.findViewById(R.id.remember);


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(remember.isChecked())
                {
                    //将来这个变量存储在shareXXX中
                    getActivity().getSharedPreferences("isremember", Context.MODE_PRIVATE).edit()
                            .putBoolean("is_remember",true)
                            .commit();
                }
                dialog.cancel();
                ((LibraryActivity)getActivity()).onConfirm();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("亲爱的用户:").setView(v);
        noticeInfo.setText(info);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }
}
