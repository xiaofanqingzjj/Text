package com.mingwei.myprocess;

import com.google.auto.service.AutoService;
import com.mingwe.myanno.BindView;
import com.mingwe.myanno.FlutterPlugin;
import com.mingwe.myanno.OnClick;
import com.mingwei.myprocess.model.AnnotatedClass;
import com.mingwei.myprocess.model.BindViewField;
import com.mingwei.myprocess.model.OnClickMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * Created by mingwei on 12/10/16.
 * CSDN:    http://blog.csdn.net/u013045971
 * Github:  https://github.com/gumingwei
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

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
    /**
     * 解析的目标注解集合
     */
    private Map<String, AnnotatedClass> mAnnotatedClassMap = new HashMap<>();

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
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        types.add(FlutterPlugin.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    /**
     *
     *
     * @param annotations 要处理的
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        System.out.println(TAG + ": process, annotations size:" + annotations.size());
        Observable.fromIterable(annotations).forEach((Consumer<TypeElement>) typeElement -> System.out.println("annotion:" + typeElement));

        FlutterPluginAnnotationHandler.handle(roundEnv, mFiler, mElementUtils, mMessager);


//        handleFlutterPluginAnnotation(roundEnv);

//        Observable.fromIterable()

        mAnnotatedClassMap.clear();
        try {
            processBindView(roundEnv);
            processOnClick(roundEnv);
        } catch (IllegalArgumentException e) {
            error(e.getMessage());
            return true;
        }

        try {
            for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
                info("generating file for %s", annotatedClass.getFullClassName());
                annotatedClass.generateFinder().writeTo(mFiler);
            }
        } catch (Exception e) {
            e.printStackTrace();
            error("Generate file failed,reason:%s", e.getMessage());
        }
        return true;
    }


    /**
     *
     * 自动注册Flutter插件
     * @param roundEnv
     */
    private void handleFlutterPluginAnnotation( RoundEnvironment roundEnv) {
        final Set<? extends  Element> plugins = roundEnv.getElementsAnnotatedWith(FlutterPlugin.class);
        plugins.forEach((java.util.function.Consumer<Element>) element -> {
            System.out.println("element:" + element);
            System.out.println("getEnclosingElement: " + element.getEnclosingElement());

            System.out.println("element.getKind():" + element.getKind());
            System.out.println("getSimpleName: " + element.getSimpleName());
            System.out.println("********asType: " + element.asType().getClass());

            TypeElement te = (TypeElement) element;
            System.out.println("getQualifiedName: " + te.getQualifiedName());


//            element.getEnclosingElement();


        });

        /**
         * 自动注册的代码为：
         *
         * class $PluginRegister {
         *  void registers(PluginCenter pluginCenter) {
         *      // findClass
         *      // new Obj
         *      // register obj
         *
         *     PluginCenter.resister(String name, Object plugin)
         *  }
         *
         * }
         *
         */


        // 方法签名
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("registers")
                .addModifiers(Modifier.PUBLIC)
                .addModifiers(Modifier.STATIC)
                .addParameter(PLUGIN_CENTER, "pluginCenter");

        plugins.forEach(new java.util.function.Consumer<Element>() {
            @Override
            public void accept(Element element) {

                FlutterPlugin fp = element.getAnnotation(FlutterPlugin.class);

                try {

                    methodBuilder.addCode("try {");
                    TypeElement typeElement = (TypeElement) element;
                    Name fullName = typeElement.getQualifiedName();
//                    Class clazz = Class.forName(fullName.toString());
//                    Object plugin = clazz.newInstance();

                    methodBuilder.addStatement("$T plugin = $T.forName($S).newInstance()", ClassName.get(Object.class), ClassName.get(Class.class), fullName.toString());

                    methodBuilder.addStatement("pluginCenter.resister($S, plugin)", fp.value());
//                    el
//
//                    Object plugin = element.asType().getClass().newInstance();
//                    methodBuilder.addStatement("pluginCenter.resister($S, $L)", fp.value(), plugin);

                    methodBuilder.addCode("}catch($T e) {}", ClassName.get(Exception.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        String packageName = "com.tencent.fp.generate";
        String className = "$PluginRegister";
        ClassName bindClassName = ClassName.get(packageName, className);


        /**
         * 构建类
         */
        TypeSpec finderClass = TypeSpec.classBuilder(bindClassName.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();


        try {
            JavaFile.builder(packageName, finderClass).build().writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getPackageName(TypeElement type) {
        return mElementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    static final ClassName PLUGIN_CENTER = ClassName.get("com.tencent.fp", "PluginCenter");

//
//    public JavaFile generateFinder() {
//
//        /**
//         * 构建方法
//         *
//         * public void inject(final MainActivity host, Object source, Finder finder) {
//         *
//         * }
//         */
//        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("inject")
//                .addModifiers(Modifier.PUBLIC)
//                .addAnnotation(Override.class)
//                .addParameter(TypeName.get(mClassElement.asType()), "host", Modifier.FINAL)
//                .addParameter(TypeName.OBJECT, "source")
//                .addParameter(TypeUtil.FINDER, "finder");
//        /**
//         * 遍历添加类成员
//         *
//         *  host.mBtn=(Button)finder.findView(source,2131165217);
//         *  host.mBtn2=(Button)finder.findView(source,2131165218);
//         *
//         */
//        for (BindViewField field : mFiled) {
//            methodBuilder.addStatement("host.$N=($T)finder.findView(source,$L)", field.getFieldName()
//                    , ClassName.get(field.getFieldType()), field.getResId());
//        }
//        /**
//         * 声明Listener
//         */
//        if (mMethod.size() > 0) {
//            methodBuilder.addStatement("$T listener", TypeUtil.ONCLICK_LISTENER);
//        }
//
//        for (OnClickMethod method : mMethod) {
//            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
//                    .addSuperinterface(TypeUtil.ONCLICK_LISTENER)
//                    .addMethod(MethodSpec.methodBuilder("onClick")
//                            .addAnnotation(Override.class)
//                            .addModifiers(Modifier.PUBLIC)
//                            .returns(TypeName.VOID)
//                            .addParameter(TypeUtil.ANDROID_VIEW, "view")
//                            .addStatement("host.$N()", method.getMethodName())
//                            .build())
//                    .build();
//            methodBuilder.addStatement("listener = $L ", listener);
//            for (int id : method.ids) {
//                methodBuilder.addStatement("finder.findView(source,$L).setOnClickListener(listener)", id);
//            }
//        }
//
//        String packageName = getPackageName(mClassElement);
//        String className = getClassName(mClassElement, packageName);
//        ClassName bindClassName = ClassName.get(packageName, className);
//
//
//        /**
//         * 构建类
//         */
//        TypeSpec finderClass = TypeSpec.classBuilder(bindClassName.simpleName() + "$$Injector")
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.INJECTOR, TypeName.get(mClassElement.asType())))
//                .addMethod(methodBuilder.build())
//                .build();
//
//
//
//        return JavaFile.builder(packageName, finderClass).build();
//    }




    /**
     * @param roundEnv
     */
    private void processBindView(RoundEnvironment roundEnv) {

        // 查找所有用到BindView的Element
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {

            // 用到BindView的类
            AnnotatedClass annotatedClass = getAnnotatedClass(element);

            // 用到BindView的字段，同时解析出BindView对应的value
            BindViewField field = new BindViewField(element);
            annotatedClass.addField(field);

            System.out.print("p_element=" + element.getSimpleName() + ",p_set=" + element.getModifiers());
        }
    }

    private AnnotatedClass getAnnotatedClass(Element element) {
        // 获取关联的类
        TypeElement encloseElement = (TypeElement) element.getEnclosingElement();

        // 类名
        String fullClassName = encloseElement.getQualifiedName().toString();


        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(fullClassName);

        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(encloseElement, mElementUtils);
            mAnnotatedClassMap.put(fullClassName, annotatedClass);
        }

        return annotatedClass;
    }

    private void processOnClick(RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(OnClick.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            OnClickMethod method = new OnClickMethod(element);
            annotatedClass.addMethod(method);
        }
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }
}