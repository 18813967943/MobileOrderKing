package com.lejia.mobile.orderking.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.lejia.mobile.orderking.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.view.Gravity.CENTER_HORIZONTAL;

/**
 * 作者：hyq on 2017/8/3 09:50
 * 邮箱：u1234@126.com
 */


public class DialogUtils {
    public static int TRUE_AND_FALSE = 0;     //确定和取消按钮
    public static int ONLY_TRUE = 1;          //只有一个确定按钮
    public static int NO_BUTTON_AND_HINT = 2; //仅作提示框
    public static int EDIT_TEXT = 3; //编辑框

    /**
     * @param context         上下文
     * @param with            宽  如果输入0，则占满宽，否则输入相应的dp值
     * @param ButtonType      按钮类型，有确定取消按钮或否
     * @param canPressOutSide 点击外面能否消失,true即为点击外面可让Dialog消失
     * @param content         提示信息
     * @param title           标题，如果不想显示则传入null,想显示标题直接传入
     * @param dialogListenner 监控按钮的动作
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static Dialog createDialog(final Context context, float with, final int ButtonType, Boolean canPressOutSide, String content, String title, final DialogListenner dialogListenner) {
        final AlertDialog myDialog = new AlertDialog.Builder(context).create();
        //调用这个方法时，按对话框以外的地方不起作用。按返回键起作用
        myDialog.setCanceledOnTouchOutside(canPressOutSide);
//        调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用
        myDialog.setCancelable(canPressOutSide);
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加这句代码
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.dialog_utils);
        final EditText edit = (EditText) myDialog.getWindow().findViewById(R.id.edit); //编辑框
        TextView yes = (TextView) myDialog.getWindow().findViewById(R.id.yes);
        TextView no = (TextView) myDialog.getWindow().findViewById(R.id.no);
        TextView contentView = (TextView) myDialog.getWindow().findViewById(R.id.content);
        ImageView closeView = (ImageView) myDialog.getWindow().findViewById(R.id.close);
        TextView titleView = (TextView) myDialog.getWindow().findViewById(R.id.title);
        LinearLayout ll_button = (LinearLayout) myDialog.getWindow().findViewById(R.id.ll_button);
        LinearLayout root = (LinearLayout) myDialog.getWindow().findViewById(R.id.root);
        View line_view = (View) myDialog.getWindow().findViewById(R.id.line_view);
        View v_line = (View) myDialog.getWindow().findViewById(R.id.v_line);

        contentView.setText(content);

        if (with != 0) {
            Window dialogWindow = myDialog.getWindow();
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            dialogWindow.setGravity(CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//            lp.x = 100; // 新位置X坐标
//            lp.y = 100; // 新位置Y坐标
            lp.width = DensityUtil.dip2px(context, with); // 宽度
//            lp.height = 300; // 高度
            lp.alpha = 0.7f; // 透明度

            // 当Window的Attributes改变时系统会调用此函数,可以直接调用以应用上面对窗口参数的更改,也可以用setAttributes
            // dialog.onWindowAttributesChanged(lp);
            dialogWindow.setAttributes(lp);
        }

        if (!TextUtils.isEmpty(title) || title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        }

        switch (ButtonType) {
            case 0:
                break;
            case 1:
                no.setVisibility(View.GONE);
                v_line.setVisibility(View.GONE);
//                yes.setBackground(context.getResources().getDrawable(R.drawable.dialog_left_right));
                yes.setBackground(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dialog_left_right));
                break;
            case 2:
                yes.setVisibility(View.GONE);
                no.setVisibility(View.GONE);
                ll_button.setVisibility(View.GONE);
                closeView.setVisibility(View.VISIBLE);
                line_view.setVisibility(View.GONE);
//                root.setBackground(context.getDrawable(R.drawable.dialog_util_bg_black));
                root.setBackground(ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.dialog_util_bg_black));
                closeView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (myDialog != null && myDialog.isShowing()) {
                            myDialog.dismiss();
                        }
                    }
                }, 3000);
                break;

            case 3:
                //表示为编辑框
                edit.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.GONE);
                edit.setText(content);
                edit.requestFocus();
                edit.setFocusable(true);
                edit.setFocusableInTouchMode(true);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    public void run() {
                        InputMethodManager inputManager = (InputMethodManager) edit.getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.showSoftInput(edit, 0);
                    }

                }, 100);
                //只用下面这一行弹出对话框时需要点击输入框才能弹出软键盘
                myDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                //加上下面这一行弹出对话框时软键盘随之弹出
                myDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                break;
        }

        //确定
        myDialog.getWindow()
                .findViewById(R.id.yes)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ButtonType == EDIT_TEXT) {
                            String trim = edit.getText().toString().trim();
                            if (TextUtils.isEmpty(trim)) {
                                Toast.makeText(context, "请输入内容", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            dialogListenner.onPressYesByEdit(trim);
                        } else {
                            dialogListenner.onPressYes();
                        }
                        myDialog.dismiss();
                    }
                });
        //取消
        myDialog.getWindow()
                .findViewById(R.id.no)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogListenner.onPressNo();
                        myDialog.dismiss();
                    }
                });
        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                root.getBackground().setAlpha(255);
            }
        });

        return myDialog;
    }

    public interface DialogListenner {
        void onPressYes();

        void onPressYesByEdit(String content);  //编辑框时调用这个

        void onPressNo();
    }


    public void closeDialog(Dialog myDialog) {
        if (myDialog != null && myDialog.isShowing()) {
            myDialog.dismiss();
            myDialog = null;
        }
    }

}


