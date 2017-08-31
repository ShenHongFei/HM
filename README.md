# HM 槐盟(大连高校环境联盟)	网站
# http://huaimeng.org

![LOGO](http://tva2.sinaimg.cn/crop.0.0.180.180.180/a121378fjw1e8qgp5bmzyj2050050aa8.jpg)

## 如何在本地开发及调试代码
1. `git clone https://git.oschina.net/shenhongfei/HM.git` 
    将项目下载到本地或导入IDE
    
    或者下载zip包（右上角）

2. 下载服务器 [http://shenhongfei.site/HM.jar](http://shenhongfei.site/HM.jar) 

3. 将服务器`HM.jar`和`启动槐盟服务器.bat`放到HM文件夹内,运行启动脚本 `启动槐盟服务器.bat`，（JRE 1.8 Required)
    
    if 脚本输出java 不是内部或外部命令，先安装https://www.java.com/zh_CN/
    
    if 80端口被占用,listen on port 80 failed to start 原因是80端口被占用，可以运行`run7777PORT.bat` 用localhost:7777代替后面的localhost

4. 浏览器打开http://localhost

5. 浏览器中显示的网页`http://localhost/index.html`对应于本地文件夹中的`HM/web/index.html`，修改文件内容后刷新浏览器即可看到效果.

6. Postman设置HM全局变量为`localhost` 后,即可正常使用

7. 编程访问相应的后端接口写相对路径， 比如查看当前登录的用户 , Ajax中填 `url: user/info` 


#### 后端接口导入及更新（重导入）
Postman：

https://www.getpostman.com/collections/948411cb62444b74997f

## 如何提交修改

参照 [https://git-scm.com/book/zh/v2](https://git-scm.com/book/zh/v2) 选择合适的方法提交到码云，即可同步到 http://shenhongfei.site 

