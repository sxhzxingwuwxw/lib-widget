package com.android.lib.media.player;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.lib.media.R;
import com.android.lib.media.utils.LibMediaUtils;
import com.android.lib.weici.widget.ExpandIconView;

import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

/**
 * Created by weici on 2020/12/2.
 * Describe:付费视频列表
 */
public class ViewMediaStudyList extends ScrollView {

    private PayInfoModel payInfoApi;
    private View mCurrentItem;
    private List<MediaItem> mList;
    public ViewMediaStudyList(Context context) {
        super(context);
    }

    public void setData(String data, PayInfoModel payInfoApi) {
        mList = new MediaItem().parse(data);
        this.payInfoApi = payInfoApi;
        if (mList == null || mList.size() == 0) return;
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(VERTICAL);

        LinearLayout childLayout = null;
        for (int i = 0; i < mList.size(); i++) {
            MediaItem item = mList.get(i);
            if (item.children) {
                boolean isChildLast;
                if(i + i < mList.size()) isChildLast = !mList.get(i + 1).children;
                else isChildLast = true;
                View childItem = getItemChildView(item, isChildLast);
                if(childLayout != null)
                childLayout.addView(childItem);
            } else {
                View itemView = createItemView(item);
                childLayout = itemView.findViewById(R.id.child_content);
                layout.addView(itemView);
            }
        }
        layout.setPadding(0, 0, 0, LibMediaUtils.dip2px(80));
        this.addView(layout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private View createItemView(MediaItem mediaItem) {
        View itemLayout = View.inflate(getContext(), R.layout.view_media_study_list_layout, null);
        //View titleBottomLine = itemLayout.findViewById(R.id.line_bottom);
        ExpandIconView expandIconView = itemLayout.findViewById(R.id.left_arrow);
        expandIconView.switchState(false);
        LibMediaUtils.setTextView(itemLayout, R.id.title, mediaItem.catalog_name);

        LinearLayout childLayout = itemLayout.findViewById(R.id.child_content);
        itemLayout.findViewById(R.id.title_layout).setOnClickListener(v -> {
            boolean show = childLayout.getVisibility() == VISIBLE;
            childLayout.setVisibility(show ? GONE : VISIBLE);
            expandIconView.switchState(true);
        });
        return itemLayout;
    }

    private View getItemChildView(MediaItem child, boolean isLast) {
        View childItem = View.inflate(getContext(), R.layout.view_media_study_list_item_layout, null);
        String title = child.title, subTitle = "";
        if(!TextUtils.isEmpty(title) && title.contains("——")){
            String[] titles = title.split("——");
            title = titles[0];
            subTitle = titles[1];
        }
        LibMediaUtils.setTextView(childItem, R.id.item_title, title);
        LibMediaUtils.setTextView(childItem, R.id.item_title_sub, subTitle);
        //String itemTime = "";
        //if(child.total_time > 0) itemTime = "时长 " + TimeUtils.formatDateTime(child.total_time);
        //ViewUtils.setTextViewInvisible(childItem, R.id.item_time, itemTime);
        int id = child.id;
        int tryFlag = child.try_flag;
        int try_time = child.try_time;
        String tryTimeText = try_time == 0 ? "" : (LibMediaUtils.formatDateTime(try_time));
        TextView textView = childItem.findViewById(R.id.item_text);
        setPlayState(textView, child.pid, id, tryFlag == 1 ? ("可试看" + tryTimeText) : "");
        childItem.setTag(child.pid + "-" + id);
        if (isLast) {
            childItem.findViewById(R.id.line).setVisibility(GONE);
            childItem.findViewById(R.id.line_bottom).setVisibility(VISIBLE);
        }

        childItem.setOnClickListener(v -> {
            if (mListener != null) {
                int time = tryFlag == 0 ? -1 : try_time;
                mListener.onItemClick(child.pid, child.id, time, false);
                LibMediaUtils.reportMediaItem(getContext(), child.title, child.id);

                setItemBg(childItem);
            }
        });
        return childItem;
    }

    private void setPlayState(TextView textView, int pid, int id, String text) {
        if (payInfoApi.isPay()) {
            getPlayText(textView, pid, id);
        } else {
            LibMediaUtils.setTextView(textView, text);
        }
    }

    private void getPlayText(TextView textView, int pid, int id) {
        long time = LibMediaUtils.getPlayRecord(getContext(), payInfoApi.getCourseId(), pid, id);

        String text = getText(time);
        if ("已学完".contentEquals(text) || "未学习".contentEquals(text)) {
            textView.setTextColor(Color.parseColor("#FFB8B8B8"));
        } else{
            textView.setTextColor(Color.parseColor("#00B400"));
        }
        LibMediaUtils.setTextView(textView, text);
    }

    private String getText(long time) {
        if (time == -1) {
            return "已学完";
        }
        time = time / 1000;
        if (time > 0) {
            if(time / 60 == 0) return "已学习1分钟";
            return "已学习" + (time / 60) + "分钟";
        }
        return "未学习";
    }

    private void setItemBg(View item) {
        setItemNormalBg();
        mCurrentItem = item;
        if(mCurrentItem != null)
        mCurrentItem.setBackgroundColor(Color.parseColor("#FFF6F3"));
    }

    public void setItemNormalBg(){
        if (mCurrentItem != null)
            mCurrentItem.setBackgroundResource(R.drawable.lib_media_item_selector);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        // time 可看时长 -1 不可看 0 可以完全试看 大于0 可试看的时长
        void onItemClick(int pid, int id, long time, boolean retry);
    }

    public void onPayChangeListener() {
        for (int i = 0; i < mList.size(); i++) {
            MediaItem item = mList.get(i);
            if (item == null || !item.children) continue;
            View view = findViewWithTag(item.pid + "-" + item.id);
            TextView textView = view.findViewById(R.id.item_text);
            textView.setTextColor(Color.parseColor("#FFB8B8B8"));
            textView.setText("未学习");
            textView.setVisibility(VISIBLE);
        }
    }

    public void onPlayCompleteListener(int pid, int id, long duration) {
        View view = findViewWithTag(pid + "-" + id);
        TextView textView = view.findViewById(R.id.item_text);
        LibMediaUtils.i("是否完成:" + duration);
        boolean find = false;
        for (int i = 0; i < mList.size(); i++) {
            MediaItem item = mList.get(i);
            if (item == null || !item.children) continue;
            if (pid == item.pid && id == item.id) {
                textView.setTextColor(Color.parseColor("#FFB8B8B8"));
                textView.setText("已学完");
                find = true;
                continue;
            }

            if (find && mListener != null) {
                LibMediaUtils.i("播放下一个");
                view = findViewWithTag(item.pid + "-" + item.id);
                setItemBg(view);
                mListener.onItemClick(item.pid, item.id, item.try_time,false);
                break;
            }
        }
    }

    public void updateItemDuration() {
        if (mCurrentItem == null) return;
        View view = mCurrentItem;
        TextView textView = view.findViewById(R.id.item_text);
        String tag = (String) view.getTag();
        String[] ids = tag.split("-");
        getPlayText(textView, Integer.parseInt(ids[0]), Integer.parseInt(ids[1]));
    }
}
