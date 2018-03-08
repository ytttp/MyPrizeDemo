package cn.wangxiao.crm.myprizedemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by Auser on 2018/3/2.
 */

public class MyPrizeView extends View {
    private static final String IN_PATH = "/Android/pic/";
    private List<MyPrizeBean> myPrizeList;
    /**
     * 当前控件的宽高
     */
    private int minWidthAndHeight;
    /**
     * 当前控件的半径
     */
    private int radius;
    /**
     * 写字和画圆弧的画笔
     */
    Paint mTextPaint;
    Paint mBgPaint;

    /**
     * 当前角度
     */
    int initAngle = 0;
    /**
     * 每一个奖品占的角度，例如 六个奖品，sweepAngle=360/6
     */
    int sweepAngle;

    private Canvas mCanvas;

    public MyPrizeView(Context context) {
        this(context, null);
    }

    public MyPrizeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPrizeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.i("ytt", "MyPrizeView: onSizeChanged");
        //获取宽高
        minWidthAndHeight = Math.min(w, h);
        radius = minWidthAndHeight / 2;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        if (myPrizeList != null && myPrizeList.size() > 0) {
            mTextPaint.setTextSize(radius / 10);
            //每一个奖品占有的角度
            sweepAngle = 360 / myPrizeList.size();
            for (int i = 0; i < myPrizeList.size(); i++) {
                RectF rectF = new RectF(0, 0, minWidthAndHeight, minWidthAndHeight);
                if (i % 2 == 0) {
                    mTextPaint.setColor(Color.rgb(255, 133, 132));
                } else {
                    mTextPaint.setColor(Color.rgb(254, 104, 105));
                }
                //绘制圆弧
                canvas.drawArc(rectF, initAngle, sweepAngle, true, mTextPaint);

                //绘制文字
                mTextPaint.setColor(Color.WHITE);
                drawCurrentText(rectF, initAngle, sweepAngle, myPrizeList.get(i).text, canvas);
                //绘制图片
                if (!TextUtils.isEmpty(myPrizeList.get(i).address)) {
                    if (myPrizeList.get(i).myBitmap != null) {
                        drawCurrentPicture(initAngle, myPrizeList.get(i).myBitmap);
                    } else {
                        myPrizeList.get(i).myBitmap = BitmapFactory.decodeFile(myPrizeList.get(i).address);
                        drawCurrentPicture(initAngle, myPrizeList.get(i).myBitmap);
                        Log.i("ytt", "地址:" + myPrizeList.get(i).address);
                    }
                } else {
                    //加载网络图片
                    loadingPicture(i, myPrizeList.get(i).icon);
                }
                initAngle += sweepAngle;
            }
        }
        initAngle = initAngle % 360;
    }

    /**
     * 绘制文字
     */
    private void drawCurrentText(RectF rectF, int angle, int sweepAngle, String text, Canvas canvas) {
        Path path = new Path();
        path.addArc(rectF, angle, sweepAngle);
        float textWidth = mTextPaint.measureText(text);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        //圆弧的水平偏移 先计算出弧长 减去文字长度/2的长度就是距离两边的距离
        float hOffset = (float) (2*Math.PI*radius * sweepAngle/360-textWidth)/2;
        //圆弧的垂直偏移,文字的高度
        float vOffset = Math.abs(fontMetrics.top - fontMetrics.bottom);
        canvas.drawTextOnPath(text, path, hOffset, vOffset, mTextPaint);
    }

    /**
     * 绘制图片
     */
    private void drawCurrentPicture(int startAngle, Bitmap myBitmap) {
        // 设置图片的宽度
        int imgWidth = minWidthAndHeight / (myPrizeList.size() <= 3 ? myPrizeList.size() + 2 : myPrizeList.size());

        float angle = (float) ((360 / myPrizeList.size() / 2 + startAngle) * (Math.PI / 180));

        int x = (int) (radius + minWidthAndHeight / 2 / 2 * Math.cos(angle));
        int y = (int) (radius + minWidthAndHeight / 2 / 2 * Math.sin(angle));

        int addInt = imgWidth / 2;

        RectF rect = new RectF(x - addInt, y - addInt, x + addInt, y + addInt);

        if (myBitmap != null && !myBitmap.isRecycled()) {
            mCanvas.drawBitmap(myBitmap, null, rect, null);
        }
    }

    /***
     * 加载网络图片
     * */
    private void loadingPicture(final int position, final String address) {
        if (TextUtils.isEmpty(address)) {
            return;
        }
        /**
         * 设置ImageSize和DisplayImageOptions避免图片太大
         * */
        ImageLoader.getInstance().loadImage(address, new ImageSize(50, 50), DisplayImageOptions.createSimple(), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                Log.i("ytt", "图片加载onLoadingCancelled  " + position);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                Log.i("ytt", "图片加载完成onLoadingComplete " + position);
                if (myPrizeList != null && myPrizeList.size() > position) {
                    myPrizeList.get(position).address = saveBitmap(loadedImage);
                    ViewCompat.postInvalidateOnAnimation(MyPrizeView.this);
                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                Log.i("ytt", "图片加载onLoadingFailed " + position);
            }

        });
    }

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 保存bitmap到本地
     *
     * @param
     * @param mBitmap
     * @return
     */
    public String saveBitmap(Bitmap mBitmap) {
        String savePath;
        File filePic;
        savePath = getContext().getApplicationContext().getFilesDir().getAbsolutePath() + IN_PATH;
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    public void setListData(List<MyPrizeBean> myPrizeList) {
        this.myPrizeList = myPrizeList;
        invalidate();
    }
    /**
     * 开始转动
     */
    public void startRotate(int position) {
        //算出未转动时的角度
        int currentAngle = 360 - (position - 1) * sweepAngle + 270 - sweepAngle / 2 + 360 * 2;
        ValueAnimator animtor = ValueAnimator.ofInt(initAngle % 360, currentAngle + initAngle / 360 * 360);
        animtor.setInterpolator(new AccelerateDecelerateInterpolator());
        animtor.setDuration(2500);
        animtor.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int updateValue = (int) animation.getAnimatedValue();
                initAngle = (updateValue % 360 + 360) % 360;
                ViewCompat.postInvalidateOnAnimation(MyPrizeView.this);
            }
        });
        animtor.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }
        });
        animtor.start();
    }

}
