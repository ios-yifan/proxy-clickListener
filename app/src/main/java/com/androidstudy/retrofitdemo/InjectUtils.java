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

                    EventType eventType = annotationType.getAnnotation(EventType.class);

                    Class listenerType = eventType.listenerType();
                    String listenerSetter = eventType.listenerSetter();

                    try {
                        Method valueMethod = annotationType.getDeclaredMethod("value");
                        int[] viewIds = (int[]) valueMethod.invoke(annotation);

                        ListenerInvocationHandler<Activity> listenerInvocationHandler = new ListenerInvocationHandler(valueMethod,mainActivity);
                        Object proxyInstance = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerInvocationHandler);

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
