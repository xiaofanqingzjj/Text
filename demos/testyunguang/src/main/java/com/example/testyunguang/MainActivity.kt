package com.example.testyunguang

import android.os.Bundle
import com.bedrock.module_base.MenuActivity
import com.tencent.mars.xlog.Log
import com.tencent.opentelemetry.api.common.Attributes
import com.tencent.opentelemetry.api.config.ConfigConstants
import com.tencent.opentelemetry.api.config.TenantConfig.tenantID
import com.tencent.opentelemetry.api.metrics.BoundLongCounter
import com.tencent.opentelemetry.api.metrics.LongCounter
import com.tencent.opentelemetry.api.metrics.Meter
import com.tencent.opentelemetry.otlp.metrics.OtlpHttpJsonMetricExporter
import com.tencent.opentelemetry.sdk.metrics.SdkMeterProvider
import com.tencent.opentelemetry.sdk.metrics.export.IntervalMetricReader
import com.tencent.opentelemetry.sdk.metrics.export.MetricExporter
import com.tencent.opentelemetry.sdk.metrics.export.MetricProducer
import com.tencent.opentelemetry.sdk.util.ResourcesUtil
import java.util.*


class MainActivity : MenuActivity() {

    companion object {
        const val  TAG = "MainActivityTAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addMenu("Test") {


            Log.i(TAG, "TEST XLOG");

            // 设置导出器
            val meteData: MutableMap<String, String> = HashMap()
            meteData[ConfigConstants.TENANT_KEY] = tenantID
            val metricExporter: MetricExporter = OtlpHttpJsonMetricExporter(ConfigConstants.DEFAULT_METRIC_SERVICE_ADDRESS, meteData)

            // 初始化meterProvider
            val meterProvider = SdkMeterProvider.builder()
                .setResource(ResourcesUtil.buildResource())
                .buildAndRegisterGlobal()

            // 初始化intervalMetricReader
            val intervalMetricReader: IntervalMetricReader = IntervalMetricReader.builder()
                .setMetricExporter(metricExporter)
                .setMetricProducers(Collections.singleton(meterProvider) as Collection<MetricProducer>?)
                .setExportIntervalMillis(10000)
                .buildAndStart()

            // --------------------

            //获取或创建一个命名仪表实例
            val meter: Meter = meterProvider.meterBuilder("instrumentation-library-name")
                .setInstrumentationVersion("1.0.0")
                .build()

//            //建立计数器，例如 长计数器
//            val counter: LongCounter = meter
////                .longCounterBuilder("")
//                .counterBuilder("request_start_total")
//                .setDescription("Total number of request started on the client.")
//                .setUnit("1")
//                .build()
//
//            //建议API用户在整个时间内始终保持对绑定计数器的引用，或者
//            //不再需要时，调用unbind。
//            val someWorkCounter: BoundLongCounter = counter.bind(Attributes.of(Attributes.stringKey("Key"), "SomeWork"))
//
//            //记录数据
//            someWorkCounter.add(123)
//
//            //或者，用户可以使用无界计数器并显式
//            //指定在通话时设置的标签:
//            counter.add(123, Attributes.of(Attributes.stringKey("Key"), "SomeWork"))
        }
    }

}