package com.bedrock.module_base.util

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper


/**
 * 执行一个异步操作
 *
 * @param preExecute
 * @param doBackground 在后台线程执行的任务
 * @param postExecute 后台任务执行完成后执行的任务，该任务在ui现场执行
 *
 * @author fortunexiao
 */
fun <ResultType> async(preExecute: (() -> Unit)? = null,
                       doBackground: () -> ResultType?,
                       postExecute: ((result: ResultType?)->Unit)? = null) {


    (object : AsyncTask<Unit, Unit, ResultType>() {
        override fun onPreExecute() {
            preExecute?.invoke()
        }

        override fun doInBackground(vararg params: Unit?): ResultType? {
            return doBackground.invoke()
        }

        override fun onPostExecute(result: ResultType) {
            postExecute?.invoke(result)
        }

    }).execute()

}

/**
 * 异步执行一个操作
 */
fun async(doBackground: () -> Unit) {
    async<Unit>(doBackground = doBackground)
}



//fun runUIThread(action: (()->Unit)?) {
//    runUIThread(action, 0)
//}

private val handler = Handler(Looper.getMainLooper())

/**
 * 在ui线程运行某个任务
 */
@JvmOverloads fun runUIThread(delay: Long = 0, action: (()->Unit)?) {
    handler.postDelayed(Runnable {
        action?.invoke()
    }, delay)
}
