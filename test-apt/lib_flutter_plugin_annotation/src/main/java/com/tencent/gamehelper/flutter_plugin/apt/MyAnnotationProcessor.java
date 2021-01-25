package com.tencent.gamehelper.flutter_plugin.apt;


import com.google.auto.service.AutoService;
import com.tencent.gamehelper.flutter_plugin.annotation.FlutterPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;


@AutoService(Processor.class)
public class MyAnnotationProcessor extends AbstractProcessor {

    static  final  String TAG = "BindViewProcessor";

    /**
     * 文件相关的辅助类
     */
    private Filer mFiler;
    /**
     * 元素相关的辅助类
     */
    private Elements mElementUtils;
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);


        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();

        System.out.println("APT --------------------------------------------" + TAG + ": init --------------------------------------------------");
        System.out.println("APT --------------------------------------------" + TAG + ": init --------------------------------------------------");
        System.out.println("APT --------------------------------------------" + TAG + ": init --------------------------------------------------");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(FlutterPlugin.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     * @param annotations 要处理的
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println(TAG + ": process, annotations size:" + annotations.size());
        FlutterPluginAnnotationHandler.handle(roundEnv, mFiler, mElementUtils, mMessager);
        return true;
    }

}