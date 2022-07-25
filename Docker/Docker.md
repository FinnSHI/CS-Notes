# Docker

## 1 Docker原理

### 1.1 Docker架构图

![image-20211030145847957](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108132.png)

<br />

### 1.2 Docker三要素

- 仓库 Registry

  ​	保存了多个镜像

- 镜像 image

- 容器 Container

  ​	image创建的运行实例。容器只包含业务运行所需的runtime环境。

> 镜像和容器可以类比成java中的类和实例

<br />

### 1.3 Docker原理

#### 1.3.1 Docker底层原理

- Docker是个Client-Server结构的系统
- 守护进程运行在主机上，通过Socket从客户端访问。守护进程从Client接收命令并管理运行在主机上的容器
- 容器，是一个运行时环境

![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108887.png)

<br />

#### 1.3.2 Docker与传统虚拟化方式的不同

- 传统虚拟机技术是虚拟出一套硬件后，在其上运行一个完整操作系统，在该系统上再运行所需应用进程；
- 而容器内的应用进程直接运行于宿主的内核，容器内没有自己的内核，而且也没有进行硬件虚拟。因此容器要比传统虚拟机更为轻便；
- 每个容器之间互相隔离，每个容器有自己的文件系统 ，容器之间进程不会相互影响，能区分计算资源。

![image-20211030153002120](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108457.png)

<br />

## 2 Image

### 2.1 image概述

- 镜像是一种轻量级、可执行的独立软件包，用来打包软件运行环境和基于运行环境开发的软件，它包含运行某个软件所需的所有内容，包括代码、运行时、库、环境变量和配置文件。

- Docker的镜像实际上由**一层一层的文件系统组成**，这种层级的文件系统叫UnionFS。

> UnionFS（联合文件系统）：
>
> - Union文件系统（UnionFS）是一种分层、轻量级并且高性能的文件系统，它支持<font color="red">对文件系统的修改作为一次提交来一层层的叠加</font>，同时可以将不同目录挂载到同一个虚拟文件系统下(unite several directories into a single virtual filesystem)。Union 文件系统是 Docker 镜像的基础。镜像可以通过分层来进行继承，基于基础镜像（没有父镜像），可以制作各种具体的应用镜像
> - 特性：一次同时加载多个文件系统，但从外面看起来，只能看到一个文件系统，联合加载会把各层文件系统叠加起来，这样最终的文件系统会包含所有底层的文件和目录



<br />

### 2.2 Image原理

#### 2.2.1 镜像加载原理

- Docker镜像的最底层是bootfs。

  > **bootfs **(boot file system)：主要包含bootloader和kernel。
  >
  > - bootloader主要是引导加载kernel, Linux刚启动时会加载bootfs文件系统，在。这一层与我们典型的Linux/Unix系统是一样的，包含boot加载器和内核。当boot加载完成之后整个内核就都在内存中了，此时内存的使用权已由bootfs转交给内核，此时系统也会卸载bootfs。
  >
  > **rootfs** (root file system) ，在bootfs之上。包含的就是典型 Linux 系统中的 /dev, /proc, /bin, /etc 等标准目录和文件。rootfs就是各种不同的操作系统发行版，比如Ubuntu，Centos等等。
  >
  > - 对于一个精简的OS，rootfs可以很小，只需要包括最基本的命令、工具和程序库就可以了，因为底层直接用Host的kernel，自己只需要提供 rootfs 就行了。由此可见对于不同的linux发行版, bootfs基本是一致的, rootfs会有差别, 因此不同的发行版可以公用bootfs。

  ![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108559.png)

<br />

- 平时我们安装进虚拟机的CentOS都是好几个G，为什么docker这里才200M？

  对于一个精简的OS，rootfs可以很小，只需要包括最基本的命令、工具和程序库就可以了，因为底层直接用宿主机的kernel，自己只需要提供 rootfs 就行了。由此可见对于不同的linux发行版, bootfs基本是一致的, rootfs会有差别, 因此不同的发行版可以公用bootfs。

  

<br />

#### 2.2.2 分层的镜像

以我们的pull为例，在下载的过程中我们可以看到docker的镜像好像是在一层一层的在下载

![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108647.png)

<br />

<img src="https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108936.png" alt="image-20211030193533650" style="zoom:50%;" />

<br />

> 为什么 Docker 镜像要采用这种分层结构呢?
>
> - 可以共享资源
>   - <font color="red">有多个镜像都从相同的 base 镜像构建而来，那么宿主机只需在磁盘上保存一份base镜像</font>
>   - 同时<font color="red">内存中也只需加载一份 base 镜像</font>，就可以为<font color="red">所有容器</font>服务了。
>   - 镜像的每一层都可以被<font color="red">共享</font>。

<br />

### 2.3 镜像特点

- Docker镜像都是只读的
- 当容器启动时，一个新的可写层被加载到镜像的顶部。这一层通常被称作“容器层”，“容器层”之下的都叫“镜像层”。

<br />

### 2.4 常用命令

#### 2.4.1 docker images

List images

- `docker images -a`：列出所有镜像，包含中间印象层
- `docker images -q`：显示当前镜像的id
- `docker images -qa`：列出所有镜像的id
- `docker images -digests`: 显示镜像摘要信息
- `docker images -digests --no-trunc`: 显示镜像完整的摘要信息
- `docker images prune`：可以清理无用的images

<br />

#### 2.4.2 docker search

Search the Docker Hub for images

- `docker search -filter stars=300 tomcat `：列出STARS不小于300的镜像

<br />

#### 2.4.3 docker pull

Pull an image or a repository from a registry

- `docker pull xxxx` 等价于 `docker pull xxxx:latest`

<br />

#### 2.4.4 docker rmi

remove an or more images

- `docker rmi xxxx  `
- `docker rmi -f xxxx  `：Force removal of an image
- `docker rmi -f xxxx oooo  `：Force removal of more images
- `docker rmi -f $(docker images -qa)  `：docker images -qa列出的id全传递给rmi参数并删掉

<br/>

### 2.5 commit操作

`docker commit`：提交容器副本使之成为一个新的镜像。可以创建自己的镜像

- `docker commit -m=“提交的描述信息” -a=“作者” 容器ID 要创建的目标镜像名:[标签名]`

<br />

### 2.6 push操作

`dockerpush`：把image提交到docker hub的repository中。

1. `docker login`：登录账号密码
2. `docker tag [imageName] finn5842/[repoName]:[tagName]`: 指定一个tag。比如docker tag nginx finn5842/nginx:http1.1
3. `docker push [tagName]`: 提交。比如：docker push finn5842/nginx:http1.1

![image-20211104135402360](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202111041354426.png)

<br />



## 3 Container

### 3.1 常用命令

#### 3.1.1 docker run

新建并启动容器

`docker run [OPTIONS] IMAGE [COMMAND]` 

​	**--name="name"**：容器名称

​	**-d**：启动守护式容器。即后台运行容器，并返回容器ID

​	**-i**：以交互模式运行容器，通常与-t一起使用

​	**-t**：为容器重新分配一个伪输入终端，通常与-i一起使用

> ​	docker run -it --name="myMySQL" mysql

​	**-P**：随机分配端口号

​	**-p**：指定端口号

>  	-p 10060:8080  前面是docker暴露给外界的端口，后面是tomcat服务的接口

<br />

- `docker run -d xxxx`：以后台模式启动一个容器⭐

  > docker机制：
  >
  > -  docker容器后台运行时，必须有个前台进程
  > - 容器运行的命令除非要求一直挂起，如运行top, tail，否则就会自动退出
  >
  > 
  >
  > 问题：通过docker ps -a 进行查看时， 发现容器自动退出。
  >
  > > 原因：所以，容器以后台模式运行时，如果没有发现前台进程，就是立即自动退出
  >
  > > 解决办法：运行的容器以前台运行的方式进行。

<br />

#### 3.1.2 docker ps

列出container

- `docker ps`：列出正在运行的容器

- `docker ps -a`：列出所有**正在运行**和**历史上运行过**的容器

- `docker ps -l`：列出最近创建的容器

- `docker ps -q`：列出正在运行的容器编号

  > docker ps -aq 就是列出所有容器的编号

- `docker ps -n xx`：列出最近创建的xx个容器

<br />

#### 3.1.3 退出容器

- `exit`
- `ctrl+shift+z`

<br />

#### 3.1.4 暂时离开容器

离开容器，但不关闭容器

- `ctrl+p+q`

<br />

#### 3.1.5 启动容器

- `docker start xxxx`：启动容器
- `docker restart xxxx`：重启容器

<br />

#### 3.1.6 停止容器

- `docker stop xxxx`
- `docker kill`：强制停止容器

<br />

#### 3.1.7 删除容器

- `docker rm xxxx`：删除已停止的容器

- `docker rm -f xxxx`：强制删除容器
- `docker rm -f $(docker ps -aq)`：强制删除所有容器
- `docker ps -aq|xargs docker rm`：强制删除所有容器（把docker ps -aq传给xargs，并作为下一个命令的参数）

<br />

#### 3.1.8 重新进入容器

- `docker attach xxxx`：直接进入容器启动命令终端，不会启动新的进程
- `docker exec -it xxxx`：在容器中打开终端，并启动新的进程。默认是以`docker exec -it xxxx /bin/bash`的方式交互
  - `docker exec -it xxxx ls -l /tmp`：直接对xxxx容器进行ls -l /tmp操作并返回，并不进入容器

<br />

#### 3.1.9 容器日志

![image-20211030173507422](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108977.png)

<br />

#### 3.1.10 容器内部细节

`docker inspect xxxx`

<br />

#### 3.1.11 容器内容保存到主机

`docker cp xxxx:容器目录 主机目录`：复制容器内容到主机

<br />

#### 3.1.12 docker cp

**从容器->宿主机**

容器的opt目录下的aa.txt文件拷贝到宿主机的/usr/目录下

```typescript
docker cp mycontainer:/opt/aa.txt /usr/
```

**主机->容器**

```groovy
docker cp /usr/aa.txt mycontainer:/opt
```

<br />



### 3.2 常用指令总结

![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231109784.png)

<br />

## 4 Volume

容器数据卷

### 4.1 容器数据卷

- 用来数据持久化
- 容器之间可以共享持久化的数据

> Docker容器产生的数据，如果不通过docker commit生成新的镜像，使得数据做为镜像的一部分保存下来，那么当容器删除后，数据自然也就没有了。

总结：

**容器的持久化 + 容器间或容器和宿主机间继承和数据共享**

<br>

### 4.2 添加数据卷

#### 4.2.1 直接命令添加

- `docker run -it -v /宿主机绝对路径目录:/容器内目录 镜像名` 

- **举例：**

  - **docker run -it -v /宿主机目录:/容器内目录 centos /bin/bash**

    ![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108294.png)

  - **查看数据卷是否挂载成功**：`docker inspect xxxx`

    ![在这里插入图片描述](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110302038205.png)

  

  - **容器和宿主机之间实现了数据共享**

    ![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231108184.png)

    

  - **容器停止退出后，主机修改后数据仍然同步**

    ![在这里插入图片描述](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202110302040535.png)

<br />

- `docker run -it -v /宿主机绝对路径目录:/容器内目录:ro 镜像名`：**命令(带权限)**。此时容器内数据卷只能读，不能写

<br>

#### 4.2.2 DockerFile添加

- `VOLUME["/dataVolumeContainer","/dataVolumeContainer2","/dataVolumeContainer3"]`：可在Dockerfile中使用VOLUME指令来给镜像添加一个或多个数据卷

  **示例：**

  ```dockerfile
  # volume test
  FROM centos
  VOLUME ["/dataVolumeContainer1","/dataVolumeContainer2"]
  CMD echo "finished--------create successfully"
  CMD /bin/bash
  ```

  - build后生成image

    ![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231109347.png)

  - run

    ![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231109010.png)

  - 查看

    ![在这里插入图片描述](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231109328.png)

    ![image-20220723111039654](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231110708.png)

  - 主机对应默认地址

    ![image-20220723111050921](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231110986.png)

    

  > 注意：
  >
  > - 如果Docker挂载主机目录时，访问出现错误：Docker cannot open directory .: Permission denied
  >   解决办法：在挂载目录后多加一个–privileged=true参数即可

<br>

### 4.3 数据卷容器

#### 4.3.1 概述

​	命名的容器挂载数据卷，其它容器通过挂载这个(父容器)实现数据共享，挂载数据卷的容器，称之为数据卷容器

![image-20220723111100006](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231111073.png)

#### 4.3.2 容器间数据共享

- 先启动一个父容器dc01

- `docker run -it --name dc02 --volumes-from dc01 zzyy/centos`：dc02继承自dc01 （image：zzyy/centos）

  

<br>

## 5 DockerFile

### 5.1 DockerFile概述

​	Dockerfile是用来构建Docker image的文件，是由一系列命令和参数构成的脚本

#### 构建三步骤

1. 编写Dockerfile文件
2. docker build
3. docker run

<br>

### 5.2 DockerFile构建

#### 5.2.1 Dockerfile内容基础知识

- 每条保留字指令都必须为**大写**字母且后面要**跟随至少一个参数**

- 指令按照**从上到下**，顺序执行

- 每条指令都会创建一个新的镜像层，并对镜像进行提交

  > `#` 表示注释

#### 5.2.2 Docker执行DockerFile流程

1. Docker从<u>基础image</u>运行一个container
2. 执行一条指令并对container作出修改
3. Docker提交容器到一个新的image层
4. Docker再基于刚提交的image运行一个新container
5. 执行DockerFile中的下一条指令
6. ...

#### 5.2.3 总结

从应用软件的角度来看，DockerFile、Image与Container分别代表软件的三个不同阶段:

- Dockerfile是软件的原材料
- Image是软件的交付品
- Container则可以认为是软件的运行态。

Dockerfile面向开发，Docker镜像成为交付标准，Docker容器则涉及部署与运维，三者缺一不可，合力充当Docker体系的基石。

![image-20220723111128882](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231111946.png)

描述：

- Dockerfile：Dockerfile定义了进程需要的一切东西。Dockerfile涉及的内容包括执行代码或者是文件、环境变量、依赖包、运行时环境、动态链接库、操作系统的发行版、服务进程和内核进程(当应用进程需要和系统服务和内核进程打交道，这时需要考虑如何设计namespace的权限控制)等等;
- Docker镜像：在用Dockerfile定义后，用`docker build`指令产生一个Docker镜像。运行Docker镜像，创建容器;
- Docker容器：容器是提供服务的。

### 5.3 DockerFile保留字指令

- FROM :基础镜像，当前新镜像是基于哪个镜像的
- MAINTAINER :镜像维护者的姓名和邮箱地址
- RUN : 容器构建时需要运行的命令
- EXPOSE : 当前容器对外暴露出的端口
- WORKDIR : 指定在创建容器后，终端默认登陆的进来工作目录，一个落脚点
- ENV : 用来在构建镜像过程中设置环境变量
- ADD : 将宿主机目录下的文件拷贝进镜像且ADD命令会自动处理URL和解压tar压缩包
- COPY ： 类似ADD，拷贝文件和目录到镜像中。将从构建上下文目录中 <源路径> 的文件/目录复制到新的一层的镜像内的 <目标路径> 位置
- VOLUME ： 容器数据卷，用于数据保存和持久化工作
- CMD ：指定一个容器启动时要运行的命令。Dockerfile 中可以有多个 CMD 指令，但只有最后一个生效，CMD 会被 docker run 之后的参数替换
- ENTRYPOINT ： 指定一个容器启动时要运行的命令，ENTRYPOINT 的目的和 CMD 一样，都是在指定容器启动程序及参数。
- ONBUILD ： 当构建一个被继承的Dockerfile时运行命令，父镜像在被子继承后父镜像的onbuild被触发

<br>

## 6 案例

### 6.1 Base镜像

​	Docker Hub 中 99% 的镜像都是通过在 base 镜像 scratch 中安装和配置需要的软件构建出来的.

![image-20220723111118647](https://finn-typora.oss-cn-shanghai.aliyuncs.com/pic/202207231111709.png)

<br>

### 6.2 自定义image

#### 6.2.1 自定义镜像myTomcat

1. 创建myTomcat文件夹：

   ```
   mkdir -p /finn/mydockerfile/myTomcat
   ```

   `mkdir -p`是递归创建目录，如果上级文件夹不存在，会一起创建出来

2. 在上述目录下`touch c.txt`

3. 将jdk和tomcat安装的压缩包拷贝进上一步目录







<br>

## 7 Docker Compose

### 7.1 概述

​	使用一个 `Dockerfile` 模板文件，可以让用户很方便的定义一个单独的应用容器。然而，在日常工作中，经常会碰到需要多个容器相互配合来完成某项任务的情况。例如要实现一个 Web 项目，除了 Web 服务容器本身，往往还需要再加上后端的数据库服务容器，甚至还包括负载均衡容器等。

`	Compose` 恰好满足了这样的需求。它允许用户通过一个单独的 `docker-compose.yml` 模板文件（YAML 格式）来定义一组相关联的应用容器为一个项目（project）。

- `Compose` 中有两个重要的概念：
  - 服务 (`service`)：一个应用容器，实际上可以包括运行多个相同镜像的容器实例。
  - 项目 (`project`)：由一组关联的应用容器组成的一个完整业务单元，在 `docker-compose.yml` 文件中定义。

- `Compose` 的默认管理对象是项目，通过子命令对项目中的一组容器进行便捷地生命周期管理。

- `Compose` 项目由 Python 编写，实现上调用了 Docker 服务提供的 API 来对容器进行管理。因此，只要所操作的平台支持 Docker API，就可以在其上利用 `Compose` 来进行编排管理。

> 手册资料：
>
> https://docs.docker.com/compose/

<br>

### 7.2 yaml

​	yaml是docker-compose的核心。它一共有三层

```yaml
#3层

version: '' #版本
services: #服务
    服务1:
    #服务配置
    
    
#其他配置 网络/卷、全局规则
volumes:
networks:
configs:
```



### 7.3 部署上线

```
docker-compose up
```



### 7.4 重新部署上线

```
docker-compose up --build
```



### 7.5 下线

```
docker-compose down
```

