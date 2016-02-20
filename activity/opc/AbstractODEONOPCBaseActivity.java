package uk.co.odeon.androidapp.activity.opc;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.AbstractODEONBaseActivity;

public abstract class AbstractODEONOPCBaseActivity extends AbstractODEONBaseActivity {
    protected abstract void configureNavigationHeader();

    protected void showHelpfulHint(String msg) {
        Builder builder = new Builder(this);
        builder.setTitle(getResources().getString(R.string.opc_helpful_hint_title)).setMessage(msg).setCancelable(false).setNeutralButton(getResources().getString(R.string.opc_helpful_hint_button_label), new OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    protected void performRestartOfJoin() {
        ODEONApplication.getInstance().clearCustomerDataInPrefs();
        Intent opcChoosePackage = new Intent(this, OPCChoosePackageActivity.class);
        opcChoosePackage.setFlags(67108864);
        opcChoosePackage.setAction(Constants.ACTION_RESTART_OPC_JOIN);
        startActivity(opcChoosePackage);
    }

    protected void performEndOfJoin() {
        Intent opcChoosePackage = new Intent(this, OPCChoosePackageActivity.class);
        opcChoosePackage.setFlags(67108864);
        opcChoosePackage.setAction(Constants.ACTION_END_OF_OPC_JOIN);
        startActivity(opcChoosePackage);
    }
}
