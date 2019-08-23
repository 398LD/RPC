package com.kexun.server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerCenterImpl implements ServerCenter {

    //用map当做一个注册中心 key 接口的全限定名 value:实现类的class 后面获取的时候可以通过接口名字获取其实现类
    private static Map<String, Class> centermap = new HashMap<String, Class>();

    //线程池 这里不多阐述(百度一下)
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    //输出流对象
    ObjectOutputStream objectOutputStream = null;

    //输入流对象
    ObjectInputStream objectInputStream = null;

    //服务器对象
    ServerSocket server = null;

    //启动注册中心的方法
    @Override
    public void start() {
        try {
            //创建一个socket服务器对象
            server = new ServerSocket();
            //绑定8888端口
            server.bind(new InetSocketAddress(8888));
            //一个死循环 应为不知道要调用多少次方法
            while (true) {
                System.out.println("注册中心已启动");
                //等待客户端
                Socket socket = server.accept();
                System.out.println("客户端已连接");
                //启动一个线程 我这里使用了内部类的方式
                executor.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //等到客户端连接
                            objectInputStream = new ObjectInputStream(socket.getInputStream());
                            //接受接口名称
                            String interfaceName = objectInputStream.readUTF();
                            //接受方法名
                            String methodName = objectInputStream.readUTF();
                            //接受方法参数类型列表
                            Class[] parameters = (Class[]) objectInputStream.readObject();
                            //接受列表
                            Object[] objects = (Object[]) objectInputStream.readObject();
                            //通过客户端传过来的接口名,获取到一经注册的实体类
                            Class newclass = centermap.get(interfaceName);
                            //通过方法名字和参数类型得到方法
                            Method method = newclass.getMethod(methodName, parameters);
                            //调用方法拿到返回值
                            Object result = method.invoke(newclass.newInstance(), objects);
                            //获取对象输出流
                            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            //将返回的值写到流里面
                            objectOutputStream.writeObject(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                //关闭输出
                                if (objectOutputStream != null)
                                    objectOutputStream.close();
                                //关闭输入
                                if (objectInputStream != null)
                                    objectInputStream.close();
                                //关闭socket
                                if (socket != null)
                                    socket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void stop() {
        try {
            if (server != null) server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void registry(String interfaceName, Class imp) {
        centermap.put(interfaceName, imp);
    }
}
