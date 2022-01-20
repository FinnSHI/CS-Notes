# Transport Layer

## TCP

### 思维导图





### Detailed knowledage

1. ##### TCP send and receive buffers

![image-20210911185106877](E:\Typora_Documents\TCP.assets\image-20210911185106877.png)



**MSS**： (Maximum Segment Size); to limit the maximum amount of data that can be grabbed and placed in a <i>segment</i>.

**MTU**: (Maximum Transmission Unit); to set the MSS to ensure that a TCP segment(encapsulate in a IP datagram) plus TCP/IP header length(40 bytes) will fit into a single link-layer frame.



2. ##### TCP Segment Structure

   ![image-20210911184446072](E:\Typora_Documents\TCP.assets\image-20210911184446072.png)

