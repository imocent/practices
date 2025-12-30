package com.fit.service;

import com.fit.util.JSONUtil;
import com.fit.util.JdbcTemplateUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @AUTO
 * @Author AIM
 * @DATE 2019/6/3
 */
@Service("dict")
public class DictService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getExamSubjects() {
        String sb = "SELECT t.`ID`,t.`TITLE` as name FROM `lms_exam_subject` t";
        return JdbcTemplateUtil.queryForListMap(jdbcTemplate, sb);
    }
}