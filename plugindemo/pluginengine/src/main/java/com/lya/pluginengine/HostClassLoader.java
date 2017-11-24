package com.lya.pluginengine;

import com.lya.pluginengine.utils.LogUtils;
import com.lya.pluginengine.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Enumeration;

import dalvik.system.PathClassLoader;

/**
 * Created by liyongan on 17/11/24.
 */

public class HostClassLoader extends PathClassLoader {

    private ClassLoader mOrig;
    private Method mFindResourceMethod;
    private Method mFindResourcesMethod;
    private Method mFindLibraryMethod;
    private Method mGetPackageMethod;

    public HostClassLoader(ClassLoader parent, ClassLoader orig) {
        super("", "", parent);
        mOrig = orig;
        copyFromOriginal(orig);
        initMethods(orig);
    }

    private void initMethods(ClassLoader cl) {
        Class<?> c = cl.getClass();
        mFindResourceMethod = ReflectUtils.getMethod(c, "findResource", String.class);
        mFindResourceMethod.setAccessible(true);
        mFindResourcesMethod = ReflectUtils.getMethod(c, "findResources", String.class);
        mFindResourcesMethod.setAccessible(true);
        mFindLibraryMethod = ReflectUtils.getMethod(c, "findLibrary", String.class);
        mFindLibraryMethod.setAccessible(true);
        mGetPackageMethod = ReflectUtils.getMethod(c, "getPackage", String.class);
        mGetPackageMethod.setAccessible(true);
    }

    private void copyFromOriginal(ClassLoader orig) {
        try {
            Field f = ReflectUtils.getField(orig.getClass(), "pathList");
            if (f == null) {
                return;
            }
            // 删除final修饰符
            ReflectUtils.removeFieldFinalModifier(f);
            // 复制Field中的值到this里
            Object o = ReflectUtils.readField(f, orig);
            ReflectUtils.writeField(f, this, o);
        } catch (IllegalAccessException e) {
            LogUtils.log("ReflectUtils getField of pathList error");
        }
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
        Class<?> c = null;
        // TODO: 17/11/24 这里可以将load占位activity改为原本要load的插件中对应的activity
//        c = PMF.loadClass(className, resolve);
        if (c != null) {
            return c;
        }
        try {
            c = mOrig.loadClass(className);
            LogUtils.log("load class: " + className + " in hostclassloader use origin classloader");
            return c;
        } catch (Throwable e) {
            e.printStackTrace();
            LogUtils.log("use default classloader load class: " +  className +" fail");
        }
        return super.loadClass(className, resolve);
    }

    @Override
    protected URL findResource(String resName) {
        try {
            return (URL) mFindResourceMethod.invoke(mOrig, resName);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            LogUtils.log("findResource fail in hostclassloader");
        }
        return super.findResource(resName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Enumeration<URL> findResources(String resName) {
        try {
            return (Enumeration<URL>) mFindResourcesMethod.invoke(mOrig, resName);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            LogUtils.log("findResources fail in hostclassloader");
        }
        return super.findResources(resName);
    }

    @Override
    public String findLibrary(String libName) {
        try {
            return (String) mFindLibraryMethod.invoke(mOrig, libName);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            LogUtils.log("findLibrary fail in hostclassloader");
        }
        return super.findLibrary(libName);
    }

    @Override
    protected Package getPackage(String name) {
        // 金立手机的某些ROM(F103,F103L,F303,M3)代码ClassLoader.getPackage去掉了关键的保护和错误处理(2015.11~2015.12左右)，会返回null
        // 悬浮窗某些draw代码触发getPackage(...).getName()，getName出现空指针解引，导致悬浮窗进程出现了大量崩溃
        // 此处实现和AOSP一致确保不会返回null
        // SONGZHAOCHUN, 2016/02/29
        if (name != null && !name.isEmpty()) {
            Package pack = null;
            try {
                pack = (Package) mGetPackageMethod.invoke(mOrig, name);
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                LogUtils.log("getPackage fail in hostclassloader");
            }
            if (pack == null) {
                pack = super.getPackage(name);
            }
            if (pack == null) {
                return definePackage(name, "Unknown", "0.0", "Unknown", "Unknown", "0.0", "Unknown", null);
            }
            return pack;
        }
        return null;
    }
}
