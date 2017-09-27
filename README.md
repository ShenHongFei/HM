# 槐盟(大连高校环境联盟)网站
# http://huaimeng.org

![LOGO](http://tva2.sinaimg.cn/crop.0.0.180.180.180/a121378fjw1e8qgp5bmzyj2050050aa8.jpg)

## 前端开发指南
1. `git clone https://github.com/ShenHongFei/HM.git`
    将项目下载到本地或导入IDE

    或者下载zip包（右上角）

2. 下载服务器 [http://shenhongfei.site/HM.jar](http://shenhongfei.site/HM.jar) 

3. 确保服务器 `HM.jar` 、 `启动槐盟服务器.bat` 启动脚本 和 `web文件夹` 在同一个文件夹内,运行启动脚本 `启动槐盟服务器.bat`，（JRE 1.8 Required)。

    if 脚本输出 java不是内部或外部命令，先安装JRE https://www.java.com/zh_CN/

4. 浏览器打开 http://localhost:7777 可以看到网站。

5. 浏览器中显示的网页 `http://localhost:7777/index.html` 对应于 `web文件夹` 中的 `index.html`，以此类推，

    `http://localhost:7777/xxx.html` 对应于 `web文件夹` 中的 `xxx.html`

    修改文件内容后刷新浏览器即可看到效果。

6. Postman设置HM全局变量为`localhost:7777` 后，即可正常发送请求、查看响应。

7. 编程访问相应的后端接口写相对路径， 比如查看当前登录的用户 , Ajax中填 `url: user/info` 


#### 后端接口导入及更新（重导入）
Postman：

https://www.getpostman.com/collections/948411cb62444b74997f

## 后端开发指南

1.  cd /path/to/HM

2.  `gradlew bootRun` 编译及运行服务器，浏览器打开 http://localhost:7777 可看到网站。

    或者

     `gradlew bootRepackage` 编译生成jar包

## 如何提交修改

参照 [https://git-scm.com/book/zh/v2](https://git-scm.com/book/zh/v2) 选择合适的方法提交到GitHub

