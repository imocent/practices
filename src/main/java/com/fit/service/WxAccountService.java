package com.fit.service;

import com.fit.base.BaseCrudService;
import com.fit.dao.WxAccountDao;
import com.fit.entity.WxAccount;
import org.springframework.stereotype.Service;

@Service
public class WxAccountService extends BaseCrudService<WxAccountDao, WxAccount> {
}