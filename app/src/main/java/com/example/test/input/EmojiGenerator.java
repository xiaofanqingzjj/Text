package com.example.test.input;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author samzhan
 * @date 2015年8月20日
 * <p/>
 * 表情生成器
 */
public class EmojiGenerator {

    private static EmojiGenerator instance;

    /**
     * 每页的表情数
     */
    private static final int DEFAULT_PAGE_SIZE = 23;

    /**
     * 表情配置文件名前缀
     */
    private static final String DEFAULT_EMOJI_PROPERTIES_FILE = "emoji";

    public static final String RESOURCE_TYPE_DRAWABLE = "drawable";


    /**
     * 所有的表情列表
     */
    private List<Emoji> mEmojiList = new ArrayList<Emoji>();

    private List<List<Emoji>> mEmojis = new ArrayList<List<Emoji>>();

    private Map<String, Emoji> mEmojiMap = new HashMap<String, Emoji>();

    @SuppressLint("UseSparseArrays")
    private Map<Integer, EmojiPropertis> mEmojiPro = new HashMap<Integer, EmojiPropertis>();

    private Context mContext;

    private boolean hasInited;

    private EmojiGenerator(Context context) {
        mContext = context.getApplicationContext();
    }

    public static EmojiGenerator getInstance(Context context) {
        synchronized (EmojiGenerator.class) {
            if (instance == null) {
                instance = new EmojiGenerator(context);
                instance.init();
            }
            return instance;
        }
    }

    /**
     * 初始化游戏表情信息
     *
     * @return 表情生成器
     */
    public EmojiGenerator init() {
        InputStream is = null;
        try {
            if (hasInited) { // 已经读取过表情配置，直接返回
                return this;
            }

            // 没有读取过表情配置，直接初始化
            is = mContext.getResources().getAssets().open(DEFAULT_EMOJI_PROPERTIES_FILE);

            BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
            String emojiPro = br.readLine();

            if(emojiPro != null) {
                JSONArray emojiJsonArray = new JSONArray(emojiPro);

                int totalEmoji = emojiJsonArray.length();

                for (int i = 0; i < totalEmoji; i++) {
                    JSONObject obj = emojiJsonArray.getJSONObject(i);
                    String fileName = obj.optString("fileName");
                    int resId = mContext.getResources().getIdentifier(fileName, RESOURCE_TYPE_DRAWABLE,  mContext.getPackageName());
                    if (resId == 0) {
                        continue;
                    }


                    Emoji emoji = new Emoji();
                    emoji.f_name = obj.optString("name");
                    emoji.f_showName = obj.optString("showName");

                    emoji.f_fileName = fileName;
                    emoji.f_resId = resId;

                    mEmojiList.add(emoji);

                    // 添加到表情库，用于聊天信息检索匹配表情字段
                    mEmojiMap.put(fileName, emoji);
                }
            }
            br.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try{
                if(is != null)
                    is.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }



        int pageCount = (int) Math.ceil(mEmojiList.size() / DEFAULT_PAGE_SIZE + 0.1);
        for (int i = 0; i < pageCount; i++) {
            mEmojis.add(getPageData(i));
        }
        hasInited = true;

        return this;
    }


    public List<List<Emoji>> getGameEmoji() {
        return mEmojis;
    }

    public List<Emoji> getPageData(int page) {
        int startIndex = page * DEFAULT_PAGE_SIZE;
        int endIndex = (page + 1) * DEFAULT_PAGE_SIZE;
        if (endIndex > mEmojiList.size()) {
            endIndex = mEmojiList.size();
        }

        List<Emoji> list = new ArrayList<Emoji>();
        list.addAll(mEmojiList.subList(startIndex, endIndex));
        if (list.size() < DEFAULT_PAGE_SIZE) {
            for (int i = list.size(); i < DEFAULT_PAGE_SIZE; i++) {
                Emoji object = new Emoji();
                list.add(object);
            }
        }
        if (list.size() == DEFAULT_PAGE_SIZE) {
            Emoji object = new Emoji();
            object.f_resId = 0; //R.drawable.face_del_ico_dafeult;
            list.add(object);
        }

        return list;
    }

    public Emoji getEmojiByName(String emojiName) {
        return mEmojiMap.get(emojiName);
    }

    public boolean isEmojiExist(String emojiName) {
        return mEmojiMap.containsKey(emojiName);
    }

    public Map<String, Emoji> getEmojiMap() {
        return mEmojiMap;
    }

    private class EmojiPropertis {
        private List<Emoji> emojiList;
        private List<List<Emoji>> emojis;
        private Map<String, Emoji> emojiMap;
    }

    public List<Emoji> getAllEmojiList() {
        return mEmojiList;
    }

}
