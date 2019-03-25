package com.jovial.jrpn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class fmConfigure extends AppCompatActivity {

    EditText prog_mem;
    CheckBox sync;
    EditText sleep;
    EditText num_reg;
    Spinner orientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // programmatically remove the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);

        // load the spinner with the options
        orientation = (Spinner) findViewById(R.id.orientation);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.orientation, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orientation.setAdapter(adapter);

        // populate widgets with existing values
        prog_mem = (EditText) findViewById(R.id.prog_mem);
        prog_mem.setText(fmMain.prop.getProperty("PrgmMemoryLines"));

        sync = (CheckBox) findViewById(R.id.sync_conv);
        sync.setChecked(Boolean.valueOf(fmMain.prop.getProperty("SyncConversions")));

        sleep = (EditText) findViewById(R.id.sleep_delay);
        sleep.setText(fmMain.prop.getProperty("SleepDelay"));

        num_reg = (EditText) findViewById(R.id.num_reg);
        num_reg.setText(fmMain.prop.getProperty("NumRegisters"));

        switch(fmMain.prop.getProperty("Orientation"))
        {
            case "Auto":
                orientation.setSelection(0);
                break;
            case "Landscape":
                orientation.setSelection(1);
                break;
            case "Portrait":
                orientation.setSelection(2);
                break;
        }
    }

    // write the configuration file back
    public void Save(View v)
    {
        // Warning!  There is no sanity checking going on here!
        fmMain.prop.setProperty("PrgmMemoryLines", prog_mem.getText().toString());
        fmMain.prop.setProperty("SyncConversions", Boolean.toString(sync.isChecked()));
        fmMain.prop.setProperty("SleepDelay", sleep.getText().toString());
        fmMain.prop.setProperty("NumRegisters", num_reg.getText().toString());
        fmMain.prop.setProperty("Orientation", orientation.getSelectedItem().toString());

        File config = new File(getExternalFilesDir(null), "jrpn.config");
        try {
            // save the changes
            fmMain.prop.storeToXML(new FileOutputStream(config), null);

            Toast.makeText(this, getString(R.string.config_save_success), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.config_title));
            builder.setMessage(getString(R.string.config_save_failure)
                    + e.getMessage());
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(getString(R.string.button_ok),
                    new DialogInterface.OnClickListener() {

                        //@Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.show();
            Log.e("JRPN", "Save Prop: " + e.getMessage());
        }
    }

    // let's blow this pop stand!
    public void Close(View v) {
        finish();
    }
}

