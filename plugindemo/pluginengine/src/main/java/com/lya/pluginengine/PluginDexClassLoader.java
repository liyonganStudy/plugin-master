/*
 * Copyright (C) 2005-2017 Qihoo 360 Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.lya.pluginengine;

import com.lya.pluginengine.utils.LogUtils;

import dalvik.system.DexClassLoader;

/**
 * 插件的DexClassLoader。用来做一些“更高级”的特性，在RePluginConfig中可直接配置
 * <p>注：原本只需要DexClassLoader即可，但若要支持一些高级特性（如可自由使用宿主的Class），则仍需实现相应方法
 *
 * @author RePlugin Team
 */

public class PluginDexClassLoader extends DexClassLoader {
    private ClassLoader mHostClassLoader;

    public PluginDexClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent, ClassLoader hostClassLoader) {
        super(dexPath, optimizedDirectory, librarySearchPath, parent);
        mHostClassLoader = hostClassLoader;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        // 插件自己的Class。从自己开始一直到BootClassLoader，采用正常的双亲委派模型流程，读到了就直接返回
        Class<?> pc;
        try {
            pc = super.loadClass(className, resolve);

            // 首次加载插件activity时会加载非常多的class，原因未知。
            LogUtils.logForClassLoader("=======PluginDexClassLoader loadClass: " + className + " result: " + pc);
            if (pc != null) {
                return pc;
            }
        } catch (ClassNotFoundException e) {
            // 和配置成是否需要，如果插件provided一个jar包，jar包在宿主内
            // 查找jar包中的类的话就需要loadClassFromHost
//            pc = loadClassFromHost(className, resolve);
            pc = mHostClassLoader.loadClass(className);
            LogUtils.logForClassLoader("=======PluginDexClassLoader loadClassFromHost: " + className + " result: " + pc);
            return pc;
        }
        return null;
    }

    // 不需要使用反射
//    private static void initMethods(ClassLoader cl) {
//        Class<?> clz = cl.getClass();
//        if (sLoadClassMethod == null) {
//            sLoadClassMethod = ReflectUtils.getMethod(clz, "loadClass", String.class, Boolean.TYPE);
//            if (sLoadClassMethod == null) {
//                throw new NoSuchMethodError("loadClass");
//            }
//        }
//    }
//
//    private Class<?> loadClassFromHost(String className, boolean resolve) throws ClassNotFoundException {
//        Class<?> c;
//        try {
//            c = (Class<?>) sLoadClassMethod.invoke(mHostClassLoader, className, resolve);
//            // 只有开启“详细日志”才会输出，防止“刷屏”现象
//        } catch (IllegalAccessException e) {
//            // Just rethrow
//            throw new ClassNotFoundException("Calling the loadClass method failed (IllegalAccessException)", e);
//        } catch (InvocationTargetException e) {
//            // Just rethrow
//            throw new ClassNotFoundException("Calling the loadClass method failed (InvocationTargetException)", e);
//        }
//        return c;
//    }

}
