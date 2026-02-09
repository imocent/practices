#!/usr/bin/env python
# -*- coding: utf-8 -*-
import dataclasses
import json
import random
import re

import bs4
import requests

from mysql_pool import MySQLPool

db = MySQLPool(database='fit')
sql = """INSERT INTO `lms_question_learn`(`SUBJECT_ID`,`SUBJECT_NAME`,`AUTHOR`,`TITLE`,`CONTENT`,`NOTES`) VALUES(4,'语文',%s, %s, %s, %s)"""


class Web(object):
    def __init__(self):
        self.__headers = [
            "Mozilla/5.0 (Windows NT 5.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 7.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.116 Safari/537.36",
            "Mozilla/5.0 (Windows NT 8.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3440.126 Safari/537.36",
            "Mozilla/5.0 (Windows NT 9.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.3440.166 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.3282.186 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36",
            "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10.5; en-US; rv:1.9.2.15) Gecko/20110303 Firefox/3.6.15",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
        ]
        self.__timeout = 30
        self.__head = {
            'User-Agent': random.choice(self.__headers),
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.9,en;q=0.8',
            'Accept-Encoding': 'gzip, deflate',
            'Connection': 'close',
            'Content-Type': 'text/html; charset=UTF-8',
        }

    def get_pages(self, _url):
        result_list = list()
        with requests.get(url=_url, headers=self.__head, timeout=self.__timeout) as response:
            response.raise_for_status()
            if response.status_code == 200:
                soup = bs4.BeautifulSoup(response.text, "html.parser")
                find_all = soup.find('ul', class_='main-data').find_all('li')
                for child in find_all:
                    result_list.append(child.find('a').get('href'))
        return result_list

    def get_html(self, _page):
        with requests.get(url=_page, headers=self.__head, timeout=self.__timeout) as resp:
            resp.raise_for_status()
            resp.encoding = resp.apparent_encoding
            if resp.status_code == 200:
                soup = bs4.BeautifulSoup(resp.text, "html.parser")
                find_author = soup.find('div', class_='author-simple-info').find_all('a')
                author = "{}·{}".format(find_author[0].get_text(), find_author[1].get_text())
                title = soup.find('div', class_='gushi-info').find('h1').get_text()
                content = soup.find('div', class_='shicicontent').get_text()
                notes = ""
                try:
                    notes = soup.find('div', class_='shici-content').get_text()
                except:
                    pass
                print("{}-{}".format(author, title))
                with db.get_connection() as conn:
                    with conn.cursor() as cursor:
                        cursor.execute(sql, (author, title, content, notes))
                        conn.commit()


if __name__ == "__main__":
    url = "https://www.gushiji.cc/gushi/p{}.html"
    web = Web()
    for i in range(1, 197):
        try:
            datas = web.get_pages(url.format(i))
            for data in datas:
                print("https://www.gushiji.cc{}".format(data))
                try:
                    web.get_html("https://www.gushiji.cc/{}".format(data))
                except Exception as e:
                    print(e)
                    pass
        except:
            pass
