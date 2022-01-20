# **Analyze and Compare Several Aspects of Different HTTP Protocols**

## Abstract















## 1 Introduction

### 1.1 HTTP Protocol

HTTP protocol is the abbreviation of Hyper Text Transfer Protocol, which is a transfer protocol used to transfer hypertext from a World Wide Web (WWW: World Wide Web) server to a local browser. HTTP is a communication protocol based on TCP/IP to transfer data (HTML files, image files, query results, etc.). 

### 1.2 Nginx

Nginx is a free and open web server with an asynchronous framework, and can also be used as a reverse proxy, load balancer, and HTTP cache. It is developed by Igor Sesoyev and published in 2004. 

### 1.3 Nginx-quic





### 1.4 Azure Virtual Machines

Microsoft provides remote virtual machines for local hosts to request resources. By building servers on Azure virtual machines and run our websites. Local host is able to access the website page by IP address or domain name. 

Microsoft provides 200$ free quota for new users to build remote virtual machines. We applied two Azure virtual machines, one is located at US East, and another is located in Hong Kong. Both two 





### 1.5 BoringSSL

BoringSSL is an OpenSSL branch created by Google, used for encryption algorithm supporting TLS1.3. TLS1.3 is a standard protocol to implement 0-RTT data transmission based on UDP. Some features of BoringSSL will by synchronized to OpenSSL at the right time.  

Whether HTTP/2 or HTTP/3 is always based on HTTPS, and it needs BoringSSL to provide corresponding algorithm for encrypting. For Nginx, the corresponding BoringSSL needs to be configured during compilation. 




## 2 HTTP Protocols   

### 2.1 HTTP/1.1

HTTP 1.1 builds on HTTP/1.0. To overcome the shortcoming for too much cost and latency of establishing and closing connections of HTTP/1.0, HTTP 1.1 supports persistent connections. By means of allowing multiple HTTP requests and responses to be sent over a single TCP connection, it can reduce the cost and latency of establishing and closing connections.

#### 2.1.1 Basics of HTTP/1.1

HTTP/1.1 works on TCP port 80 by default, and users visiting websites start with http:// are standard HTTP services. The TCP/IP protocol suite is a system composed of a four-layer protocol. The four layers are: application layer, transport layer, network layer, and link layer.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/xkeylove/Pictures/raw/master/image-20211119150405765.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: TCP/IP Protocol Suite
    </div> 
</center>

**Application layer**. The application layer is generally an application program, which determines the application services provided to users. The application layer can communicate with the transport layer through system calls. There are many protocols at the application layer, such as FTP (File Transfer Protocol), DNS (Domain Name System), and HTTP (HyperText Transfer Protocol).

**Transport layer**. The transport layer provides the application layer with data transmission functions between two computers in a network connection through system calls. There are two different protocols in the transport layer: TCP (Transmission Control Protocol) and UDP (User Data Protocol).

**Network layer**. The network layer is used to process data packets flowing on the network, and the data packet is the smallest data unit for network transmission. This layer specifies the path (transmission route) through which way to reach the other computer and transmit data packets to the other computer.

**Link layer**. The link layer is used to process the hardware part of the connection network, including the control operating system, hardware device drivers, NIC (Network Interface Card, network adapter), and optical fiber and other physically visible parts. The scope of the hardware is within the scope of the link layer. 

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111211904661.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: TCP/IP Protocol Suite
    </div> 
</center>

Application data will be passed along the protocol stack from top to bottom before being sent to the physical network. Each layer of protocol will add its own header information based on the upper layer protocol data (the link layer will also add tail information) to provide the necessary information to realize the functions of this layer, as the figure 2 shows. When the sender sends data, the data will be transmitted from the upper layer to the lower layer, and each layer will be marked with the header information of that layer. When the receiving end receives data, the data will be transmitted from the lower layer to the upper layer, and the header information of the lower layer will be deleted before transmission.

#### 2.1.2  How HTTP/1.1 Works

The HTTP protocol uses a request/response model. The client sends a request message to the server. The request message contains the requested method, URL, protocol version, request header, and request data. The server responds with a status line. The content of the response includes the protocol version, success or error code, server information, response headers and response data.

The following are the HTTP request/response steps: 

**Step 1**. Client connects to the Web server An HTTP client, usually a browser, establishes a TCP socket connection with the HTTP port of the Web server (the default port is 80).

**Step 2**. Sending an HTTP request Through a TCP socket, the client sends a text request message to the Web server. A request message consists of 4 parts: request line, request header, blank line, and request data.

**Step 3**. The server accepts the request and returns an HTTP response. The web server parses the request and locates the requested resource. The server writes a copy of the resource to the TCP socket, which is read by the client. A response consists of 4 parts: status line, response header, blank line, and response data.

**Step 4**. Release the connection TCP connection.  If the connection mode is close, the server actively closes the TCP connection. The client passively closes the connection and releases the TCP connection; if the connection mode is keep alive, the connection will remain for a period. You can continue to receive requests during this time.

**Step 5**. Client browser parses HTML content. The client browser first parses the status line and looks at the status code indicating whether the request was successful. Then each response header is parsed, and the response header informs that the following is a few-byte HTML document and the character set of the document. The client browser reads the response data HTML, formats it according to the HTML syntax, and displays it in the browser window.



### 2.2 HTTPS

The HTTP protocol sends content in plain text and does not provide any means of data encryption. If an attacker intercepts the transmission message between a web browser and a website server, he can directly read the information in it. Therefore, the HTTP protocol is not suitable for some situations. Some sensitive information, such as payment information such as credit card numbers and passwords.

To solve this defect of the HTTP protocol, another protocol is needed: the secure socket layer hypertext transfer protocol HTTPS. For the security of data transmission, HTTPS adds the SSL/TLS protocol based on HTTP, and SSL/TLS relies on the certificate verifies the identity of the server and encrypts the communication between the browser and the server.

The main functions of the HTTPS protocol can be divided into two types: one is to establish an information security channel to ensure the security of data transmission; the other is to confirm the authenticity of the website.

#### 2.2.1 Communication with Web

**Step 1**. The client uses the https URL to access the Web server and requires an SSL connection with the Web server.

**Step 2**. After the web server receives the client's request, it will send a copy of the website's certificate information (the certificate contains the public key) to the client.

**Step 3**. The client's browser and the Web server begin to negotiate the security level of the SSL/TLS connection which is the level of information encryption.

**Step 4**. The client's browser establishes a session key according to the security level agreed by both parties, encrypts the session key with the website's public key, and transmits it to the website.

**Step 5**. The Web server uses its own private key to decrypt the session key.

**Step 6**. The Web server uses the session key to encrypt the communication with the client.



#### 2.2.2 Computing links involved in HTTPS

**Asymmetric key exchange**. For example, RSA, Diffie-Hellman, ECDHE. The main function of such algorithms is to generate a symmetric key through a high-strength key generation algorithm based on the asymmetric information between the client and the server, which is used to encrypt and decrypt subsequent application messages.

**Symmetric encryption and decryption**. The server uses the key A to encrypt the response content, and the client uses the same key A to decrypt the encrypted content, and vice versa.

**Message consistency verification**. Each piece of encrypted content will be appended with a MAC message, that is, a message authentication code. Simply put, it is a secure hash calculation of the content, and the receiver needs to verify the MAC code.

**Certificate signature verification**. This stage mainly occurs when the client verifies the identity of the server certificate, and the certificate signature needs to be verified to ensure the authenticity of the certificate.



#### 2.3.3 Features of HTTPS

**More time in connection**. Users only need to complete the TCP three-way handshake to establish a TCP connection and then users can send HTTP requests to obtain application layer data. In addition, there is no need to consume computing resources during the entire access process.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/xkeylove/Pictures/raw/master/master/clip_image004.jpg">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Client communicates with Server over HTTPS
    </div> 
</center>

**More time in calculation**. The calculations related to encryption and decryption are time-consuming both in Browser side and Server side.





### 2.3 HTTP/2

HTTP/2 is an extension of HTTP/1.x, not a replacement. Therefore, the semantics of HTTP remain unchanged, the functions provided remain unchanged, and core concepts such as HTTP methods, status codes, URLs, and header fields remain unchanged.

The reason for increasing a major version to 2.0 is mainly because it changes the way data is exchanged between the client and the server. HTTP 2.0 adds a new binary framing data layer, and this layer is not compatible with the previous HTTP/1.x server and client-so called 2.0.

Since HTTPS has done very well in terms of security, the only goal of HTTP/2 is to improve performance. The current mainstream browser HTTP/2 implementations are all based on SSL/TLS, which means that the websites that use HTTP/2 are all HTTPS protocol. The HTTP/2 connection establishment process based on SSL/TLS is similar to that of HTTPS. During the SSL/TLS handshake negotiation, the client sets the ALPN (application layer protocol negotiation) extension in the ClientHello message to indicate that it expects to use the HTTP/2 protocol, and the server replies in the same way. In this way, HTTP/2 is established during the SSL/TLS handshake negotiation process.

#### 2.3.1 Structure of HTTP/2

HTTP/2 is a frame-based protocol. The use of framing is to encapsulate important information so that the resolution of the protocol can easily read, parse and restore the information.

However, HTTP/1.1 is separated by text. Parsing HTTP/1.1 does not require any high-tech, but it is often slow and error-prone. You need to keep reading in bytes until you encounter the separator CRLF, and also consider unruly clients, which will only send LF.

Parsing HTTP/1.1 requests or responses will also encounter the following problems:

a. Only one request or response can be processed at a time, and the parsing cannot be stopped before completion.

b. It is impossible to predict how much memory is required for parsing.
With frames in HTTP/2, the program that processes the protocol can know in advance what it will receive, and HTTP/2 has a field that indicates the length of the frame.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://raw.githubusercontent.com/xkeylove/Pictures/main/image-20211119182919218.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Frame Structure
    </div> 
</center>



#### 2.3.2 Features of HTTP/2

**Message**. HTTP message generally refers to HTTP request or response. The message consists of one or more frames. These frames can be sent out of order, and then reassembled according to the stream ID in the header of each frame. A message consists of at least the HEADERS frame (which initializes the stream), and can additionally contain CONTINUATION and DATA frames, as well as other HEADERS frames. 

There are multiple Streams on a TCP Connection, and each Stream can send its own HTTP request. In the figure below, each square is a FRAME, and the squares indicate which stream they belong to and which frame type they belong to.

<center>
    <img style="border-radius: 0.3125em; zoom: 50%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/xkeylove/Pictures/raw/master/master/image-20211119190800613.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Multiple Streams
    </div> 
</center>



**Stream**. The HTTP/2 specification defines a stream as an independent, two-way frame sequence exchange on an HTTP/2 connection. If the client wants to make a request, it will start a new stream, and the server will reply on this stream. Due to the framing, multiple requests and responses can be interleaved without blocking each other. The stream ID is used to identify the stream to which the frame belongs. After the HTTP/2 connection between the client and the server is established, a new stream is started by sending a HEADERS frame. If the header needs to span multiple frames, a CONTINUATION frame may also be sent. The HEADERS frame may come from a request or response. When subsequent streams are started, a new HEADERS frame with an incremental stream ID is sent.

**Multiplexing**. In HTTP/1.1, if the client wants to send multiple parallel requests, it must use multiple TCP connections. However, The binary framing layer of HTTP/2 breaks through this limitation. All requests and responses are sent on the same TCP connection: the client and server decompose the HTTP message into multiple frames, and then send them out of order. One end is recombined according to the stream ID. This mechanism brings huge performance improvements to HTTP.

**Header compression**. One problem with HTTP/1.1 is the bloated header. HTTP/2 improves on this problem and can compress the header. In a Web page, there are usually a large number of requests, and the headers of many requests often have many repetitive parts. HTTP/2 uses "header tables" on the client and server to track and store previously sent key-value pairs. For the same data, it is no longer sent through each request and response. When the client sends a request, it creates a table based on the header value.

<center>
    <img style="border-radius: 0.3125em; zoom: 50%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111211945392.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Dynamic Table
    </div> 
</center>





### 2.4 HTTP/3 

Internet is under rapid development. With the release of HTTP/1.1 in 1999,  and the standardization of HTTP/2 in 2015, HTTP/3 appears. HTTP/3 is a application layer network protocol, running over QUIC.  

#### 2.4.1 QUIC

Quick UDP Internet Connections (QUIC) is a transport layer protocol. Actually, this protocol was designed, implemented and deployed before the standardization of HTTP/2 by Google. 

TCP/IP is built with TCP, TLS and HTTP/2. Unlike TCP/IP, QUIC is a reliable transmission of TLS+HTTP/2 based on UDP, consisted of congestion control, loss recovery, TLS 1.3, and multistreaming. It speeds up the TLS handshake speed to only one round trip, avoids the slow start of TCP, and provides reliability when switching networks. 

QUIC improves performance of connection-oriented web applications that are currently using TCP. Because QUIC is built on UDP, compared with TCP, QUIC protocol can reduce latency. QUIC achieves this effect by using UDP to set up a number of multiplexed connections between two endpoints. What's more, QUIC works with HTTP/2's multiplexed connections. It allows multiple data streams to reach all the endpoints independently. On the contrary, if any packets forwarded between TCP are delayed or lost, all multiple data streams will suffer from the head-of-line blocking delay. 

<center>
    <img style="border-radius: 0.3125em; zoom: 60%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111181128324.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: TCP/IP and QUIC
    </div> 
</center>

**Multistreaming**. Each data stream in QUIC is controlled individually, and one stream will not affect other streams.

**TLS 1.3**. TLS 1.3 is a update to previous TLS versions. It introduces a new key agreement mechanism, PSK. TLS 1.3 can access faster, because it supports 0-RTT data transmission, which saves round trip time (RTT) when establishing a connection. TLS 1.3 only needs 1 RTT to complete the handshake, while TLS 1.2 needs 2 RTTs. What's more, plaintext is greatly reduced in TLS 1.3, and the encrypted message is no longer allowed to be compressed. This will bring more security to Internet.

**Loss recovery**. In QUIC, the lost data is retransmitted at the QUIC level instead of the UDP level. It means that if an error occurs in one of data streams, QUIC protocol stack is still able to continue providing services for other streams separately . This improves the performance on error-prone links. QUIC can process other data when repairing a single stream, which means that even if an error occurs in one request, it will never affect other requests. 

**Congestion control**. QUIC implements congestion control based on TCP's NewReno algorithm. QUIC establishes every connection in slow start with the congestion window which is set to an initial value. Unlike TCP which is based on the number of packets, congestion control in QUIC is executed by the number of bytes since QUIC has a better control byte count.  Different terminals may use different congestion control algorithms. The signal used for congestion control provided by QUIC is universal.



#### 2.4.2 Establishing connections via QUIC

**Initial connection**. When the browser sends a request to the server at the first time,  it has no idea whether the server supports QUIC protocol. Because of it, the browser will forward the first request by TCP protocol. After receiving the request, the server supporting QUIC should response to the request with a HTTP response header called Alt-Svc, so that the browser knows that the server supports QUIC with port 443/UDP. Then, the browser tries to use QUIC to make the next request. After the request is sent, the browser will establish connections to the server with the competition of QUIC and TCP. If TCP wins the competition, the second request will be sent via TCP. However, if QUIC succeeds once, and the connection is established by QUIC successfully, all future requests will be sent through the QUIC connection. 

**Non-initial connection**. QUIC-enabled servers will always support 0-RTT handshake. When the browser sends a request to a server who has previously used QUIC, the connection will also be set up by competing between TCP and QUIC. However, as long as the browser is able to conduct a 0-RTT handshake, QUIC will always win and the browser will communication with the server on the QUIC connection. 

**Failed connection**. If the QUIC handshake fails, for example, firewall blocks UDP connection or UDP port is not open, the browser will mark QUIC as "broken". Any requests will be resent via TCP. After 5 minutes, the browser will mark the expired QUIC as "recently broken", and the 0-RTT handshake will be disabled. When the next request is sent to the server, the browser will let TCP and QUIC compete. If QUIC handshake fails again, QUIC will be marked as "broken" again , and TTL will be set for 10 minutes this time. However, if the handshake is successful, the request will be sent via QUIC, and QUIC will no longer be marked as "recently broken". 



#### 2.4.3 HTTP/3 over QUIC

HTTP/3 is the upcoming third major version of the HTTP protocol. It is implemented by using QUIC protocol. We can see that the websites of Google and YouTube have already supported HTTP/3.

<center>
    <img style="border-radius: 0.3125em; zoom: 50%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111181558831.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: YouTube
    </div> 
</center>





## 3 Experiment Design

### 3.1 Experimental Environment

#### 3.1.1 Test Equipment

**Azure Virtual Machine**. 











**Host**. End system is Lenovo IdeaPad 5 Pro. Host operating system is Windows 10.

**Host network environment**. By using a network cable, we connect our PC with PolyU network, PolyUWLAN, in the laboratory PQ603, and get a stable network connection environment. 

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111191421986.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Host network environment
    </div> 
</center>


#### 3.1.2 Enable QUIC

QUIC is employed in most connections between Chrome and Google servers, and supported by Microsoft Edge, Firefox and Safari.  Because Microsoft Edge is a derivative of Chrome, we use Microsoft Edge browser to enable QUIC protocol. The version  of Microsoft Edge we use is 95.0.1020.53, and the operation system is Windows 10. 

**Step 1**. Type "edge://flags/#enable-quic" in the browser address bar and enter experimental model of Microsoft Edge. 

<center>
    <img style="border-radius: 0.3125em; zoom: 60%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111181056557.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Experimental model of Microsoft Edge
    </div> 
</center>




**Step 2**. Find the function: Experimental QUIC protocol, and enable this function.

<center>
    <img style="border-radius: 0.3125em; zoom: 60%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111181102967.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Experimental QUIC protocol
    </div> 
</center>


**Step 3**. Restart Microsoft Edge.

**Step 4**. Visit https://www.google.com and test whether the network protocol is h3 or not.

<center>
    <img style="border-radius: 0.3125em; zoom: 60%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111181107715.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Test QUIC
    </div> 
</center>


If the protocol is h3, it means that Microsoft Edge communicates with Google server over HTTP/3 protocol, so QUIC is enabled. Otherwise, it is not.



#### 3.2.3 Static Website Page

We coded a static web page to test HTTP/1.1, HTTP/2 and HTTP/3. In order to make test results more obvious, we cut one photo into $23×23$ small images to test the situation of the host requesting our own web page.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111191609635.png">
    <br>
    <div style="color:orange; border-bottom: 0px; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Static Website Page
    </div> 
</center>








### 3.2 Deploy Nginx-quic

Deploy nginx-quic on Azure server is not easy. It needs a powerful cloud virtual machine and to be installed and made in the cloud virtual machine by ourselves. The steps are as follows:

**Step 1**. Install GCC and G++. The version of GCC downloaded by default is 4.8.5.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111211022496.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Check GCC and G++
    </div> 
</center>

**step 2**. Install CMake 3.18.2. CMake is an open source cross-platform used for automated software construction. It can be complemented to manage software build produces. 

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131304390.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Check GCC and G++
    </div> 
</center>

**step 3**. Install and compile Perl. Perl is a family of two high-level, general-purpose, interpreted, dynamic programming languages. Here, we install Perl 5.34.0.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111131306361.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Check Perl Version
    </div> 
</center>

**step 4**. Install and compile Golang. The version of go we installed is 1.17.3.

**step 5**. Install and compile GCC 11.2.0. The default version of GCC downloaded by *yum* is 4.8.5, which is too old to support for making Nginx-quic. So, we downloaded the latest version of GCC and t compiled it in Azure Virtual Machine. 

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111211413728.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: Check GCC Version
    </div> 
</center>

**step 6**. Install and compile BoringSSL.

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111211414951.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000; padding: 2px;">
    Figure X: The Process of BoringSSL Compilation
    </div> 
</center>

**step 7**. Install, compile and start Nginx-quic. Nginx-quic is maintained in `https://hg.nginx.org/nginx-quic`. We downloaded the source code and compiled it in our cloud virtual machine. 





### 3.3 Realize HTTP/1.1 Protocol

<center>
    <img style="border-radius: 0.3125em; zoom: 100%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111211853388.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000; padding: 2px;">
    Figure X: The Process of BoringSSL Compilation
    </div> 
</center>







### 3.4 Realize HTTP/2 Protocol

#### 3.4.1 Apply for a Domain Name

In order to implement HTTP/2 protocol, we need to apply a domain name first. We signed in GoDaddy, a domain name suppler, and brought domain names respectively for our two Azure virtual machines. By configuring DNS management, we can use domain name `shifan-web.fun` to visit our website page on US East server and `yufeng-projects.store` to visit website on Hong Kong server.



#### 3.4.2 Apply for SSL

To update our websites to HTTPS, we applied a 90-day SSL respectively for `shifan-web.fun` and `yufeng-projects.store`. We uploaded the certificate files, certificate.crt and private.key, to our NGINX servers and configured in Nginx-quic. Then, our two servers can use SSL to connect with hosts.



#### 3.4.3 Update to HTTP/2

In order to update our websites to HTTP/2. We opened 443 port of servers for TCP connection, and made them listen requests over TCP connection. We changed the Nginx-quic configuration, specified the SSL path. 

Reload the nginx server.







### 3.6 Realize HTTP/3 Protocol

The server opens 443 port for UDP protocol, and listens QUIC requests over UDP. 







Check if HTTP/3 takes effect. 

<center>
    <img style="border-radius: 0.3125em; zoom: 60%; box-shadow: 0 2px 4px 0 rgba(34,36,38,.12) 0 2px 10px 0 rgba(34,36,38,.08);" src="https://gitee.com/FinnSHI/PicBed/raw/master/imgs/202111191642795.png">
    <br>
    <div style="color:orange; display: inline-block; color:  #000000;    padding: 2px;">
    Figure X: HTTP/3 is effective
    </div> 
</center>










## 4 Analysis and Comparison 

### 4.1 Request Methods







### 4.2 Package Header







### 4.3 Response Content







### 4.4 Connection Method









### 4.5 Connection Time









### 4.6 Page Load







## 5 Conclusion

### 5.1 limitation

说是对视频流的加速更大，但是我们只测试了静态网页的请求。。



### 5.2 Conclusion



### 5.3 Future work

后续测试其他页面时quic的能力









