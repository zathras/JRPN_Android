
/*
   Portions of this file copyright 2018 Bill Foote
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.emmetgray.wrpn;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.Properties;

public class fmMain extends AppCompatActivity {

    static Properties prop;
    private CalcState cs;
    private Calculator c;
    private TextView lbFKey, lbGKey, lbCarry, lbOverflow, lbPrgm;
    private DynamicEditText tbDisplay;
    private CalcFace pnCalcFace;
    private final ScaleInfo scaleInfo = new ScaleInfo();
    public static final String TAG = "WRPN";

    public final static int CALC_WIDTH = 512;
    public final static int CALC_HEIGHT = 320;
    public final static int BUTTON_WIDTH = 37;
    public final static int BUTTON_HEIGHT = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // programmatically remove the status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // configure the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(R.string.app_title);

        // resize it a bit... it takes up too much room
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = getResources().getDimensionPixelSize(tv.resourceId);
        // make it 2/3 of the "standard" size
        myToolbar.setMinimumHeight((int) (actionBarHeight * 0.66));
        myToolbar.getLayoutParams().height = (int) (actionBarHeight * 0.66);

        // get the version number
        String version = "1.0";
        try {
            version =  getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            // ignore errors
            Log.e(TAG, "Version: " + e.getMessage());
        }

        // set some defaults if there is no config file.
        prop = new Properties();
        prop.setProperty("NumRegisters", "32");
        prop.setProperty("PrgmMemoryLines", "302");
        prop.setProperty("SleepDelay", "1500");
        prop.setProperty("SyncConversions", "true");
        prop.setProperty("HomeURL", "http://www.wrpn.emmet-gray.com");
        prop.setProperty("Email", "egray1@hot.rr.com");
        prop.setProperty("Version", version);
        prop.setProperty("HelpURL",
                "http://www.wrpn.emmet-gray.com/UsersGuide.html");
        prop.setProperty("Orientation", "Auto");

        File config = new File(getExternalFilesDir(null), "wrpn.config");
        try {
            // load the configuration from the file
            if (config.exists())
                prop.loadFromXML(new FileInputStream(config));
        } catch (Exception e) {
            // ignore errors
            Log.e(TAG, "Properties: " + e.getMessage());
        }

        // doesn't exist or not the right version, then create/overwrite from a prototype file
        if (!config.exists() || !prop.getProperty("Version").equals(version)) {
            BufferedWriter sw = null;
            String line;

            try {
                sw = new BufferedWriter(new FileWriter(config));
                BufferedReader sr = new BufferedReader(new InputStreamReader(
                        this.getResources().openRawResource(R.raw.wrpnconfig)));

                // copy the file
                while ((line = sr.readLine()) != null) {
                    sw.write(line + "\n");
                }
                sr.close();
            } catch (Exception ex) {
                Log.e(TAG, "copy config: " + ex.getMessage());
            } finally {
                if (sw != null) {
                    try {
                        sw.flush();
                        sw.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        // determine the orientation preference (new feature for v6.0.8)
        switch(prop.getProperty("Orientation", "Auto")) {
            case "Landscape":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case "Portrait":
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }

        // load the frequently used Views into variables
        lbFKey = (TextView) findViewById(R.id.lbFKey);
        lbGKey = (TextView) findViewById(R.id.lbGKey);
        lbCarry = (TextView) findViewById(R.id.lbCarry);
        lbOverflow = (TextView) findViewById(R.id.lbOverflow);
        lbPrgm = (TextView) findViewById(R.id.lbPrgm);
        tbDisplay = (DynamicEditText) findViewById(R.id.tbDisplay);
        pnCalcFace = (CalcFace) findViewById(R.id.calc_face);

        // attach our Resize Listener to the layout
        pnCalcFace.SetOnResizeListener(listener);
        pnCalcFace.setFocusable(true);

        cs = new CalcState();
        c = new Calculator(cs);
        LoadState();

        initScreen();

        // Note that doResize() will be called for us after onCreate()
        // is finished.
    }

    private GButton findButton(int id) {
        return (GButton) findViewById(id);
    }

    // Called from onCreate(), this sets up the buttons and other rendered text
    // on the calculator face.

    private void initScreen() {
        GButton bn0 = findButton(R.id.GButton0);
        GButton bn1 = findButton(R.id.GButton1);
        GButton bn2 = findButton(R.id.GButton2);
        GButton bn3 = findButton(R.id.GButton3);
        GButton bn4 = findButton(R.id.GButton4);
        GButton bn5 = findButton(R.id.GButton5);
        GButton bn6 = findButton(R.id.GButton6);
        GButton bn7 = findButton(R.id.GButton7);
        GButton bn8 = findButton(R.id.GButton8);
        GButton bn9 = findButton(R.id.GButton9);
        GButton bnA = findButton(R.id.GButtonA);
        GButton bnB = findButton(R.id.GButtonB);
        GButton bnBIN = findButton(R.id.GButtonBIN);
        GButton bnBSP = findButton(R.id.GButtonBSP);
        GButton bnC = findButton(R.id.GButtonC);
        GButton bnCHS = findButton(R.id.GButtonCHS);
        GButton bnD = findButton(R.id.GButtonD);
        GButton bnDEC = findButton(R.id.GButtonDEC);
        GButton bnDiv = findButton(R.id.GButtonDiv);
        GButton bnDp = findButton(R.id.GButtonDp);
        GButton bnE = findButton(R.id.GButtonE);
        GButton bnEnt = findButton(R.id.GButtonEnt);
        GButton bnF = findButton(R.id.GButtonF);
        GButton bnFKey = findButton(R.id.GButtonFKey);
        GButton bnGKey = findButton(R.id.GButtonGKey);
        GButton bnGSB = findButton(R.id.GButtonGSB);
        GButton bnGTO = findButton(R.id.GButtonGTO);
        GButton bnHEX = findButton(R.id.GButtonHEX);
        GButton bnMin = findButton(R.id.GButtonMin);
        GButton bnMul = findButton(R.id.GButtonMul);
        GButton bnOCT = findButton(R.id.GButtonOCT);
        GButton bnON = findButton(R.id.GButtonOn);
        GButton bnPls = findButton(R.id.GButtonPls);
        GButton bnRCL = findButton(R.id.GButtonRCL);
        GButton bnRS = findButton(R.id.GButtonRS);
        GButton bnRol = findButton(R.id.GButtonRol);
        GButton bnSST = findButton(R.id.GButtonSST);
        GButton bnSTO = findButton(R.id.GButtonSTO);
        GButton bnXY = findButton(R.id.GButtonXY);

        bnA.setWhiteLabel("A");
        bnA.setBlueLabel("LJ");
        bnB.setWhiteLabel("B");
        bnB.setBlueLabel("ASR");
        bnC.setWhiteLabel("C");
        bnC.setBlueLabel("RLC");
        bnD.setWhiteLabel("D");
        bnD.setBlueLabel("RRC");
        bnE.setWhiteLabel("E");
        bnE.setBlueLabel("RCLn");
        bnF.setWhiteLabel("F");
        bnF.setBlueLabel("RRCn");
        bn7.setWhiteLabel("7");
        bn7.setBlueLabel("#B");
        bn8.setWhiteLabel("8");
        bn8.setBlueLabel("ABS");
        bn9.setWhiteLabel("9");
        bn9.setBlueLabel("DBLR");
        bnDiv.setWhiteLabel("\u00F7");  // ÷
        bnDiv.setBlueLabel("DBL\u00F7"); // DBL÷
        bnGSB.setWhiteLabel("GSB");
        bnGSB.setBlueLabel("RTN");
        bnGTO.setWhiteLabel("GTO");
        bnGTO.setBlueLabel("LBL");
        bnHEX.setWhiteLabel("HEX");
        bnHEX.setBlueLabel("DSZ");
        bnDEC.setWhiteLabel("DEC");
        bnDEC.setBlueLabel("ISZ");
        bnOCT.setWhiteLabel("OCT");
        bnOCT.setBlueLabel("\u221Ax\u0305");   // √x with "combining overline"
        bnBIN.setWhiteLabel("BIN");
        bnBIN.setBlueLabel("1/x");
        bn4.setWhiteLabel("4");
        bn4.setBlueLabel("SF");
        bn5.setWhiteLabel("5");
        bn5.setBlueLabel("CF");
        bn6.setWhiteLabel("6");
        bn6.setBlueLabel("F?");
        bnMul.setWhiteLabel("X");
        bnMul.setBlueLabel("DBL\u00D7");  // DBL×
        bnRS.setWhiteLabel("R/S");
        bnRS.setBlueLabel("P/R");
        bnSST.setWhiteLabel("SST");
        bnSST.setBlueLabel("BST");
        bnRol.setWhiteLabel("R\u2193");  // R↓
        bnRol.setBlueLabel("R\u2191");   // R↑
        bnXY.setWhiteLabel("x\u21FFy");  // x⇿y
        bnXY.setBlueLabel("PSE");
        bnBSP.setWhiteLabel("BSP");
        bnBSP.setBlueLabel("CLx");
        bnEnt.setWhiteLabel("ENTER");
        bnEnt.setBlueLabel("LSTx");
        bn1.setWhiteLabel("1");
        bn1.setBlueLabel("x\u2264y");  // x≤y
        bn2.setWhiteLabel("2");
        bn2.setBlueLabel("x<0");
        bn3.setWhiteLabel("3");
        bn3.setBlueLabel("x>y");
        bnMin.setWhiteLabel("-");
        bnMin.setBlueLabel("x>0");
        bnON.setWhiteLabel("ON");
        bnON.setBlueLabel("");
        bnFKey.setWhiteLabel("f");
        bnFKey.setBlueLabel("");
        bnGKey.setWhiteLabel("g");
        bnGKey.setBlueLabel("");
        bnSTO.setWhiteLabel("STO");
        bnSTO.setBlueLabel("<");
        bnRCL.setWhiteLabel("RCL");
        bnRCL.setBlueLabel(">");
        bn0.setWhiteLabel("0");
        bn0.setBlueLabel("x\u2260y");  // x≠y
        bnDp.setWhiteLabel("\u2219");   // ∙
        bnDp.setBlueLabel("x\u22600");  // x≠0
        bnCHS.setWhiteLabel("CHS");
        bnCHS.setBlueLabel("x=y");
        bnPls.setWhiteLabel("+");
        bnPls.setBlueLabel("x=0");
        pnCalcFace.yellowText = new CalcFace.YellowText[] {
                new CalcFace.YellowText(bnA, "SL"),
                new CalcFace.YellowText(bnB, "SR"),
                new CalcFace.YellowText(bnC, "RL"),
                new CalcFace.YellowText(bnD, "RR"),
                new CalcFace.YellowText(bnE, "RLn"),
                new CalcFace.YellowText(bnF, "RRn"),
                new CalcFace.YellowText(bn7, "MASKL"),
                new CalcFace.YellowText(bn8, "MASKR"),
                new CalcFace.YellowText(bn9, "RMD"),
                new CalcFace.YellowText(bnDiv, "XOR"),
                new CalcFace.YellowText(bnGSB, "x\u21FF(i)"),  // x⇿(i)
                new CalcFace.YellowText(bnGTO, "x\u21FFI"),  // x⇿I
                new CalcFace.YellowMultiText(bnHEX, bnBIN, 0, "SHOW"),
                new CalcFace.YellowText(bn4, "SB"),
                new CalcFace.YellowText(bn5, "CB"),
                new CalcFace.YellowText(bn6, "B?"),
                new CalcFace.YellowText(bnMul, "AND"),
                new CalcFace.YellowText(bnRS, "(i)"),
                new CalcFace.YellowText(bnSST, "I"),
                new CalcFace.YellowMultiText(bnRol, bnBSP, 1, "CLEAR"),
                new CalcFace.YellowText(bnRol, "PRGM"),
                new CalcFace.YellowText(bnXY, "REG"),
                new CalcFace.YellowText(bnBSP, "PREFIX"),
                new CalcFace.YellowText(bnEnt, "WINDOW"),
                new CalcFace.YellowMultiText(bn1, bn3, 1, "SET COMPL"),
                new CalcFace.YellowText(bn1, "1'S"),
                new CalcFace.YellowText(bn2, "2'S"),
                new CalcFace.YellowText(bn3, "UNSGN"),
                new CalcFace.YellowText(bnMin, "NOT"),
                new CalcFace.YellowText(bnSTO, "WSIZE"),
                new CalcFace.YellowText(bnRCL, "FLOAT"),
                new CalcFace.YellowText(bn0, "MEM"),
                new CalcFace.YellowText(bnDp, "STATUS"),
                new CalcFace.YellowText(bnCHS, "EEX"),
                new CalcFace.YellowText(bnPls, "OR")
        };
        pnCalcFace.setScaleInfo(scaleInfo);
        GButton.setupScaleInfo(scaleInfo);
        for (int i = 0; i < pnCalcFace.getChildCount(); i++) {
            View v = pnCalcFace.getChildAt(i);
            if (v instanceof GButton) {
                GButton btn = (GButton) v;
                btn.setScaleInfo(scaleInfo);
            }
        }
    }


    // if coming back from a pause
    @Override
    protected void onResume() {
        super.onResume();

        // load from internal preferences
        LoadInternalState();
    }

    // being overridden by another app
    @Override
    protected void onPause() {
        // save it to the internal preferences
        try {
            SaveInternalState();
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        super.onPause();
    }

    // being killed off
    @Override
    protected void onStop() {
        // store State to the default file
        if (cs.isSaveOnExit()) {
            try {
                SaveState();
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.file_open:
                // create a list of files at the correct location
                final File datadir = getExternalFilesDir(null);
                FilterBy filter = new FilterBy("xml");
                final String[] files = datadir.list(filter);

                AlertDialog.Builder open_file = new AlertDialog.Builder(this);
                open_file.setTitle(getString(R.string.dialog_open_title));
                open_file.setItems(files, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int item) {
                        File fullpath = new File(datadir, files[item]);
                        LoadState(fullpath.getPath());
                        ProcessPacket(c.ProcessKey(-1));
                    }
                });
                open_file.show();
                return true;
            case R.id.file_save:
                SaveState();
                return true;
            case R.id.file_saveas:
                AlertDialog.Builder save_file = new AlertDialog.Builder(this);
                save_file.setTitle(getString(R.string.dialog_save_title));
                save_file.setMessage(getString(R.string.dialog_save_msg));
                // we need a text box for entering the file name
                final EditText input = new EditText(this);
                save_file.setView(input);
                save_file.setPositiveButton(getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                String filename = input.getText().toString();
                                if (!filename.toLowerCase(Locale.US).endsWith(".xml")) {
                                    filename += ".xml";
                                }
                                File fullpath = new File(getExternalFilesDir(null), filename);
                                SaveState(fullpath.getPath());
                            }
                        });
                save_file.setNegativeButton(getString(R.string.button_cancel),
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // Canceled
                            }
                        });
                save_file.show();
                return true;
            case R.id.file_exit:
                finish();
                return true;
            case R.id.mode_float:
                cs.setOpMode(CalcState.CalcOpMode.Float);
                cs.setFloatPrecision(3);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.mode_hex:
                cs.setOpMode(CalcState.CalcOpMode.Hex);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.mode_dec:
                cs.setOpMode(CalcState.CalcOpMode.Dec);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.mode_oct:
                cs.setOpMode(CalcState.CalcOpMode.Oct);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.mode_bin:
                cs.setOpMode(CalcState.CalcOpMode.Bin);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.mode_si:
                cs.setOpMode(CalcState.CalcOpMode.Float);
                if (cs.getFloatPrecision() == Calculator.k.KeyDp.index()) {
                    cs.setFloatPrecision(3);
                } else {
                    cs.setFloatPrecision(Calculator.k.KeyDp.index());
                }
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_clear:
                LoadState(this.getResources().openRawResource(R.raw.calcstate));
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_save:
                cs.setSaveOnExit(!cs.isSaveOnExit());
                return true;
            case R.id.opt_8bit:
                cs.setWordSize(8);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_16bit:
                cs.setWordSize(16);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_32bit:
                cs.setWordSize(32);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_64bit:
                cs.setWordSize(64);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_1s:
                cs.setArithMode(CalcState.CalcArithMode.OnesComp);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_2s:
                cs.setArithMode(CalcState.CalcArithMode.TwosComp);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.opt_unsigned:
                cs.setArithMode(CalcState.CalcArithMode.Unsigned);
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.flag0:
                cs.setFlag(CalcState.CalcFlag.User0,
                        !cs.isFlag(CalcState.CalcFlag.User0));
                return true;
            case R.id.flag1:
                cs.setFlag(CalcState.CalcFlag.User1,
                        !cs.isFlag(CalcState.CalcFlag.User1));
                return true;
            case R.id.flag2:
                cs.setFlag(CalcState.CalcFlag.User2,
                        !cs.isFlag(CalcState.CalcFlag.User2));
                return true;
            case R.id.flagZeros:
                cs.setFlag(CalcState.CalcFlag.LeadingZero,
                        !cs.isFlag(CalcState.CalcFlag.LeadingZero));
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.flagCarry:
                cs.setFlag(CalcState.CalcFlag.Carry,
                        !cs.isFlag(CalcState.CalcFlag.Carry));
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.flagOverflow:
                cs.setFlag(CalcState.CalcFlag.Overflow,
                        !cs.isFlag(CalcState.CalcFlag.Overflow));
                ProcessPacket(c.ProcessKey(-1));
                return true;
            case R.id.config:
                Intent myConfigIntent = new Intent(this, fmConfigure.class);
                startActivity(myConfigIntent);
                return true;
            case R.id.help_content:
                Uri uri = Uri.parse(prop.getProperty("HelpURL"));
                Intent myContentIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(myContentIntent);
                return true;
            case R.id.help_about:
                Intent myAboutIntent = new Intent(this, fmAbout.class);
                startActivity(myAboutIntent);
                return true;
            case R.id.help_backpanel:
                Intent myBackPanelIntent = new Intent(this, fmBackPanel.class);
                startActivity(myBackPanelIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Load the saved internal Calculator State
    private void LoadInternalState() {
        String config;
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        config = preferences.getString("CalcState", null);
        if (config != null) {
            try {
                cs.Deserialize(config);
            } catch (Exception e) {
                Log.e(TAG, "LoadInternalState: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // do we need to restart a program?
        if (preferences.getBoolean("WasRunning", false)) {
            // OK, I'll admit... this is extremely unlikely, but hey...
            ProcessPacket(c.ProcessKey(Calculator.k.KeyRS.index()));
        }
    }

    // Load the saved Calculator State from the default file
    private void LoadState() {
        File CalcState = new File(getExternalFilesDir(null), "CalcState.xml");
        if (CalcState.exists()) {
            LoadState(CalcState.getPath());
        }
    }

    // Load the saved Calculator State from a file
    private void LoadState(String FileName) {
        BufferedReader sr = null;

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            sr = new BufferedReader(new FileReader(FileName));
            while ((line = sr.readLine()) != null) {
                sb.append(line);
            }
            cs.Deserialize(sb.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error_title_read));
            builder.setMessage(getString(R.string.error_msg_read)
                    + ex.getMessage());
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(getString(R.string.button_ok),
                    new DialogInterface.OnClickListener() {

                        //@Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.show();
        } finally {
            if (sr != null) {
                try {
                    sr.close();
                } catch (IOException e) {
                }
            }
        }
    }

    // Load a saved state from a resource stream
    private void LoadState(InputStream stream) {
        BufferedReader sr = null;

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            sr = new BufferedReader(new InputStreamReader(stream));

            while ((line = sr.readLine()) != null) {
                sb.append(line);
            }
            cs.Deserialize(sb.toString());
        } catch (Exception ex) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error_title_read));
            builder.setMessage(getString(R.string.error_msg_read)
                    + ex.getMessage());
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(getString(R.string.button_ok),
                    new DialogInterface.OnClickListener() {

                        //@Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.show();
        } finally {
            if (sr != null) {
                try {
                    sr.close();
                } catch (IOException e) {
                }
            }
        }
    }

    // Save the Calculator State
    private void SaveInternalState() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        try {
            editor.putBoolean("WasRunning", cs.isPrgmRunning());

            // stop any running application
            if (cs.isPrgmRunning()) {
                cs.setPrgmRunning(false);
                Thread.sleep(100);
            }
            editor.putString("CalcState", cs.Serialize());
        } catch (Exception e) {
            Log.e(TAG, "SaveState: " + e.getMessage());
        }

        // Commit to storage
        editor.apply();
    }

    // Save the Calculator state to the default file
    private void SaveState() {
        File CalcState = new File(getExternalFilesDir(null), "CalcState.xml");
        SaveState(CalcState.getPath());
    }

    // Save the Calculator state to a named file
    private void SaveState(String FileName) {
        BufferedWriter sw = null;

        try {
            sw = new BufferedWriter(new FileWriter(FileName));
            sw.write(cs.Serialize());
        } catch (Exception ex) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.error_title_save));
            builder.setMessage(getString(R.string.error_msg_save)
                    + ex.getMessage());
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setPositiveButton(getString(R.string.button_ok),
                    new DialogInterface.OnClickListener() {

                        //@Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            builder.show();
        } finally {
            if (sw != null) {
                try {
                    sw.flush();
                    sw.close();
                } catch (IOException e) {
                }
            }
        }
    }

    // Map a few keys to their corresponding buttons
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                ProcessPacket(c.ProcessKey(Calculator.k.Key0.index()));
                break;
            case KeyEvent.KEYCODE_1:
                ProcessPacket(c.ProcessKey(Calculator.k.Key1.index()));
                break;
            case KeyEvent.KEYCODE_2:
                ProcessPacket(c.ProcessKey(Calculator.k.Key2.index()));
                break;
            case KeyEvent.KEYCODE_3:
                ProcessPacket(c.ProcessKey(Calculator.k.Key3.index()));
                break;
            case KeyEvent.KEYCODE_4:
                ProcessPacket(c.ProcessKey(Calculator.k.Key4.index()));
                break;
            case KeyEvent.KEYCODE_5:
                ProcessPacket(c.ProcessKey(Calculator.k.Key5.index()));
                break;
            case KeyEvent.KEYCODE_6:
                ProcessPacket(c.ProcessKey(Calculator.k.Key6.index()));
                break;
            case KeyEvent.KEYCODE_7:
                ProcessPacket(c.ProcessKey(Calculator.k.Key7.index()));
                break;
            case KeyEvent.KEYCODE_8:
                ProcessPacket(c.ProcessKey(Calculator.k.Key8.index()));
                break;
            case KeyEvent.KEYCODE_9:
                ProcessPacket(c.ProcessKey(Calculator.k.Key9.index()));
                break;
            case KeyEvent.KEYCODE_A:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyA.index()));
                break;
            case KeyEvent.KEYCODE_B:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyB.index()));
                break;
            case KeyEvent.KEYCODE_C:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyC.index()));
                break;
            case KeyEvent.KEYCODE_D:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyD.index()));
                break;
            case KeyEvent.KEYCODE_E:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyE.index()));
                break;
            case KeyEvent.KEYCODE_F:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyF.index()));
                break;
            case KeyEvent.KEYCODE_PLUS:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyAdd.index()));
                break;
            case KeyEvent.KEYCODE_MINUS:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyMin.index()));
                break;
            case KeyEvent.KEYCODE_STAR:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyMul.index()));
                break;
            case KeyEvent.KEYCODE_SLASH:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyDiv.index()));
                break;
            case KeyEvent.KEYCODE_PERIOD:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyDp.index()));
                break;
            case KeyEvent.KEYCODE_ENTER:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyEnt.index()));
                break;
            case KeyEvent.KEYCODE_DEL:
                ProcessPacket(c.ProcessKey(Calculator.k.KeyBSP.index()));
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    private void doResize(int w, int h) {
        if (h < w) {
            // landscape mode
            if (512 * w / CALC_WIDTH <= 512 * h / CALC_HEIGHT) {
                scaleInfo.drawScaleNumerator = w;
                scaleInfo.drawScaleDenominator = CALC_WIDTH;
            } else {
                scaleInfo.drawScaleNumerator = h;
                scaleInfo.drawScaleDenominator = CALC_HEIGHT;
            }
            scaleInfo.drawScaleNumeratorX = w;
            scaleInfo.drawScaleDenominatorX = CALC_WIDTH;
            scaleInfo.drawScaleNumeratorY = h;
            scaleInfo.drawScaleDenominatorY = CALC_HEIGHT;
        } else {
            // portrait mode.  The calculator face image for portrait
            // mode has width CALC_HEIGHT, and height CALC_WIDTH
            if (512 * h / CALC_WIDTH <= 512 * w / CALC_HEIGHT) {
                scaleInfo.drawScaleNumerator = h;
                scaleInfo.drawScaleDenominator = CALC_WIDTH;
            } else {
                scaleInfo.drawScaleNumerator = w;
                scaleInfo.drawScaleDenominator = CALC_HEIGHT;
            }
            scaleInfo.drawScaleNumeratorX = w;
            scaleInfo.drawScaleDenominatorX = CALC_HEIGHT;
            scaleInfo.drawScaleNumeratorY = h;
            scaleInfo.drawScaleDenominatorY = CALC_WIDTH;
        }
        GButton.setupScaleInfo(scaleInfo);

        int x, y, width, height;

        // loop thru all of the child views
        for (int i = 0; i < pnCalcFace.getChildCount(); i++) {
            View v = pnCalcFace.getChildAt(i);

            // do the EditText (there's only one)
            if (v instanceof DynamicEditText) {
                DynamicEditText et = (DynamicEditText) v;
                et.setPadding(scaleInfo.scale(6), 0, 0, 0);

                if (h > w) {
                    // portrait mode
                    x = 27 * w / CALC_HEIGHT;
                    y = 26 * h / CALC_WIDTH;
                    height = (50 * h / CALC_WIDTH) -3;
                    width = 266 * w / CALC_HEIGHT;
                } else {
                    // landscape mode
                    x = 54 * w / CALC_WIDTH;
                    y = 26 * h / CALC_HEIGHT;
                    height = (50 * h / CALC_HEIGHT) -3;
                    width = 320 * w / CALC_WIDTH;
                }

                //et.layout(x, y, x + width, y + height);
                pnCalcFace.updateViewLayout(et, new AbsoluteLayout.LayoutParams(width,
                        height, x, y));

                et.setTextSizes(scaleInfo, width);
                continue;
            }

            // do the TextViews (there are 5)
            if (v instanceof TextView) {
                TextView tv = (TextView) v;

                if (h > w) {
                    // the originalX location is stored in the Tag
                    x = Integer.parseInt(tv.getTag().toString()) * w
                            / CALC_HEIGHT;

                    y = 55 * h / CALC_WIDTH;
                    width = 30 * w / CALC_HEIGHT;
                    height = AbsoluteLayout.LayoutParams.WRAP_CONTENT;
                } else {
                    x = Integer.parseInt(tv.getTag().toString()) * w
                            / CALC_WIDTH;
                    y = 55 * h / CALC_HEIGHT;
                    width = 30 * w / CALC_WIDTH;
                    height = AbsoluteLayout.LayoutParams.WRAP_CONTENT;
                }

                //tv.layout(x, y, x + width, y + height);
                pnCalcFace.updateViewLayout(tv, new AbsoluteLayout.LayoutParams(width,
                        height, x, y));

                tv.setTextSize(scaleInfo.scale(40) / 10f);
                continue;
            }

            // do the buttons (there are 39)
            if (v instanceof GButton) {
                GButton btn = (GButton) v;

                if (h > w) {
                    // portrait mode
                    x = btn.getOriginalX() * w / CALC_HEIGHT;
                    y = btn.getOriginalY() * h / CALC_WIDTH;
                    if (btn instanceof GButtonEnter) {
                        height = 89 * h / CALC_WIDTH;
                    } else {
                        height = BUTTON_HEIGHT * h / CALC_WIDTH;
                    }
                    width = BUTTON_WIDTH * w / CALC_HEIGHT;
                } else {
                    // landscape mode
                    x = btn.getOriginalX() * w / CALC_WIDTH;
                    y = btn.getOriginalY() * h / CALC_HEIGHT;
                    if (btn instanceof GButtonEnter) {
                        height = 84 * h / CALC_HEIGHT;
                    } else {
                        height = BUTTON_HEIGHT * h / CALC_HEIGHT;
                    }
                    width = BUTTON_WIDTH * w / CALC_WIDTH;
                }
                pnCalcFace.updateViewLayout(btn, new AbsoluteLayout.LayoutParams(width,
                        height, x, y));
                btn.alignText(width, height);
            }
        }

        pnCalcFace.resize();  // Sets scaleInfo.yellowPaint
        // process a dummy key to refresh the display
        ProcessPacket(c.ProcessKey(-1));
    }


    // handle all of the resizing here.  I like precise control over
    // the location and sizes.
    OnResizeListener listener = new OnResizeListener() {
        @Override
        public void OnResize(int id, int w, int h, int oldw, int oldh) {
            doResize(w, h);
        }
    };

    // the button click event
    public void GButton_Click(View v) {
        int KeyCode;
        DisplayPacket pkt;

        GButton bn = (GButton) v;
        KeyCode = bn.getKeyCode();

        // any keystroke will terminate a running program
        cs.setPrgmRunning(false);

        // Send the keystrokes to the calculator engine
        pkt = c.ProcessKey(KeyCode);

        // show the results in the GUI
        ProcessPacket(pkt);

        // should we start a program?
        if (pkt.getStart() == DisplayPacket.StartType.RunProgram) {
            // fire up a background thread to run our program
            Thread t = new Thread(RunProgram);
            t.start();
        } else if (pkt.getStart() == DisplayPacket.StartType.RunLine) {
            // just run one line at a time
            RunLine();
        }
    }

    // process the return packet
    private void ProcessPacket(final DisplayPacket pkt) {
        // Display the Shifted Annunciators
        if (pkt.isF_Annunciator()) {
            lbFKey.setText("f");
        } else {
            lbFKey.setText("");
        }
        if (pkt.isG_Annunciator()) {
            lbGKey.setText("g");
        } else {
            lbGKey.setText("");
        }

        // Display the System Flag Annunciators
        if (pkt.isCarry_Annunciator()) {
            lbCarry.setText("C");
        } else {
            lbCarry.setText("");
        }
        if (pkt.isOverflow_Annunciator()) {
            lbOverflow.setText("G");
        } else {
            lbOverflow.setText("");
        }

        // Display the Program Annunciator
        if (pkt.isPrgm_Annunciator()) {
            lbPrgm.setText("PRGM");
        } else {
            lbPrgm.setText("");
        }

        // Did the engine ask for a beep?
        if (!cs.isPrgmRunning() && pkt.isBeep()) {
            // based upon crash reports
            try {
                ToneGenerator toneGenerator = new ToneGenerator(
                        AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
            } catch (final Exception e) {
                // ignore
            }
        }

        // Optionally show an alternate message for a short time
        if (pkt.getDelay() > 0) {
            tbDisplay.setText(pkt.getAlternateText());

            Thread t = new Thread() {

                @Override
                public void run() {
                    // delay before replacing the display text
                    tbDisplay.postDelayed(new Runnable() {

                        //@Override
                        public void run() {
                            tbDisplay.setText(pkt.getDisplayText());

                        }
                    }, pkt.getDelay());
                }
            };
            t.start();
        } else {
            if (pkt.getAlternateText() == null
                    || pkt.getAlternateText().length() == 0) {
                tbDisplay.setText(pkt.getDisplayText());
            } else {
                tbDisplay.setText(pkt.getAlternateText());
            }
        }
    }

    // change the calculator display in a thread-safe way
    private void SetDisplayText(final String text) {
        new Thread(new Runnable() {

            public void run() {
                // run this on the GUI thread
                // v6.0.7 - 22 Jul 2015
                runOnUiThread(new Runnable() {

                    public void run() {
                        tbDisplay.setText(text);
                    }
                });
            }
        }).start();
    }

    // Run a program starting at the current line number
    Runnable RunProgram = new Runnable() {

        //@Override
        public void run() {
            while (cs.getPrgmPosition() < cs.getPrgmMemory().size()) {
                // execute the instructions
                if (RunLine()) {
                    // Some error occurred
                    break;
                }

                // prepare to process the next line
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);

                // stop if somebody pressed a key
                if (cs.isPrgmRunning() == false) {
                    break;
                }
            }
            // v6.0.7 - 22 Jul 2015
            ProcessPacket(c.ProcessKey(-1));
        }
    };

    // Execute instructions at the current program line
    private boolean RunLine() {
        String line;
        int k1, k2, k3;
        final DisplayPacket p;

        // A quick sanity check
        // v6.0.2 - 26 Apr 12
        if (cs.getPrgmPosition() < 0 || cs.getPrgmPosition() >= cs.getPrgmMemory().size()) {
            // if you "step off the edge", then just stop
            return true;
        }
        line = cs.getPrgmMemory().get(cs.getPrgmPosition());

        // Go to the current line and process the keys found there.
        // The line will be in 1 of 3 formats
        if (line.startsWith("      ")) {
            try {
                k1 = Integer.parseInt(line.substring(6, 8).trim(), 16);
            } catch (final Exception e) {
                // I'm anticipating that folks might edit the XML by hand
                // to make minor tweaks to a program. So, we have to be
                // ready for a corrupted file
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        this);
                builder.setTitle(getString(R.string.error_prgm_title));
                builder.setMessage(getString(R.string.error_prgm_msg)
                        + cs.getPrgmPosition() + "\n" + e.getMessage());
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton(getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {

                            //@Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        });
                builder.show();
                return true;
            }
            p = c.ProcessKey(k1);
        } else if (line.startsWith("   ")) {
            try {
                k1 = Integer.parseInt(line.substring(3, 5).trim(), 16);
                k2 = Integer.parseInt(line.substring(6, 8).trim(), 16);
            } catch (final Exception e) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        this);
                builder.setTitle(getString(R.string.error_prgm_title));
                builder.setMessage(getString(R.string.error_prgm_msg)
                        + cs.getPrgmPosition() + "\n" + e.getMessage());
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton(getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {

                            //@Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        });
                builder.show();
                return true;
            }
            c.ProcessKey(k1);
            p = c.ProcessKey(k2);
        } else {
            try {
                k1 = Integer.parseInt(line.substring(0, 2).trim(), 16);
                k2 = Integer.parseInt(line.substring(3, 5).trim(), 16);
                k3 = Integer.parseInt(line.substring(6, 8).trim(), 16);
            } catch (final Exception e) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(
                        this);
                builder.setTitle(getString(R.string.error_prgm_title));
                builder.setMessage(getString(R.string.error_prgm_msg)
                        + cs.getPrgmPosition() + "\n" + e.getMessage());
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton(getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {

                            //@Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                return;
                            }
                        });
                builder.show();
                return true;
            }
            c.ProcessKey(k1);
            c.ProcessKey(k2);
            p = c.ProcessKey(k3);
        }

        // We only update the display if there is a pause. No Annunciator
        // flags are updated while a program is running.
        if (p.getDelay() > 0) {
            if (p.getAlternateText() == null
                    || p.getAlternateText().length() == 0) {
                SetDisplayText(p.getDisplayText());

                try {
                    Thread.sleep(p.getDelay());
                } catch (InterruptedException e) {
                }
            } else {
                SetDisplayText(p.getAlternateText());

                try {
                    Thread.sleep(p.getDelay());
                } catch (InterruptedException e) {
                }

                SetDisplayText(p.getDisplayText());
            }
        }
        return false;
    }

    // load the menu with all of the current settings
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // The Mode Menu
        MenuItem mModeFloat, mModeHex, mModeDec, mModeOct, mModeBin, mModeSI;
        mModeFloat = menu.findItem(R.id.mode_float);
        mModeHex = menu.findItem(R.id.mode_hex);
        mModeDec = menu.findItem(R.id.mode_dec);
        mModeOct = menu.findItem(R.id.mode_oct);
        mModeBin = menu.findItem(R.id.mode_bin);
        mModeSI = menu.findItem(R.id.mode_si);

        mModeFloat.setChecked(false);
        mModeHex.setChecked(false);
        mModeDec.setChecked(false);
        mModeBin.setChecked(false);
        mModeOct.setChecked(false);
        mModeSI.setChecked(false);

        switch (cs.getOpMode()) {
            case Float:
                mModeFloat.setChecked(true);
                if (cs.getFloatPrecision() == Calculator.k.KeyDp.index()) {
                    mModeSI.setChecked(true);
                } else {
                    mModeSI.setChecked(false);
                }
                break;
            case Hex:
                mModeHex.setChecked(true);
                break;
            case Dec:
                mModeDec.setChecked(true);
                break;
            case Oct:
                mModeOct.setChecked(true);
                break;
            case Bin:
                mModeBin.setChecked(true);
                break;
        }

        // The Options Menu
        MenuItem mOptionSave, mOption8bit, mOption16bit, mOption32bit,
                mOption64bit, mOption1sComp, mOption2sComp, mOptionUnsigned;
        mOptionSave = menu.findItem(R.id.opt_save);
        mOption8bit = menu.findItem(R.id.opt_8bit);
        mOption16bit = menu.findItem(R.id.opt_16bit);
        mOption32bit = menu.findItem(R.id.opt_32bit);
        mOption64bit = menu.findItem(R.id.opt_64bit);
        mOption1sComp = menu.findItem(R.id.opt_1s);
        mOption2sComp = menu.findItem(R.id.opt_2s);
        mOptionUnsigned = menu.findItem(R.id.opt_unsigned);

        mOptionSave.setChecked(cs.isSaveOnExit());
        mOption8bit.setChecked(false);
        mOption16bit.setChecked(false);
        mOption32bit.setChecked(false);
        mOption64bit.setChecked(false);

        switch (cs.getWordSize()) {
            case 8:
                mOption8bit.setChecked(true);
                break;
            case 16:
                mOption16bit.setChecked(true);
                break;
            case 32:
                mOption32bit.setChecked(true);
                break;
            case 64:
                mOption64bit.setChecked(true);
                break;
            // unlike a lot of other menu items, this one may
            // not have anything checked
        }
        mOption1sComp.setChecked(false);
        mOption2sComp.setChecked(false);
        mOptionUnsigned.setChecked(false);

        switch (cs.getArithMode()) {
            case OnesComp:
                mOption1sComp.setChecked(true);
                break;
            case TwosComp:
                mOption2sComp.setChecked(true);
                break;
            case Unsigned:
                mOptionUnsigned.setChecked(true);
                break;
        }

        // The Flags Menu
        menu.findItem(R.id.flag0).setChecked(
                cs.isFlag(CalcState.CalcFlag.User0));
        menu.findItem(R.id.flag1).setChecked(
                cs.isFlag(CalcState.CalcFlag.User1));
        menu.findItem(R.id.flag2).setChecked(
                cs.isFlag(CalcState.CalcFlag.User2));
        menu.findItem(R.id.flagZeros).setChecked(
                cs.isFlag(CalcState.CalcFlag.LeadingZero));
        menu.findItem(R.id.flagCarry).setChecked(
                cs.isFlag(CalcState.CalcFlag.Carry));
        menu.findItem(R.id.flagOverflow).setChecked(
                cs.isFlag(CalcState.CalcFlag.Overflow));

        return super.onPrepareOptionsMenu(menu);
    }

    // filter files by extension
    private class FilterBy implements FilenameFilter {
        String pExtension;

        public FilterBy(String extension) {
            pExtension = "." + extension;
        }

        public boolean accept(File directory, String filename) {
            return filename.endsWith(pExtension);
        }
    }
}

