package com.fit.dao;

import com.fit.base.BaseCrudDao;
import com.fit.entity.WxAccount;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WxAccountDao extends BaseCrudDao<WxAccount> {
}