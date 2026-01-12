#!/usr/bin/env python
# -*- coding: utf-8 -*-
import dataclasses
import json
import random
import re

import bs4
import requests


@dataclasses.dataclass
class Question:

    def __init__(self, question=None):
        self.answer = None
        self.options = dict()
        self.question = question

    def __str__(self) -> str:
        return "{}".format(json.dumps(self, indent=4, ensure_ascii=False))


class JSYKS(object):
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

    def get_data(self, _url, curr):
        with requests.get(url=_url, headers=self.__head, timeout=self.__timeout) as response:
            response.raise_for_status()
            result_list = list()
            if response.status_code == 200:
                soup = bs4.BeautifulSoup(response.text, "html.parser")
                element_page = soup.find('div', class_='PageL')
                if element_page.contents:
                    end_page = element_page.find_all('a')[-1].text
                    if end_page == '下页' or end_page == '尾页':
                        curr += 1
                    else:
                        curr = 0
                else:
                    curr = 0
                find_all = soup.find('div', class_='ListCnt').ul.find_all('li')
                for child in find_all:
                    result = Question()
                    for line in child.a.get_text(separator='\n', strip=True).split('\n'):
                        if re.match(r'^[A-D]、', line):
                            result.options[line[0]] = str(line[2:]).strip()
                        elif line.startswith('答案：'):
                            result.answer = line.replace('答案：', '')
                        else:
                            result.question = line

                    result_list.append(result)
                return result_list, curr


if __name__ == "__main__":
    print("下载数据")
    # for i in range(1, 8):
    page = 1
    lists = list()
    while page:
        try:
            datas, page = JSYKS().get_data("https://tiba.jsyks.com/kmstk_2004_{}".format(page), page)
            print(len(datas))
            lists.extend([question.__dict__ for question in datas])
            if page == 0:
                break
        except Exception as e:
            print(e)
            page = 1
    with open('jsyks_4_{}.json'.format(9), 'w', encoding='utf-8') as f:
        json.dump(lists, f, indent=2, ensure_ascii=False)
