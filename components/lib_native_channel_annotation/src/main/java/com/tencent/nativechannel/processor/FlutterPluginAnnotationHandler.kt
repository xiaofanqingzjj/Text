package com.tencent.nativechannel.processor

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import com.tencent.nativechannel.annotation.FlutterPlugin
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
 * 收集类中使用到\@FlutterPlugin的类，创建其对象注册到PluginCenter中
 * class $PluginRegister {
 * void registers() {
 * // findClass
 * // new Obj
 * // register obj
 *
 * // 调用NativeChannelPluginCenter的注册方法
 * com.tencent.native_channel.NativeChannelPluginCenter.resister("name", Plugin());
 * }
 * }
 *
 * com.tencent.native_channel.NativeChannelPluginCenter.resister("name", Plugin());
 */
object FlutterPluginAnnotationHandler {
    /**
     * 代码中用到的PluginCenter类
     */
    private val PLUGIN_CENTER: ClassName = ClassName.get(
        "com.tencent.native_channel",
        "NativeChannelPluginManager"
    )
    private val BaseMethodPlugin: ClassName = ClassName.get(
        "com.tencent.native_channel",
        "NativePlugin"
    )
    private val KEEP: ClassName = ClassName.get("androidx.annotation", "Keep")

    /**
     * 自动注册Flutter插件
     *
     *
     *
     * @param roundEnv
     */
    /*

生成类
@Keep
public class $PluginRegister {
  public static void registers() {
    try {
    	Object plugin;
    	plugin = Class.forName("com.tencent.gamehelper.flutter.plugin.ConfigSkinManagerPlugin").newInstance();
    	NativeChannelPluginManager.resister("ConfigSkin", (NativePlugin)plugin);

    	plugin = Class.forName("com.tencent.gamehelper.flutter.plugin.CalendarReminderPlugin").newInstance();
    	NativeChannelPluginManager.resister("EventReminder", (NativePlugin)plugin);
        ...

    } catch(Exception e) {e.printStackTrace();}}
}

     */
    @JvmStatic
    fun handle(
        roundEnv: RoundEnvironment,
        filer: Filer?,
        elements: Elements?,
        messager: Messager?
    ) {

        // 获取使用了FlutterPlugin注解的元素
        val plugins = roundEnv.getElementsAnnotatedWith(
            FlutterPlugin::class.java
        )

        if (plugins != null && plugins.size > 0) {

            // 方法签名:void registers(PluginCenter pluginCenter)
            val methodBuilder = MethodSpec.methodBuilder("registers")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)


            methodBuilder.addCode("try {\n")
            methodBuilder.addStatement("\t\$T plugin", ClassName.get(Any::class.java))

            // 收集用到了@FlutterPlugin的类
            plugins.forEach { element ->

                // 一个Element表示一个程序元素，这里表示一个类信息

                // 下面可以打印出元素信息
                println("element:$element")
                println("getEnclosingElement: " + element.enclosingElement)
                println("element.getKind():" + element.kind)
                println("getSimpleName: " + element.simpleName)
                println("********asType: " + element.asType().javaClass)

                require(element.kind == ElementKind.CLASS) {
                    String.format(
                        "Only class can be annotated with @%s",
                        FlutterPlugin::class.java.simpleName
                    )
                }

                // FlutterPlugin只能注册到类上，所以这里可以强转成TypeElement
                val typeElement = element as TypeElement

                // 获取注解信息
                val annotation = element.getAnnotation(
                    FlutterPlugin::class.java
                )


                try {

                    /**
                     * 生成如下代码：
                     *
                     * plugin = Class.forName("com.tencent.gamehelper.flutter.plugin.ConfigSkinManagerPlugin").newInstance();
                     * NativeChannelPluginManager.resister("ConfigSkin", (NativePlugin)plugin);
                     */
                    // 创建被注册的类对象
                    val fullName = typeElement.qualifiedName
                    methodBuilder.addStatement("\tplugin = ${ClassName.get(Class::class.java)}.forName($fullName).newInstance()")
                    methodBuilder.addStatement("\t${PLUGIN_CENTER}.resister(${annotation.value}, (${BaseMethodPlugin})plugin)")
                    methodBuilder.addCode("\n")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            methodBuilder.addCode(
                "} catch(\$T e) {e.printStackTrace();}", ClassName.get(
                    Exception::class.java
                )
            )


            val packageName = "com.tencent.nativechannel.generate"

            /*
             * 构建类
             */
            val finderClass = TypeSpec.classBuilder(ClassName.get(packageName, "\$PluginRegister"))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(KEEP)
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