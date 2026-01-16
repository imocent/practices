package com.fit.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2026/1/13
 */
public class DictRoomUtil {
    private static Map<String, String> ofMap(String... keyValues) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            map.put(keyValues[i], keyValues[i + 1]);
        }
        return map;
    }

    // 答题室状态
    private static final Map<String, String> ROOM_STATUS = ofMap("0", "停用", "1", "新建", "2", "发布", "3", "结束", "4", "归档");
    // 答卷模式
    private static final Map<String, String> ROOM_MODE = ofMap("0", "标准答题模式", "1", "随机抽取模式", "2", "习题练习模式", "3", "只读学习模式");
    // 答题人员模式
    private static final Map<String, String> EXAMINEE_MODE = ofMap("false", "任何人员", "true", "指定人员");
    // 成绩时间类型
    private static final Map<String, String> MARK_TIME_MODE = ofMap("0", "不发布", "1", "全场阅卷后", "2", "答卷阅卷后");

    public static String getDict(Integer mode, String dictKey) {
        switch (mode) {
            case 0:
                return ROOM_STATUS.get(dictKey);
            case 1:
                return ROOM_MODE.get(dictKey);
            case 2:
                return EXAMINEE_MODE.get(dictKey);
            case 3:
                return MARK_TIME_MODE.get(dictKey);
            default:
                return "未知类型";
        }
    }
}
