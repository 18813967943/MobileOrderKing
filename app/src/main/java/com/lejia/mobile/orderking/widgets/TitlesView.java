package com.lejia.mobile.orderking.widgets;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lejia.mobile.orderking.R;
import com.lejia.mobile.orderking.utils.TextUtils;

public class TitlesView extends View {

	private static final int DEFAULT_NUMBER = 5; // 默认主题数量
	private static final int DEFAULT_TEXT_SIZE = 16; // 默认字体大小
	private static final int DEFAULT_SPLIT_PADDING = 8; // 分割线内边距

	private int titlesWidth; // 宽度
	private int titlesHeight; // 高度

	private int number; // 主题数量
	private int color; // 背景色
	private String[] titles; // 主题
	private boolean isSplitor; // 分割线

	private Paint normalTextPaint; // 普通颜色画笔
	private Paint blueTextPaint; // 蓝色颜色画笔
	private Paint splitorPaint; // 分割线画笔

	private int position = -1; // 选中位置，默认选中

	private OnTitlesStatusListener onTitlesStatusListener; // 反馈信息接口

	/**
	 * 获取属性
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		if (attrs != null) {
			TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);
			number = typeArray.getInt(R.styleable.TitleBar_buttonNum, DEFAULT_NUMBER);
			color = typeArray.getColor(R.styleable.TitleBar_backgroundColor, Color.WHITE);
			isSplitor = typeArray.getBoolean(R.styleable.TitleBar_buttonSplitor, false);
			String arrayId = typeArray.getString(R.styleable.TitleBar_buttonTitles);
			if (arrayId != null && arrayId.contains("@") && arrayId.startsWith("@")) {
				titles = getResources().getStringArray(Integer.parseInt(arrayId.substring(1)));
			}
			typeArray.recycle();
		} else {
			number = DEFAULT_NUMBER; // 默认主题数量
			color = Color.WHITE; // 默认背景色
			titles = new String[] { "title1", "title2", "title3", "title4", "title5" }; // 默认主题
			isSplitor = false; // 默认不需要分割线
		}
		// 初始化画笔
		normalTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		normalTextPaint.setAntiAlias(true);
		normalTextPaint.setColor(0xFF000000);
		normalTextPaint.setTextSize(TextUtils.sp2px(getContext(), DEFAULT_TEXT_SIZE));

		blueTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		blueTextPaint.setAntiAlias(true);
		blueTextPaint.setColor(0xff6ebaf4);
		blueTextPaint.setTextSize(TextUtils.sp2px(getContext(), DEFAULT_TEXT_SIZE));

		splitorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		splitorPaint.setAntiAlias(true);
		splitorPaint.setColor(0xFFEAEBEE);
		splitorPaint.setStrokeWidth(3f);
	}

	public TitlesView(Context context) {
		super(context);
		initAttrs(context, null);
	}

	public TitlesView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitlesView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context, attrs);
	}

	/**
	 * 绑定信息反馈接口
	 *
	 * @param onTitlesStatusListener
	 */
	public void setOnTitlesStatusListener(OnTitlesStatusListener onTitlesStatusListener) {
		this.onTitlesStatusListener = onTitlesStatusListener;
	}

	/**
	 * 设置主题
	 *
	 * @param titles
	 */
	public void setTitles(String[] titles) {
		this.titles = titles;
		this.number = this.titles.length;
		this.position = -1;
		invalidate();
	}

	/**
	 * 设置字体颜色
	 *
	 * @param color
	 */
	public void setNormalTextColor(int color) {
		this.normalTextPaint.setColor(color);
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		titlesWidth = getDefaultSize(0, widthMeasureSpec);
		titlesHeight = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(titlesWidth, titlesHeight);
	}

	/**
	 * 获取当前选择位置
	 *
	 * @return
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * 设置选择位置
	 *
	 * @param position
	 */
	public void setPosition(int position) {
		this.position = position;
		if (this.position == -1)
			return;
		if (onTitlesStatusListener != null) {
			onTitlesStatusListener.onItemTitleClick(position, titles[position], false);
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			// 绘制背景色
			Paint bgColorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			bgColorPaint.setAntiAlias(true);
			bgColorPaint.setColor(color);
			canvas.drawRect(0, 0, titlesWidth, titlesHeight, bgColorPaint);
			if (titles != null) {
				// 绘制顶部横线
				canvas.drawLine(0, 0, titlesWidth, 0, splitorPaint);
				// 计算子项宽高
				int avgWidth = titlesWidth / number;
				// 计算字体高度
				FontMetrics fm = normalTextPaint.getFontMetrics();
				float textHeight = fm.bottom - fm.top;
				// 计算字体所在y坐标
				float y = (titlesHeight + textHeight / 2) / 2;
				for (int i = 0; i < number; i++) {
					String title = titles[i];
					// 计算字体宽度
					float textWidth = normalTextPaint.measureText(title);
					// 计算主题绘制位置
					float x = i * avgWidth + (avgWidth - textWidth) / 2;
					// 绘制所有主题
					if (i == position) {
						canvas.drawText(title, x, y, blueTextPaint);
					} else {
						canvas.drawText(title, x, y, normalTextPaint);
					}
					// 绘制分割线
					if (isSplitor) {
						if (i > 0) {
							float sx = avgWidth * i;
							float sy = titlesHeight - DEFAULT_SPLIT_PADDING;
							canvas.drawLine(sx, DEFAULT_SPLIT_PADDING, sx, sy, splitorPaint);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				// 计算子项宽高
				int avgWidth = titlesWidth / number;
				for (int i = 0; i < number; i++) {
					// 计算点击位置
					if (x >= avgWidth * i && x < (i + 1) * avgWidth) {
						position = i;
						if (onTitlesStatusListener != null) {
							onTitlesStatusListener.onItemTitleClick(position, titles[i], true);
						}
						invalidate();
					}
				}
				// 拦截触摸事件，避免进入到底部布局中触发事件
				return true;
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * @author heke 点击事件回调接口
	 */
	public interface OnTitlesStatusListener {
		void onItemTitleClick(int position, String title, boolean isfromUser);
	}

}
