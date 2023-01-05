package com.tencent.strinker

import com.android.build.api.transform.*
import com.android.build.api.transform.QualifiedContent.DefaultContentType.CLASSES
import com.tencent.strinker.util.FileUtils
//import com.tencent.strinker.util.FileUtils
import java.io.File
import java.io.IOException
import java.io.UncheckedIOException

/**
 * Transform允许你在把字节码变成dex之前做一些转换
 *
 */
class InlineRTransform(private val config: ShrinkerConfig) : Transform() {


    /**
     * 转换器的名字
     */
    override fun getName(): String {
        return "InlineRTransform"
    }


    // 输入的数据类型为Class
    override fun getInputTypes(): Set<QualifiedContent.ContentType> {
        // 输入类型为class
        return mutableSetOf<QualifiedContent.ContentType>(CLASSES)
    }


    /**
     *
     * transform的作用域
     *
     * PROJECT(1), 当前工程
     * SUB_PROJECTS(4), 子工程
     * EXTERNAL_LIBRARIES(16), 外部依赖
     * TESTED_CODE(32), 测试代码
     * PROVIDED_ONLY(64), compile only
     * @return
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return if (!config.enableShrink)
            mutableSetOf<QualifiedContent.Scope>()
        else mutableSetOf<QualifiedContent.Scope>(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS,
                QualifiedContent.Scope.EXTERNAL_LIBRARIES)

        // 范围为全部
        // full
    }

    /**
     * 引用作用域
     */
    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> {
        return if (config.enableShrink)
            mutableSetOf<QualifiedContent.Scope>()
        else
            mutableSetOf<QualifiedContent.Scope>(QualifiedContent.Scope.PROJECT)
    }

    override fun isIncremental(): Boolean {
        return false
    }


    /**
     * 是否要压缩
     */
    private fun isShrink(buildType: String?): Boolean {
        if ("release" == buildType) return true
        return config.shrinkBuildType.find { it == buildType } != null
    }

    /**
     * 核心，转换class
     *
     * TransformInput结构，一个TransformInput包括一组jarInput和一组folderInputs
     * {
     *  rootLocation = null,
     *  jarInputs = [
     *      JarInput = {
     *          name,
     *          file,
     *          contentType,
     *          scopes,
     *          status
     *      },
     *      ...
     *  ],
     *  folderInputs = [
     *      dirInput {
     *          name,
     *          file,
     *          contentType,
     *          scopes,
     *          changedFiles = {}
     *      }
     *  ]
     * }
     *
     */
    @Throws(TransformException::class, InterruptedException::class, IOException::class)
    override fun transform(transformInvocation: TransformInvocation) {

        // 如果没有开启，则啥也不做，因为getScope那里也做了判断，如果没有开启，scope返回为空
        if (!config.enableShrink) {
            println("skip enableShrink transform!")
            return
        }

        if (transformInvocation.isIncremental) {
            throw UnsupportedOperationException("Unsupported incremental build!")
        }

        println("start transform")

        val inputs : Collection<TransformInput> = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider


        println("inputs:$inputs")

        // 删除所有的outputs
        outputProvider.deleteAll()

        // 如果不是release或者不是白名单里的buildType，则跳过，直接copy input到output
        if (!isShrink(transformInvocation.context?.variantName)) {
            println("not release buildType, just copy to output")

            // 如果不做shrink，则直接拷贝输入到输出
            copyInputToOutputs(inputs, outputProvider)
            return
        }

        // 第一步收集所有class里的R$xxx类和R.styleables.xxxx
        // 找到所有的R文件，生成表格
        println("collectionAllRFiles")
        val rSymbols = RSymbols.collectAllRFiles(inputs)
//        val rSymbols = RSymbols()
//        rSymbols.from(inputs)
        println("collectionAllRFilesFinish")

        // transforms/${name}/${buildType}/${index of 'styleables'}
        // 返回合并R.styleables.java的目录
        val styleablesSavePath = outputProvider.getContentLocation("styleables", this.inputTypes, this.scopes, Format.DIRECTORY)

        // 生成自己的R.styleables类
        RStyleablesFileGenerate.generate(rSymbols, styleablesSavePath)
        println("generateStyleables files finish")


        // 替换R调用
        inline(
                rSymbols = rSymbols,
                inputs = inputs,
                outputProvider = outputProvider
        )

        println("inline  finish")
    }


    private fun inline(rSymbols: RSymbols, inputs: Collection<TransformInput>, outputProvider: TransformOutputProvider) {

        val inputStreams = inputs.flatMap {
            mutableListOf<QualifiedContent>().apply {
                addAll(it.jarInputs)
                addAll(it.directoryInputs)
            }
        }.parallelStream()

        inputStreams.forEach { input ->

            val src = input.file.toPath()
            val dst = getTargetPath(input, outputProvider).toPath()

            if (input is DirectoryInput) {
                DirProcessor.proceed(rSymbols, src, dst)
            } else if (input is JarInput) {
                JarProcessor.proceed(rSymbols, src, dst)
            }
        }
    }


    // 根据input获取对应的output path
    private fun getTargetPath(input: QualifiedContent, outputProvider: TransformOutputProvider): File {
        val format: Format = when (input) {
            is DirectoryInput -> Format.DIRECTORY
            is JarInput -> Format.JAR
            else -> throw UnsupportedOperationException("Unknown format readAll input $input")
        }

        // TransformOutputProvider提供获取输出路径的方法
        val f = outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, format)

        if (!f.parentFile.exists()) f.parentFile.mkdirs()
        return f
    }

    /**
     * 直接把输入拷贝到输出
     *
     * 所谓的转换，是把编译结果的一种状态变换成另一种状态
     *
     * 编译结果的状态就是一组jar文件和一组class目录的列表
     *
     * output为：<project>/build/intermediates/<YourTransform>/<buildType>/xx.jar，jar名一般是按数字命名，
     *
     *
     *
     */
    private fun copyInputToOutputs(inputs: Collection<TransformInput>, outputProvider: TransformOutputProvider) {

        // 获取所有的编译结果的路径
        val allInputs: List<QualifiedContent> = inputs.flatMap {
            mutableListOf<QualifiedContent>().apply {
                addAll(it.directoryInputs)
                addAll(it.jarInputs)
            }
        };


        allInputs.forEach {

            // 获取对应jar包的输出路径
            val dest = getTargetPath(it, outputProvider)

            println("copyInput to output in:${it.file}, out:${dest}")

            if (it is DirectoryInput) {
                try {
                    // 比较原始，直接把输入路径拷贝到输出路径
                    FileUtils.copyDirectory(it.file, dest)
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            } else if (it is JarInput) {
                if (dest.exists()) {
                    throw RuntimeException("Jar file " + it.name + " already exists!" +
                            " src: " + it.file.path + ", dest: " + dest.path)
                }
                try {
                    FileUtils.copyFile(it.file, dest)
                } catch (e: IOException) {
                    throw UncheckedIOException(e)
                }
            }
        }
//                .parallelStream().forEach {
//            val dest = getTargetPath(it, outputProvider)
//            if (it is DirectoryInput) {
//                try {
//                    FileUtils.copyDirectory(it.file, dest)
//                } catch (e: IOException) {
//                    throw UncheckedIOException(e)
//                }
//            } else if (it is JarInput) {
//                if (dest.exists()) {
//                    throw RuntimeException("Jar file " + it.name + " already exists!" +
//                            " src: " + it.file.path + ", dest: " + dest.path)
//                }
//                try {
//                    FileUtils.copyFile(it.file, dest)
//                } catch (e: IOException) {
//                    throw UncheckedIOException(e)
//                }
//            }
//        }
    }
}