package com.voyah.window.manager

import android.os.Bundle
import android.os.ParcelFileDescriptor
import com.blankj.utilcode.util.LogUtils
import com.voyah.cockpit.window.ICallback
import com.voyah.window.util.BundleUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class WindowHelper {
    companion object {
        const val TAG = "WindowHelper"
        const val METHOD = "method"

        /**
         * sync、 async method result must contain code\message fields
         */
        const val CODE = "code"
        const val MESSAGE = "message"
        const val CODE_METHOD_MISSING = -4
        const val MESSAGE_METHOD_MISSING = "method is missing"

        const val CODE_METHOD_NO_DEFINE = -3
        const val MESSAGE_METHOD_NO_DEFINE = "server method is not define"

        const val CODE_PARAM_ERROR = -2
        const val MESSAGE_PARAM_ERROR = "param is error"

        const val INVALID = -1
        const val MESSAGE_INVALID = "method exec invalid"

        const val VALID = 1
        const val MESSAGE_SUCCESS = "success"
        const val RESULT = "result"

        const val METHOD_CALL = "call"
        //sync call
        const val SHOW_CARD = "showCard"

        //async call
        const val TEST_BLOCK = "testBlock"
        const val GET_FILE = "getFile"

        //register
        const val TEST_REGISTER = "testRegister"

        /**
         * register callback name list, if you want to register a callback, you need to add the name to this list
         * one callback name can only be registered once, if you want to register a callback with the same name, you need to unregister it first
         * one callback also can execute multiple actions, but the action name must be different
         * for example:
         * msgCallback -> action1 Bundle{method=action1, param1=1, param2=2}
         * msgCallback -> action2 Bundle{method=action2, param1=3, param2=4}
         * msgCallback -> action3 Bundle{method=action3, param1=1, param2=2}
         *
         * if action does not contain method, it will register-callback just one event
         * for example:
         * msgCallback -> action Bundle{param1=1, param2=2}
         *
         * if bundle is null, it will register-callback just one event, and do not contain any param
         * for example:
         * msgCallback -> action
         */
        val REGISTER_NAMES = hashSetOf("msgCallback", "stateCallback")
    }

    /**
     * call method name list, if you want to call a method, you need to add the name to this map
     */
    private val syncMap = HashMap<String, (Bundle) -> Bundle>().apply {
        put(SHOW_CARD, ::showCard)
    }

    /**
     * async method name list, if you want to call a method, you need to add the name to this map
     */
    private val asyncMap = HashMap<String, (Bundle, ICallback?) -> Unit>().apply {
        put(TEST_BLOCK, ::testBlock)
        put(GET_FILE, ::getFile)
    }

    private val registerMap = HashMap<String, WeakReference<ICallback>>()

    private var weakReference : WeakReference<VoyahWindowManager> = WeakReference<VoyahWindowManager>(null)

    fun setBinder(windowManager: VoyahWindowManager) {
        weakReference = WeakReference(windowManager)
        weakReference.get()?.bindHelper(this)
    }

    private fun executeTask(task: () -> Any?, onComplete: (Result<Any?>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = runCatching { task() }
            withContext(Dispatchers.Main) {
                onComplete(result)
            }
        }
    }

    fun success(result: Any?): Bundle {
        return BundleUtil().builder().apply {
            put(CODE, VALID)
            put(MESSAGE, MESSAGE_SUCCESS)
            put(RESULT, result)
        }.build()
    }

    fun fail(): Bundle {
        return BundleUtil().builder().apply {
            put(CODE, INVALID)
            put(MESSAGE, MESSAGE_INVALID)
        }.build()
    }

    fun callSync(method: String?, bundle: Bundle): Bundle {
        method?.let {
            return syncMap[it]?.invoke(bundle) ?: Bundle().apply {
                putInt(CODE, CODE_METHOD_NO_DEFINE)
                LogUtils.e("$TAG callSync method not define: $it")
            }
        }
        return Bundle().apply {
            putInt(CODE, CODE_METHOD_MISSING)
            LogUtils.e("$TAG callSync method is missing: ")
        }
    }

    fun callAsync(method: String?, bundle: Bundle, callback: ICallback?) {
        method?.let {
            asyncMap[it]?.invoke(bundle, callback) ?: Bundle().apply {
                putInt(CODE, CODE_METHOD_NO_DEFINE)
                LogUtils.e("$TAG callAsync method not define: $it")
            }
        } ?: run {
            LogUtils.e("$TAG callSync method is missing: ")
        }
    }

    private fun showCard(bundle: Bundle): Bundle {
        LogUtils.d("$TAG showCard: $bundle")
        weakReference.get()?.showCard(bundle.getString("cardInfo"))
        return success(1)
    }

    private fun testBlock(bundle: Bundle, callback: ICallback?) {
        executeTask(
            task = {
                // test
//                delay(1000)
                bundle
            },
            onComplete = { result ->
                callback?.let { ic ->
                    result.fold(
                        onSuccess = { value -> ic.onResult(success(value))},
                        onFailure = { ic.onResult(fail()) }
                    )
                }
            }
        )
    }

    private fun getFile(bundle: Bundle, callback: ICallback?) {
        executeTask(
            task = {
                val filePath = bundle.getString("filePath")
                val file = File(filePath?:"")
                file.exists().let {
                    ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                }
            },
            onComplete = { result ->
                callback?.let { ic ->
                    result.fold(
                        onSuccess = { value -> ic.onResult(success(value))},
                        onFailure = { ic.onResult(fail()) }
                    )
                }
            }
        )
//        val filePath = bundle.getString("filePath")
//        val file = File(filePath?:"")
//        file.exists().let {
//            val pfd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//            callback?.onResult(success(pfd))
//        }
    }

    fun register(name: String?, callback: ICallback?) {
        REGISTER_NAMES.contains(name).let {
            if (!it) {
                LogUtils.e("$TAG register callback not define: $name")
                return
            }
        }
        callback?: run {
            LogUtils.e("$TAG register callback is null: $name")
            return
        }
        registerMap[name]?.let {
            LogUtils.e("$TAG register callback already exist: $name")
            registerMap[name!!] = WeakReference(callback)
        }?: run {
            registerMap.put(name!!, WeakReference(callback))
        }
    }

    fun action(name: String?, bundle: Bundle?) {
        name?: run {
            LogUtils.e("$TAG action name is null: $name")
            return
        }
        registerMap[name]?.get()?.let {
            bundle?.let { b ->
                b.getString(METHOD)?.let { method ->
                    it.onResult(b)
                } ?: run {
                    LogUtils.i("$TAG action method is null: $name")
                    it.onResult(b)
                }
            } ?: run {
                it.onResult(bundle)
                LogUtils.e("$TAG action bundle is null: $name")
            }
        } ?: run {
            LogUtils.e("$TAG action callback not exist: $name")
        }

    }

    fun unregister(name: String?) {
        name?: run {
            LogUtils.e("$TAG unregister callback is null: $name")
            return
        }
        registerMap.remove(name)
    }

    fun destroy() {
        registerMap.clear()
        weakReference.clear()
    }
}