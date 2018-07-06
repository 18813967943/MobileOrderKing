package com.lejia.mobile.orderking.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.lejia.mobile.orderking.utils.TextUtils;

/**
 * Author by HEKE
 *
 * @time 2018/7/6 14:50
 * TODO: 验证码控件
 */
public class AuthcodeView extends View {

    // 使用数字验证码
    private static final char[] CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    // 使用字符颜色
    private static final int[] COLORS = {0xFFFFDEAD,0xFF97FFFF, 0xFFFFDAB9, 0xFF54FF9F, 0xFF00FF7F,
            0xFF6495ED, 0xFF000080, 0xFF00CD66, 0xFF00BFFF, 0xFFC0FF3E, 0xFFFFF68F, 0xFFADFF2F, 0xFFFF6A6A,
            0xFFD2691E, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFF00FF, 0xFF00FFFF, 0xFF9932CC,
            0xFFEE2C2C, 0xFFA020F0};

    private int codeWidth;
    private int codeHeight;

    // 编码
    private int[] codes;
    // 画笔
    private Paint paint;

    private void initAttrs() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true);
        paint.setAntiAlias(true);
        codes = new int[]{7, 6, 7, 9, 8, 3};
    }

    public AuthcodeView(Context context) {
        super(context);
        initAttrs();
    }

    public AuthcodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs();
    }

    public AuthcodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs();
    }

    // 获取随机颜色
    private int randomColor() {
        int position = (int) (Math.random() * COLORS.length);
        return COLORS[position];
    }

    // 获取随机字体大小
    private int randomTextSize() {
        return (int) (Math.random() * 7) + 12;
    }

    // 获取随机字体间距
    private int randomTextMargin() {
        return (int) (Math.random() * 6) + 4;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        codeWidth = getDefaultSize(0, widthMeasureSpec);
        codeHeight = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(codeWidth, codeHeight);
    }

    // 获取当前验证码
    public int[] getCodes() {
        return codes;
    }

    // 设置验证码
    public void setCodes(int[] codes) {
        this.codes = codes;
        invalidate();
    }

    // 本地随机验证码
    public void loadCodes() {
        for (int i = 0; i < codes.length; i++) {
            int p = (int) (Math.random() * CHARS.length);
            codes[i] = Integer.parseInt("" + CHARS[p]);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (codes != null && codes.length > 0) {
                // 绘制背景
                paint.setColor(0xFFFFFFE0);
                canvas.drawRect(0, 0, codeWidth, codeHeight, paint);
                // 绘制字符
                int p = 0; // 字符位置
                for (int i = 0; i < codes.length; i++) {
                    String v = "" + codes[i];
                    int color = randomColor();
                    int txtsize = randomTextSize();
                    int margin = randomTextMargin();
                    float textWidth = paint.measureText(v);
                    paint.setTextSize(TextUtils.dip2px(getContext(), txtsize));
                    paint.setColor(color);
                    // 字体高度
                    Paint.FontMetrics fm = paint.getFontMetrics();
                    float txtHeight = fm.bottom - fm.top;
                    // 绘制字符
                    canvas.drawText(v, p + margin, codeHeight - txtHeight * 0.75f + margin / 2, paint);
                    // 移动位置
                    p += (2 * margin + textWidth);
                }
                // 复制随机遮盖线段
                int lineSize = (int) (Math.random() * 3 + 1);
                for (int i = 0; i < lineSize; i++) {
                    int x1 = (int) (Math.random() * (codeWidth / 10));
                    int y1 = (int) (Math.random() * codeHeight);
                    int x2 = (int) (codeWidth - Math.random() * (codeWidth / 2));
                    int y2 = (int) (Math.random() * codeHeight);
                    int color = randomColor();
                    paint.setColor(color);
                    canvas.drawLine(x1, y1, x2, y2, paint);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
