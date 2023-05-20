# -*- coding: utf-8 -*-
import re
import time
import traceback
import uuid

from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.wait import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import requests

debug = False
# debug = True

head_type = 'headless'


# todo 1. 如果域名不存在处理 2. 批量处理 3. 页面异常处理

class OpenMysql:
    def __init__(self, *args, **kwargs):
        """
        kwargs like host='www', user='root', passwd='123456', db='test', port=3306
        """
        # 将传进来的变量保存到self,不能在这个函数进行conn的创建
        # 因为初始化变量后不一定会执行变量的__exit__,容易造成僵尸连接
        self.mysql_config = kwargs

    def __enter__(self):
        try:
            import pymysql
        except Exception:
            raise Exception('pymysql must be installed at your environment')
        self.conn = pymysql.connect(**self.mysql_config)
        self.cursor = self.conn.cursor()
        return self

    def execute(self, sql):
        self.ping()
        self.cursor.execute(sql)
        result = self.cursor.fetchall()
        return result

    def commit(self):
        self.ping()
        self.conn.commit()

    def ping(self):
        try:
            self.conn.ping()
        except:
            self.__enter__()

    def __exit__(self, exc_type, exc_val, exc_tb):
        self.cursor.close()
        self.conn.close()
        del self


def write_database(case, module_id, project_id, user):
    """将数据写入数据库"""

    sql = "insert into liuma.{database} (id, name, level, module_id, project_id, method, path, protocol, domain_sign, description, header, body, query, rest,  create_user, update_user, create_time, update_time, status) values " \
          "('{uuid}', '{name}','p1','{module_id}','{project_id}','{method}','{path}','{protocol}','{domain_sign}','{description}','[]','{body}','[]','[]','{user}','{user}',{time},{time},'Normal')" \
        .format(uuid=str(uuid.uuid1()), name=case.name, method=case.request_method, path=case.url,
                description=case.description, protocol=case.protocol,
                domain_sign=domain_map[re.sub('[^a-zA-Z.0-9]', '', case.domain)],
                time=int(time.time() * 1000), body=case.body, database=database, module_id=module_id,
                project_id=project_id, user=user)
    print(sql)
    # 执行sql语句
    mysqlc.execute(sql)
    # 提交到数据库执行
    mysqlc.commit()


class request_data:
    login_url = 'http://47.111.97.142//server/index.php?s=/api/user/login'
    param = {
        's': '/api/user/login'
    }

    @staticmethod
    def api_param(page_id):
        return {
            'page_id': str(page_id)
        }

    data = {
        'username': '***',
        'password': '***',
        'v_code': '',
        'redirect_login': False
    }

    headers = {
        'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36'
    }


class Api_case:
    def __init__(self, description='', url='', request_method='', body='{"raw": "", "file": [], "form": [], '
                                                                       '"json": "", "type": "form-urlencoded"}',
                 domain='',
                 protocol='', name='', form_list=None):
        if form_list is None:
            form_list = []
        self.description = description
        self.url = url
        self.request_method = request_method
        self.body = body
        self.domain = domain
        self.protocol = protocol
        self.name = name
        self.form_list = form_list


def analyze_case(tmp_case: Api_case):
    """
    分析接口数据，填充接口类数据
    """
    # 对末尾的数据进行处理，去除加密的参数
    for re_i in ['/v{validParams}', '/{{signature}}']:
        tmp_case.url = tmp_case.url.replace(re_i, '')
    # 如果接口以https开头，就将protocol设置为HTTPS，去除接口中的HTTP前缀
    if tmp_case.url.startswith('https://'):
        tmp_case.protocol = 'HTTPS'
        tmp_case.url = tmp_case.url[8:]
    elif tmp_case.url.startswith('http://'):
        tmp_case.protocol = 'HTTP'
        tmp_case.url = tmp_case.url[7:]
    else:
        tmp_case.protocol = 'HTTPS'
    # 将域名写入case中
    if tmp_case.domain != '请求域名错误':
        domain_re = '[a-z]+(?=\.[a-zA-Z0-9/]+\.[a-zA-Z]+)'
        tmp_case.domain = re.findall(domain_re, tmp_case.domain)[0]
    else:
        try:
            tmp_case.domain, tmp_case.url = tmp_case.url.split('/', 1)
            tmp_case.url = '/' + tmp_case.url
            tmp_case.url = tmp_case.url.replace(' ', '')
        except:
            traceback.print_exc()
    tmp_case.domain = re.sub('.(?:alltuu|guituu).com', '', tmp_case.domain)

    # 将请求方式改为大写
    tmp_case.request_method = tmp_case.request_method.upper()
    # 如果接口名没有的话，就使用接口路径代替
    if not tmp_case.name:
        tmp_case.name = str(tmp_case.url)
    if tmp_case.form_list != []:
        tmp_case.body = '{{"raw": "", "file": [], "form": {form}, "json": "", "type": "form-urlencoded"}}'.format(
            form=str(tmp_case.form_list).replace("'", "\"").replace('True', 'true').replace('False', 'false'))
    return tmp_case


class Locator:
    table_head = (By.XPATH, '//*[text()="请求Body参数" or text()="参数"]/following-sibling::div[1]/table/thead/tr/th')
    table_body_items = (By.XPATH, '//*[text()="请求Body参数"]/following-sibling::div[1]/table/tbody/tr')
    title = (By.XPATH, '//*[@id="doc-title"]')
    request_url = (By.XPATH, '//*[@id="h5--url"]/following-sibling::ul[1]//code')
    request_method = (By.XPATH, '//*[text()="请求方式"]/following-sibling::ul[1]/li[1]')
    request_description = (By.XPATH, '//*[text()="简要描述"]/following-sibling::ul[1]/li[1]')
    request_domain = (By.XPATH, "(//h5[contains(text(),'域名')])[1]/following-sibling::ul[1]/li[1]")

    @staticmethod
    def table_body(row, col):
        return By.XPATH, '//*[text()="请求Body参数"]/following-sibling::div[1]/table/tbody/tr[{}]/td[{}]'.format(
            row + 1, col + 1)


class BaseOpt:
    def __init__(self, driver):
        self.driver = driver

    def find_element_text(self, locator) -> str:
        text = self.driver.find_element(*locator).text
        return text


def transfer_data_type(param_type):
    """修改一下接口参数的类型"""
    if param_type.upper() == 'STRING':
        return 'String'
    elif param_type.upper() == 'INT':
        return 'String'
    elif param_type.upper() == 'FLOAT':
        return 'String'
    elif param_type.upper() == 'Float':
        return 'String'
    elif param_type.upper() == 'BOOLEAN':
        return 'Boolean'
    elif param_type.upper() == 'JSONOBJECT':
        return 'JSONObject'
    elif param_type.upper() == 'JSONARRAY':
        return 'JSONArray'
    elif param_type.upper() == 'FILE':
        return 'File'
    else:
        return 'String'


def get_request_title():
    """获得接口名称方式"""
    ele = ''
    try:
        ele = web_opt.find_element_text(Locator.title)
    except:
        traceback.print_exc()
    return ele


def get_request_method():
    """获得请求方式"""
    ele = '请求url报错'
    try:
        ele = web_opt.find_element_text(Locator.request_method)
    except:
        traceback.print_exc()
    return ele


def get_request_url():
    """获得请求方式"""
    ele = '请求方式报错'
    try:
        ele = web_opt.find_element_text(Locator.request_url)
    except:
        traceback.print_exc()
    return ele


def get_request_description():
    """获得接口的描述"""
    ele = '无'
    try:
        # 获得请求方式
        ele = web_opt.find_element_text(Locator.request_description)
    except:
        traceback.print_exc()
    return ele


def get_request_domain():
    """获得接口的域名"""
    ele = '请求域名错误'
    try:
        # 获得请求方式
        ele = web_opt.find_element_text(Locator.request_domain)
    except:
        traceback.print_exc()
    return ele


# 返回请求参数体
def request_body_param():
    """获取参数信息"""
    index_param = 0
    index_required = 1
    index_type = 2
    index_description = 3
    tmp_list = []
    try:
        ele = WebDriverWait(driver, 7).until(EC.presence_of_all_elements_located(Locator.table_head))
        # 判断每一列对应的意义
        for index, item in enumerate(ele):
            if '参数' in item.text:
                index_param = index
            elif '类型' in item.text:
                index_type = index
            elif '说明' in item.text:
                index_description = index
            elif '必选' in item.text:
                index_required = index
        # 将获取到的参数写入列表中
        eles = webdriver_wait.until(EC.presence_of_all_elements_located(Locator.table_body_items))
        for index, item in enumerate(eles):
            tmp_list.append(
                {
                    "name": web_opt.find_element_text(Locator.table_body(index, index_param)),
                    "type": transfer_data_type(web_opt.find_element_text(Locator.table_body(index, index_type))),
                    "value": web_opt.find_element_text(Locator.table_body(index, index_description)),
                    "required": web_opt.find_element_text(Locator.table_body(index, index_required)),
                }
            )
    except:
        traceback.print_exc()
    return tmp_list


def get_domain_map():
    """使用sql获得域名信息"""
    sql = 'select name, id from liuma.domain_sign;'
    res = mysqlc.execute(sql)
    data = {value[0]: value[1] for value in res}
    data.update({'请求域名错误': res[0][1]})
    return data


def init_driver(url) -> webdriver:
    """初始化浏览器页面"""
    session = requests.Session()
    options = webdriver.ChromeOptions()
    if head_type == 'headless':
        options.add_argument('headless')
        options.add_argument('window-size=1920x1080')
        options.add_argument("disable-gpu")
        options.add_argument("--no-sandbox")
    chrome_driver = webdriver.Chrome(options=options)

    session.post(url=request_data.login_url, data=request_data.data, headers=request_data.headers)

    chrome_driver.get(url)
    # 将cookie赋给selenium实现登入
    chrome_driver.delete_all_cookies()
    for k, v in session.cookies.items():  # 获取requests侧的cookies
        chrome_driver.add_cookie({'name': k, 'value': v})  # 向selenium侧传入以requests侧cookies的name为键value为值的字典
    chrome_driver.get(url)
    return chrome_driver


def run(module_id, project_id, user):
    """运行主程序"""
    api_case = Api_case()
    api_case.form_list = request_body_param()
    api_case.name = get_request_title()
    api_case.request_method = get_request_method()
    api_case.url = get_request_url()
    api_case.description = get_request_description()
    api_case.domain = get_request_domain()
    api_case = analyze_case(api_case)
    # 写入数据库
    write_database(api_case, module_id, project_id, user)


if __name__ == '__main__':
    import argparse
    # dubug下直接给数据，正常下从此命令行中获取
    if debug:
        project_id = '3333f9b1-b81a-11ed-8641-52540085c262'
        module_id = '38218b71-4f74-45bd-9bc5-c40f9b197adf'
        user = 'cxy'
        url = 'http://47.111.97.142//web/#/29/2766'
    else:
        parser = argparse.ArgumentParser()
        parser.add_argument('-p', '--project_id', nargs='?', const=1, required=True)
        parser.add_argument('-m', '--module_id', nargs='?', const=1, required=True)
        parser.add_argument('-u', '--user', nargs='?', const=1, required=True)
        parser.add_argument('-ru', '--url', nargs='?', const=1, required=True)
        args = parser.parse_args()
        project_id, module_id, user, url = [getattr(args, i) for i in vars(args)]
    # 校验url是否正确
    url_re = '47\.111\.97\.142//web/#/[0-9]+/{0,}([0-9]+){0,}$'
    assert re.search(url_re, url), 'url校验失败'

    try:
        driver = init_driver(url)  # 初始浏览器页面
        webdriver_wait = WebDriverWait(driver, 0)  # 将默认的等待时间设置成0，降低等待时间
        web_opt = BaseOpt(driver)  # 初始化浏览器操作

        # 初始化数据
        mysql_config = dict(host='1.117.81.152', user='***', passwd='***')  # 数据库信息
        mysqlc = OpenMysql(**mysql_config)

        # database = 'api_copy'  # 需要写入的数据库，api为真实数据库，api_copy为测试数据库
        database = 'api'  # 需要写入的数据库，api为真实数据库，api_copy为测试数据库
        # 获得当前项目目前的所有域名信息
        domain_map = get_domain_map()

        run(module_id, project_id, user)

    except Exception as e:
        traceback.print_exc()
        raise
    finally:
        driver.quit()
