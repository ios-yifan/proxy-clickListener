package com.androidstudy.retrofitdemo;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventType(listenerType = View.OnClickListener.class,listenerSetter = "setOnClickListener")
public @interface OnClick {
    int[] value();  // int 类型的数组，因为同一个页面内 多个控件都需要点击事件，所以保存着多个控件 ID
}
