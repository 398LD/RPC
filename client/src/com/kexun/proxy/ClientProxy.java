package com.kexun.proxy;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientProxy {


    public static <T> T getProxy(Class serviceInterface, InetSocketAddress addr) {

        //获取java动态代理对象
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

/*
                proxy:当前调用的对象
                        method:调用的方法
                                args:调用的参数*/
                //创建客户端
                Socket socket = new Socket();
                //连接服务器 InetSocketAddress:封装了
                socket.connect(addr);
                System.out.println("成功连接到服务器");
                //Method 没有实现序列化接口 只有拆开发送了
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                //发送接口名字
                objectOutputStream.writeUTF(serviceInterface.getName());
                //发送方法名称
                objectOutputStream.writeUTF(method.getName());
                //发送方法参数类型列表
                objectOutputStream.writeObject(method.getParameterTypes());
                //发送方法参数
                objectOutputStream.writeObject(args);

                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //获取到服务端返回的对象
                Object result = objectInputStream.readObject();
                //关流
                objectInputStream.close();
                objectOutputStream.close();
                socket.close();

                return result;
            }
        });


    }


}
