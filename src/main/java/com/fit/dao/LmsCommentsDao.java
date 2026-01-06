package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.LmsComments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LmsCommentsDao extends BaseCrudDao<LmsComments> {
}