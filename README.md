<h2>Docker-java实现应用和组件的远程发布</h2>

博客链接： [Docker-java实现应用和组件的远程发布](https://blog.csdn.net/weixin_42405670/article/details/128704104?spm=1001.2014.3001.5501)

<h4>1. 需求背景</h4>

博主在某个项目上有个功能，我们会根据各种软件编写最佳实践编的shell脚本。而对于编写的shell脚本需要进行测试，如果是在虚拟机或服务器，测试完成后可能会有残留文件或配置，考虑到各种因素，最好是在一个开箱即用，用完就丢的环境进行测试，这时候，使用Docker就是一个非常不错的方案。

<h4>2. 对docker的具体操作需要涉及以下：</h4>

- 实现远程连接、安全连接；

- 创建容器、加载镜像、拉取镜像、删除镜像、移除容器、启动容器、停止容器等；

- 执行命令、实现docker 执行脚本文件，并携带参数；

- 实现路径挂载.

<h4>3.对镜像环境的操作涉及：</h4>

- 通过目录映射和挂载，将需要测试脚本和安装包上传到本地的目录

- 在远程docker镜像执行脚本进行安装软件，这里的脚本来自挂载和映射的文件路径下
