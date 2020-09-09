//package com.example.test.ipc;
//
//public class Test {
//
//
//    public interface IRemoteServiceInterface extends android.os.IInterface {
//        /**
//         * Local-side IPC implementation stub class.
//         */
//        public static abstract class Stub extends android.os.Binder implements com.example.test.ipc.IRemoteServiceInterface {
//            private static final java.lang.String DESCRIPTOR = "com.example.test.ipc.IRemoteServiceInterface";
//
//            /**
//             * Construct the stub at attach it to the interface.
//             */
//            public Stub() {
//                this.attachInterface(this, DESCRIPTOR);
//            }
//
//            /**
//             * Cast an IBinder object into an com.example.test.ipc.IRemoteServiceInterface interface,
//             * generating a proxy if needed.
//             */
//            public static com.example.test.ipc.IRemoteServiceInterface asInterface(android.os.IBinder obj) {
//                if ((obj == null)) {
//                    return null;
//                }
//                android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
//                if (((iin != null) && (iin instanceof com.example.test.ipc.IRemoteServiceInterface))) {
//                    return ((com.example.test.ipc.IRemoteServiceInterface) iin);
//                }
//                return new com.example.test.ipc.IRemoteServiceInterface.Stub.Proxy(obj);
//            }
//
//            @Override
//            public android.os.IBinder asBinder() {
//                return this;
//            }
//
//            @Override
//            public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
//                java.lang.String descriptor = DESCRIPTOR;
//                switch (code) {
//                    case INTERFACE_TRANSACTION: {
//                        reply.writeString(descriptor);
//                        return true;
//                    }
//                    case TRANSACTION_add: {
//                        data.enforceInterface(descriptor);
//                        int _arg0;
//                        _arg0 = data.readInt();
//                        int _arg1;
//                        _arg1 = data.readInt();
//                        int _result = this.add(_arg0, _arg1);
//                        reply.writeNoException();
//                        reply.writeInt(_result);
//                        return true;
//                    }
//                    case TRANSACTION_setBookName: {
//                        data.enforceInterface(descriptor);
//                        com.example.test.ipc.Book _arg0;
//                        _arg0 = new com.example.test.ipc.Book();
//                        com.example.test.ipc.Book _arg1;
//                        if ((0 != data.readInt())) {
//                            _arg1 = com.example.test.ipc.Book.CREATOR.createFromParcel(data);
//                        } else {
//                            _arg1 = null;
//                        }
//                        com.example.test.ipc.Book _arg2;
//                        if ((0 != data.readInt())) {
//                            _arg2 = com.example.test.ipc.Book.CREATOR.createFromParcel(data);
//                        } else {
//                            _arg2 = null;
//                        }
//                        java.lang.String _arg3;
//                        _arg3 = data.readString();
//                        this.setBookName(_arg0, _arg1, _arg2, _arg3);
//                        reply.writeNoException();
//                        if ((_arg0 != null)) {
//                            reply.writeInt(1);
//                            _arg0.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
//                        } else {
//                            reply.writeInt(0);
//                        }
//                        if ((_arg2 != null)) {
//                            reply.writeInt(1);
//                            _arg2.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
//                        } else {
//                            reply.writeInt(0);
//                        }
//                        return true;
//                    }
//                    case TRANSACTION_requestAllBooks: {
//                        data.enforceInterface(descriptor);
//                        com.example.test.ipc.IRequestBooksCallback _arg0;
//                        _arg0 = com.example.test.ipc.IRequestBooksCallback.Stub.asInterface(data.readStrongBinder());
//                        this.requestAllBooks(_arg0);
//                        reply.writeNoException();
//                        return true;
//                    }
//                    default: {
//                        return super.onTransact(code, data, reply, flags);
//                    }
//                }
//            }
//
//            private static class Proxy implements com.example.test.ipc.IRemoteServiceInterface {
//                private android.os.IBinder mRemote;
//
//                Proxy(android.os.IBinder remote) {
//                    mRemote = remote;
//                }
//
//                @Override
//                public android.os.IBinder asBinder() {
//                    return mRemote;
//                }
//
//                public java.lang.String getInterfaceDescriptor() {
//                    return DESCRIPTOR;
//                }
//
//                @Override
//                public int add(int a, int b) throws android.os.RemoteException {
//                    android.os.Parcel _data = android.os.Parcel.obtain();
//                    android.os.Parcel _reply = android.os.Parcel.obtain();
//                    int _result;
//                    try {
//                        _data.writeInterfaceToken(DESCRIPTOR);
//                        _data.writeInt(a);
//                        _data.writeInt(b);
//                        mRemote.transact(Stub.TRANSACTION_add, _data, _reply, 0);
//                        _reply.readException();
//                        _result = _reply.readInt();
//                    } finally {
//                        _reply.recycle();
//                        _data.recycle();
//                    }
//                    return _result;
//                }
//// 方法传递了一个自定义的类
//
//                @Override
//                public void setBookName(Book book, Book book2, Book book3, String name) throws android.os.RemoteException {
//                    android.os.Parcel _data = android.os.Parcel.obtain();
//                    android.os.Parcel _reply = android.os.Parcel.obtain();
//                    try {
//                        _data.writeInterfaceToken(DESCRIPTOR);
//                        if ((book2 != null)) {
//                            _data.writeInt(1);
//                            book2.writeToParcel(_data, 0);
//                        } else {
//                            _data.writeInt(0);
//                        }
//                        if ((book3 != null)) {
//                            _data.writeInt(1);
//                            book3.writeToParcel(_data, 0);
//                        } else {
//                            _data.writeInt(0);
//                        }
//                        _data.writeString(name);
//                        mRemote.transact(Stub.TRANSACTION_setBookName, _data, _reply, 0);
//                        _reply.readException();
//                        if ((0 != _reply.readInt())) {
//                            book.readFromParcel(_reply);
//                        }
//                        if ((0 != _reply.readInt())) {
//                            book3.readFromParcel(_reply);
//                        }
//                    } finally {
//                        _reply.recycle();
//                        _data.recycle();
//                    }
//                }
//// 一个异步请求方法，
//
//                @Override
//                public void requestAllBooks(com.example.test.ipc.IRequestBooksCallback callback) throws android.os.RemoteException {
//                    android.os.Parcel _data = android.os.Parcel.obtain();
//                    android.os.Parcel _reply = android.os.Parcel.obtain();
//                    try {
//                        _data.writeInterfaceToken(DESCRIPTOR);
//                        _data.writeStrongBinder((((callback != null)) ? (callback.asBinder()) : (null)));
//                        mRemote.transact(Stub.TRANSACTION_requestAllBooks, _data, _reply, 0);
//                        _reply.readException();
//                    } finally {
//                        _reply.recycle();
//                        _data.recycle();
//                    }
//                }
//            }
//
//            static final int TRANSACTION_add = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
//            static final int TRANSACTION_setBookName = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
//            static final int TRANSACTION_requestAllBooks = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
//        }
//
//        public int add(int a, int b) throws android.os.RemoteException;
//// 方法传递了一个自定义的类
//
//        public void setBookName(com.example.test.ipc.Book book, com.example.test.ipc.Book book2, com.example.test.ipc.Book book3, java.lang.String name) throws android.os.RemoteException;
//// 一个异步请求方法，
//
//        public void requestAllBooks(com.example.test.ipc.IRequestBooksCallback callback) throws android.os.RemoteException;
//    }
//}
