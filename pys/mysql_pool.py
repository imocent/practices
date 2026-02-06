#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
@File   :pymysql.py
@Author :Imocence
@date   :2026/1/10
"""
import json
from contextlib import contextmanager

import pymysql
from dbutils.pooled_db import PooledDB


class MySQLPool:
    _instance = None

    def __new__(cls, *args, **kwargs):
        """单例模式"""
        if not cls._instance:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self, **config):
        # 基本配置
        pool_config = {
            'creator': pymysql,
            'host': config.get('host', 'localhost'),
            'user': config.get('user', 'root'),
            'password': config.get('password', 'root'),
            'database': config.get('database', ''),
            'port': config.get('port', 3306),
            'charset': 'utf8mb4',
        }
        pool_config.update(config)
        # 创建连接池
        self.pool = PooledDB(**pool_config)

    @contextmanager
    def get_connection(self):
        """从连接池获取连接"""
        connection = self.pool.connection()
        try:
            yield connection
        finally:
            connection.close()

    def insert_and_get_id(self, _sql, data=()):
        """插入数据并返回ID"""
        with self.get_connection() as connection:
            try:
                with connection.cursor() as cursor:
                    # 执行插入
                    cursor.execute(_sql, data)
                    # 获取插入的ID
                    insert_id = cursor.lastrowid
                    # 提交事务
                    connection.commit()
                    print(f"插入成功，ID: {insert_id}")
                    return insert_id
            except Exception as e:
                print(f"插入失败: {e}")
                connection.rollback()
                raise


if __name__ == "__main__":
    db = MySQLPool(database='fit')
    with db.get_connection() as conn:
        with conn.cursor() as cursor:
            cursor.execute("SELECT DATABASE()")
            db_name = cursor.fetchone()
            print(f"当前数据库: {db_name}")
    j_dict = dict()
    j_dict[1] = "第1章：道路交通安全法律、法规和规章"
    j_dict[2] = "第2章：交通信号"
    j_dict[3] = "第3章：安全行车、文明驾驶基础知识"
    j_dict[4] = "第4章：机动车驾驶操作相关基础知识"
    j_dict[5] = "第5章：货车专用"
    j_dict[6] = "第6章：客车专用"
    j_dict[7] = "第7章：摩托车专用试题"
    j_dict[8] = "第1章：违法行为综合判断与案例分析"
    j_dict[9] = "第2章：安全行车常识"
    j_dict[10] = "第3章：常见交通标志、标线和交通手势辨识"
    j_dict[11] = "第4章：驾驶职业道德和文明驾驶常识"
    j_dict[12] = "第5章：恶劣气候和复杂道路条件下驾驶常识"
    j_dict[13] = "第6章：紧急情况下避险常识"
    j_dict[14] = "第7章：交通事故救护及常见危化品处置常识"
    j_dict[15] = "第8章：摩托车专用"
    j_dict[16] = "第9章：客货车专用"
    for k, v in j_dict.items():
        with open("jsyks_{}.json".format(k), 'r', encoding='utf-8') as f:
            data_list = json.load(f)  # 使用json.load()，不是json.loads()
        for item in data_list:
            options = item.get('options', {})
            try:
                sql = """
                INSERT INTO `lms_question` (`EXAM_ROOM_ID`, `EXAM_ROOM_NAME`, `MOLD`, `CONTENT`)
                VALUES (%s, %s, %s, %s)
                """
                mold = 0 if len(options) > 0 else 2
                params_question = (k, v, mold, item.get('question', ''))
                # 插入数据并获取ID
                question_id = db.insert_and_get_id(sql, params_question)
                aql = "insert into `lms_question_answer`(`QUESTION_ID`,`CONTENT`,`VERIFY`)VALUES(%s, %s, %s)"
                if mold == 2:
                    params_answer = (question_id, '对', 1 if item.get('answer') == '对' else 0)
                    db.insert_and_get_id(aql, params_answer)
                    params_answer = (question_id, '错', 1 if item.get('answer') == '错' else 0)
                    db.insert_and_get_id(aql, params_answer)
                else:
                    for key, value in options.items():
                        params_answer = (question_id, value, 1 if item.get('answer') == key else 0)
                        db.insert_and_get_id(aql, params_answer)

                print("插入成功，ID: {}".format(id))
            except Exception as e:
                print(f"插入失败，错误: {e}")
                print(f"失败的数据: {item}")
                continue
