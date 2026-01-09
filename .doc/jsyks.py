#!/usr/bin/env python
# -*- coding: utf-8 -*-
import bs4
import random
import re
import requests


class JSYKS(object):
    def __init__(self, target):
        self.__headers = [
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; …) Gecko/20100101 Firefox/61.0",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
            "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10.5; en-US; rv:1.9.2.15) Gecko/20110303 Firefox/3.6.15",
        ]
        self.__url = re.search(r"http[s]?://[.\w-]*(:\d{,8})?((?=/)|(?!/))", target).group()
        self.__target_url = target
        self.__timeout = 30
        self.__head = {
            'Content-Type': 'text/html; charset=UTF-8',
            'User-Agent': random.choice(self.__headers),
            'Connection': 'close'
        }

    def get_data(self):
        with requests.get(url=self.__target_url, headers=self.__head, timeout=self.__timeout) as response:
            response.raise_for_status()
            if response.status_code == 200:
                # Content > div.ListCnt
                soup = bs4.BeautifulSoup(response.text, "html.parser")
                find_all = soup.find('div', class_='ListCnt').ul.find_all('li')
                result = {
                    "question": "",
                    "options": {},
                    "answer": ""
                }
                for child in find_all:
                    for line in child.a.get_text(separator='\n', strip=True).split('\n'):
                        print(line)
                        result["question"] = line[0]
                        if line.startswith('答案：'):
                            result["answer"] = line.replace('答案：', '')
                        elif line.startswith('A、'):
                            result["options"]["A"] = line.replace('A、', '')
                        elif line.startswith('B、'):
                            result["options"]["B"] = line.replace('B、', '')
                        elif line.startswith('C、'):
                            result["options"]["C"] = line.replace('C、', '')
                        elif line.startswith('D、'):
                            result["options"]["D"] = line.replace('D、', '')


if __name__ == "__main__":
    print("下载数据")
    page = 1
    urls = "https://tiba.jsyks.com/kmytk_150{}_{}".format("1", page)
    try:
        JSYKS(urls).get_data()
        page += 1
    except:
        pass
