package com.jovial.jrpn;

import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;
import android.util.Xml;

// The internal state of the calculator.  These setting are what get
// stored while the calculator is turned off.
public class CalcState {

    // The major operating modes of the calculator
    public enum CalcOpMode {
        Float(0), Hex(1), Dec(2), Oct(3), Bin(4);

        // make a java enum a bit more civilized
        private int pindex;

        CalcOpMode(int val) {
            this.pindex = val;
        }

        public int index() {
            return pindex;
        }

        public static CalcOpMode toCalcOpMode(int val) {
            for (CalcOpMode enum_val : CalcOpMode.values()) {
                if (enum_val.pindex == val) {
                    return enum_val;
                }
            }
            // should this throw an exception?
            return null;
        }
    }

    // The Arithmetic modes of the calculator
    public enum CalcArithMode {
        Unsigned(0), OnesComp(1), TwosComp(2);

        // make a java enum a bit more civilized
        private int pindex;

        CalcArithMode(int val) {
            this.pindex = val;
        }

        public int index() {
            return pindex;
        }

        public static CalcArithMode toCalcArithMode(int val) {
            for (CalcArithMode enum_val : CalcArithMode.values()) {
                if (enum_val.pindex == val) {
                    return enum_val;
                }
            }
            // should this throw an exception?
            return null;
        }
    }

    // The calculator flags
    public enum CalcFlag {
        User0(0), User1(1), User2(2), LeadingZero(3), Carry(4), Overflow(5);

        // make a java enum a bit more civilized
        private int pindex;

        CalcFlag(int val) {
            this.pindex = val;
        }

        public int index() {
            return pindex;
        }

        public static CalcFlag toCalcFlag(int val) {
            for (CalcFlag enum_val : CalcFlag.values()) {
                if (enum_val.pindex == val) {
                    return enum_val;
                }
            }
            // should this throw an exception?
            return null;
        }
    }

    // In the real calculator the number of registers and program memory
    // "compete" for the same 203 bytes. So the max you can have are:
    // 1) 406 registers (at 4-bit word size) and 0 program memory
    // 2) 0 registers and 203 program memory
    //
    // However, in this version, there is no competition and the numbers
    // are static. You can choose any numbers you like in the config file.
    // The default is 32 registers and 203 lines of memory.

    private final int NUM_FLAGS = 6;

    private Boolean pSaveOnExit;
    private Integer pWordSize;
    private CalcOpMode pOpMode;
    private CalcArithMode pArithMode;
    private Integer pFloatPrecision;
    private Boolean[] pFlags = new Boolean[NUM_FLAGS];
    private Register[] pReg = new Register[Integer.parseInt(fmMain.prop
            .getProperty("NumRegisters"))];
    private Register pRegIndex;
    private CStack pStack;
    private Register pRegLastX;
    private Integer pPrgmPosition;
    private java.util.List<String> pPrgmMemory;
    private java.util.Stack<Integer> pPrgmRetStack;
    private Boolean pPrgmRunning;

    // Default constructor
    public CalcState() {
        pSaveOnExit = true;
        pWordSize = 16;
        pOpMode = CalcOpMode.Float;
        pArithMode = CalcArithMode.TwosComp;
        pFloatPrecision = 3;
        for (int i = 0; i < NUM_FLAGS; i++) {
            pFlags[i] = false;
        }
        pFlags[3] = true; // leading zeros
        for (int i = 0; i < Integer.parseInt(fmMain.prop
                .getProperty("NumRegisters")); i++) {
            pReg[i] = new Register(pWordSize, pArithMode);
        }
        pRegIndex = new Register(64, pArithMode); // RegIndex has a fixed size
        pStack = new CStack(pWordSize, pArithMode);
        pRegLastX = new Register(pWordSize, pArithMode);
        pPrgmPosition = 0;
        pPrgmMemory = new java.util.ArrayList<String>();
        pPrgmRetStack = new java.util.Stack<Integer>();
        pPrgmRunning = false;
    }

    // Save the configuration on Exit
    public boolean isSaveOnExit() {
        return pSaveOnExit;
    }

    public void setSaveOnExit(boolean save) {
        pSaveOnExit = save;
    }

    // The current operating mode of the calculator
    public CalcOpMode getOpMode() {
        return pOpMode;
    }

    public void setOpMode(CalcOpMode opmode) {
        pOpMode = opmode;
    }

    // The Arithmetic mode (the complement mode)
    public CalcArithMode getArithMode() {
        return pArithMode;
    }

    public void setArithMode(CalcArithMode mode) {
        if (pArithMode != mode) {
            // change the Arithmetic Mode of everything
            ReArithAll(mode);
        }
        pArithMode = mode;
    }

    // The number of decimal points for the display
    public int getFloatPrecision() {
        return pFloatPrecision;
    }

    public void setFloatPrecision(int fp) {
        pFloatPrecision = fp;
    }

    // The size of the integers
    public int getWordSize() {
        return pWordSize;
    }

    public void setWordSize(int size) {
        if (pWordSize != size) {
            // change the size of everything
            ReSizeAll(size);
        }
        pWordSize = size;
    }

    // The 6 user and systems flags
    public Boolean isFlag(CalcFlag flag) {
        return pFlags[flag.index()];
    }

    public void setFlag(CalcFlag flag, Boolean val) {
        pFlags[flag.index()] = val;
    }

    // The storage registers
    public Register getReg(int index) {
        return pReg[index];
    }

    public void setReg(int index, Register reg) {
        pReg[index] = reg;
    }

    // The index register
    public Register getRegIndex() {
        return pRegIndex;
    }

    public void setRegIndex(Register i) {
        pRegIndex = i;
    }

    // The Last X register
    public Register getRegLastX() {
        return pRegLastX;
    }

    public void setRegLastX(Register lx) {
        pRegLastX = lx;
    }

    // The location (the line) where the program is currently executing
    public int getPrgmPosition() {
        return pPrgmPosition;
    }

    public void setPrgmPosition(int pp) {
        pPrgmPosition = pp;
    }

    // Program memory
    public java.util.List<String> getPrgmMemory() {
        return pPrgmMemory;
    }

    // Program Return Stack
    public java.util.Stack<Integer> getPrgmRetStack() {
        return pPrgmRetStack;
    }

    // The Calculator Stack (x, y, z, and t registers)
    public CStack getStack() {
        return pStack;
    }

    // Is the calculator currently running a program
    public boolean isPrgmRunning() {
        return pPrgmRunning;
    }

    public void setPrgmRunning(boolean pr) {
        pPrgmRunning = pr;
    }

    // Save the CalcState as an XML string
    public String Serialize() throws ParserConfigurationException,
            IllegalArgumentException, IllegalStateException, IOException {
        XmlSerializer s = Xml.newSerializer();
        s.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output",
                true);
        StringWriter sw = new StringWriter();

        s.setOutput(sw);
        s.startDocument("UTF-8", true);

        // create the root element
        s.startTag("", "CalcState");
        s.attribute("", "saved", new java.util.Date().toString());

        s.comment("WRPN CalcState v"
                + fmMain.prop.getProperty("Version").substring(0, 3));

        s.startTag("", "SaveOnExit");
        s.text(pSaveOnExit.toString());
        s.endTag("", "SaveOnExit");

        s.startTag("", "WordSize");
        s.text(pWordSize.toString());
        s.endTag("", "WordSize");

        s.startTag("", "OpMode");
        s.text(pOpMode.toString());
        s.endTag("", "OpMode");

        s.startTag("", "ArithMode");
        s.text(pArithMode.toString());
        s.endTag("", "ArithMode");

        s.startTag("", "FloatPrecision");
        s.text(pFloatPrecision.toString());
        s.endTag("", "FloatPrecision");

        String[] FlagName = { "User0", "User1", "User2", "LeadingZero",
                "Carry", "Overflow" };
        s.startTag("", "Flags");
        for (int i = 0; i < NUM_FLAGS; i++) {
            s.startTag("", "Flag");
            s.attribute("", "name", FlagName[i]);
            s.text(pFlags[i].toString());
            s.endTag("", "Flag");
        }
        s.endTag("", "Flags");

        String[] RegName = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "A", "B", "C", "D", "E", "F", ".0", ".1", ".2", ".3", ".4",
                ".5", ".6", ".7", ".8", ".9", ".A", ".B", ".C", ".D", ".E",
                ".F" };
        s.startTag("", "Regs");
        for (int i = 0; i < Integer.parseInt(fmMain.prop
                .getProperty("NumRegisters")); i++) {
            s.startTag("", "Reg");
            if (i < RegName.length) {
                s.attribute("", "name", RegName[i]);
            } else {
                s.attribute("", "name", "Reg" + i);
            }
            s.startTag("", "FVal");
            s.text(pReg[i].getFVal().toString());
            s.endTag("", "FVal");
            s.startTag("", "BiVal");
            s.text(pReg[i].getBiVal().ToStringHex());
            s.endTag("", "BiVal");
            s.endTag("", "Reg");
        }
        s.endTag("", "Regs");

        s.startTag("", "RegIndex");
        s.startTag("", "FVal");
        s.text(pRegIndex.getFVal().toString());
        s.endTag("", "FVal");
        s.startTag("", "BiVal");
        s.text(pRegIndex.getBiVal().ToStringHex());
        s.endTag("", "BiVal");
        s.endTag("", "RegIndex");

        String[] StackName = { "T", "Z", "Y", "X" };
        Register[] StackArray = pStack.ToArray();
        s.startTag("", "Stacks");
        for (int i = 0; i < 4; i++) {
            s.startTag("", "Stack");
            s.attribute("", "name", StackName[i]);
            s.startTag("", "FVal");
            s.text(StackArray[i].getFVal().toString());
            s.endTag("", "FVal");
            s.startTag("", "BiVal");
            s.text(StackArray[i].getBiVal().ToStringHex());
            s.endTag("", "BiVal");
            s.endTag("", "Stack");
        }
        s.endTag("", "Stacks");

        s.startTag("", "RegLastX");
        s.startTag("", "FVal");
        s.text(pRegLastX.getFVal().toString());
        s.endTag("", "FVal");
        s.startTag("", "BiVal");
        s.text(pRegLastX.getBiVal().ToStringHex());
        s.endTag("", "BiVal");
        s.endTag("", "RegLastX");

        s.startTag("", "PrgmPosition");
        s.text(pPrgmPosition.toString());
        s.endTag("", "PrgmPosition");

        // start with 1, since that's what the display uses
        Integer num = 1;
        s.startTag("", "PrgmMemory");
        for (String line : pPrgmMemory) {
            s.startTag("", "Line");
            s.attribute("", "name", String.format("%1$03d", num));
            s.text(line);
            s.endTag("", "Line");
            num++;
        }
        s.endTag("", "PrgmMemory");

        num = 0;
        s.startTag("", "PrgmRetStack");
        if (pPrgmRetStack.size() > 0) {
            for (Integer i : pPrgmRetStack.toArray(new Integer[pPrgmRetStack
                    .size()])) {
                s.startTag("", "Return");
                s.attribute("", "name", num.toString());
                s.text(i.toString());
                s.endTag("", "Return");
                num++;
            }
        }
        s.endTag("", "PrgmRetStack");
        s.endTag("", "CalcState");
        s.endDocument();

        return sw.toString();
    }

    // Convert a saved XML file into the CalcState
    public void Deserialize(String state) throws ParserConfigurationException,
            SAXException, IOException {
        java.io.DataInputStream stream = null;

        try {
            // setIgnoringElementContentWhitespace doesn't work, so we have to
            // strip the newlines "by hand". Oh well.... such is the life
            // of an Android programmer.
            state = state.replaceAll(">\\s*<", "><");

            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
                    state.getBytes("UTF-8"));
            stream = new java.io.DataInputStream(bais);
            Document doc = docBuilder.parse(stream);
            NodeList nl, subnl;

            nl = doc.getElementsByTagName("SaveOnExit");
            pSaveOnExit = Boolean.parseBoolean(nl.item(0).getFirstChild()
                    .getNodeValue());

            nl = doc.getElementsByTagName("WordSize");
            pWordSize = Integer.parseInt(nl.item(0).getFirstChild()
                    .getNodeValue());

            nl = doc.getElementsByTagName("OpMode");
            pOpMode = CalcOpMode.valueOf(nl.item(0).getFirstChild()
                    .getNodeValue());

            nl = doc.getElementsByTagName("ArithMode");
            pArithMode = CalcArithMode.valueOf(nl.item(0).getFirstChild()
                    .getNodeValue());
            BigInt.ArithMode bimode = BigInt.ArithMode.toArithMode(pArithMode
                    .index());

            nl = doc.getElementsByTagName("FloatPrecision");
            pFloatPrecision = Integer.parseInt(nl.item(0).getFirstChild()
                    .getNodeValue());

            nl = doc.getElementsByTagName("Flag");
            for (int i = 0; i < nl.getLength(); i++) {
                pFlags[i] = Boolean.parseBoolean(nl.item(i).getFirstChild()
                        .getNodeValue());
            }

            nl = doc.getElementsByTagName("Reg");
            // it's possible that somebody changed the configuration, so we
            // can't count on NumRegisters
            int num_regs = Math.min(nl.getLength(),
                    Integer.parseInt(fmMain.prop.getProperty("NumRegisters")));
            for (int i = 0; i < num_regs; i++) {
                subnl = nl.item(i).getChildNodes();
                pReg[i].setFVal(Double.parseDouble(subnl.item(0)
                        .getFirstChild().getNodeValue()));
                pReg[i].setBiVal(new BigInt("&H"
                        + subnl.item(1).getFirstChild().getNodeValue(),
                        pWordSize, bimode));
            }

            nl = doc.getElementsByTagName("RegIndex");
            subnl = nl.item(0).getChildNodes();
            pRegIndex.setFVal(Double.parseDouble(subnl.item(0).getFirstChild()
                    .getNodeValue()));
            // recall that RegIndex size is fixed at 64 bits
            pRegIndex
                    .setBiVal(new BigInt("&H"
                            + subnl.item(1).getFirstChild().getNodeValue(), 64,
                            bimode));

            nl = doc.getElementsByTagName("Stack");
            Register stack;
            for (int i = 0; i < nl.getLength(); i++) {
                subnl = nl.item(i).getChildNodes();
                stack = new Register();
                stack.setFVal(Double.parseDouble(subnl.item(0).getFirstChild()
                        .getNodeValue()));
                stack.setBiVal(new BigInt("&H"
                        + subnl.item(1).getFirstChild().getNodeValue(),
                        pWordSize, bimode));
                pStack.Push(stack);
            }

            nl = doc.getElementsByTagName("RegLastX");
            subnl = nl.item(0).getChildNodes();
            pRegLastX.setFVal(Double.parseDouble(subnl.item(0).getFirstChild()
                    .getNodeValue()));
            pRegLastX.setBiVal(new BigInt("&H"
                    + subnl.item(1).getFirstChild().getNodeValue(), pWordSize,
                    bimode));

            nl = doc.getElementsByTagName("PrgmPosition");
            pPrgmPosition = Integer.parseInt(nl.item(0).getFirstChild()
                    .getNodeValue());

            nl = doc.getElementsByTagName("Line");
            pPrgmMemory.clear();
            if (nl != null) {
                for (int i = 0; i < nl.getLength(); i++) {
                    pPrgmMemory.add(nl.item(i).getFirstChild().getNodeValue());
                }
            }

            nl = doc.getElementsByTagName("Return");
            pPrgmRetStack.clear();
            if (nl != null) {
                for (int i = 0; i < nl.getLength(); i++) {
                    pPrgmRetStack.push(Integer.parseInt(nl.item(i)
                            .getFirstChild().getNodeValue()));
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    // Resize all of the Big Integer values inside the registers
    private void ReSizeAll(int size) {
        if (Boolean.parseBoolean(fmMain.prop.getProperty("SyncConversions"))) {
            for (int i = 0; i < Integer.parseInt(fmMain.prop
                    .getProperty("NumRegisters")); i++) {
                pReg[i].getBiVal().setWordSize(size);
            }
            pRegLastX.getBiVal().setWordSize(size);
            pStack.getX().getBiVal().setWordSize(size);
            pStack.getY().getBiVal().setWordSize(size);
            pStack.getZ().getBiVal().setWordSize(size);
            pStack.getT().getBiVal().setWordSize(size);
        } else {
            // Using the rules on page 32 and 66 the storage registers
            // are not resized. Also, the stack does not preserve the
            // sign bit on widening
            BigInt.ArithMode tempArith = BigInt.ArithMode
                    .toArithMode(pArithMode.index());
            pRegLastX.getBiVal().setBIArithMode(BigInt.ArithMode.Unsigned);
            pStack.getX().getBiVal().setBIArithMode(BigInt.ArithMode.Unsigned);
            pStack.getY().getBiVal().setBIArithMode(BigInt.ArithMode.Unsigned);
            pStack.getZ().getBiVal().setBIArithMode(BigInt.ArithMode.Unsigned);
            pStack.getT().getBiVal().setBIArithMode(BigInt.ArithMode.Unsigned);

            pRegLastX.getBiVal().setWordSize(size);
            pStack.getX().getBiVal().setWordSize(size);
            pStack.getY().getBiVal().setWordSize(size);
            pStack.getZ().getBiVal().setWordSize(size);
            pStack.getT().getBiVal().setWordSize(size);

            pRegLastX.getBiVal().setBIArithMode(tempArith);
            pStack.getX().getBiVal().setBIArithMode(tempArith);
            pStack.getY().getBiVal().setBIArithMode(tempArith);
            pStack.getZ().getBiVal().setBIArithMode(tempArith);
            pStack.getT().getBiVal().setBIArithMode(tempArith);
        }
    }

    // Reset the Arithmetic mode of all of the BigInt values inside the
    // registers
    private void ReArithAll(CalcArithMode mode) {
        BigInt.ArithMode bimode = BigInt.ArithMode.toArithMode(mode.index());
        for (int i = 0; i < Integer.parseInt(fmMain.prop
                .getProperty("NumRegisters")); i++) {
            pReg[i].getBiVal().setBIArithMode(bimode);
        }
        pRegLastX.getBiVal().setBIArithMode(bimode);
        pRegIndex.getBiVal().setBIArithMode(bimode);
        pStack.getX().getBiVal().setBIArithMode(bimode);
        pStack.getY().getBiVal().setBIArithMode(bimode);
        pStack.getZ().getBiVal().setBIArithMode(bimode);
        pStack.getT().getBiVal().setBIArithMode(bimode);
    }

    // Synchronize the Big Integer and Float values within a Register
    public void SyncValues() {
        if (Boolean.parseBoolean(fmMain.prop.getProperty("SyncConversions"))) {
            // With SyncConversion set to true, we do NOT follow the behavior
            // of the real calculator. Instead, we synchronize the integer
            // and float values when switching between modes. Obviously, there
            // will likely be some loss of precision when decimal values are
            // truncated.
            if (getOpMode() == CalcOpMode.Float) {
                BigInt.ArithMode bimode = BigInt.ArithMode
                        .toArithMode(pArithMode.index());

                // if we're currently in the float mode, then we need to
                // copy those values to the to the integer mode
                for (int i = 0; i < Integer.parseInt(fmMain.prop
                        .getProperty("NumRegisters")); i++) {
                    pReg[i].setBiVal(new BigInt(pReg[i].getFVal(), pWordSize,
                            bimode));
                }
                pRegLastX.setBiVal(new BigInt(pRegLastX.getFVal(), pWordSize,
                        bimode));
                pStack.getX().setBiVal(
                        new BigInt(pStack.getX().getFVal(), pWordSize, bimode));
                pStack.getY().setBiVal(
                        new BigInt(pStack.getY().getFVal(), pWordSize, bimode));
                pStack.getZ().setBiVal(
                        new BigInt(pStack.getZ().getFVal(), pWordSize, bimode));
                pStack.getT().setBiVal(
                        new BigInt(pStack.getT().getFVal(), pWordSize, bimode));
                pRegIndex.setBiVal(new BigInt(pRegIndex.getFVal(), 64, bimode));
            } else {
                // copy the values to the float mode
                for (int i = 0; i < Integer.parseInt(fmMain.prop
                        .getProperty("NumRegisters")); i++) {
                    pReg[i].setFVal(pReg[i].getBiVal().ToLong());
                }
                pRegLastX.setFVal(pRegLastX.getBiVal().ToLong());
                pStack.getX().setFVal(pStack.getX().getBiVal().ToLong());
                pStack.getY().setFVal(pStack.getY().getBiVal().ToLong());
                pStack.getZ().setFVal(pStack.getZ().getBiVal().ToLong());
                pStack.getT().setFVal(pStack.getT().getBiVal().ToLong());
                pRegIndex.setFVal(pRegIndex.getBiVal().ToLong());
            }
        } else {
            // If no "Sync Conversion" then we use the rules from the real
            // calculator (yuk!)
            if (getOpMode() == CalcOpMode.Float) {
                // The rules for converting from Float mode to Integer mode
                // are found at page 60
                pWordSize = 56;

                double x, temp;
                long tempX, tempY;
                boolean neg;

                tempX = 0;
                tempY = 0;
                neg = false;
                if (pStack.getX().getFVal() != 0.0) {
                    x = pStack.getX().getFVal();
                    if (x < 0) {
                        neg = true;
                        x = x * -1;
                    }
                    temp = x / Math.pow(2, 32);
                    tempX = (long) (Math.log(temp) / Math.log(2));
                    tempY = (long) (x / Math.pow(2, tempX));
                    if (neg) {
                        tempY = tempY * -1;
                    }
                }

                BigInt.ArithMode bimode = BigInt.ArithMode
                        .toArithMode(pArithMode.index());
                pStack.getX().setBiVal(new BigInt(tempX, pWordSize, bimode));
                pStack.getY().setBiVal(new BigInt(tempY, pWordSize, bimode));
            } else {
                // The rules for converting from Integer mode to Float mode
                // are found at page 57-58
                pStack.getY().setFVal(0);
                pStack.getZ().setFVal(0);
                pStack.getT().setFVal(0);
                pRegLastX.setFVal(0);

                // set x = (y)(2^x)
                pStack.getX()
                        .setFVal(
                                pStack.getY().getBiVal().ToLong()
                                        * Math.pow(2, pStack.getX().getBiVal()
                                                .ToLong()));
            }
        }
    }
}
