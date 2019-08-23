package com.kexun.server;

public interface ServerCenter {

    //启动服务
    void start();

    //关闭服务
    void stop();


    //注册服务
    void registry(String interfaceName, Class imp);

}
