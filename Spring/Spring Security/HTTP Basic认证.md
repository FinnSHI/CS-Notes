# SpringSecurity+JWT认证流程解析

![9329806-8eb5612b9ba8bb2a.jpeg](https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202201201052675.jpeg)

## Basic认证

Basic认证是HTTP的基本认证，就是client请求server一个受限资源时，server要求client携带携带basic认证的base64编码形式。

1. **客户端(例如Web浏览器)**：服务器，请把/family/son.jpg 图片传给我。

```text
GET /family/son.jpg  HTTP/1.1
```

2. **服务器**：客户端你好，这个资源在安全区**family**里，是受限资源，需要基本认证，请带上你的用户名和密码再来

```text
HTTP/1.1 401 Authorization Required
www-Authenticate: Basic realm= "family" 
```

服务器会返回401，告知客户端这个资源需要使用基本认证的方式访问，我们可以看到在 `www-Authenticate`这个Header里面 有两个值，Basic：说明需要基本认证，realm：说明客户端需要输入这个安全区的用户名和密码，而不是其他区的。因为服务器可以为不同的安全区设置不同的用户名和密码。如果服务器只有一个安全区，那么所有的基本认证用户名和密码都是一样的。

3. **客户端**：服务器，我已经按照你的要求，携带了相应的用户名和密码信息了，你看一下
   如果客户端是浏览器，那么此时就会弹出一个弹窗，让用户输入用户名和密码。
   Basic 内容为： **用户名:密码** 的base64形式 。 例如我的用户名为Shusheng007,密码为ss007 那么我的Basic的内容为 **Shusheng007:ss007** 对应的base64 编码内容U2h1c2hlbmcwMDcldUZGMUFzczAwNw==，如下所示

```text
GET /family/son.jpg  HTTP/1.1 Authorization: Basic
U2h1c2hlbmcwMDcldUZGMUFzczAwNw==
```

4. **服务器**：客户端你好，我已经校验了你的用户名和密码，是正确的，这是你要的资源。

```text
HTTP/1.1 200 OK Content-type: image/jpg ...
```