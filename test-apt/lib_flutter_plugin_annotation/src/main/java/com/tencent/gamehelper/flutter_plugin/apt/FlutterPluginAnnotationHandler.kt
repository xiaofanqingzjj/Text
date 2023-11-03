package com.tencent.gamehelper.flutter_plugin.apt;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.tencent.gamehelper.flutter_plugin.annotation.FlutterPlugin;

import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;


/**
 * \@FlutterPlugin注解处理器
 * <p>
 * 收集类中使用到\@FlutterPlugin的类，创建其对象注册到PluginCenter中
 * <p>
 * class $PluginRegister {
 *  void registers(PluginCenter pluginCenter) {
 *      // findClass
 *      // new Obj
 *      // register obj
 *      pluginCenter.resister(String name, Object plugin)
 *  }
 * }
 */
public class FlutterPluginAnnotationHandler {


    /**
     * 代码中用到的PluginCenter类
     */
    static final ClassName PLUGIN_CENTER = ClassName.get("com.tencent.gamehelper.flutter_plugin", "PluginCenter");


    /**
     * 自动注册Flutter插件
     *
     * @param roundEnv
     */
    static void handle(RoundEnvironment roundEnv, final Filer filer, final Elements elements, final Messager messager) {

        final Set<? extends Element> plugins = roundEnv.getElementsAnnotatedWith(FlutterPlugin.class);

        if (plugins != null && plugins.size() > 0) {

            // 方法签名:void registers(PluginCenter pluginCenter)
            final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("registers")
                    .addModifiers(Modifier.PUBLIC)
                    .addModifiers(Modifier.STATIC)
                    .addParameter(PLUGIN_CENTER, "pluginCenter");

            // 收集用到了@FlutterPlugin的类

            System.out.println("plugins:" + plugins);

            methodBuilder.addCode("try {");

            methodBuilder.addStatement("$T plugin", ClassName.get(Object.class));

            plugins.forEach(new Consumer<Element>() {
                @Override
                public void accept(Element element) {
                    System.out.println("element:" + element);
                    System.out.println("getEnclosingElement: " + element.getEnclosingElement());

                    System.out.println("element.getKind():" + element.getKind());
                    System.out.println("getSimpleName: " + element.getSimpleName());
                    System.out.println("********asType: " + element.asType().getClass());

                    if (element.getKind() != ElementKind.CLASS) {
                        throw new IllegalArgumentException(String.format("Only class can be annotated with @%s", FlutterPlugin.class.getSimpleName()));
                    }

                    TypeElement typeElement = (TypeElement) element;
                    FlutterPlugin annotation = element.getAnnotation(FlutterPlugin.class);

                    try {

                        // 创建被注册的类对象
                        Name fullName = typeElement.getQualifiedName();
                        methodBuilder.addStatement("plugin = $T.forName($S).newInstance()", ClassName.get(Class.class), fullName.toString());

                        methodBuilder.addStatement("pluginCenter.resister($S, plugin)", annotation.value());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            methodBuilder.addCode("}catch($T e) {}", ClassName.get(Exception.class));

            String packageName = "com.tencent.fp.generate";
            String className = "PluginRegister";
            ClassName generatedClass = ClassName.get(packageName, className);


            /**
             * 构建类
             */
            TypeSpec finderClass = TypeSpec.classBuilder(generatedClass.simpleName())
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(methodBuilder.build())
                    .build();

            try {
                JavaFile.builder(packageName, finderClass).build().writeTo(filer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
