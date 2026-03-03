package com.fit.service;

import com.fit.base.BaseCrudService;
import com.fit.dao.WxUserDao;
import com.fit.entity.WxUser;
import org.springframework.stereotype.Service;

@Service
public class WxUserService extends BaseCrudService<WxUserDao, WxUser> {
}