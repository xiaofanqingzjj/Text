package some;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;


@TargetApi(Build.VERSION_CODES.M)
public class PermissionProxyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        super.onCreate(savedInstanceState);
        handleIntent(getIntent(), true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i  = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];
            if (grantResult == PackageManager.PERMISSION_GRANTED) {
                PermissionComp.notifyPermissionResult(permission, true, false);
            } else {
                PermissionComp.notifyPermissionResult(permission, false, shouldShowRequestPermissionRationale(permission));
            }
        }
        finish();
    }



    void handleIntent(Intent intent, boolean fromOnCreate) {

        if (!(intent.hasExtra(EXT_PERMISSIONES)
                || intent.getStringArrayListExtra(EXT_PERMISSIONES) == null )&& fromOnCreate) {
            finish();
            return;
        }


        ArrayList<String> permissions =  intent.getStringArrayListExtra(EXT_PERMISSIONES);

        if (permissions != null && permissions.size() > 0) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), 1);
        } else {
            finish();
        }
    }

    private static final String EXT_PERMISSIONES = "permissions";

    static void requestPermissions( Context context, ArrayList<String> permissions) {
        if (context == null || permissions == null || permissions.isEmpty()) return;

        Intent intent = new Intent(context, PermissionProxyActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXT_PERMISSIONES, permissions);

        context.startActivity(intent);
    }
}
