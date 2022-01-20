# Nginx

类似Tomcat这样的Web服务器，运行的Web应用程序通常都是**业务系统**，因此，这类服务器也被称为**应用服务器**。应用服务器并不擅长处理静态文件，也不适合直接暴露给用户。通常，我们在生产环境部署时，总是使用类似Nginx这样的服务器充当**反向代理和静态服务器**，只有动态请求才会放行给应用服务器，所以，部署架构如下：

```ascii
             ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐

             │  /static/*            │
┌───────┐      ┌──────────> file
│Browser├────┼─┤                     │    ┌ ─ ─ ─ ─ ─ ─ ┐
└───────┘      │/          proxy_pass
             │ └─────────────────────┼───>│  Web Server │
                       Nginx
             └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘    └ ─ ─ ─ ─ ─ ─ ┘
```

使用Nginx配合Tomcat服务器，可以充分发挥Nginx作为网关的优势<u>，既可以高效处理静态文件，也可以把https、防火墙、限速、反爬虫等功能放到Nginx中</u>，使得我们自己的WebApp能专注于业务逻辑。



## HTTPS

### 宿主机目录结构

/home/finn/nginx

nginx

​	-web

​	-etc

​		<font color="red">nginx.conf</font>

​		--conf.d		

​				<font color="red">default.conf</font>

​		--ssl

​				<font color="red">shifan-web.pem</font>

​				<font color="red">shifan-web.key</font>



### 默认Nginx目录

1. **/etc/nginx/conf.d/default.conf**

   ```bash
   server {
       listen       80;
       listen  [::]:80;
       server_name  localhost;
   
       #access_log  /var/log/nginx/host.access.log  main;
   
       location / {
           root   /usr/share/nginx/html;
           index  index.html index.htm;
       }
   
       #error_page  404              /404.html;
   
       # redirect server error pages to the static page /50x.html
       #
       error_page   500 502 503 504  /50x.html;
       location = /50x.html {
           root   /usr/share/nginx/html;
       }
   
       # proxy the PHP scripts to Apache listening on 127.0.0.1:80
       #
       #location ~ \.php$ {
       #    proxy_pass   http://127.0.0.1;
       #}
   
       # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
       #
       #location ~ \.php$ {
       #    root           html;
       #    fastcgi_pass   127.0.0.1:9000;
       #    fastcgi_index  index.php;
       #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
       #    include        fastcgi_params;
       #}
   
       # deny access to .htaccess files, if Apache's document root
       # concurs with nginx's one
       #
       #location ~ /\.ht {
       #    deny  all;
       #}
   }
   
   ```

   

2. **/etc/nginx/nginx.conf**

   ```bash
   user  nginx;
   worker_processes  auto;
   
   error_log  /var/log/nginx/error.log notice;
   pid        /var/run/nginx.pid;
   
   
   events {
       worker_connections  1024;
   }
   
   
   http {
       include       /etc/nginx/mime.types;
       default_type  application/octet-stream;
   
       log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                         '$status $body_bytes_sent "$http_referer" '
                         '"$http_user_agent" "$http_x_forwarded_for"';
   
       access_log  /var/log/nginx/access.log  main;
   
       sendfile        on;
       #tcp_nopush     on;
   
       keepalive_timeout  65;
   
       #gzip  on;
   
       include /etc/nginx/conf.d/*.conf;
   }
   ```

   



### 宿主机配置

1. nginx.conf

   ```bash
   #运行nginx的用户
   user  nginx;
   #启动进程设置成和CPU数量相等
   worker_processes  1;
   
   #全局错误日志:
   #错误日志级别：常见的错误日志级别有[debug | info | notice | warn | error | crit | alert | emerg]，级别越高记录的信息越少。
   #PID文件的位置
   error_log  /var/log/nginx/error.log debug;
   pid        /var/run/nginx.pid;
   
   #工作模式及连接数上限
   events {
           #单个后台work进程最大并发数设置为1024
       worker_connections  1024;
   }
   
   
   http {
           #设定mime类型
       include       /etc/nginx/mime.types;
       default_type  application/octet-stream;
   
           #设定日志格式
       log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                         '$status $body_bytes_sent "$http_referer" '
                         '"$http_user_agent" "$http_x_forwarded_for"';
   
       access_log  /var/log/nginx/access.log  main;
   
       sendfile        on;
       #tcp_nopush     on;
   
           #设置连接超时的事件
       keepalive_timeout  65;
   
           #开启GZIP压缩
       #gzip  on;
   
       include /etc/nginx/conf.d/*.conf;
   }
   ```

   

2. default.conf

   ```bash
   server {
       listen    80;       #侦听80端口，如果强制所有的访问都必须是HTTPs的，这行需要注销掉
       listen    443 ssl;
       server_name  www.shifan-web.xyz;             #域名
   
       # 增加ssl
       #ssl on;        #如果强制HTTPs访问，这行要打开
       ssl_certificate /ssl/shifan-web.pem;
       ssl_certificate_key /ssl/shifan-web.key;
   
       ssl_session_cache    shared:SSL:1m;
       ssl_session_timeout  5m;
   
        # 指定密码为openssl支持的格式
        ssl_protocols  SSLv2 SSLv3 TLSv1.2;
   
        ssl_ciphers  HIGH:!aNULL:!MD5;  # 密码加密方式
        ssl_prefer_server_ciphers  on;   # 依赖SSLv3和TLSv1协议的服务器密码将优先于客户端密码
   
        # 定义首页索引目录和名称
        location / {
           root   /usr/share/nginx/html;
           index  index.html index.htm;
        }
   
       #重定向错误页面到 /50x.html
       error_page   500 502 503 504  /50x.html;
       location = /50x.html {
           root   /usr/share/nginx/html;
       }
   }
   ```



### ZeroSSL申请SSL并配置

#### Installing SSL Certificate on NGINX

**Before You Start**

After downloading your certificate, you should have a ZIP containing the following certificate files:

- certificate.crt
- ca_bundle.crt
- private.key



1. **Upload Certificate Files**

   First and foremost, you will need to upload the certificate files above (certificate.crt, ca_bundle.crt and private.key) to your NGINX server in a directory of your choice.

2. **Merge .crt Files**

   NGINX requires all .crt files to be merged in order to allow SSL installation. You will need to run the following command in order to merge your certificate.crt and ca_bundle.crt files.

   ```
   $ cat certificate.crt ca_bundle.crt >> certificate.crt
   ```

3. **Edit Virtual Hosts File**

   Next, you will need to find your NGINX virtual hosts file and add some code to point it to your new SSL certificate. As soon as you have opened your virtual hosts file, create a copy of the existing non-secure server module and paste it below the original.

```bash
server {
    listen    80;       #侦听80端口，如果强制所有的访问都必须是HTTPs的，这行需要注销掉
    listen    443 ssl;
    server_name  www.shifan-web.xyz;             #域名

    # 增加ssl
    #ssl on;        #如果强制HTTPs访问，这行要打开
    ssl_certificate /ssl/shifan-web.pem;
    ssl_certificate_key /ssl/shifan-web.key;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

     # 指定密码为openssl支持的格式
     ssl_protocols  SSLv2 SSLv3 TLSv1.2;

     ssl_ciphers  HIGH:!aNULL:!MD5;  # 密码加密方式
     ssl_prefer_server_ciphers  on;   # 依赖SSLv3和TLSv1协议的服务器密码将优先于客户端密码

     # 定义首页索引目录和名称
     location / {
        root   /usr/share/nginx/html;
        index  index.html index.htm;
     }

    #重定向错误页面到 /50x.html
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
}
```



### 启动Nginx

```bash
docker run --detach \
        --name nginxhttps \
        -p 443:443 \
        -p 80:80 \
        -v /home/finn/nginx/web:/usr/share/nginx/html:rw \
        -v /home/finn/nginx/etc/nginx.conf:/etc/nginx/nginx.conf/:rw \
        -v /home/finn/nginx/etc/conf.d/default.conf:/etc/nginx/conf.d/default.conf:rw \
        -v /home/finn/nginx/logs/:/var/log/nginx/:rw \
        -v /home/finn/nginx/ssl/:/ssl/:rw \
        -d nginx
```



### 结果

![image-20211108180937027](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131304587.png)

![image-20211108180917119](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131304203.png)





## HTTP2

### 升级到HTTP2

default.cnf

```bash
server {
    listen    80;       
    listen    443 ssl http2; #添加http2
    server_name  www.shifan-web.xyz;

    ssl on;        #打开。强制HTTPs访问
    ssl_certificate /ssl/shifan-web.pem;
    ssl_certificate_key /ssl/shifan-web.key;

    ssl_session_cache    shared:SSL:1m;
    ssl_session_timeout  5m;

    ssl_protocols  SSLv2 SSLv3 TLSv1.2;

    ssl_ciphers  HIGH:!aNULL:!MD5;  
    ssl_prefer_server_ciphers  on;

    location / {
    root   /usr/share/nginx/html;
    index  index.html index.htm;
    }

    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
    root   /usr/share/nginx/html;
    }
}
```





## HTTP3

### 安装gcc和g++

gcc

```
yum install gcc
```

g++

```
yum install gcc-c++
```

检查

![image-20211109113536255](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131304882.png)

![image-20211109113557661](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131304144.png)

### 编译安装cmake

先安装

If OK:

```
yum install -y openssl openssl-devel
```

else：

```
yum install openssl-devel
```

Then:

```
wget https://github.com/Kitware/CMake/releases/download/v3.18.2/cmake-3.18.2.tar.gz
tar -zxvf cmake-3.18.2.tar.gz
cd cmake-3.18.2/
./bootstrap
gmake
make
make install
```

检查

![image-20211109113700950](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131304390.png)

### **编译安装perl**

在`www.cpan.org/src`查看版本

![image-20211109131859254](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131147132.png)



下载最新版

```
wget https://www.cpan.org/src/5.0/perl-5.34.0.tar.gz --no-check-certificate
tar -xzf perl-5.34.0.tar.gz
cd perl-5.34.0
```

```
./Configure -des -Dprefix=/usr/local/perl
```

```
make -j 8
```

![img](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131306929.png)

```
make test
```

![image-20211109123720997](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131306908.png)

```
make install
```

![image-20211109131544067](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131305188.png)

查看perl版本

```
perl -v
```

![image-20211109131729658](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131306361.png)

### **安装golang**

从`https://golang.org/dl`下载最新版

![image-20211109133604265](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131306474.png)

下载压缩包

```
wget https://golang.org/dl/go1.17.3.linux-amd64.tar.gz
```

```
tar -zxvf go1.17.3.linux-amd64.tar.gz -C /usr/local
# 修改系统默认的go文件
ln -s /usr/local/go/bin/go /usr/bin/go
```

在`/etc/profile`里添加配置

```
export GOROOT=/usr/local/go
export GOBIN=$GOROOT/bin
export PATH=$PATH:$GOBIN
export GOPATH=/home/gopath
```

### **编译安装新版gcc**

`http://ftp.gnu.org/gnu/gcc/`

```
wget http://ftp.gnu.org/gnu/gcc/gcc-11.2.0/gcc-11.2.0.tar.gz
tar -zxvf gcc-11.2.0.tar.gz
cd gcc-11.2.0/
# 解压完成后需要下载四个依赖，我们执行脚本即可直接下载，服务器网络不好的同学也可以手动下载
./contrib/download_prerequisites
# 创建一个专门用来编译的目录
mkdir gcc-build-11.2.0
cd gcc-build-11.2.0/
# 这里需要对c和c++进行支持，为了节省时间禁用掉了交叉编译
. ../configure -enable-checking=release -enable-languages=c,c++ -disable-multilib
# 接下来这里需要很久
make -j 8
make install

# 安装完成之后我们需要替换原来的cc文件和c++文件，确保它们的版本都是最新的版本
# 一般来说原来系统的cc文件和c++文件都在/usr/bin/目录下，而我们编译安装的cc文件和c++文件在/usr/local/bin/
cd /usr/bin/
mv /usr/bin/cc /usr/bin/cc.4.8.5
mv /usr/bin/c++ /usr/bin/c++.4.8.5
ln -s /usr/local/bin/gcc /usr/bin/cc
ln -s /usr/local/bin/c++ /usr/bin/c++
# 最后检查版本
/usr/bin/cc -v
/usr/bin/c++ -v
```

因为服务器内存不够，无法支持编译出gcc11



如果用gcc4.8来编译

![image-20211110140924121](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131306436.png)

无法通过编译

创建一个8G虚拟机

编译完成



### BoringSSL

git clone https://boringssl.googlesource.com/boringssl

```bash
cd boringssl/
mkdir build
cd build
cmake ../
make
```



### Nginx-quic

下载源码

```
hg clone -b quic https://hg.nginx.org/nginx-quic
```

报错

![image-20211113130754702](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131307786.png)

python2版本太低

![image-20211111234929206](https://raw.githubusercontent.com/FinnSHI/PictureBed/main/imgs/202111112349251.png)

[CentOS 7 从Python2.7.5升级到Python2.7.16版本教程 - 思有云 - IOIOX](https://www.ioiox.com/archives/50.html)

更新了还是不行





重新下载 https://hg.nginx.org/nginx-quic/shortlog/quic/nginx-quic-quic.tar.gz

解压上传到服务器，进入目录

```
bash ./auto/configure --prefix=/home/nginx \
                       --with-debug --with-http_ssl_module \
                       --with-http_v2_module --with-http_v3_module \
                       --with-cc-opt="-I/home/quic/software/boringssl/include"   \
                       --with-ld-opt="-L/home/quic/software/boringssl/build/ssl  \
                                      -L/home/quic/software/boringssl/build/crypto"
```

报新错

![image-20211112001750161](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131307283.png)

将编译好的库libcrypto.a和libssl.a手动拷贝到/usr/local/lib目录下

为了避免nginx重新编译openssl，执行下面动作更改ssl.h时间

touch ../boringssl/.openssl/include/openssl/ssl.h

![image-20211112002555297](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308534.png)

![image-20211112002624818](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308672.png)

touch ../boringssl/openssl/include/openssl/ssl.h

![image-20211112002903904](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308741.png)



nginx-quic make报错，无法make。后来发现是nginx-quic上传的文件不完整。

重新上传

进入nginx-quic-quic

```
bash ./auto/configure --prefix=/home/nginx \
                       --with-debug --with-http_ssl_module \
                       --with-http_v2_module --with-http_v3_module       \
                       --with-cc-opt="-I/home/quic/software/boringssl/include"   \
                       --with-ld-opt="-L/home/quic/software/boringssl/build/ssl  \
                                      -L/home/quic/software/boringssl/build/crypto"
```

然后

```
make
make install
```

成功！



修改nginx.conf

```
user  root;
worker_processes  1;

error_log  /root/nginx/logs/error.log;
#error_log  /root/nginx/logs/error.log  notice;
#error_log  /root/nginx/logs/error.log  info;

pid        /root/nginx/logs/nginx.pid;


events {
    worker_connections  1024;
}

http {
    include       mime.types;
    default_type  application/octet-stream;

    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /root/nginx/logs/access.log  main;


    #keepalive_timeout  0;
    keepalive_timeout  65;

    #gzip  on;


    server {
        listen 443 ssl http2;              # TCP listener for HTTP/2
        listen 443 http3 reuseport;  # UDP listener for QUIC+HTTP/3
        #listen 443 quic reuseport; 

        ssl_protocols       TLSv1.3; # QUIC requires TLS 1.3
        ssl_certificate     /home/finn/ssl/certificate.crt;
        ssl_certificate_key /home/finn/ssl/private.key;
        
        add_header Alt-Svc 'quic=":443"; h3-27=":443";h3-25=":443"; h3-T050=":443"; h3-Q050=":443";h3-Q049=":443";h3-Q048=":443"; h3-Q046=":443"; h3-Q043=":443"'; # Advertise that QUIC is available
     }


    server {
        #listen       443 ssl http2;
        #listen       443 http3 reuseport;
        listen 80;
        server_name  www.shifan-web.xyz;

        #ssl_protocols       TLSv1.3; # QUIC requires TLS 1.3
        #ssl_certificate     /home/finn/nginxhttp3/ssl/certificate.crt;
        #ssl_certificate_key /home/finn/nginxhttp3/ssl/private.key;




        #charset koi8-r;

        #access_log  /home/finn/quic/logs/access.log  main;

        location / {
            root   /home/finn/web;
            index  index.html index.htm;
            #add_header Alt-Svc 'quic=":443"; h3-27=":443";h3-25=":443"; h3-T050=":443"; h3-Q050=":443";h3-Q049=":443";h3-Q048=":443"; h3-Q046=":443"; h3-Q043=":443"'; # Advertise that QUIC is available

        }

        #error_page  404              /404.html;

        # redirect server error pages to the static page /50x.html
        #
        error_page   500 502 503 504  /50x.html;
        location = /50x.html {
            root   html;
        }

        # proxy the PHP scripts to Apache listening on 127.0.0.1:80
        #
        #location ~ \.php$ {
        #    proxy_pass   http://127.0.0.1;
        #}

        # pass the PHP scripts to FastCGI server listening on 127.0.0.1:9000
        #
        #location ~ \.php$ {
        #    root           html;
        #    fastcgi_pass   127.0.0.1:9000;
        #    fastcgi_index  index.php;
        #    fastcgi_param  SCRIPT_FILENAME  /scripts$fastcgi_script_name;
        #    include        fastcgi_params;
        #}

        # deny access to .htaccess files, if Apache's document root
        # concurs with nginx's one
        #
        #location ~ /\.ht {
        #    deny  all;
        #}

    }
}
```



![image-20211112093927665](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308416.png)

![image-20211112093954301](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308887.png)



wireshark抓包

![image-20211112093912108](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308922.png)

![image-20211112094055018](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308507.png)

可以看到走的都是quic协议

详细看一个

![image-20211112094212017](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131308242.png)
