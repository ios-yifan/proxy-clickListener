package com.androidstudy.retrofitdemo;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectUtils {
    public static void injectEvent(MainActivity mainActivity) {

        //获取 class 对象
        Class<? extends MainActivity> activityClass = mainActivity.getClass();
        //获取所有的方法
        Method[] declaredMethods = activityClass.getDeclaredMethods();


        for (Method declaredMethod : declaredMethods) {
            //获取方法中所有的注解
            Annotation[] annotations = declaredMethod.getAnnotations();

            for (Annotation annotation : annotations) {

                Class<? extends Annotation> annotationType = annotation.annotationType();

                //判断当前注解是否有 eventType 的元注解
                if (annotationType.isAnnotationPresent(EventType.class)){

                    // 获取 当前注解中 EventType注解的注解信息  annotationType拿到的是 OnClick 注解，通过 onClick 注解再去拿 EventType 注解
                    EventType eventType = annotationType.getAnnotation(EventType.class);

                    // 获取到具体的类信息
                    Class listenerType = eventType.listenerType();
                    String listenerSetter = eventType.listenerSetter();

                    try {
                        // 两种方式
                        // 1. 获取OnClick 的 value 值
                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        int[] viewIds = (int[]) valueMethod.invoke(annotation);

                        // 2
                        //int[] ids = ((OnClick) annotation).value();


                        // 构建一个接口的实现类 方便保存 target（就是 activity），保证按钮的点击事件响应到正确的 activity
                        ListenerInvocationHandler<Activity> listenerInvocationHandler = new ListenerInvocationHandler(valueMethod,mainActivity);

                        // 生成一个代理对象，
                        Object proxyInstance = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerInvocationHandler);

                        // 遍历 ID 获取到 view，给 view 添加
                        for (int viewId : viewIds) {
                            View viewById = mainActivity.findViewById(viewId);
                            Method setter = viewById.getClass().getMethod(listenerSetter,listenerType);
                            setter.invoke(viewById,proxyInstance);
                        }

                        valueMethod.setAccessible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class ListenerInvocationHandler<T> implements InvocationHandler{

        private Method method;
        private T target;

        public ListenerInvocationHandler(Method method, T target) {
            this.method = method;
            this.target = target;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return method.invoke(target,args);
        }
    }
}
