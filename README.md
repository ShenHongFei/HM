# HM 槐盟(大连高校环境联盟)	网站
# http://shenhongfei.site

![LOGO](http://tva2.sinaimg.cn/crop.0.0.180.180.180/a121378fjw1e8qgp5bmzyj2050050aa8.jpg)

##如何在本地开发及调试代码
1. `git clone https://git.oschina.net/shenhongfei/HM.git` 
    将项目下载到本地或导入IDE
    
    或者下载zip包（右上角）

2. 下载服务器 [http://shenhongfei.site/HM.jar](http://shenhongfei.site/HM.jar) 

3. 将服务器`HM.jar`放到HM根目录,运行启动脚本 `启动槐盟服务器.bat`，（JRE 1.8 Required)

4. 浏览器打开http://localhost

5. 浏览器中显示的网页对应于**`HM/web/`**中的网页(前端工作目录)，修改文件内容后刷新浏览器即可看到效果.

6. Postman设置HM全局变量为`localhost` 后,即可正常使用

7. 编程访问相应的后端接口写相对路径， 比如查看当前登录的用户 , Ajax中填 `url: user/info` 

## ```后端会更新服务器 HM.jar，请适时下载覆盖本地服务器```

####后端接口导入及更新（重导入）
Postman：

https://www.getpostman.com/collections/948411cb62444b74997f

##如何提交修改

参照 [https://git-scm.com/book/zh/v2](https://git-scm.com/book/zh/v2) 选择合适的方法提交到码云，即可同步到 http://shenhongfei.site 

