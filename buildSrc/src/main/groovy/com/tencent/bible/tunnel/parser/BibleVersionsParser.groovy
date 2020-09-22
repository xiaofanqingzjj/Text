package com.tencent.bible.tunnel.parser

import groovy.json.JsonSlurper

class BibleVersionsParser {

    static Map merge(Map[] sources) {
        if (sources.length == 0) return [:]
        if (sources.length == 1) return sources[0]

        sources.inject([:]) { result, source ->
            source.each { k, v ->
                result[k] = result[k] instanceof Map ? merge(result[k], v) : v
            }
            result
        }
    }

    static def parse(File... jsonFiles) {
        ArrayList<Map> jsonList = new ArrayList<>()
        if (jsonFiles != null) {
            jsonFiles.each {
                jsonList.add(parseJSON(it))
            }
        }
        return merge(jsonList as Map[])
    }

    private static def parseJSON(File jsonFile) {
        def jsonSlurper = new JsonSlurper()
        def reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), "UTF-8"))
        def data = jsonSlurper.parse(reader)
        return data
    }

}