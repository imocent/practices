package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.LmsQuestion;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LmsQuestionDao extends BaseCrudDao<LmsQuestion> {
}