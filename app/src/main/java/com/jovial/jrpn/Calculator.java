package com.jovial.jrpn;

import java.text.NumberFormat;

// This is the calculator "engine"... it processes keystrokes and
// performs the appropriate functions
public class Calculator {

    private final static int F_KEY = 128;
    private final static int G_KEY = 256;

    // The native key codes
    // Digits (0-9 and A-F) use their own hex values, all others use a grid
    // system like this:
    //  A,  B,  C,  D,  E,  F, 7,  8,  9, 10
    // 21, 22, 23, 24, 25, 26, 4,  5,  6, 20
    // 31, 32, 33, 35, 35, 36, 1,  2,  3, 30
    // 41, 42, 43, 44, 45,     0, 48, 49, 40
    public enum k {

        // The normal un-shifted keys
        KeyA(0xa), KeyB(0xb), KeyC(0xc), KeyD(0xd), KeyE(0xe), KeyF(0xf), Key7(
                0x7), Key8(0x8), Key9(0x9), KeyDiv(0x10), KeyGSB(0x21), KeyGTO(
                0x22), KeyHEX(0x23), KeyDEC(0x24), KeyOCT(0x25), KeyBIN(0x26), Key4(
                0x4), Key5(0x5), Key6(0x6), KeyMul(0x20), KeyRS(0x31), KeySST(
                0x32), KeyRol(0x33), KeyXY(0x34), KeyBSP(0x35), KeyEnt(0x36), Key1(
                0x1), Key2(0x2), Key3(0x3), KeyMin(0x30), KeyON(0x41), KeyFKey(
                0x42), KeyGKey(0x43), KeySTO(0x44), KeyRCL(0x45), Key0(0x0), KeyDp(
                0x48), KeyCHS(0x49), KeyAdd(0x40),

        // The Yellow (F key) shifted functions
        FnSL(k.KeyA.index() + F_KEY), FnSR(k.KeyB.index() + F_KEY), FnRL(k.KeyC
                .index() + F_KEY), FnRR(k.KeyD.index() + F_KEY), FnRLn(k.KeyE
                .index() + F_KEY), FnRRn(k.KeyF.index() + F_KEY), FnMASKL(
                k.Key7.index() + F_KEY), FnMASKR(k.Key8.index() + F_KEY), FnRMD(
                k.Key9.index() + F_KEY), FnXOR(k.KeyDiv.index() + F_KEY), FnXIndex(
                k.KeyGSB.index() + F_KEY), FnXI(k.KeyGTO.index() + F_KEY), FnShowHex(
                k.KeyHEX.index() + F_KEY), FnShowDec(k.KeyDEC.index() + F_KEY), FnShowOct(
                k.KeyOCT.index() + F_KEY), FnShowBin(k.KeyBIN.index() + F_KEY), FnSB(
                k.Key4.index() + F_KEY), FnCB(k.Key5.index() + F_KEY), FnBSet(
                k.Key6.index() + F_KEY), FnAND(k.KeyMul.index() + F_KEY), FnIndex(
                k.KeyRS.index() + F_KEY), FnI(k.KeySST.index() + F_KEY), FnClearPrgm(
                k.KeyRol.index() + F_KEY), FnClearReg(k.KeyXY.index() + F_KEY), FnClearPrefix(
                k.KeyBSP.index() + F_KEY), FnWINDOW(k.KeyEnt.index() + F_KEY), FnSet1s(
                k.Key1.index() + F_KEY), FnSet2s(k.Key2.index() + F_KEY), FnSetUnsgn(
                k.Key3.index() + F_KEY), FnNOT(k.KeyMin.index() + F_KEY), FnWSIZE(
                k.KeySTO.index() + F_KEY), FnFLOAT(k.KeyRCL.index() + F_KEY), FnMEM(
                k.Key0.index() + F_KEY), FnSTATUS(k.KeyDp.index() + F_KEY), FnEEX(
                k.KeyCHS.index() + F_KEY), FnOR(k.KeyAdd.index() + F_KEY),

        // The Blue (G key) shifted functions
        FnLJ(k.KeyA.index() + G_KEY), FnASR(k.KeyB.index() + G_KEY), FnRLC(
                k.KeyC.index() + G_KEY), FnRRC(k.KeyD.index() + G_KEY), FnRLCn(
                k.KeyE.index() + G_KEY), FnRRCn(k.KeyF.index() + G_KEY), FnNumB(
                k.Key7.index() + G_KEY), FnABS(k.Key8.index() + G_KEY), FnDBLR(
                k.Key9.index() + G_KEY), FnDBLDiv(k.KeyDiv.index() + G_KEY), FnRTN(
                k.KeyGSB.index() + G_KEY), FnLBL(k.KeyGTO.index() + G_KEY), FnDSZ(
                k.KeyHEX.index() + G_KEY), FnISZ(k.KeyDEC.index() + G_KEY), FnSqrt(
                k.KeyOCT.index() + G_KEY), FnInv(k.KeyBIN.index() + G_KEY), FnSF(
                k.Key4.index() + G_KEY), FnCF(k.Key5.index() + G_KEY), FnFSet(
                k.Key6.index() + G_KEY), FnDBLMul(k.KeyMul.index() + G_KEY), FnPR(
                k.KeyRS.index() + G_KEY), FnBST(k.KeySST.index() + G_KEY), FnRolUp(
                k.KeyRol.index() + G_KEY), FnPSE(k.KeyXY.index() + G_KEY), FnCLX(
                k.KeyBSP.index() + G_KEY), FnLSTX(k.KeyEnt.index() + G_KEY), FnXlteY(
                k.Key1.index() + G_KEY), FnXlt0(k.Key2.index() + G_KEY), FnXgtY(
                k.Key3.index() + G_KEY), FnXgt0(k.KeyMin.index() + G_KEY), FnSLeft(
                k.KeySTO.index() + G_KEY), FnSRight(k.KeyRCL.index() + G_KEY), FnXneY(
                k.Key0.index() + G_KEY), FnXne0(k.KeyDp.index() + G_KEY), FnXeqY(
                k.KeyCHS.index() + G_KEY), FnXeq0(k.KeyAdd.index() + G_KEY),
            
        // a pseudo key for refreshing the screen
        Refresh(-1);
        
        // make a java enum a bit more civilized
        private int pindex;

        k() {
            this.pindex = pindex + 1;
        }

        k(int val) {
            this.pindex = val;
        }

        public int index() {
            return pindex;
        }

        public static k toK(int val) {
            for (k enum_val : k.values()) {
                if (enum_val.pindex == val) {
                    return enum_val;
                }
            }
            // should this throw an exception?
            return null;
        }
    }

    private CalcState cs;
    private DisplayPacket Packet;
    private StringBuilder RawDisplay;
    private int WinPos;

    private k[] ShortCutAllowed = { k.KeySTO, k.KeyRCL, k.KeyGSB,
        k.KeyGTO };
    private k[] DpNotAllowed = { k.FnWINDOW, k.FnSF, k.FnCF, k.FnFSet,
        k.FnLBL, k.KeyGSB };
    private k[] TerminateInput = { k.KeyBSP, k.KeyDp, k.KeyCHS, k.FnEEX };
    private k[] DoNotResetWinPos = { k.FnSLeft, k.FnSRight, k.KeySTO,
        k.FnMEM, k.FnSTATUS, k.FnPR, k.FnLBL, k.FnRTN, k.FnSF, k.FnCF,
        k.FnFSet, k.FnPSE, k.KeyRS, k.KeyGSB, k.KeyGTO, k.KeySST, k.FnABS,
        k.FnDSZ, k.FnISZ, k.FnShowBin, k.FnClearPrgm, k.FnClearReg,
        k.FnClearPrefix, k.FnXeq0, k.FnXeqY, k.FnXgt0, k.FnXgtY, k.FnXlt0,
        k.FnXlteY, k.FnXne0, k.FnXneY, k.FnWINDOW };


    // The default constructor
    public Calculator(CalcState calc_state) {
        cs = calc_state;
        RawDisplay = new StringBuilder();
        WinPos = 0;

        Packet = new DisplayPacket();
        Packet.setCarry_Annunciator(cs.isFlag(CalcState.CalcFlag.Carry));
        Packet.setOverflow_Annunciator(cs.isFlag(CalcState.CalcFlag.Overflow));
        Packet.setDisplayText(FormatDisplay());
    }

    private static int Prefix = 0;
    private static java.util.Stack<Integer> Stage = new java.util.Stack<Integer>();
    private static boolean PrgmEntry = false;
    private static boolean StackDisable = false;
    private static boolean PadDecimal = false;

    // Process a calculator key stroke
    public DisplayPacket ProcessKey(int CalcKey) {
        k key;
        int val;

        // There are 3 types of "keys" that we deal with here:
        // 1) Ordinary "white" keys (like the plus key for addition)
        // 2) Shifted functions (the yellow or blue functions) that
        //    require the yellow f or blue g key followed by an ordinary key
        // 3) Multi-stage functions that require 1 or more additional
        //    keystrokes as operands. Example: the STO function
        //    can't be processed until a 2nd key is pressed to indicate
        //    the register number. Note: Multi-stage can also be
        //    shifted.

        // Shifted functions are handled by adding the prefix value
        if (CalcKey == k.KeyFKey.index() || CalcKey == k.KeyGKey.index()) {
            // in case we accidently "double" shifted
            key = k.toK(CalcKey);
        } else {
            if (CalcKey == -1) {
                key = k.Refresh;
            } else {
                key = k.toK(CalcKey + Prefix);
            }
        }

        // clean up the packet for this next run
        Packet.setAlternateText("");
        Packet.setF_Annunciator(false);
        Packet.setG_Annunciator(false);
        Packet.setPrgm_Annunciator(PrgmEntry);
        Packet.setMenuNeedsUpdating(false);
        Packet.setDelay(0);
        Packet.setBeep(false);
        Packet.setStart(DisplayPacket.StartType.None);

        // This is the main loop for processing the keys and functions
        switch (key) {
        case KeyA: // The A key (for hex numbers)
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.KeyA.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Hex) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       A");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("A");
            }
            break;
        case KeyB: // The B key (for hex numbers)
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.KeyB.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Hex) {
                Packet.setBeep(true);
                break;
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "      B");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("B");
            }
            break;
        case KeyC: // The C key (for hex numbers)
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.KeyC.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Hex) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "      C");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("C");
            }
            break;
        case KeyD: // The D key (for hex numbers)
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.KeyD.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Hex) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       D");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("D");
            }
            break;
        case KeyE: // The E key (for hex numbers)
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.KeyE.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Hex) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       E");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("E");
            }
            break;
        case KeyF: // The F key (for hex numbers)
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.KeyF.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Hex) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       F");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("F");
            }
            break;
        case Key7: // The 7 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key7.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Oct.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       7");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("7");
            }
            break;
        case Key8: // The 8 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key8.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Dec.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       8");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("8");
            }
            break;
        case Key9: // The 9 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key9.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Dec.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       9");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("9");
            }
            break;
        case KeyDiv: // Division
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "/"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    // v6.0.7 - 22 Jul 2015
                    if (cs.getStack().getX().getFVal() == 0) {
                        Packet.setAlternateText("Error 0 - Improper Math Operation");
                        return Packet;
                    }

                    double y;
                    y = cs.getStack().Pop().getFVal();
                    temp.setFVal(cs.getStack().Pop().getFVal() / y);
                    cs.setFlag(CalcState.CalcFlag.Overflow,
                            Double.isInfinite(temp.getFVal()));
                } else {
                    // v6.0.7 - 22 Jul 2015
                    if (cs.getStack().getX().getBiVal().IsZero()) {
                        Packet.setAlternateText("Error 0 - Improper Math Operation");
                        return Packet;
                    }

                    BigInt y;
                    y = cs.getStack().Pop().getBiVal();
                    temp.setBiVal(BigInt.Divide(cs.getStack().Pop().getBiVal(),
                            y));
                    cs.setFlag(CalcState.CalcFlag.Carry, temp.getBiVal()
                            .isCarryBit());
                    cs.setFlag(CalcState.CalcFlag.Overflow, temp.getBiVal()
                            .isOverflow());
                }
                Packet.setMenuNeedsUpdating(true);
                cs.getStack().Push(temp);
            }
            break;
        case KeyGSB: // Go to Subroutine (Program Mode)
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.KeyGSB.index());
                break;
            }

            // Get the stage operand
            val = Stage.pop();
            Stage.clear();

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", key.index(), val,
                                "GSB " + String.format("%1$X", val)));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                StackDisable = false;
                
                // A quick sanity check
                if (cs.getPrgmMemory().isEmpty()) {
                    Packet.setAlternateText("Error 4 - Improper Label");
                    return Packet;
                }

                // find the starting location
                int start = FindLabel(cs.getPrgmPosition(), val);
                if (start < 0) {
                    Packet.setAlternateText("Error 4 - Improper Label");
                    return Packet;
                }

                // Store the current location for an eventual RTN
                // Note: Unlike the real calculator, I did not limit the stack
                // depth to 4 (so there is no "Error 5 - Subroutine Level Too
                // Deep")
                cs.getPrgmRetStack().push(cs.getPrgmPosition());
                cs.setPrgmPosition(start);

                // start a program
                if (!cs.isPrgmRunning()) {
                    cs.setPrgmRunning(true);
                    cs.getPrgmRetStack().clear();

                    Packet.setAlternateText("Running");
                    Packet.setStart(DisplayPacket.StartType.RunProgram);
                }
            }
            break;
        case KeyGTO: // Go To Label (Program/Record Mode)
            // There are two version of GTO... during execution of a program
            // it accepts a single operand. However, during editing of a program
            // GTO accepts a decimal point and 3 digits (for the line number)
            if (Stage.size() == 0) {
                Stage.push(k.KeyGTO.index());
                break;
            }

            // check for the decimal point as the 2nd item
            if (Stage.size() >= 2
                    && (Integer) Stage.toArray()[1] == k.KeyDp.index()) {
                if (Stage.size() < 5) {
                    // we're not done yet... we need more digits
                    break;
                } else {
                    // decode the 3-digit line number
                    int digit1, digit2, digit3, line;
                    digit3 = Stage.pop();
                    digit2 = Stage.pop();
                    digit1 = Stage.pop();

                    Stage.clear();
                    // do some sanity checking for digit entry
                    if (digit1 > 9 || digit2 > 9 || digit3 > 9) {
                        Packet.setAlternateText("Error 1 - Improper GTO. Number");
                        return Packet;
                    }
                    line = (digit1 * 100) + (digit2 * 10) + digit3;

                    // and some more checking on the decoded number
                    // v6.0.2 - 26 Apr 12
                    if (line < 0 || line > cs.getPrgmMemory().size()) {
                        Packet.setAlternateText("Error 4 - Improper Line Number");
                        return Packet;
                    }

                    cs.setPrgmPosition(line);
                    break;
                }
            }

            // Get the stage operand
            val = Stage.pop();
            Stage.clear();

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", key.index(), val,
                                "GTO " + String.format("%1$X", val)));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                val = FindLabel(cs.getPrgmPosition(), val);
                if (val < 0) {
                    cs.setPrgmRunning(false);
                    Prefix = 0;
                    Packet.setAlternateText("Error 4 - Improper Label");
                    return Packet;
                }
                cs.setPrgmPosition(val);
            }
            break;
        // Note: the "GTO.nnn" version can not be stored in Program Mode
        case KeyHEX: // Hexadecimal Mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "HEX"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            }

            // This is a bit strange... if you are entering a program, then
            // you also need to convert to that mode.
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                cs.SyncValues();
            }
            cs.setOpMode(CalcState.CalcOpMode.Hex);
            Packet.setMenuNeedsUpdating(true);
            break;
        case KeyDEC: // Decimal Mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "DEC"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            }

            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                cs.SyncValues();
            }
            cs.setOpMode(CalcState.CalcOpMode.Dec);
            Packet.setMenuNeedsUpdating(true);
            break;
        case KeyOCT: // Octal Mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "OCT"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            }

            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                cs.SyncValues();
            }
            cs.setOpMode(CalcState.CalcOpMode.Oct);
            Packet.setMenuNeedsUpdating(true);
            break;
        case KeyBIN: // Binary Mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "BIN"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            }

            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                cs.SyncValues();
            }
            cs.setOpMode(CalcState.CalcOpMode.Bin);
            Packet.setMenuNeedsUpdating(true);
            break;
        case Key4: // The 4 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key4.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Oct.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       4");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("4");
            }
            break;
        case Key5: // The 5 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key5.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Oct.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       5");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("5");
            }
            break;
        case Key6: // The 6 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key6.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Oct.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       6");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("6");
            }
            break;
        case KeyMul: // Multiplication
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "*"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    temp.setFVal(cs.getStack().Pop().getFVal()
                            * cs.getStack().Pop().getFVal());
                    cs.setFlag(CalcState.CalcFlag.Overflow,
                            Double.isInfinite(temp.getFVal()));
                } else {
                    temp.setBiVal(BigInt.Multiply(cs.getStack().Pop()
                            .getBiVal(), cs.getStack().Pop().getBiVal()));
                    cs.setFlag(CalcState.CalcFlag.Overflow, temp.getBiVal()
                            .isOverflow());
                }
                Packet.setMenuNeedsUpdating(true);
                cs.getStack().Push(temp);
            }
            break;
        case KeyRS: // Run / Stop (Program Mode)
            // There is a strange shortcut allowed here... When used as an
            // operand with a function, then you can ignore the Yellow shift
            // (f) key and still get the affect of k.FnIndex (rather than
            // k.KeyRS)
            if (Stage.size() == 1
                    && ArrayindexOf(ShortCutAllowed, k.toK(Stage.peek())) >= 0) {
                Stage.push(k.FnIndex.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "R/S"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // a quick sanity check
                if (cs.getPrgmMemory().size() > 0) {
                    if (cs.isPrgmRunning()) {
                        // if inside a program, the only option is to stop
                        cs.setPrgmRunning(false);
                    } else {
                        // If outside a program, the only option is to start
                        // since any keystroke would have stopped a running
                        // application.
                        cs.setPrgmRunning(true);
                        Packet.setAlternateText("Running");
                        Packet.setStart(DisplayPacket.StartType.RunProgram);
                    }
                }
            }
            break;
        case KeySST: // Single step (Program Mode)
            // There is a strange shortcut allowed here... When used as an
            // operand with a function, then you can ignore the Yellow shift
            // (f) key and still get the affect of k.FnI (rather than k.KeySST)
            if (Stage.size() == 1
                    && ArrayindexOf(ShortCutAllowed, k.toK(Stage.peek())) >= 0) {
                Stage.push(k.FnI.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // In PrgmEntry mode, we just increment the position, but in the
            // run mode, we actually execute the instructions
            if (PrgmEntry) {
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                if (cs.getPrgmPosition() > cs.getPrgmMemory().size()) {
                    cs.setPrgmPosition(0);
                }
            } else {
                // a quick sanity check
                if (cs.getPrgmMemory().size() > 0) {
                    // v6.0.2 - 26 Apr 12
                    if (cs.getPrgmPosition() >= cs.getPrgmMemory().size()) {
                        cs.setPrgmPosition(0);
                    }
                    // temporarily show the program codes
                    Packet.setAlternateText(String.format("%1$03d",
                            cs.getPrgmPosition() + 1)
                            + "- "
                            + cs.getPrgmMemory().get(cs.getPrgmPosition())
                                    .substring(0, 8));
                    Packet.setDelay(Integer.parseInt(fmMain.prop
                            .getProperty("SleepDelay")) / 2);

                    // run one line
                    Packet.setStart(DisplayPacket.StartType.RunLine);

                    cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    if (cs.getPrgmPosition() >= cs.getPrgmMemory().size()) {
                        cs.setPrgmPosition(0);
                    }
                }
            }
            break;
        // Note: SST can not be stored in Program Mode
        case KeyRol: // Roll the stack
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "Rv"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp;
                temp = cs.getStack().getX().Copy();
                cs.getStack().setX(cs.getStack().getY().Copy());
                cs.getStack().setY(cs.getStack().getZ().Copy());
                cs.getStack().setZ(cs.getStack().getT().Copy());
                cs.getStack().setT(temp);
                StackDisable = false;
            }
            break;
        case KeyXY: // Exchange X and Y
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "X:Y"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp;
                // v6.0.1 - 29 Mar 12
                temp = cs.getStack().getY().Copy();
                cs.getStack().setY(cs.getStack().getX().Copy());
                cs.getStack().setX(temp);
                StackDisable = false;
            }
            break;
        case KeyBSP: // The Backspace Key
            if (PrgmEntry) {
                if (cs.getPrgmPosition() > 0) {
                    cs.getPrgmMemory().remove(cs.getPrgmPosition() - 1);
                    cs.setPrgmPosition(cs.getPrgmPosition() - 1);
                }
            } else {
                // If digit entry has already been terminated, then this
                // acts just like CLx
                if (RawDisplay.length() == 0) {
                    cs.getStack().setX(
                            new Register(cs.getWordSize(), cs.getArithMode()));
                    StackDisable = true;
                } else {
                    RawDisplay.delete(RawDisplay.length() - 1,
                            RawDisplay.length());
                    if (RawDisplay.length() == 0) {
                        cs.getStack().setX(
                                new Register(cs.getWordSize(), cs
                                        .getArithMode()));
                    }

                    // Since we allow you to backspace over a number
                    // in scientific notation, we must make sure the
                    // raw display will still parse
                    String temp = RawDisplay.toString();
                    if (temp.endsWith("e")) {
                        RawDisplay.delete(RawDisplay.length() - 1,
                                RawDisplay.length());
                    } else if (temp.endsWith("e-")) {
                        RawDisplay.delete(RawDisplay.length() - 2,
                                RawDisplay.length());
                    }

                    // What about leaving a "naked" minus sign
                    if (temp.equals("-")) {
                        RawDisplay.setLength(0);
                        cs.getStack().setX(
                                new Register(cs.getWordSize(), cs
                                        .getArithMode()));
                    }

                    // Here is a strange twist... if we only have 1 digit
                    // remaining then we have to explicitly tell the code that
                    // follows this select statement that this 1 digit is not
                    // a new entry. We do with via the StackDisable variable
                    // (although this has nothing to do with disabling the
                    // stack).
                    if (RawDisplay.length() == 1) {
                        StackDisable = true;
                    }
                }
            }
            break;
        // Note: BSP can not be stored in Program Mode
        case KeyEnt: // The Enter key
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "Enter"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // push the X value on the stack
                cs.getStack().Push(cs.getStack().getX().Copy());
                // "disable" the stack for the next entry
                StackDisable = true;
            }
            break;
        case Key1: // The 1 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key1.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       1");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("1");
            }
            break;
        case Key2: // The 2 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key2.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Oct.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       2");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("2");
            }
            break;
        case Key3: // The 3 key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key3.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            // Is this valid for the current mode?
            if (cs.getOpMode().index() > CalcState.CalcOpMode.Oct.index()) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       3");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("3");
            }
            break;
        case KeyMin: // Subtraction
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "-"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    double y = cs.getStack().Pop().getFVal();
                    temp.setFVal(cs.getStack().Pop().getFVal() - y);
                    cs.setFlag(CalcState.CalcFlag.Overflow,
                            Double.isInfinite(temp.getFVal()));
                } else {
                    BigInt y = cs.getStack().Pop().getBiVal();
                    temp.setBiVal(BigInt.Subtract(cs.getStack().Pop()
                            .getBiVal(), y));
                    cs.setFlag(CalcState.CalcFlag.Carry, temp.getBiVal()
                            .isCarryBit());
                    cs.setFlag(CalcState.CalcFlag.Overflow, temp.getBiVal()
                            .isOverflow());
                }
                Packet.setMenuNeedsUpdating(true);
                cs.getStack().Push(temp);
            }
            break;
        case KeyON: // The ON key
//          // used for calibrating the automatic screen widths
//          Packet.setDisplayText("00000000 00000000 00000000 00000000 .b.");
//          Packet.setF_Annunciator(true);
//          Packet.setG_Annunciator(true);
//          Packet.setCarry_Annunciator(true);
//          Packet.setOverflow_Annunciator(true);
//          Packet.setPrgm_Annunciator(true);
//          return Packet;
            break;
        case KeyFKey: // The yellow F prefix key
            Prefix = 128;

            Packet.setF_Annunciator(true);
            return Packet;
        case KeyGKey: // The blue G prefix key
            Prefix = 256;

            Packet.setG_Annunciator(true);
            return Packet;
        case KeySTO: // Store a value to a register
            // This is a multi-stage command, so we have to store up 2 or 3
            // more keystrokes before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.KeySTO.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.KeySTO.index()) {
                Stage.clear();
                Stage.push(k.KeySTO.index());
                break;
            }

            // This is an odd, one... there could be 2 or 3 stages
            if (Stage.peek() == k.KeyDp.index()) {
                // we need another digit to continue
                break;
            }

            // convert the values in the stages into a register number
            val = Stage.pop();
            if (val >= Integer
                    .parseInt(fmMain.prop.getProperty("NumRegisters"))
                    && val != k.FnI.index() && val != k.FnIndex.index()) {
                Packet.setAlternateText("Error 3 - Improper Register Number");
                Stage.clear();
                return Packet;
            }

            if (Stage.pop() == k.KeyDp.index()) {
                val = val + 16;
            }

            // clear the stage, we've completed a sequence
            Stage.clear();

            if (PrgmEntry) {
                if (val == k.FnI.index()) {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X%2$3X    '%3$s", key.index(),
                                    k.KeySST.index(), "STO I"));
                } else if (val == k.FnIndex.index()) {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X%2$3X    '%3$s", key.index(),
                                    k.KeyRS.index(), "STO (i)"));
                } else if (val >= 16) {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X .%2$1X    '%3$s", key.index(),
                                    val - 16,
                                    "STO ." + String.format("%1$X", val - 16)));
                } else {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X%2$3X    '%3$s", key.index(),
                                    val, "STO " + String.format("%1$X", val)));
                }
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                StackDisable = false;
                // does not effect stack

                if (val == k.FnI.index()) {
                    // Store into the I register
                    cs.setRegIndex(cs.getStack().getX().Copy());
                    cs.getRegIndex().getBiVal().setWordSize(64);
                } else if (val == k.FnIndex.index()) {
                    // Store into the register who's number is stored in I
                    int i;
                    if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                        i = (int) (Math.abs(cs.getRegIndex().getFVal()));
                    } else {
                        i = Math.abs(cs.getRegIndex().getBiVal().ToInteger());
                    }

                    if (i >= Integer.parseInt(fmMain.prop
                            .getProperty("NumRegisters"))) {
                        Packet.setAlternateText("Error 3 - Improper Register Number");
                        return Packet;
                    }
                    cs.setReg(i, cs.getStack().getX().Copy());
                } else {
                    // Just store into a normal "named" register
                    cs.setReg(val, cs.getStack().getX().Copy());
                }
            }
            break;
        case KeyRCL: // Recall a value from a register
            // This is a multi-stage command, so we have to store up 2 or 3
            // more keystrokes before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.KeyRCL.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.KeyRCL.index()) {
                Stage.clear();
                Stage.push(k.KeyRCL.index());
                break;
            }

            // This is an odd, one... there could be 2 or 3 stages
            if (Stage.peek() == k.KeyDp.index()) {
                // we need another digit to continue
                break;
            }

            // convert the values in the stages into a register number
            val = Stage.pop();

            if (val >= Integer
                    .parseInt(fmMain.prop.getProperty("NumRegisters"))
                    && val != k.FnI.index() && val != k.FnIndex.index()) {
                Packet.setAlternateText("Error 3 - Improper Register Number");
                Stage.clear();
                return Packet;
            }

            if (Stage.pop() == k.KeyDp.index()) {
                val = val + 16;
            }

            Stage.clear();

            if (PrgmEntry) {
                if (val == k.FnI.index()) {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X%2$3X    '%3$s", key.index(),
                                    k.KeySST.index(), "RCL I"));
                } else if (val == k.FnIndex.index()) {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X%2$3X    '%3$s", key.index(),
                                    k.KeyRS.index(), "RCL (i)"));
                } else if (val >= 16) {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X .%2$1X    '%3$s", key.index(),
                                    val - 16,
                                    "RCL ." + String.format("%1$X", val - 16)));
                } else {
                    cs.getPrgmMemory().add(
                            cs.getPrgmPosition(),
                            String.format("%1$5X%2$3X    '%3$s", key.index(),
                                    val, "RCL " + String.format("%1$X", val)));
                }
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp;
                if (val == k.FnI.index()) {
                    // recall value from the I register
                    temp = cs.getRegIndex().Copy();
                } else if (val == k.FnIndex.index()) {
                    // recall the value from the register who's number is stored
                    // in I
                    int i;
                    if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                        i = (int) (Math.abs(cs.getRegIndex().getFVal()));
                    } else {
                        i = Math.abs(cs.getRegIndex().getBiVal().ToInteger());
                    }

                    if (i >= Integer.parseInt(fmMain.prop
                            .getProperty("NumRegisters"))) {
                        Packet.setAlternateText("Error 3 - Improper Register Number");
                        return Packet;
                    }
                    temp = cs.getReg(i).Copy();
                } else {
                    // Just recall from a normal "named" register
                    temp = cs.getReg(val).Copy();
                }
                // v6.0.6 - 1 Mar 2014
                if (StackDisable) {
                    cs.getStack().setX(temp);
                    StackDisable = false;
                } else {
                    cs.getStack().Push(temp);
                }
            }
            break;
        case Key0: // The 0 zero key
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                Stage.push(k.Key0.index());

                // return to the first stage for processing
                return ProcessKey((Integer) Stage.toArray()[0]);
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(), "       0");
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                RawDisplay.append("0");
            }
            break;
        case KeyDp: // Decimal point
            // Is this just a part of a multi-stage command?
            if (Stage.size() > 0) {
                // Check for valid stages that accept a decimal point. It makes
                // since to check here so we can get immediate feedback
                if (ArrayindexOf(DpNotAllowed,
                        k.toK((Integer) Stage.toArray()[0])) >= 0) {
                    // Note: The real calculator is very forgiving (and silent)
                    // under these conditions. It just processes the decimal
                    // point as if there was no staging.
                    Stage.clear();
                } else {
                    Stage.push(k.KeyDp.index());

                    // return to the first stage for processing
                    return ProcessKey((Integer) Stage.toArray()[0]);
                }
            }

            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "."));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (RawDisplay.length() == 0) {
                    RawDisplay.append("0.");
                    // v6.0.4 - 29 May 12
                    // Since we have artificially padded the display we need
                    // another way to indicate that this is a new digit
                    PadDecimal = true;
                } else {
                    // extra decimal points are silently ignored
                    if (RawDisplay.toString().contains(".") == false) {
                        RawDisplay.append(".");
                    }
                }
            }
            break;
        case KeyCHS: // Change sign
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "CHS"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // an exception to the rule for terminating input (page 99)
                if (RawDisplay.length() > 0
                        && cs.getOpMode() != CalcState.CalcOpMode.Float) {
                    RawDisplay.setLength(0);
                }

                // changing signs while keying digits
                if (RawDisplay.length() > 0) {
                    String t = RawDisplay.toString();
                    RawDisplay.setLength(0);
                    // is there an exponent showing?
                    if (t.contains("e")) {
                        // it's rather odd that once there is an exponent
                        // you can no longer change the sign of the mantissa
                        if (t.contains("e-")) {
                            t = t.replace("e-", "e");
                        } else {
                            t = t.replace("e", "e-");
                        }
                        RawDisplay.append(t);
                    } else {
                        if (t.startsWith("-")) {
                            RawDisplay.append(t.substring(1));
                        } else {
                            RawDisplay.append("-");
                            RawDisplay.append(t);
                        }
                    }
                } else {
                    // just change the sign of what's in the X Register
                    if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                        cs.getStack().getX()
                                .setFVal(-cs.getStack().getX().getFVal());
                    } else {
                        // LastX is updated only in the integer mode (page 100)
                        cs.setRegLastX(cs.getStack().getX().Copy());
                        cs.getStack().getX().getBiVal().ChangeSign();
                        cs.setFlag(CalcState.CalcFlag.Overflow, cs.getStack()
                                .getX().getBiVal().isOverflow());
                        Packet.setMenuNeedsUpdating(true);
                    }
                    StackDisable = false;
                }
            }
            break;
        case KeyAdd: // Addition
            if (PrgmEntry) {
                cs.getPrgmMemory().add(cs.getPrgmPosition(),
                        String.format("%1$8X    '%2$s", key.index(), "+"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    temp.setFVal(cs.getStack().Pop().getFVal()
                            + cs.getStack().Pop().getFVal());
                    cs.setFlag(CalcState.CalcFlag.Overflow,
                            Double.isInfinite(temp.getFVal()));
                } else {
                    temp.setBiVal(BigInt.Add(cs.getStack().Pop().getBiVal(), cs
                            .getStack().Pop().getBiVal()));
                    cs.setFlag(CalcState.CalcFlag.Carry, temp.getBiVal()
                            .isCarryBit());
                    cs.setFlag(CalcState.CalcFlag.Overflow, temp.getBiVal()
                            .isOverflow());
                }
                Packet.setMenuNeedsUpdating(true);
                cs.getStack().Push(temp);
            }
            break;
        case FnSL: // Shift left
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnSL.index() - F_KEY, "f SL"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal().LeftShift(1, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnSR: // Shift Right
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnSR.index() - F_KEY, "f SR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .RightShift(1, false, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRL: // Rotate Left
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnRL.index() - F_KEY, "f RL"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .RotateLeft(1, false, false, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRR: // Rotate Right
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnRR.index() - F_KEY, "f RR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .RotateRight(1, false, false, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRLn: // Rotate Left n times
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnRLn.index() - F_KEY, "f RLn"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val > cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX().getBiVal()
                        .RotateLeft(val, false, false, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRRn: // Rotate Right n times
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnRRn.index() - F_KEY, "f RRn"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val > cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX().getBiVal()
                        .RotateRight(val, false, false, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnMASKL: // Create Left justified Mask
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnMASKL.index() - F_KEY, "f MASKL"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val > cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                temp.setBiVal(BigInt.CreateMask(val, true, cs.getWordSize()));
                cs.getStack().Push(temp);
            }
            break;
        case FnMASKR: // Create Right justified Mask
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnMASKR.index() - F_KEY, "f MASKR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val > cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                temp.setBiVal(BigInt.CreateMask(val, false, cs.getWordSize()));
                cs.getStack().Push(temp);
            }
            break;
        case FnRMD: // Remainder after division
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnRMD.index() - F_KEY, "f RMD"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register(cs.getWordSize(),
                        cs.getArithMode());
                BigInt y;

                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                // v6.0.7 - 22 Jul 2015
                if (cs.getStack().getX().getBiVal().IsZero()) {
                    Packet.setAlternateText("Error 0 - Improper Math Operation");
                    Prefix = 0;
                    return Packet;
                }
                y = cs.getStack().Pop().getBiVal();
                temp.setBiVal(BigInt.Remainder(cs.getStack().Pop().getBiVal(),
                        y));
                cs.getStack().Push(temp);
            }
            break;
        case FnXOR: // Bitwise Exclusive OR
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnXOR.index() - F_KEY, "f XOR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register();
                BigInt y;

                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                y = cs.getStack().Pop().getBiVal();
                temp.setBiVal(BigInt.Xor(cs.getStack().Pop().getBiVal(), y));
                cs.getStack().Push(temp);
            }
            break;
        case FnXIndex: // Swap X with indirect value of I
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnXIndex.index() - F_KEY, "f X:(i)"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    // The absolute value of the integer portion
                    val = (int) (Math.abs(cs.getRegIndex().getFVal()));
                } else {
                    val = Math.abs(cs.getRegIndex().getBiVal().ToInteger());
                }
                StackDisable = false;
                
                // a quick sanity check
                if (val >= Integer.parseInt(fmMain.prop
                        .getProperty("NumRegisters"))) {
                    Packet.setAlternateText("Error 3 - Improper Register Number");
                    Prefix = 0;
                    return Packet;
                }

                temp = cs.getReg(val).Copy();
                cs.setReg(val, cs.getStack().getX().Copy());
                cs.getStack().setX(temp);
            }
            break;
        case FnXI: // Swap X and I
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnXI.index() - F_KEY, "f X:I"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp;
                temp = cs.getRegIndex().Copy();
                temp.getBiVal().setWordSize(cs.getWordSize());
                cs.setRegIndex(cs.getStack().getX().Copy());
                cs.getRegIndex().getBiVal().setWordSize(64);
                cs.getStack().setX(temp);
                StackDisable = false;
            }
            break;
        case FnShowHex: // Show HEX
            if (Boolean
                    .parseBoolean(fmMain.prop.getProperty("SyncConversions"))) {
                // Note: The real calculator doesn't allow this in the float
                // mode
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    // synchronize the float and integer values
                    cs.SyncValues();
                }
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    Packet.setBeep(true);
                    break;
                }
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnShowHex.index() - F_KEY, "f ShowHEX"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Prefix = 0;
                Packet.setAlternateText(FormatDisplay(CalcState.CalcOpMode.Hex,
                        0));
                Packet.setDelay(Integer.parseInt(fmMain.prop
                        .getProperty("SleepDelay")));
                return Packet;
            }
            break;
        case FnShowDec: // Show DEC
            if (Boolean
                    .parseBoolean(fmMain.prop.getProperty("SyncConversions"))) {
                // Note: The real calculator doesn't allow this in the float
                // mode
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    // synchronize the float and integer values
                    cs.SyncValues();
                }
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    Packet.setBeep(true);
                    break;
                }
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnShowDec.index() - F_KEY, "f ShowDEC"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Prefix = 0;
                Packet.setAlternateText(FormatDisplay(CalcState.CalcOpMode.Dec,
                        0));
                Packet.setDelay(Integer.parseInt(fmMain.prop
                        .getProperty("SleepDelay")));
                return Packet;
            }
            break;
        case FnShowOct: // Show OCT
            if (Boolean
                    .parseBoolean(fmMain.prop.getProperty("SyncConversions"))) {
                // Note: The real calculator doesn't allow this in the float
                // mode
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    // synchronize the float and integer values
                    cs.SyncValues();
                }
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    Packet.setBeep(true);
                    break;
                }
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnShowOct.index() - F_KEY, "f ShowOCT"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Prefix = 0;
                Packet.setAlternateText(FormatDisplay(CalcState.CalcOpMode.Oct,
                        0));
                Packet.setDelay(Integer.parseInt(fmMain.prop
                        .getProperty("SleepDelay")));
                return Packet;
            }
            break;
        case FnShowBin: // Show BIN
            if (Boolean
                    .parseBoolean(fmMain.prop.getProperty("SyncConversions"))) {
                // Note: The real calculator doesn't allow this in the float
                // mode
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    // synchronize the float and integer values
                    cs.SyncValues();
                }
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    Packet.setBeep(true);
                    break;
                }
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnShowBin.index() - F_KEY, "f ShowBIN"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Prefix = 0;
                Packet.setAlternateText(FormatDisplay(CalcState.CalcOpMode.Bin,
                        0));
                Packet.setDelay(Integer.parseInt(fmMain.prop
                        .getProperty("SleepDelay")));
                return Packet;
            }
            break;
        case FnSB: // Set Bit
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnSB.index() - F_KEY, "f SB"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val >= cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX().getBiVal().BitSet(val);
            }
            break;
        case FnCB: // Clear Bit
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnCB.index() - F_KEY, "f CB"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val >= cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX().getBiVal().BitClear(val);
            }
            break;
        case FnBSet: // Is the bit set?
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnBSet.index() - F_KEY, "f B?"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val >= cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                if (!cs.getStack().getX().getBiVal().BitTest(val)) {
                    cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                }
            }
            break;
        case FnAND: // Bitwise AND operator
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnAND.index() - F_KEY, "f AND"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register();
                BigInt y;

                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                y = cs.getStack().Pop().getBiVal();
                temp.setBiVal(BigInt.BitwiseAnd(cs.getStack().Pop().getBiVal(),
                        y));
                cs.getStack().Push(temp);
            }
            break;
        case FnIndex: // Indirect value of I
            // OK, this one is a bit odd... if there is no stage, then
            // act like a RCL was staged.
            if (Stage.size() == 0) {
                Stage.push(k.KeyRCL.index());
            }

            Stage.push(k.FnIndex.index());

            // return to the first stage for processing
            Prefix = 0;
            return ProcessKey((Integer) Stage.toArray()[0]);
            // Note: No PrgmMode processing is required here, since this is
            // always a stage operand
        case FnI: // Index
            // OK, this one is a bit odd... if there is no stage, then
            // act like a RCL was staged.
            if (Stage.size() == 0) {
                Stage.push(k.KeyRCL.index());
            }

            Stage.push(k.FnI.index());

            // return to the first stage for processing
            Prefix = 0;
            return ProcessKey((Integer) Stage.toArray()[0]);
            // Note: No PrgmMode processing is required here, since this is
            // always a stage operand
        case FnClearPrgm: // Clear Program
            // This has two different meanings... if in the program
            // mode it will clear all program lines. However, if in
            // the Run mode, it will merely reset the program position
            // to zero (the program is NOT erased)
            if (PrgmEntry) {
                cs.getPrgmMemory().clear();
                cs.getPrgmRetStack().clear();
            }
            StackDisable = false;
            cs.setPrgmPosition(0);
            break;
        case FnClearReg: // Clear all storage registers
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnClearReg.index() - F_KEY, "f ClearREG"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // Clears storage registers (and Index), but not the stack
                for (int i = 0; i <= Integer.parseInt(fmMain.prop
                        .getProperty("NumRegisters")) - 1; i++) {
                    cs.setReg(i,
                            new Register(cs.getWordSize(), cs.getArithMode()));
                }
                cs.setRegIndex(new Register(64, cs.getArithMode()));
            }
            break;
        case FnClearPrefix: // Clear any pending prefix/stage
            Prefix = 0;
            Stage.clear();

            break;
        // Note: No PrgmMode, since this is just used for editing
        case FnWINDOW: // show a window
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.FnWINDOW.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.FnWINDOW.index()) {
                Stage.clear();
                Stage.push(k.FnWINDOW.index());
                break;
            }

            // Since our display can hold 32 bits, we only need two
            // windows (rather than 8 on the real calculator)
            val = Stage.pop();
            if (val < 0 || val > 7) {
                Stage.clear();
                Packet.setAlternateText("Error 1 - Improper Windows Number");
                Prefix = 0;
                return Packet;
            }

            Stage.clear();

            // we'll cut you some slack... for old time's sake
            if (val > 1) {
                val = 1;
            }

            if (PrgmEntry) {
                // For some reason, a program line with 3 elements uses commas
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$2X,%2$2X,%3$2X    '%4$s",
                                k.KeyFKey.index(), k.FnWINDOW.index() - F_KEY,
                                val, "f WINDOW " + val));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            }
            // only allow this when the conditions warrant it
            if (cs.getOpMode() == CalcState.CalcOpMode.Bin
                    && cs.getWordSize() > 32) {
                // this is just a code for Window 1
                WinPos = val * 36;
            }
            break;
        case FnSet1s: // switch to 1's complement mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnSet1s.index() - F_KEY, "f Set1's"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setArithMode(CalcState.CalcArithMode.OnesComp);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnSet2s: // switch to 2's complement mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnSet2s.index() - F_KEY, "f Set2's"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setArithMode(CalcState.CalcArithMode.TwosComp);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnSetUnsgn: // switch to unsigned mode
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnSetUnsgn.index() - F_KEY, "f SetUNSGN"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setArithMode(CalcState.CalcArithMode.Unsigned);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnNOT: // The Bitwise NOT operator
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnNOT.index() - F_KEY, "f NOT"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .OnesComplement(cs.getWordSize());
            }
            break;
        case FnWSIZE: // set the word size
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnWSIZE.index() - F_KEY, "f WSIZE"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;

                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());

                // This is the "escape clause", allowing you to select a new
                // word size when the current word size won't allow the new
                // larger number to be entered.
                if (val == 0) {
                    val = 64;
                }

                // a quick sanity check
                if (val > 64) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.setWordSize(val);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnFLOAT: // set the float mode
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.FnFLOAT.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.FnFLOAT.index()) {
                Stage.clear();
                Stage.push(k.FnFLOAT.index());
                break;
            }

            // Check for valid stage operands
            val = Stage.pop();
            if (val != k.KeyDp.index() && val > 9) {
                Stage.clear();
                Packet.setAlternateText("Error 1 - Improper Float Number");
                Prefix = 0;
                return Packet;
            }

            Stage.clear();

            if (PrgmEntry) {
                // For some reason, a program line with 3 elements uses commas
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$2X,%2$2X,%3$2X    '%4$s",
                                k.KeyFKey.index(), k.FnFLOAT.index() - F_KEY,
                                val, "f FLOAT " + val));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() != CalcState.CalcOpMode.Float) {
                    // Here is a strange LastX rule (page 100)
                    cs.setRegLastX(new Register(cs.getWordSize(), cs
                            .getArithMode()));
                    cs.SyncValues();

                    // reset the carry and overflow flags
                    cs.setFlag(CalcState.CalcFlag.Carry, false);
                    cs.setFlag(CalcState.CalcFlag.Overflow, false);
                } else {
                    // only in float mode
                    StackDisable = false;
                }
                cs.setOpMode(CalcState.CalcOpMode.Float);
                cs.setFloatPrecision(val);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnMEM: // Display the Memory configuration
            // It should be obvious that these values don't change
            Packet.setAlternateText(String.format("P-%1$03d R-%2$03d", Integer
                    .parseInt(fmMain.prop.getProperty("PrgmMemoryLines")),
                    Integer.parseInt(fmMain.prop.getProperty("NumRegisters"))));
            Packet.setDelay(Integer.parseInt(fmMain.prop
                    .getProperty("SleepDelay")));
            Prefix = 0;
            return Packet;
            // Note: MEM can not be stored in Program Mode
        case FnSTATUS: // Display the Calculator configuration
            // Combine flags 0-3 into a single number
            int FlagVal = 0;
            for (int i = 0; i <= 3; i++) {
                FlagVal += cs.isFlag(CalcState.CalcFlag.toCalcFlag(i)) ? (int) (Math
                        .pow(10, i)) : 0;
            }

            Packet.setDelay(Integer.parseInt(fmMain.prop
                    .getProperty("SleepDelay")));
            // v6.0.3 - 5 May 12
            Packet.setAlternateText(String.format("%1$1d-%2$02d-%3$04d", 
                    cs.getArithMode().index(), cs.getWordSize(), FlagVal));
            Prefix = 0;
            return Packet;
            // Note: STATUS can not be stored in Program Mode
        case FnEEX: // Enter exponent
            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnEEX.index() - F_KEY, "f EEX"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // This is a bit strange... if this is the first keystroke,
                // then let's insert a 1 before it
                if (RawDisplay.length() == 0) {
                    RawDisplay.append("1");
                }
                RawDisplay.append("e");
            }
            break;
        case FnOR: // The Bitwise OR operator
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyFKey.index(),
                                k.FnOR.index() - F_KEY, "f OR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp = new Register();
                BigInt y;

                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                y = cs.getStack().Pop().getBiVal();
                temp.setBiVal(BigInt.BitwiseOr(cs.getStack().Pop().getBiVal(),
                        y));
                cs.getStack().Push(temp);
            }
            break;
        case FnLJ: // Left Justify
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnLJ.index() - G_KEY, "g LJ"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                BigInt[] ret;
                Register x = new Register(cs.getWordSize(), cs.getArithMode());
                Register y = new Register(cs.getWordSize(), cs.getArithMode());
                ret = BigInt.LeftJustify(cs.getStack().Pop().getBiVal());
                x.setBiVal(ret[0]);
                y.setBiVal(ret[1]);
                cs.getStack().Push(x);
                cs.getStack().Push(y);
            }
            break;
        case FnASR: // Arithmetic Shift
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnASR.index() - G_KEY, "g ASR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .RightShift(1, true, cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRLC: // Rotate Left with Carry
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnRLC.index() - G_KEY, "g RLC"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .RotateLeft(1, true, cs.isFlag(CalcState.CalcFlag.Carry), cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRRC: // Rotate Right with Carry
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnRRC.index() - G_KEY, "g RRC"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal()
                        .RotateRight(1, true, cs.isFlag(CalcState.CalcFlag.Carry), cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRLCn: // Rotate Left with Carry n times
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnRLCn.index() - G_KEY, "g RLCn"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val > cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX().getBiVal()
                        .RotateLeft(val, true, cs.isFlag(CalcState.CalcFlag.Carry), cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRRCn: // Rotate Right with Carry n times
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnRRCn.index() - G_KEY, "g RRCn"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                val = Math.abs(cs.getStack().Pop().getBiVal().ToInteger());
                if (val > cs.getWordSize()) {
                    Packet.setAlternateText("Error 2 - Improper Bit Number");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX().getBiVal()
                        .RotateRight(val, true, cs.isFlag(CalcState.CalcFlag.Carry), cs.getWordSize());
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnNumB: // Number of bits
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnNumB.index() - G_KEY, "g #B"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                cs.getStack().getX().getBiVal().SumBits();
            }
            break;
        case FnABS: // Absolute value
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnABS.index() - G_KEY, "g ABS"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    cs.getStack().getX()
                            .setFVal(Math.abs(cs.getStack().getX().getFVal()));
                } else {
                    cs.getStack().getX().getBiVal().AbsoluteValue();
                    cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                            .getBiVal().isCarryBit());
                    Packet.setMenuNeedsUpdating(true);
                }
            }
            break;
        case FnDBLR: // Double remainder (after division)
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnDBLR.index() - G_KEY, "g DBLR"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getStack().getX().getBiVal().IsZero()) {
                    Packet.setAlternateText("Error 0 - Improper Math Operation");
                    Prefix = 0;
                    return Packet;
                }

                Register x, y, z;
                Register large = new Register();
                x = cs.getStack().Pop();
                y = cs.getStack().Pop();
                z = cs.getStack().Pop();
                try {
                    large.getBiVal().Combine(y.getBiVal(), z.getBiVal());
                } catch (Exception e) {
                    // Not likely!
                }
                large.setBiVal(BigInt.Remainder(large.getBiVal(), x.getBiVal()));
                if (large.getBiVal().isOverflow()) {
                    Packet.setAlternateText("Error 0 - Improper Math Operation");
                    Prefix = 0;
                    return Packet;
                }
                large.getBiVal().setWordSize(cs.getWordSize());
                cs.getStack().Push(large);
            }
            break;
        case FnDBLDiv: // Double division
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnDBLR.index() - G_KEY, "g DBL/"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getStack().getX().getBiVal().IsZero()) {
                    Packet.setAlternateText("Error 0 - Improper Math Operation");
                    Prefix = 0;
                    return Packet;
                }
                Register x, y, z;
                Register temp2;
                Register temp = new Register();
                x = cs.getStack().Pop();
                y = cs.getStack().Pop();
                z = cs.getStack().Pop();
                try {
                    temp.getBiVal().Combine(y.getBiVal(), z.getBiVal());
                } catch (Exception e) {
                    // Not likely!
                }
                temp.getBiVal().Divide(x.getBiVal(), cs.getWordSize());

                // does the answer fit?
                temp2 = temp.Copy();
                temp2.getBiVal().setWordSize(cs.getWordSize());
                if (!temp2.getBiVal().Equals(temp.getBiVal())) {
                    // if not, then set error zero (page 53)
                    Packet.setAlternateText("Error 0 - Improper Math Operation");
                    Prefix = 0;
                    return Packet;
                }

                cs.getStack().Push(temp2);
                cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                        .getBiVal().isCarryBit());

                // always clears the overflow flag (page 98)
                cs.setFlag(CalcState.CalcFlag.Overflow, false);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnRTN: // Return from subroutine (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnRTN.index() - G_KEY, "g RTN"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // pop the return position from the subroutine stack
                if (cs.getPrgmRetStack().size() == 0) {
                    // an "empty" return also means stop
                    cs.setPrgmRunning(false);
                } else {
                    cs.setPrgmPosition(cs.getPrgmRetStack().pop());
                }
            }
            break;
        case FnLBL: // A program Label (Program Mode)
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.FnLBL.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.FnLBL.index()) {
                Stage.clear();
                Stage.push(k.FnLBL.index());
                break;
            }

            // Get the stage operand
            val = Stage.pop();
            Stage.clear();

            if (PrgmEntry) {
                // For some reason, a program line with 3 elements uses commas
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$2X,%2$2X,%3$2X    '%4$s",
                                k.KeyGKey.index(), k.FnLBL.index() - G_KEY,
                                val, "g LBL " + String.format("%1$X", val)));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            }
            break;
        case FnDSZ: // Decrement, skip on zero (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnDSZ.index() - G_KEY, "g DSZ"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    cs.getRegIndex().setFVal(cs.getRegIndex().getFVal() - 1);
                    if (cs.getRegIndex().getFVal() == 0) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    cs.getRegIndex()
                            .getBiVal()
                            .Subtract(BigInt.One(cs.getWordSize()),
                                    cs.getWordSize());
                    if (cs.getRegIndex().getBiVal().IsZero()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnISZ: // Increment, skip on zero (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnISZ.index() - G_KEY, "g ISZ"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    cs.getRegIndex().setFVal(cs.getRegIndex().getFVal() + 1);
                    // v6.0.8 - 11 Jan 16 (thanks Andru)
                    if (cs.getRegIndex().getFVal() == 0) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    cs.getRegIndex()
                            .getBiVal()
                            .Add(BigInt.One(cs.getWordSize()), cs.getWordSize());
                    // v6.0.8 - 11 Jan 16 (thanks Andru)
                    if (cs.getRegIndex().getBiVal().IsZero()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnSqrt: // Square root
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnSqrt.index() - G_KEY, "g Sqrt"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() <= 0.0) {
                        Packet.setAlternateText("Error 0 - Improper Math Operation");
                        Prefix = 0;
                        return Packet;
                    }
                    cs.getStack().getX()
                            .setFVal(Math.sqrt(cs.getStack().getX().getFVal()));
                } else {
                    BigInt zero = new BigInt(cs.getWordSize(),
                            BigInt.ArithMode.toArithMode(cs.getArithMode()
                                    .index()));
                    if (cs.getStack().getX().getBiVal().IsLessOrEqual(zero)) {
                        Packet.setAlternateText("Error 0 - Improper Math Operation");
                        Prefix = 0;
                        return Packet;
                    }
                    cs.getStack().getX().getBiVal()
                            .SquareRoot(cs.getWordSize());
                    cs.setFlag(CalcState.CalcFlag.Carry, cs.getStack().getX()
                            .getBiVal().isCarryBit());
                    Packet.setMenuNeedsUpdating(true);
                }
            }
            break;
        case FnInv: // Inverse
            // Is this valid for the current mode?
            if (cs.getOpMode() != CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnInv.index() - G_KEY, "g 1/x"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                if (cs.getStack().getX().getFVal() == 0.0) {
                    Packet.setAlternateText("Error 0 - Improper Math Operation");
                    Prefix = 0;
                    return Packet;
                }
                cs.getStack().getX()
                        .setFVal(1.0 / cs.getStack().getX().getFVal());
            }
            break;
        case FnSF: // Set Flag
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.FnSF.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.FnSF.index()) {
                Stage.clear();
                Stage.push(k.FnSF.index());
                break;
            }
            StackDisable = false;
            
            // Check for valid stage operand
            val = Stage.pop();
            if (val < 0 || val > 5) {
                Stage.clear();
                Packet.setAlternateText("Error 1 - Improper Flag Number");
                Prefix = 0;
                return Packet;
            }

            Stage.clear();

            if (PrgmEntry) {
                // For some reason, a program line with 3 elements uses commas
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$2X,%2$2X,%3$2X    '%4$s",
                                k.KeyGKey.index(), k.FnSF.index() - G_KEY, val,
                                "g SF " + val));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setFlag(CalcState.CalcFlag.toCalcFlag(val), true);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnCF: // Clear Flag
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.FnCF.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.FnCF.index()) {
                Stage.clear();
                Stage.push(k.FnCF.index());
                break;
            }
            StackDisable = false;
            
            // Check for valid stage operand
            val = Stage.pop();
            if (val > 5) {
                Stage.clear();
                Packet.setAlternateText("Error 1 - Improper Flag Number");
                Prefix = 0;
                return Packet;
            }

            Stage.clear();

            if (PrgmEntry) {
                // For some reason, a program line with 3 elements uses commas
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$2X,%2$2X,%3$2X    '%4$s",
                                k.KeyGKey.index(), k.FnCF.index() - G_KEY, val,
                                "g CF " + val));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setFlag(CalcState.CalcFlag.toCalcFlag(val), false);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnFSet: // Is the Flag set
            // This is a multi-stage command, so we have to store 1
            // more keystroke before we can do anything
            if (Stage.size() == 0) {
                Stage.push(k.FnFSet.index());
                break;
            }

            // a quick sanity check
            if ((Integer) Stage.toArray()[0] != k.FnFSet.index()) {
                Stage.clear();
                Stage.push(k.FnFSet.index());
                break;
            }

            // Check for valid stage operand
            val = Stage.pop();
            if (val > 5) {
                Stage.clear();
                Packet.setAlternateText("Error 1 - Improper Flag Number");
                Prefix = 0;
                return Packet;
            }

            Stage.clear();

            if (PrgmEntry) {
                // For some reason, a program line with 3 elements uses commas
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$2X,%2$2X,%3$2X    '%4$s",
                                k.KeyGKey.index(), k.FnFSet.index() - G_KEY,
                                val, "g F? " + val));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.isFlag(CalcState.CalcFlag.toCalcFlag(val)) == false) {
                    cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                }
            }
            break;
        case FnDBLMul: // Double multiplication
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnDBLMul.index() - G_KEY, "g DBL*"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.setRegLastX(cs.getStack().getX().Copy());
                StackDisable = false;
                Register large_y;
                Register x = new Register(cs.getWordSize(), cs.getArithMode());
                Register y = new Register(cs.getWordSize(), cs.getArithMode());
                large_y = cs.getStack().Pop();
                large_y.getBiVal().setWordSize(cs.getWordSize() * 2);
                large_y.getBiVal().Multiply(cs.getStack().Pop().getBiVal(),
                        cs.getWordSize());
                BigInt[] ret = new BigInt[2];
                try {
                    ret = large_y.getBiVal().Split();
                } catch (Exception e) {
                    // not likely!
                }
                x.setBiVal(ret[0]);
                y.setBiVal(ret[1]);
                cs.getStack().Push(x);
                cs.getStack().Push(y);

                // always clears overflow (page 98)
                cs.setFlag(CalcState.CalcFlag.Overflow, false);
                Packet.setMenuNeedsUpdating(true);
            }
            break;
        case FnPR: // Toggle between Program and Run mode
            PrgmEntry = !PrgmEntry;
            Packet.setPrgm_Annunciator(PrgmEntry);
            break;
        case FnBST: // Back step (Program Mode)
            if (cs.getPrgmPosition() > 0) {
                cs.setPrgmPosition(cs.getPrgmPosition() - 1);
            } else {
                cs.setPrgmPosition(cs.getPrgmMemory().size());
            }
            break;
        // Note: BST can not be stored
        case FnRolUp: // Roll the stack up
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnRolUp.index() - G_KEY, "g R^"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Register temp;
                temp = cs.getStack().getT().Copy();
                cs.getStack().setT(cs.getStack().getZ().Copy());
                cs.getStack().setZ(cs.getStack().getY().Copy());
                cs.getStack().setY(cs.getStack().getX().Copy());
                cs.getStack().setX(temp);
                StackDisable = false;
            }
            break;
        case FnPSE: // Pause execution (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnPSE.index() - G_KEY, "g PSE"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                Packet.setDisplayText(FormatDisplay());
                Packet.setDelay(Integer.parseInt(fmMain.prop
                        .getProperty("SleepDelay")));
            }
            break;
        case FnCLX: // Clear the X register
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnCLX.index() - G_KEY, "g CLx"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.getStack().setX(
                        new Register(cs.getWordSize(), cs.getArithMode()));
                // "disable" the stack for the next entry
                StackDisable = true;
            }
            break;
        case FnLSTX: // Recall the contents of Last X register
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnLSTX.index() - G_KEY, "g LSTx"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                cs.getStack().setX(cs.getRegLastX().Copy());
                StackDisable = false;
            }
            break;
        case FnXlteY: // Is X less than or equal to Y (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXlteY.index() - G_KEY, "g X<Y"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() > cs.getStack().getY()
                            .getFVal()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (cs.getStack().getX().getBiVal()
                            .IsGreater(cs.getStack().getY().getBiVal())) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnXlt0: // Is X less than 0 (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXlt0.index() - G_KEY, "g X<0"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() >= 0.0) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (cs.getStack().getX().getBiVal().ToLong() >= 0L) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnXgtY: // Is X greater than Y (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXgtY.index() - G_KEY, "g X>Y"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() <= cs.getStack().getY()
                            .getFVal()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (cs.getStack().getX().getBiVal()
                            .IsLessOrEqual(cs.getStack().getY().getBiVal())) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnXgt0: // Is X greater than 0 (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXgt0.index() - G_KEY, "g X>0"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() <= 0.0) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (cs.getStack().getX().getBiVal().ToLong() <= 0L) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnSLeft: // Scroll the display Left
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnSLeft.index() - G_KEY, "g <"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // only allow this when the conditions warrant it
                if (cs.getOpMode() == CalcState.CalcOpMode.Bin
                        && cs.getWordSize() > 32) {
                    WinPos--;
                    if (WinPos < 0) {
                        WinPos = 0;
                    }
                }
            }
            break;
        case FnSRight: // Scroll the display Right
            // Is this valid for the current mode?
            if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                Packet.setBeep(true);
                break;
            }

            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnSRight.index() - G_KEY, "g >"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                // only allow this when the conditions warrant it
                if (cs.getOpMode() == CalcState.CalcOpMode.Bin
                        && cs.getWordSize() > 32) {
                    int len = cs.getStack().getX().getBiVal().ToStringBin(true)
                            .length();
                    WinPos++;
                    if (WinPos > len - 35) {
                        // this is just a code to indicate window 1
                        WinPos = len - 35;
                    }
                }
            }
            break;
        case FnXneY: // Is X not equal to Y (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXneY.index() - G_KEY, "g X<>Y"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() == cs.getStack().getY()
                            .getFVal()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (cs.getStack().getX().getBiVal()
                            .Equals(cs.getStack().getY().getBiVal())) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnXne0: // Is X not equal to 0 (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXne0.index() - G_KEY, "g X=0"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() == 0.0) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (cs.getStack().getX().getBiVal().IsZero()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnXeqY: // Is X equal to Y (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXeqY.index() - G_KEY, "g X=Y"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() != cs.getStack().getY()
                            .getFVal()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (!cs.getStack().getX().getBiVal()
                            .Equals(cs.getStack().getY().getBiVal())) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        case FnXeq0: // Is X equal to 0 (Program Mode)
            if (PrgmEntry) {
                cs.getPrgmMemory().add(
                        cs.getPrgmPosition(),
                        String.format("%1$5X%2$3X    '%3$s", k.KeyGKey.index(),
                                k.FnXeq0.index() - G_KEY, "g X=0"));
                cs.setPrgmPosition(cs.getPrgmPosition() + 1);
            } else {
                if (cs.getOpMode() == CalcState.CalcOpMode.Float) {
                    if (cs.getStack().getX().getFVal() != 0.0) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                } else {
                    if (!cs.getStack().getX().getBiVal().IsZero()) {
                        cs.setPrgmPosition(cs.getPrgmPosition() + 1);
                    }
                }
            }
            break;
        default:
            break;
        }

        // There are three types of clean up operations...
        // 1) Terminate an incomplete stage
        // 2) Clear the prefix
        // 3) Reset the display window to 0

        // Terminate an incomplete stage (if any)
        if (Stage.size() > 0 && (Integer) Stage.toArray()[0] != key.index()) {
            if (key.index() > 15 && key != k.KeyDp && key != k.FnI
                    && key != k.FnIndex) {
                // The real calculator is very forgiving (and silent) when you
                // enter a nonsensical stage operand (such as "STO +"). So, I
                // guess I'll do the same here (except for the silent part)
                Packet.setBeep(true);
                Stage.clear();
            }
        }

        // clear the prefix (if set)
        Prefix = 0;

        // should we reset the window position?
        if (ArrayindexOf(DoNotResetWinPos, key) < 0) {
            WinPos = 0;
        }

        // There are 3 things that could be showing in the display
        // 1) program lines during the "Program Entry" mode
        // 2) Raw input before it has been formatted
        // 3) Fully formatted register values

        if (PrgmEntry) {
            // Is this test even necessary? The PrgmMemoryLines value is quite
            // arbitrary. I could just not bother, and nobody would notice.
            if (cs.getPrgmMemory().size() == Integer.parseInt(fmMain.prop
                    .getProperty("PrgmMemoryLines"))) {
                Packet.setAlternateText("Error 4 - Improper Line Number");
                cs.getPrgmMemory().remove(
                        Integer.parseInt(fmMain.prop
                                .getProperty("PrgmMemoryLines")));
                cs.setPrgmPosition(Integer.parseInt(fmMain.prop
                        .getProperty("PrgmMemoryLines")) - 1);
                return Packet;
            }

            if (cs.getPrgmPosition() > 0) {
                Packet.setDisplayText(String.format("%1$03d",
                        cs.getPrgmPosition())
                        + "- "
                        + cs.getPrgmMemory().get(cs.getPrgmPosition() - 1)
                                .substring(0, 8));
            } else {
                Packet.setDisplayText("000-");
            }
        } else {
            // Should we terminate any existing digit entry? There are only
            // a few keys that do NOT terminate entry (page 99)
            if (key.index() > 15 && ArrayindexOf(TerminateInput, key) < 0) {
                RawDisplay.setLength(0);
            }

            // Either display the raw text or show the value of the
            // X register
            if (RawDisplay.length() > 0) {
                // is this a new entry? If so, then push the existing value of
                // the X register deeper onto the stack.
                // v6.0.4 - 29 May 12
                if ((RawDisplay.length() == 1 || PadDecimal) && !StackDisable) {
                    cs.getStack().Push(cs.getStack().getX());
                }
                StackDisable = false;
                PadDecimal = false;

                // put it in X, but don't (yet) format the display
                ConvertInput(RawDisplay.toString());

                // does the input fit in the size of our register
                if (cs.getStack().getX().getBiVal().isLossOfPrecision()
                        || Double.isInfinite(cs.getStack().getX().getFVal())) {
                    // remove the last digit you typed
                    RawDisplay.delete(RawDisplay.length() - 1,
                            RawDisplay.length());
                    Packet.setBeep(true);
                    // try it again
                    ConvertInput(RawDisplay.toString());
                }

                // Display the partial "raw" (unformatted) input. If the input
                // exceeds 37 characters, we trim to show the right-most 
                // portion of the string. There is no scrolling/windowing in
                // the raw input mode. This trimming does NOT affect how the
                // string gets converted into a number.
                if (!cs.isPrgmRunning()) {
                    switch (cs.getOpMode()) {
                    case Float:
                        Packet.setDisplayText(StringRight(
                                RawDisplay.toString(), 37));
                        break;
                    case Hex:
                        Packet.setDisplayText(String.format("%1$39s",
                                StringRight(RawDisplay.toString(), 37) + " h"));
                        break;
                    case Dec:
                        Packet.setDisplayText(String.format("%1$39s",
                                StringRight(RawDisplay.toString(), 37) + " d"));
                        break;
                    case Oct:
                        Packet.setDisplayText(String.format("%1$39s",
                                StringRight(RawDisplay.toString(), 37) + " o"));
                        break;
                    case Bin:
                        Packet.setDisplayText(String.format("%1$39s",
                                StringRight(RawDisplay.toString(), 37) + " b"));
                        break;
                    }
                }
            } else {
                // display the formatted value of the X register
                if (!cs.isPrgmRunning()) {
                    Packet.setDisplayText(FormatDisplay(cs.getOpMode(), WinPos));
                }
            }
        }

        // update the packet info
        Packet.setCarry_Annunciator(cs.isFlag(CalcState.CalcFlag.Carry));
        Packet.setOverflow_Annunciator(cs.isFlag(CalcState.CalcFlag.Overflow));
        return Packet;
    }

    // Parse the raw input based upon the current operating mode
    private void ConvertInput(String text) {
        Register temp = new Register(cs.getWordSize(), cs.getArithMode());
        BigInt.ArithMode bimode = BigInt.ArithMode.toArithMode(cs
                .getArithMode().index());
        switch (cs.getOpMode()) {
        case Float:
            // make an incomplete Scientific Notation parseable (is that a
            // word?)
            if (text.endsWith("e")) {
                text += "0";
            }
            try {
                // v6.0.7 - 22 Jul 2015
                NumberFormat nf = NumberFormat.getInstance(java.util.Locale.US);
                temp.setFVal(nf.parse(text).doubleValue());
            } catch (Exception e) {
                // We need some sort of marker to show that an error occurred
                temp.setFVal(Double.POSITIVE_INFINITY);
            }
            break;
        case Hex:
            temp.setBiVal(new BigInt("&H" + text, cs.getWordSize(), bimode));
            break;
        case Dec:
            temp.setBiVal(new BigInt(text, cs.getWordSize(), bimode));
            break;
        case Oct:
            temp.setBiVal(new BigInt("&O" + text, cs.getWordSize(), bimode));
            break;
        case Bin:
            // Note: the "&B" is a made up notation
            temp.setBiVal(new BigInt("&B" + text, cs.getWordSize(), bimode));
            break;
        }

        cs.getStack().setX(temp);
    }

    // Format the value of the X register for the display using the default
    // OpMode
    private String FormatDisplay() {
        return FormatDisplay(cs.getOpMode(), 0);
    }

    // Format the value of the X register for the display using the given OpMode
    private String FormatDisplay(CalcState.CalcOpMode Op, int Position) {
        String DisplayText, temp;

        DisplayText = "";
        switch (Op) {
        case Float:
            // format the screen as scientific notation?
            String Formatter;
            if (cs.getFloatPrecision() == k.KeyDp.index()) {
                // You can't adjust the precision of scientific notation in
                // the real calculator, so I just picked 13... I hope you like
                // it
                Formatter = "%1$.13e";
            } else {
                Formatter = "%1$." + cs.getFloatPrecision() + "f";
            }
            DisplayText = String.format(Formatter, cs.getStack().getX()
                    .getFVal());
            // Check to see if we need to automatically switch to scientific
            // notation
            // Note: this is for display purposes only, the float/EEX mode does
            // not change
            if (DisplayText.length() > 37
                    || (DisplayText.equals("0."
                            + StringStrDup('0', cs.getFloatPrecision())) && cs
                            .getStack().getX().getFVal() != 0.0)) {
                DisplayText = String.format("%1$.13e", cs.getStack().getX()
                        .getFVal());
            }
            break;
        case Hex:
            temp = cs.getStack().getX().getBiVal().ToStringHex().toUpperCase();
            if (!cs.isFlag(CalcState.CalcFlag.LeadingZero)) {
                temp = TrimZeros(temp);
            }
            DisplayText = String.format("%1$39s", temp + " h");
            break;
        case Dec:
            // Note: the decimal mode is the only one that has to deal with the
            // Arithmetic mode
            temp = TrimZeros(cs.getStack().getX().getBiVal().ToStringDec());
            // It's rather odd, that the LeadingZero feature is forced off (page
            // 36)
            DisplayText = String.format("%1$39s", temp + " d");
            break;
        case Oct:
            temp = cs.getStack().getX().getBiVal().ToStringOct();
            if (!cs.isFlag(CalcState.CalcFlag.LeadingZero)) {
                temp = TrimZeros(temp);
            }
            DisplayText = String.format("%1$39s", temp + " o");
            break;
        case Bin:
            temp = cs.getStack().getX().getBiVal().ToStringBin(true);
            // Note: the binary mode is the only one that supports
            // windows/scrolling

            if (temp.length() > 35) {
                if (Position == 0) {
                    // Window 0
                    // Note: We ignore the Leading Zero flag for window 0
                    DisplayText = String.format("%1$39s", StringRight(temp, 35)
                            + " .b");
                } else if (Position == 36) {
                    // Window 1
                    temp = temp.substring(0, temp.length() - 36);
                    if (!cs.isFlag(CalcState.CalcFlag.LeadingZero)) {
                        temp = TrimZeros(temp);
                    }
                    DisplayText = String.format("%1$39s", temp + " b.");
                } else {
                    // Note: This is not exactly the way that the real
                    // calculator does things... it allows you to "walk off
                    // the edge" of the left-most bits, whereas I just stop
                    // at the edge of the screen.
                    int pos;
                    pos = (temp.length() - 35 - Position);
                    temp = temp.substring(pos, pos + 35);
                    // We ignore the Leading Zero flag while scrolling
                    DisplayText = String.format("%1$39s", temp + " .b.");
                }
            } else {
                if (!cs.isFlag(CalcState.CalcFlag.LeadingZero)) {
                    temp = TrimZeros(temp);
                }
                DisplayText = String.format("%1$39s", temp + " b");
            }
            break;
        }

        return DisplayText;
    }

    // Locate the line number for a given program label
    private int FindLabel(int start, int label) {
        String line, Instruction;
        String[] PrgmMemory = cs.getPrgmMemory().toArray(
                new String[cs.getPrgmMemory().size()]);

        // a quick sanity check...
        // v6.0.2 - 26 Apr 12
        // v6.0.5 - 14 Aug 13
        if (start < 0 || start > cs.getPrgmMemory().size() || cs.getPrgmMemory().size() == 0) {
            return -1;
        }
            
        // build a prototype "g LBL" instruction and append the label
        Instruction = "43,22, " + String.format("%1$X", label);

        // search for that String from the current position
        for (int i = start; i < cs.getPrgmMemory().size(); i++) {
            line = PrgmMemory[i];
            if (line.startsWith(Instruction)) {
                return i;
            }
        }

        // searches are allowed to wrap around
        for (int i = 0; i < start; i++) {
            line = PrgmMemory[i];
            if (line.startsWith(Instruction)) {
                return i;
            }
        }

        return -1;
    }

    // Trim leading zeros from a String
    private static String TrimZeros(String buf) {
        int i = 0;
        String b;
        boolean is_neg;

        is_neg = false;
        if (buf.startsWith("-")) {
            i = 1;
            is_neg = true;
        }

        do {
            b = buf.substring(i, i + 1);
            i = i + 1;
            if (i >= buf.length()) {
                break;
            }
        } while (b.equals("0") || b.equals(" "));
        i = i - 1;

        // Leave at least the last zero
        if (i == buf.length()) {
            i = i - 1;
        }

        if (is_neg) {
            return "-" + buf.substring(i);
        } else {
            return buf.substring(i);
        }
    }

    // Clean up before we shut down
    public void TerminateOnExit() {
        // stop any running application
        if (cs.isPrgmRunning()) {
            cs.setPrgmRunning(false);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    // Provide the ability to copy text directly into the RawDisplay
    public void ImportRawDisplay(String text) throws Exception {
        try {
            ConvertInput(text);
        } catch (Exception ex) {
            RawDisplay.setLength(0);
            throw new Exception("Can't import pasted data", ex);
        }

        // does the input fit in the size of our register
        if (cs.getStack().getX().getBiVal().isLossOfPrecision()
                || Double.isInfinite(cs.getStack().getX().getFVal())) {
            RawDisplay.setLength(0);
            throw new Exception("Imported data out of range");
        }
        FormatDisplay();
    }

    // The right-most part of a String
    private static String StringRight(String s, int len) {
        if (s.length() <= len) {
            return s;
        }
        return s.substring(s.length() - len, s.length());
    }

    // Create a string of duplicated characters
    private static String StringStrDup(char c, int len) {
        char[] s = new char[len];
        for (int i = 0; i < len; i++) {
            s[i] = c;
        }
        return new String(s);
    }

    // the missing Array.IndexOf() method
    private static int ArrayindexOf(k[] list, k key) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] != null && list[i].equals(key) || key == null
                    && list[i] == null) {
                return i;
            }
        }
        return -1;
    }
}
