package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.WxMsgNews;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WxMsgNewsDao extends BaseCrudDao<WxMsgNews> {
}