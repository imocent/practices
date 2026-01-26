package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.LmsQuestionUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LmsQuestionUserDao extends BaseCrudDao<LmsQuestionUser> {
}