

### lib_native_channel_annotation

一个配合flutter  native_channel组件的工具组件，功能自动注册插件。

### 用法


#### 添加依赖


```gradle
dependencies {
    ...
    implementation "com.tencent.nativechannel:native_channel_annotation:0.0.4"
    kapt "com.tencent.nativechannel:native_channel_annotation:0.0.4"
    ...
}
```

#### 初始化

```java

class YourApplication extends Application {
    void onCreate() {
       // 自动注册Native方法插件
       NativeChannelPluginManagerHelper.registerPlugins();
    }
}

```

### 使用注解注释插件

```java
@FlutterPlugin("app")
class AppPlugin : NativePlugin() {
    ...
}
```