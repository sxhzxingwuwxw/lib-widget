package com.android.lib.media.player;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weici on 2020/12/24.
 * Describe:
 */
public class MediaItem {

    public int pid;
    public String catalog_name;//":"开篇",
    public int id;
    public String title;//":"欢迎来到",
    public int try_flag;//":1,
    public int try_time;//":0,
    public int total_time;//":300
    public boolean children;

    public List<MediaItem> parse(String data){
        JSONArray array;
        try {
            array = new JSONArray(data);
        } catch (JSONException e) {
            return null;
        }
        List<MediaItem> list = new ArrayList<>();
        for(int i=0; i<array.length(); i++){
            JSONObject item = array.optJSONObject(i);
            MediaItem mediaItem = new MediaItem();
            mediaItem.children = false;
            mediaItem.pid = item.optInt("id");
            mediaItem.catalog_name = item.optString("catalog_name");
            list.add(mediaItem);

            JSONArray children = item.optJSONArray("children");
            if(children != null && children.length()>0){
                for(int j=0; j<children.length(); j++){
                    JSONObject child = children.optJSONObject(j);
                    MediaItem mediaItem1 = new MediaItem();
                    mediaItem1.children = true;
                    mediaItem1.pid = mediaItem.pid;
                    mediaItem1.id = child.optInt("id");
                    mediaItem1.title = child.optString("title");
                    mediaItem1.try_flag = child.optInt("try_flag");
                    mediaItem1.try_time = child.optInt("try_time");
                    mediaItem1.total_time = child.optInt("total_time");
                    list.add(mediaItem1);
                }
            }
        }
        return list;
    }
}
