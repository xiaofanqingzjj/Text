package com.tencent.bible.ts.my.new

import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.os.RemoteException
import com.tencent.bible.ts.ITreeService


@Target(AnnotationTarget.CLASS)
annotation class  RemoteApi{

}


interface IFoo {
    fun getName(): String?
}

@RemoteApi
class  FooImpl : IFoo {
    override fun getName(): String? {
        return "Xiao";
    }
}

object FooManager {

    fun getFoo() : IFoo {
        return FooImpl();
    }
}


// 需要自动做一下事情
class  FooBinderProxy(var iFoo: IFoo) :  Binder(), IFoo {


    override fun getName(): String? {
        return iFoo.getName();
    }

    override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {



        return super.onTransact(code, data, reply, flags)
    }


//    /** Local-side IPC implementation stub class.  */
//    abstract class Stub : Binder(), ITreeService {
//        override fun asBinder(): IBinder {
//            return this
//        }
//
//        @Throws(RemoteException::class)
//        public override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
//            val descriptor = interfaceDescriptor!!
//            return when (code) {
//                IBinder.INTERFACE_TRANSACTION -> {
//                    reply!!.writeString(descriptor)
//                    true
//                }
//                TRANSACTION_getLeafServiceBinder -> {
//                    data.enforceInterface(descriptor)
//                    val _arg0: String?
//                    _arg0 = data.readString()
//                    val _result = getLeafServiceBinder(_arg0)
//                    reply!!.writeNoException()
//                    reply.writeStrongBinder(_result)
//                    true
//                }
//                TRANSACTION_sayHello -> {
//                    data.enforceInterface(descriptor)
//                    sayHello()
//                    reply!!.writeNoException()
//                    true
//                }
//                else -> {
//                    super.onTransact(code, data, reply, flags)
//                }
//            }
//        }
//
//        private class Proxy internal constructor(private val mRemote: IBinder) : ITreeService {
//            override fun asBinder(): IBinder {
//                return mRemote
//            }
//
//            @Throws(RemoteException::class)
//            override fun getLeafServiceBinder(serviceName: String): IBinder {
//                val _data = Parcel.obtain()
//                val _reply = Parcel.obtain()
//                val _result: IBinder
//                _result = try {
//                    _data.writeInterfaceToken(interfaceDescriptor)
//                    _data.writeString(serviceName)
//                    val _status = mRemote.transact(ITreeService.Stub.TRANSACTION_getLeafServiceBinder, _data, _reply, 0)
//                    if (!_status && defaultImpl != null) {
//                        return defaultImpl!!.getLeafServiceBinder(serviceName)
//                    }
//                    _reply.readException()
//                    _reply.readStrongBinder()
//                } finally {
//                    _reply.recycle()
//                    _data.recycle()
//                }
//                return _result
//            }
//
//            @Throws(RemoteException::class)
//            override fun sayHello() {
//                val _data = Parcel.obtain()
//                val _reply = Parcel.obtain()
//                try {
//                    _data.writeInterfaceToken(interfaceDescriptor)
//                    val _status = mRemote.transact(ITreeService.Stub.TRANSACTION_sayHello, _data, _reply, 0)
//                    if (!_status && defaultImpl != null) {
//                        defaultImpl!!.sayHello()
//                        return
//                    }
//                    _reply.readException()
//                } finally {
//                    _reply.recycle()
//                    _data.recycle()
//                }
//            }
//
//            companion object {
//                var sDefaultImpl: ITreeService? = null
//            }
//
//        }
//
//        companion object {
//            const val interfaceDescriptor = "com.tencent.bible.ts.ITreeService"
//
//            /**
//             * Cast an IBinder object into an com.tencent.bible.ts.ITreeService interface,
//             * generating a proxy if needed.
//             */
//            fun asInterface(obj: IBinder?): ITreeService? {
//                if (obj == null) {
//                    return null
//                }
//                val iin = obj.queryLocalInterface(interfaceDescriptor)
//                return if (iin != null && iin is ITreeService) {
//                    iin
//                } else ITreeService.Stub.Proxy(obj)
//            }
//
//            const val TRANSACTION_getLeafServiceBinder = IBinder.FIRST_CALL_TRANSACTION + 0
//            const val TRANSACTION_sayHello = IBinder.FIRST_CALL_TRANSACTION + 1
//            fun setDefaultImpl(impl: ITreeService?): Boolean {
//                if (ITreeService.Stub.Proxy.sDefaultImpl == null && impl != null) {
//                    ITreeService.Stub.Proxy.sDefaultImpl = impl
//                    return true
//                }
//                return false
//            }
//
//            val defaultImpl: ITreeService?
//                get() = ITreeService.Stub.Proxy.sDefaultImpl
//        }
//
//        /** Construct the stub at attach it to the interface.  */
//        init {
//            attachInterface(this, interfaceDescriptor)
//        }
//    }

}

