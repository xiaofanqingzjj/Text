//package com.tencent.gamehelper.flutter_plugin.apt
//
//import com.google.devtools.ksp.processing.CodeGenerator
//import com.google.devtools.ksp.processing.KSPLogger
//import com.google.devtools.ksp.processing.Resolver
//import com.google.devtools.ksp.processing.SymbolProcessor
//import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
//import com.google.devtools.ksp.processing.SymbolProcessorProvider
//import com.google.devtools.ksp.symbol.KSAnnotated
//
//
///**
// * 注解处理器
// */
//class FlutterPluginProcessor(
//    val codeGenerator: CodeGenerator,
//    val logger: KSPLogger
//): SymbolProcessor {
//    override fun process(resolver: Resolver): List<KSAnnotated> {
//
//        val symbols = resolver.getSymbolsWithAnnotation("com.tencent.gamehelper.flutter_plugin.annotation.FlutterPlugin")
////        return  symbols;
//
//        return  emptyList();
//
//    }
//}
//
//
///**
// * 实现一个SystemProcessorProvider
// */
//class FlutterPluginProcessorProvider : SymbolProcessorProvider {
//    override fun create(
//        environment: SymbolProcessorEnvironment
//    ): SymbolProcessor {
//        return FlutterPluginProcessor(environment.codeGenerator, environment.logger)
//    }
//}