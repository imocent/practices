package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.WxArticle;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WxArticleDao extends BaseCrudDao<WxArticle> {
}