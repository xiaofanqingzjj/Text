package com.tencent.gamehelper.flutter_plugin.apt

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.tencent.gamehelper.flutter_plugin.annotation.FlutterPlugin
import java.util.function.Consumer
import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

/**
 * \@FlutterPlugin注解处理器
 *
 *
 * 收集类中使用到\@FlutterPlugin的类，创建其对象注册到PluginCenter中
 *
 *
 * class $PluginRegister {
 *      void registers(PluginCenter pluginCenter) {
 *          // findClass
 *          // new Obj
 *          // register obj
 *          pluginCenter.resister(String name, Object plugin)
 *      }
 * }
 *
 */
object FlutterPluginAnnotationHandler {
    /**
     * 代码中用到的PluginCenter类
     */
    private val PLUGIN_CENTER: ClassName = ClassName.get("com.tencent.gamehelper.flutter_plugin", "PluginCenter")

    /**
     * 自动注册Flutter插件
     *
     * @param roundEnv
     */
    @JvmStatic
    fun handle(
        roundEnv: RoundEnvironment,
        filer: Filer?,
        elements: Elements?,
        messager: Messager?
    ) {
        val plugins = roundEnv.getElementsAnnotatedWith(
            FlutterPlugin::class.java
        )
        if (plugins != null && plugins.size > 0) {

            /*
             * 方法签名:void registers(PluginCenter pluginCenter)
             */
            val methodBuilder = MethodSpec.methodBuilder("registers")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(PLUGIN_CENTER, "pluginCenter")

            // 收集用到了@FlutterPlugin的类
            println("plugins:$plugins")

            /*
            try {
                Object plugin;
                plugin = java.lang.Class.forName("com.example.test.DownloadButtom").newInstance();
                pluginCenter.resister("test", plugin);
                plugin = java.lang.Class.forName("com.example.test.MainActivity").newInstance();
                pluginCenter.resister("test3", plugin);
                plugin = java.lang.Class.forName("com.example.test.DownloadProgressBarButton").newInstance();
                pluginCenter.resister("test2", plugin);
            }catch(Exception e) {

            }
             */
            methodBuilder.addCode("try {")
            methodBuilder.addStatement("\$T plugin", ClassName.get(Any::class.java))


            plugins.forEach { element ->

                println("element:$element, enclosingElement:${element.enclosingElement}, " +
                        "kind:${element.kind}, simpleName:${element.simpleName}, asType:${element.asType().javaClass}")

                require(element.kind == ElementKind.CLASS) {
                    String.format(
                        "Only class can be annotated with @%s",
                        FlutterPlugin::class.java.simpleName
                    )
                }

                val typeElement = element as TypeElement
                val annotation = element.getAnnotation(
                    FlutterPlugin::class.java
                )
                try {

                    // 创建被注册的类对象
                    val fullName = typeElement.qualifiedName
                    methodBuilder.addStatement(
                        "plugin = ${ClassName.get(
                            Class::class.java
                        )}.forName(\"${fullName}\").newInstance()"
                    )
                    methodBuilder.addStatement(
                        "pluginCenter.resister(\$S, plugin)",
                        annotation.value
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            methodBuilder.addCode("}catch(\$T e) {}", ClassName.get(Exception::class.java))


            val packageName = "com.tencent.fp.generate"
            val className = "PluginRegister"
            val generatedClass = ClassName.get(packageName, className)

            /**
             * 构建类
             */
            val finderClass = TypeSpec.classBuilder(generatedClass.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build()
            try {
                JavaFile.builder(packageName, finderClass).build().writeTo(filer)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}