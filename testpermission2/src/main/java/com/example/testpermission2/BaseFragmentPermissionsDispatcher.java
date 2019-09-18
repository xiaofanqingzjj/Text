//// This file was generated by PermissionsDispatcher. Do not modify!
//package com.example.testpermission2.my;
//
//import android.support.v4.app.Fragment;
//
//import java.lang.ref.WeakReference;
//
//import permissions.dispatcher.PermissionRequest;
//import permissions.dispatcher.PermissionUtils;
//
//final class BaseFragmentPermissionsDispatcher {
//  private static final int REQUEST_REQUESTCAMARA = 3;
//
//  private static final String[] PERMISSION_REQUESTCAMARA = new String[] {"android.permission.CAMERA"};
//
//  private static final int REQUEST_REQUESTLOCATION = 4;
//
//  private static final String[] PERMISSION_REQUESTLOCATION = new String[] {"android.permission.ACCESS_FINE_LOCATION"};
//
//  private static final int REQUEST_REQUESTWRITEEXTERNALSTORAGE = 5;
//
//  private static final String[] PERMISSION_REQUESTWRITEEXTERNALSTORAGE = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};
//
//  private BaseFragmentPermissionsDispatcher() {
//  }
//
//  static void requestCamaraWithPermissionCheck(Fragment target) {
//    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_REQUESTCAMARA)) {
//      target.requestCamara();
//    } else {
//      if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_REQUESTCAMARA)) {
//        target.cameraPermShowRationale(new BaseFragmentRequestCamaraPermissionRequest(target));
//      } else {
//        target.requestPermissions(PERMISSION_REQUESTCAMARA, REQUEST_REQUESTCAMARA);
//      }
//    }
//  }
//
//  static void requestLocationWithPermissionCheck(Fragment target) {
//    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_REQUESTLOCATION)) {
//      target.requestLocation();
//    } else {
//      if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_REQUESTLOCATION)) {
//        target.locationShowRationale(new BaseFragmentRequestLocationPermissionRequest(target));
//      } else {
//        target.requestPermissions(PERMISSION_REQUESTLOCATION, REQUEST_REQUESTLOCATION);
//      }
//    }
//  }
//
//  static void requestWriteExternalStorageWithPermissionCheck(Fragment target) {
//    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_REQUESTWRITEEXTERNALSTORAGE)) {
//      target.requestWriteExternalStorage();
//    } else {
//      if (PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_REQUESTWRITEEXTERNALSTORAGE)) {
//        target.writeExternalStorageShowRationale(new BaseFragmentRequestWriteExternalStoragePermissionRequest(target));
//      } else {
//        target.requestPermissions(PERMISSION_REQUESTWRITEEXTERNALSTORAGE, REQUEST_REQUESTWRITEEXTERNALSTORAGE);
//      }
//    }
//  }
//
//  static void onRequestPermissionsResult(Fragment target, int requestCode, int[] grantResults) {
//    switch (requestCode) {
//      case REQUEST_REQUESTCAMARA:
//      if (PermissionUtils.verifyPermissions(grantResults)) {
//        target.requestCamara();
//      } else {
//        if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_REQUESTCAMARA)) {
//          target.onCameraNeverAsk();
//        } else {
//          target.cameraPermDenied();
//        }
//      }
//      break;
//      case REQUEST_REQUESTLOCATION:
//      if (PermissionUtils.verifyPermissions(grantResults)) {
//        target.requestLocation();
//      } else {
//        if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_REQUESTLOCATION)) {
//          target.locationPermNeverAsk();
//        } else {
//          target.locationPermDenied();
//        }
//      }
//      break;
//      case REQUEST_REQUESTWRITEEXTERNALSTORAGE:
//      if (PermissionUtils.verifyPermissions(grantResults)) {
//        target.requestWriteExternalStorage();
//      } else {
//        if (!PermissionUtils.shouldShowRequestPermissionRationale(target, PERMISSION_REQUESTWRITEEXTERNALSTORAGE)) {
//          target.writeExternalStorageDenied();
//        } else {
//          target.writeStoragePermDenied();
//        }
//      }
//      break;
//      default:
//      break;
//    }
//  }
//
//  private static final class BaseFragmentRequestCamaraPermissionRequest implements PermissionRequest {
//    private final WeakReference<BaseFragment> weakTarget;
//
//    private BaseFragmentRequestCamaraPermissionRequest(BaseFragment target) {
//      this.weakTarget = new WeakReference<BaseFragment>(target);
//    }
//
//    @Override
//    public void proceed() {
//      BaseFragment target = weakTarget.get();
//      if (target == null) return;
//      target.requestPermissions(PERMISSION_REQUESTCAMARA, REQUEST_REQUESTCAMARA);
//    }
//
//    @Override
//    public void cancel() {
//      BaseFragment target = weakTarget.get();
//      if (target == null) return;
//      target.cameraPermDenied();
//    }
//  }
//
//  private static final class BaseFragmentRequestLocationPermissionRequest implements PermissionRequest {
//    private final WeakReference<BaseFragment> weakTarget;
//
//    private BaseFragmentRequestLocationPermissionRequest(BaseFragment target) {
//      this.weakTarget = new WeakReference<BaseFragment>(target);
//    }
//
//    @Override
//    public void proceed() {
//      BaseFragment target = weakTarget.get();
//      if (target == null) return;
//      target.requestPermissions(PERMISSION_REQUESTLOCATION, REQUEST_REQUESTLOCATION);
//    }
//
//    @Override
//    public void cancel() {
//      BaseFragment target = weakTarget.get();
//      if (target == null) return;
//      target.locationPermDenied();
//    }
//  }
//
//  private static final class BaseFragmentRequestWriteExternalStoragePermissionRequest implements PermissionRequest {
//    private final WeakReference<BaseFragment> weakTarget;
//
//    private BaseFragmentRequestWriteExternalStoragePermissionRequest(BaseFragment target) {
//      this.weakTarget = new WeakReference<BaseFragment>(target);
//    }
//
//    @Override
//    public void proceed() {
//      BaseFragment target = weakTarget.get();
//      if (target == null) return;
//      target.requestPermissions(PERMISSION_REQUESTWRITEEXTERNALSTORAGE, REQUEST_REQUESTWRITEEXTERNALSTORAGE);
//    }
//
//    @Override
//    public void cancel() {
//      BaseFragment target = weakTarget.get();
//      if (target == null) return;
//      target.writeStoragePermDenied();
//    }
//  }
//}
