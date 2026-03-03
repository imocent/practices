package com.fit.service;

import com.fit.base.BaseCrudService;
import com.fit.dao.WxMsgDao;
import com.fit.entity.WxMsg;
import org.springframework.stereotype.Service;

@Service
public class WxMsgService extends BaseCrudService<WxMsgDao, WxMsg> {
}