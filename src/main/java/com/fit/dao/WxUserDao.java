package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.WxUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WxUserDao extends BaseCrudDao<WxUser> {
}