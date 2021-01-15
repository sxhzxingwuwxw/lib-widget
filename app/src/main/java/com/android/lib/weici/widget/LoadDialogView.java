package com.android.lib.weici.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.lib.weici.widget.utils.Tools;

public class LoadDialogView extends View {
    private float playProgress;
    private Paint mPaint;
    private ValueAnimator mAnimator;
    private enum STATE{LOADING, ERROR, SUCCESS}
    private STATE state = STATE.LOADING;
    private boolean isDismiss = false;

    //线条的宽度
    private float W = 15f;
    public LoadDialogView(Context context) {
        super(context);
        init();
    }

    public LoadDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(state == STATE.LOADING){
            drawRound(canvas);
        }else if(state == STATE.ERROR){
            drawError(canvas);
        }else if(state == STATE.SUCCESS){
            drawRight(canvas);
        }
    }

    public void showLoading(){
        isDismiss = false;
        state = STATE.LOADING;
        mAnimator.setRepeatCount(-1);
        mAnimator.start();
    }

    public void showError(){
        state = STATE.ERROR;
        setAnimForErrorOrSuccess();
    }

    public void showSuccess(){
        state = STATE.SUCCESS;
        setAnimForErrorOrSuccess();
    }

    private void setAnimForErrorOrSuccess(){
        if(mAnimator.isRunning()) mAnimator.cancel();
        mAnimator.setRepeatCount(0);
        mAnimator.setDuration(1000);

        mAnimator.start();
    }

    private void init(){
        W = Tools.dip2px(4);
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(W);
        mPaint.setAntiAlias(true);
        initAnim();
    }

    private void initAnim(){
        mAnimator = ValueAnimator.ofFloat(0, 1);// 那么创建动画执行器
        mAnimator.setDuration(2000);
        mAnimator.addUpdateListener(valueAnimator -> {
            playProgress = (float) valueAnimator.getAnimatedValue();// 保存当前的动画进度值
            postInvalidate();// 刷新界面（重新调用onDraw方法重绘）
        });
    }

    private void drawRound(Canvas canvas){
        mPaint.setColor(0x55ffffff);
        float circleRadiu = canvas.getWidth()/2f;
        canvas.drawCircle(circleRadiu, circleRadiu, circleRadiu - W/2f, mPaint);
        mPaint.setColor(0xffffffff);
        Path path = new Path();
        path.addArc(new RectF(W/2f, W/2f, canvas.getWidth() - W/2f, canvas.getHeight() - W/2f), 0, -360);
        Path disPath = new Path();
        PathMeasure measure = new PathMeasure();
        measure.setPath(path, false);
        // 截取线段起到渐长渐短的效果：进度的2次方 作为起点，根号2的进度作为终点
        measure.getSegment((float) (Math.pow(playProgress, 2) * measure.getLength()), (float) (Math.sqrt(playProgress) * measure.getLength()), disPath, true);
        disPath.rLineTo(0, 0);// 解决在android4.4.4以下版本导致的无法绘制path的bug
        canvas.drawPath(disPath, mPaint);
    }

    private void drawRight(Canvas canvas){
        mPaint.setColor(0x55ffffff);
        // 绘制外侧的完整浅色圆形
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 - W/2f, mPaint);
        mPaint.setColor(0xffffffff);
        Path path = new Path();
        path.addArc(new RectF(W/2f, W/2f, canvas.getWidth() - W/2f, canvas.getHeight() - W/2f), 0, -225 + 67);
        // 画对号第一笔
        path.lineTo(canvas.getWidth() * 0.42f, canvas.getHeight() * 0.68f);
        // 画对号第二笔
        path.lineTo(canvas.getWidth() * 0.75f, canvas.getHeight() * 0.35f);
        Path disPath = new Path();
        PathMeasure measure = new PathMeasure();
        measure.setPath(path, false);
        // 最终截取的长度
        measure.getSegment(Math.max(0f, playProgress - 0.31f) * measure.getLength(), playProgress * measure.getLength(), disPath, true);
        disPath.rLineTo(0, 0);// 解决在android4.4.4以下版本导致的无法绘制path的bug
        canvas.drawPath(disPath, mPaint);
        if(playProgress == 1.0f && mOnFinishListener != null && !isDismiss){
            isDismiss = true;
            mOnFinishListener.viewDismiss();
        }
    }

    private void drawError(Canvas canvas) {
        mPaint.setColor(0x55ffffff);
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, canvas.getWidth() / 2 - W/2f, mPaint);
        mPaint.setColor(0xffffffff);
        Path path = new Path();
        path.addArc(new RectF(W/2f, W/2f, canvas.getWidth() - W/2f, canvas.getHeight() - W/2f), 90, 135);
        path.lineTo(canvas.getWidth() * 0.7f, canvas.getHeight() * 0.7f);
        Path disPath = new Path();
        PathMeasure measure = new PathMeasure();
        measure.setPath(path, false);
        float size = (float) (Math.sqrt(2f* Math.pow(canvas.getWidth()*(1f-2f*0.3f), 2)) / measure.getLength());
        measure.getSegment(Math.max(0f, playProgress - size) * measure.getLength(), playProgress * measure.getLength(), disPath, true);
        disPath.rLineTo(0, 0);// 解决在android4.4.4以下版本导致的无法绘制path的bug
        canvas.drawPath(disPath, mPaint);

        Path pathRight = new Path();
        pathRight.addArc(new RectF(W/2f, W/2f, canvas.getWidth() - W/2f, canvas.getHeight() - W/2f), 90, -135);
        pathRight.lineTo(canvas.getWidth() * 0.3f, canvas.getHeight() * 0.7f);
        Path disPathRight = new Path();
        PathMeasure measureRight = new PathMeasure();
        measureRight.setPath(pathRight, false);
        float sizeRight = (float) (Math.sqrt(2f* Math.pow(canvas.getWidth()*(1f-2f*0.3f), 2)) / measureRight.getLength());
        measureRight.getSegment(Math.max(0f, playProgress - sizeRight) * measureRight.getLength(), playProgress * measureRight.getLength(), disPathRight, true);
        disPathRight.rLineTo(0, 0);// 解决在android4.4.4以下版本导致的无法绘制path的bug
        canvas.drawPath(disPathRight, mPaint);
        if(playProgress == 1.0f && mOnFinishListener != null && !isDismiss){
            isDismiss = true;
            mOnFinishListener.viewDismiss();
        }
    }

    private OnFinishListener mOnFinishListener;
    public void setOnFinishListener(OnFinishListener listener){
        mOnFinishListener = listener;
    }

    public interface OnFinishListener{
        void viewDismiss();
    }

    public void dismiss(){
        if(mAnimator != null && mAnimator.isRunning()){
            mAnimator.cancel();
            mAnimator.end();
        }
    }
}
