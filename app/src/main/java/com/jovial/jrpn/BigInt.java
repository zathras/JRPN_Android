package com.jovial.jrpn;

import java.io.IOException;

// This is the Big Integer class... it is somewhat analogous
// to the java.math.BigInteger class, except that this version
// has arbitrary precision plus a few calculator-specific
// functions
public final class BigInt {

    private boolean pOverflow;
    private boolean pCarryBit;
    private boolean pLossOfPrecision;
    private ArithMode pBIArithMode;
    private int BitSize;
    private int LastByte;
    private byte[] n;

    // The Arithmetic mode for signed decimal values
    public enum ArithMode {
        Unsigned(0), OnesComplement(1), TwosComplement(2);

        // make a java enum a bit more civilized
        private int pindex;

        ArithMode(int val) {
            this.pindex = val;
        }

        public int index() {
            return pindex;
        }

        public static ArithMode toArithMode(int val) {
            for (ArithMode enum_val : ArithMode.values()) {
                if (enum_val.pindex == val) {
                    return enum_val;
                }
            }
            // should this throw an exception?
            return null;
        }
    }

    /*
     * **************************** Constructors ******************************
     */

    // The default constructor
    public BigInt() {
        pOverflow = false;
        pCarryBit = false;
        pLossOfPrecision = false;
        pBIArithMode = ArithMode.TwosComplement;
        BitSize = 32;
        LastByte = 3;
        n = new byte[LastByte + 1];
    }

    // Constructor given a required size and mode
    public BigInt(int size, ArithMode mode) {
        this();
        pBIArithMode = mode;

        SetSize(size);
    }

    // Constructor from a byte array
    public BigInt(byte[] byte_array, int size, ArithMode mode) {
        this();
        pBIArithMode = mode;

        Import(byte_array, size, false);
    }

    // Constructor from another BigInt
    public BigInt(BigInt bi, int size, ArithMode mode) {
        this();
        pBIArithMode = mode;

        Import(bi.n, size, false);
    }

    // Constructor from a double
    public BigInt(double dbl, int size, ArithMode mode) {
        this();
        pBIArithMode = mode;

        byte[] bytes;
        long temp;

        // We actually just convert the double to a long, and then
        // perform the normal import for a long.

        // Convert only if there is some useful value
        if (dbl >= Long.MAX_VALUE || dbl <= Long.MIN_VALUE) {
            // Humm... should this generate an exception?
            pLossOfPrecision = true;
            return;
        }

        // Use default rounding rules!
        temp = (long) dbl;
        bytes = GetBytes(temp);

        // only 15 digit precision for a double
        Import(bytes, size, (dbl < 0));
    }

    // Constructor from an integer
    public BigInt(int i, int size, ArithMode mode) {
        this();
        pBIArithMode = mode;

        byte[] bytes;

        bytes = GetBytes(i);
        Import(bytes, size, (i < 0));
    }

    // Constructor from a long
    public BigInt(long ln, int size, ArithMode mode) {
        this();
        pBIArithMode = mode;

        byte[] bytes;

        bytes = GetBytes(ln);
        Import(bytes, size, (ln < 0));
    }

    // Constructor from a string
    public BigInt(String s, int size, ArithMode mode) {
        this();
        int i, bits, temp, n_byte, n_bit, finish;
        String c, number;
        BigInt z;

        if (s == null || s.equals("")) {
            return;
        }

        z = new BigInt(size, mode);
        pBIArithMode = mode;
        pLossOfPrecision = false;

        if (s.length() > 2 && s.startsWith("&")) {
            number = s.substring(2).toLowerCase().trim();
            switch (s.toLowerCase().toCharArray()[1]) {
            case 'o': // Octal notation
                // What is the "native size?
                finish = 0;
                // from right to left
                for (i = number.length() - 1; i >= 0; i--) {
                    // Stop at first unknown character
                    c = number.substring(i, i + 1);
                    if ("01234567".indexOf(c) == -1) {
                        finish = finish + 1;
                        break;
                    }
                }
                temp = (number.length() - finish) * 3;
                // Let's round up to the nearest byte
                n_bit = (((temp - 1) / 8) + 1) * 8;

                SetSize(n_bit);
                z.SetSize(n_bit);

                if (temp > BitSize) {
                    finish = (temp - BitSize) / 3;
                }

                // Process the string
                bits = 0;
                for (i = number.length() - 1; i >= finish; i--) {
                    temp = Integer.parseInt(number.substring(i, i + 1));
                    if (temp != 0) {
                        // Sneaky trick to prevent multiplication/shifts
                        z.Clear();
                        z.PutAt(temp, bits);
                        _Add(z);
                    }
                    // Number of bits to shift
                    bits = bits + 3;
                }
                break;
            case 'b': // Binary (not a really a standard notation!)
                // What is the "native size?
                finish = 0;
                // from right to left
                for (i = number.length() - 1; i >= 0; i--) {
                    // Stop at first unknown character
                    c = number.substring(i, i + 1);
                    if ("01".indexOf(c) == -1) {
                        finish = finish + 1;
                        break;
                    }
                }
                temp = number.length() - finish;
                // Let's round up to the nearest byte
                n_bit = (((temp - 1) / 8) + 1) * 8;

                SetSize(n_bit);

                if (temp > BitSize) {
                    finish = temp - BitSize;
                }

                // Process the string
                for (i = number.length() - 1; i >= finish; i--) {
                    // Which byte and which bit?
                    n_byte = (number.length() - 1 - i) / 8;
                    n_bit = (number.length() - 1 - i) % 8;
                    if (number.substring(i, i + 1).equals("1")) {
                        this.n[n_byte] = (byte) (this.n[n_byte] + Math.pow(2,
                                n_bit));
                    }
                }
                break;
            case 'h': // Hexadecimal notation
                // What is the "native size?
                finish = 0;
                // from right to left
                for (i = number.length() - 1; i >= 0; i--) {
                    // Stop at first unknown character
                    c = number.substring(i, i + 1);
                    if ("0123456789abcdef".indexOf(c) == -1) {
                        finish = finish + 1;
                        break;
                    }
                }
                temp = (number.length() - finish) * 4;
                // Let's round up to the nearest byte
                n_bit = (((temp - 1) / 8) + 1) * 8;

                SetSize(n_bit);
                z.SetSize(n_bit);

                if (temp > BitSize) {
                    finish = (temp - BitSize) / 4;
                }

                // Process the string
                bits = 0;
                for (i = number.length() - 1; i >= finish; i--) {
                    temp = Integer.parseInt(number.substring(i, i + 1), 16);
                    if (temp != 0) {
                        // Sneaky trick to prevent multiplication/shifts
                        z.Clear();
                        z.PutAt(temp, bits);
                        _Add(z);
                    }
                    // Number of bits to shift
                    bits = bits + 4;
                }
                break;
            }
        } else {
            // If no leading "&", it must be a decimal notation
            finish = 0;
            // From right to left. This might seem a bit counterintuitive
            // since "123.45" would yield "45" and not "123"
            for (i = s.length() - 1; i >= 0; i--) {
                // Stop at first unknown character (including a decimal or
                // negative sign)
                c = s.substring(i, i + 1);
                if ("0123456789".indexOf(c) == -1) {
                    finish = i + 1;
                    break;
                }
            }
            // Since we're not really doing bit manipulation, this size
            // estimate isn't all that critical
            temp = (int) ((s.length() - finish) * 3.5);

            // Let's round up to the nearest byte
            n_bit = (((temp - 1) / 8) + 1) * 8;
            SetSize(n_bit);

            BigInt mult = new BigInt(n_bit, mode);
            BigInt ten = new BigInt(n_bit, mode);
            BigInt one = BigInt.One(n_bit);
            z.SetSize(n_bit);

            // This is normal stuff. You multiply each digit times the value
            // associated with that decimal place, then add 'em all up.
            mult.n[0] = 1;
            ten.n[0] = 10;
            for (i = s.length() - 1; i >= finish; i--) {
                temp = Integer.parseInt(s.substring(i, i + 1));
                if (temp != 0) {
                    z.Clear();
                    z.n[0] = (byte) temp;
                    z.Multiply(mult, BitSize);
                    _Add(z);
                }
                // Update the Decimal place
                mult.Multiply(ten, BitSize);
                // Did you overflow the mult?
                if (mult.isOverflow()) {
                    pLossOfPrecision = true;
                    break;
                }
            }
            // If it's negative assume 2's complement
            if (s.startsWith("-")) {
                OnesComplement(BitSize);
                _Add(one);
            }
        }
        // Since we're comparing with the "wiggle room" version of the value
        // we have to temporarily tweak the ArithMode to make the comparison
        // valid
        ArithMode tmode = pBIArithMode;
        pBIArithMode = ArithMode.Unsigned;

        z = this.Copy();
        SetSize(size);
        if (!Equals(z)) {
            pLossOfPrecision = true;
        }

        // put it back the way it was...
        pBIArithMode = tmode;
    }

    /*
     * **************************** Properties ******************************
     */

    // The Overflow property
    public boolean isOverflow() {
        return pOverflow;
    }

    // The Carry Bit property
    public boolean isCarryBit() {
        return pCarryBit;
    }

    // The Loss of Precision property
    public boolean isLossOfPrecision() {
        return pLossOfPrecision;
    }

    // The Big Integer ArithMode getter/setter
    public ArithMode getBIArithMode() {
        return pBIArithMode;
    }

    public void setBIArithMode(ArithMode mode) {
        pBIArithMode = mode;
    }

    // The Word Size getter/setter
    public int getWordSize() {
        return BitSize;
    }

    public void setWordSize(int size) {
        SetSize(size);
        BitSize = size;
    }

    // The Byte Array getter/setter
    public byte[] getByteArray() {
        return this.n;
    }

    /*
     * ************************** Math Operators ******************************
     */

    // Add two BigInt values
    public static BigInt Add(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.Add(y, 0);
        return ans;
    }

    // Add two BigInt values, with required size
    public static BigInt Add(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.Add(y, size);
        return ans;
    }

    // Add a BigInt value to the current value
    public void Add(BigInt y, int size) {
        int real_bitsize;
        BigInt temp_y;
        boolean sign_x, sign_y;

        temp_y = y.Copy();

        // determine the bitsize, and make 'em all the same
        real_bitsize = (size > 0) ? size
                : ((BitSize >= temp_y.BitSize) ? BitSize : temp_y.BitSize);
        SetSize(real_bitsize);
        temp_y.SetSize(real_bitsize);

        sign_x = this.SignBit();
        sign_y = temp_y.SignBit();

        // We temporarily need a bit more "wiggle room" than just the current
        // bitsize, so we add another byte. This is similar to SetSize, but
        // never does a sign extension
        AddPadding(RoundUp(real_bitsize));
        temp_y.AddPadding(BitSize);

        _Add(temp_y);

        // We needed the wiggle room to detect carry and overflow
        pCarryBit = MaxBit() >= real_bitsize;
        switch (getBIArithMode()) {
        case Unsigned:
            // that was easy...
            pOverflow = pCarryBit;
            break;
        case OnesComplement:
            // the "wrap around" rule for adding 1's complement numbers
            if (pCarryBit) {
                _Add(BigInt.One(BitSize));
            }

            // determining overflow is a bit bizarre
            pOverflow = false;
            if (sign_x) {
                if (sign_y && !(this.BitTest(real_bitsize - 1)) && pCarryBit) {
                    pOverflow = true;
                }
            } else if (!sign_y && this.BitTest(real_bitsize - 1) && !pCarryBit) {
                pOverflow = true;
            }
            break;
        case TwosComplement:
            // determining overflow is a bit bizarre
            pOverflow = false;
            if (sign_x) {
                if (sign_y && !(this.BitTest(real_bitsize - 1)) && pCarryBit) {
                    pOverflow = true;
                }
            } else if (!sign_y && this.BitTest(real_bitsize - 1) && !pCarryBit) {
                pOverflow = true;
            }
            break;
        }
        // OK, but it back to the real bitsize
        SetSize(real_bitsize);
    }

    // Internal version of Add (does not set Carry, Overflow, LOP)
    private void _Add(BigInt y) {
        int carry, temp;

        // Normal stuff... add two bytes, keep the "carry" for the next byte
        carry = 0;
        for (int i = 0; i <= this.LastByte; i++) {
            temp = (int) (this.n[i] & 0xff) + (int) (y.n[i] & 0xff) + carry;
            this.n[i] = (byte) (temp % 256);
            carry = temp / 256;
        }
        Mask();
    }

    // Subtract two BigInt values
    public static BigInt Subtract(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.Subtract(y, 0);
        return ans;
    }

    // Subtract two BigInt values with required size
    public static BigInt Subtract(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.Subtract(y, size);
        return ans;
    }

    // Subtract a BigInt value from the current value
    public void Subtract(BigInt y, int size) {
        int real_bitsize;
        BigInt temp_y;
        boolean sign_x, sign_y;

        temp_y = y.Copy();

        // determine the bitsize, and make 'em all the same
        real_bitsize = (size > 0) ? size
                : ((BitSize >= temp_y.BitSize) ? BitSize : temp_y.BitSize);
        SetSize(real_bitsize);
        temp_y.SetSize(real_bitsize);

        sign_x = this.SignBit();
        sign_y = temp_y.SignBit();

        // We temporarily need a bit more "wiggle room" than just the current
        // bitsize, so we add another byte. This is similar to SetSize, but
        // never does a sign extension
        AddPadding(RoundUp(real_bitsize));
        temp_y.AddPadding(BitSize);

        _Subtract(temp_y);

        // We needed the wiggle room to detect carry and overflow
        pCarryBit = (MaxBit() >= real_bitsize);
        switch (getBIArithMode()) {
        case Unsigned:
            // that was easy...
            pOverflow = pCarryBit;
            break;
        case OnesComplement:
            // the "wrap around" rule for subtracting 1's complement numbers
            if (pCarryBit) {
                _Subtract(BigInt.One(BitSize));
            }
            // determining overflow is a bit bizarre
            pOverflow = false;
            if (sign_x) {
                if (!sign_y && (!this.BitTest(real_bitsize - 1)) && !pCarryBit) {
                    pOverflow = true;
                }
            } else if (sign_y && this.BitTest(real_bitsize - 1) && pCarryBit) {
                pOverflow = true;
            }
            break;
        case TwosComplement:
            // determining overflow is a bit bizarre
            pOverflow = false;
            if (sign_x) {
                if (!sign_y && (!this.BitTest(real_bitsize - 1)) && !pCarryBit) {
                    pOverflow = true;
                }
            } else if (sign_y && this.BitTest(real_bitsize - 1) && pCarryBit) {
                pOverflow = true;
            }
            break;
        }
        // OK, but it back to the real bitsize
        SetSize(real_bitsize);
    }

    // Internal version of Subtract (does not set Carry, Overflow, LOP)
    private void _Subtract(BigInt y) {
        int carry, temp;

        // Normal stuff... subtract two bytes, generate a "borrow" if needed
        carry = 0;
        for (int i = 0; i <= this.LastByte; i++) {
            temp = (int) (this.n[i] & 0xff) - (int) (y.n[i] & 0xff) - carry;
            // Do we need a "borrow"?
            if (temp < 0) {
                temp = temp + 256;
                carry = 1;
            } else {
                carry = 0;
            }
            this.n[i] = (byte) temp;
        }
        Mask();
    }

    // Multiply two BigInt values
    public static BigInt Multiply(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.Multiply(y, 0);
        return ans;
    }

    // Multiply two BigInt values with required size
    public static BigInt Multiply(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.Multiply(y, size);
        return ans;
    }

    // Multiply a BigInt value with the current value
    public void Multiply(BigInt y, int size) {
        int temp, real_bitsize;
        BigInt temp_x, temp_y, z;

        temp_x = this.Copy();
        temp_y = y.Copy();

        // We need a bit more "wiggle room" than just the current bitsize,
        // so round off to the nearest byte and add 1 byte
        real_bitsize = (size > 0) ? size
                : ((temp_x.BitSize >= temp_y.BitSize) ? temp_x.BitSize
                        : temp_y.BitSize);
        SetSize(RoundUp(real_bitsize));
        temp_x.SetSize(BitSize);
        temp_y.SetSize(BitSize);
        z = new BigInt(BitSize, pBIArithMode);
        Clear();

        // Let's handle some special cases
        if (temp_x.IsZero() || temp_y.IsZero()) {
            SetSize(real_bitsize);
            return;
        }
        // This is just like you were taught is school... multiply each digit
        // in the first number with every digit in the second. Keep track of
        // what "column" to write down the intermediate values, then add the
        // intermediate values together.
        for (int i = 0; i <= temp_x.MaxByte(); i++) {
            for (int j = 0; j <= temp_y.MaxByte(); j++) {
                temp = (int) (temp_x.n[i] & 0xff) * (int) (temp_y.n[j] & 0xff);
                if (temp != 0) {
                    if (i + j <= LastByte) {
                        // This is a sneaky trick to create intermediate values
                        // without using shifts. Normally you'd multiply the
                        // temp value times the value for that "decimal place".
                        z.Clear();
                        z.PutAt(temp, (i + j) * 8);
                        _Add(z);
                    } else {
                        pOverflow = true;
                    }
                }
            }
        }
        // We needed the wiggle room to detect over_flow
        if (!pOverflow) {
            pOverflow = (MaxBit() >= real_bitsize);
        }
        SetSize(real_bitsize);
    }

    // Divide two BigInt values
    public static BigInt Divide(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.Divide(y, 0);
        return ans;
    }

    // Divide two BigInt values with required size
    public static BigInt Divide(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.Divide(y, size);
        return ans;
    }

    // Divide the current value with a BigInt value
    public void Divide(BigInt y, int size) {
        int nx, ny, nc, ans_digit, real_bitsize;
        BigInt chunk, guess, temp_x, temp_y, z;
        boolean neg = false;

        temp_y = y.Copy();
        temp_x = this.Copy();

        // We need a bit more "wiggle room" than just the current bitsize,
        // so round off to the nearest byte and add 1 byte
        real_bitsize = (size > 0) ? size
                : ((temp_x.BitSize >= temp_y.BitSize) ? temp_x.BitSize
                        : temp_y.BitSize);
        SetSize(RoundUp(real_bitsize));
        temp_x.SetSize(BitSize);
        temp_y.SetSize(BitSize);
        guess = new BigInt(BitSize, pBIArithMode);
        chunk = new BigInt(BitSize, pBIArithMode);

        // Sanity check
        if (temp_y.IsZero()) {
            pOverflow = true;
            SetSize(real_bitsize);
            Clear();
            return;
        }
        // When using signed numbers, we first figure out the what the sign of
        // the answer will be, then convert everything to absolute value
        if (pBIArithMode != ArithMode.Unsigned) {
            // if either is negative (but not both)
            if (temp_x.SignBit() ^ temp_y.SignBit()) {
                neg = true;
            }
            temp_x.pBIArithMode = ArithMode.TwosComplement;
            temp_x.AbsoluteValue();
            temp_x.pBIArithMode = ArithMode.Unsigned;

            temp_y.pBIArithMode = ArithMode.TwosComplement;
            temp_y.AbsoluteValue();
            temp_y.pBIArithMode = ArithMode.Unsigned;
        }

        // Special case
        if (temp_x.IsLess(temp_y)) {
            if (!temp_x.IsZero()) {
                pCarryBit = true;
            }
            SetSize(real_bitsize);
            Clear();
            return;
        }

        // This is almost like you were taught in school... For each digit in
        // the answer, guess the number of times the divisor will go into a
        // "chunk" of the dividend. We know the size of the chunk will have
        // the same number of digits as x or be one digit larger. Subtract
        // the intermediate value and repeat.
        Clear();
        ny = temp_y.MaxByte();
        do {
            if (temp_x.IsLess(temp_y)) {
                break;
            }

            nx = temp_x.MaxByte();
            // Build a "chunk" that has the same number of digits as y
            chunk.Clear();
            for (int i = 0; i <= ny; i++) {
                chunk.n[i] = temp_x.n[i + nx - ny];
            }
            nc = ny;

            // If it is too small, build another chunk with one more digit
            if (chunk.IsLess(temp_y)) {
                for (int i = 0; i <= (ny + 1); i++) {
                    chunk.n[i] = temp_x.n[i + nx - ny - 1];
                }
                nc = (ny + 1);
            }

            // To keep from testing all 255 possibilities for each "answer
            // digit", we use a simplified binary tree search algorithm.
            ans_digit = 128;
            guess.Clear();
            for (int i = 6; i >= 0; i--) {
                guess.n[0] = (byte) ans_digit;
                z = Multiply(temp_y, guess);
                if (z.IsGreater(chunk)) {
                    ans_digit = ans_digit - (int) Math.pow(2, i);
                } else {
                    ans_digit = ans_digit + (int) Math.pow(2, i);
                }
            }
            guess.n[0] = (byte) ans_digit;
            z = Multiply(temp_y, guess);
            if (z.IsGreater(chunk)) {
                ans_digit = ans_digit - 1;
            }

            // Place answer digit in the proper "column"
            z.Clear();
            z.PutAt(ans_digit, (nx - nc) * 8);

            // Add it to the answer.
            _Add(z);

            // Generate the intermediate value by shifting the "last guess"
            // multiplier to its proper place (which undoes the effect of the
            // shift that occurred when we developed the chunk).
            guess.Clear();
            guess.n[nx - nc] = (byte) ans_digit;
            z = Multiply(temp_y, guess);

            // Now subtract the intermediate value and continue...
            temp_x._Subtract(z);
        } while (nx - nc > 0);

        pCarryBit = !temp_x.IsZero();
        pOverflow = (MaxBit() >= real_bitsize);
        SetSize(real_bitsize);
        if (neg) {
            ChangeSign();
            if ((pBIArithMode == ArithMode.OnesComplement)) {
                _Add(BigInt.One(BitSize));
            }
        }
    }

    // Modulus (Division Remainder)
    public static BigInt Mod(BigInt x, BigInt y) {
        return Remainder(x, y, 0);
    }

    // Modulus (Division Remainder) with required size
    public static BigInt Mod(BigInt x, BigInt y, int size) {
        return Remainder(x, y, size);
    }

    // Modulus (Division Remainder)
    public static BigInt Remainder(BigInt x, BigInt y) {
        return Remainder(x, y, 0);
    }

    // Modulus (Division Remainder) with required size
    public static BigInt Remainder(BigInt x, BigInt y, int size) {
        int nx, ny, nc, ans_digit, real_bitsize;
        BigInt chunk, guess, temp_x, temp_y, z, ans;
        boolean neg = false;

        temp_x = x.Copy();
        temp_y = y.Copy();

        // We need a bit more wiggle room than the just current bitsize.
        real_bitsize = size > 0 ? size
                : temp_x.BitSize >= temp_y.BitSize ? temp_x.BitSize
                        : temp_y.BitSize;
        ans = new BigInt(RoundUp(real_bitsize), x.pBIArithMode);
        temp_x.SetSize(ans.BitSize);
        temp_y.SetSize(ans.BitSize);
        guess = new BigInt(ans.BitSize, x.pBIArithMode);
        chunk = new BigInt(ans.BitSize, x.pBIArithMode);

        // Sanity check
        if (temp_y.IsZero()) {
            ans.SetSize(real_bitsize);
            ans.Clear();
            ans.pOverflow = true;
            return ans;
        }

        // When using signed numbers, we first figure out the what the sign of
        // the answer will be, then convert everything to absolute value
        if (x.pBIArithMode != ArithMode.Unsigned) {
            if (x.SignBit()) {
                neg = true;
            }

            temp_x.pBIArithMode = ArithMode.TwosComplement;
            temp_x.AbsoluteValue();
            temp_x.pBIArithMode = ArithMode.Unsigned;

            temp_y.pBIArithMode = ArithMode.TwosComplement;
            temp_y.AbsoluteValue();
            temp_y.pBIArithMode = ArithMode.Unsigned;
        }

        // Special case (if x < y, then return x)
        if (temp_x.IsLess(temp_y)) {
            temp_x.SetSize(real_bitsize);
            if (neg) {
                temp_x.pBIArithMode = ArithMode.TwosComplement;
                temp_x.ChangeSign();
                temp_x.pBIArithMode = x.pBIArithMode;
            }
            return temp_x;
        }

        // This is almost like you were taught in school... For each digit in
        // the answer, guess the number of times the divisor will go into a
        // "chunk" of the dividend. We know the size of the chunk will have
        // the same number of digits as x or be one digit larger. Subtract
        // the intermediate value and repeat.

        ans = temp_x.Copy();
        ny = temp_y.MaxByte();
        do {
            // If no more divisors... we're done
            if (ans.IsLess(temp_y)) {
                break;
            }

            nx = ans.MaxByte();
            // Build a "chunk" that has the same number of digits as y
            chunk.Clear();
            for (int i = 0; i <= ny; i++) {
                chunk.n[i] = ans.n[i + nx - ny];
            }
            nc = ny;

            // If it is too small, build another chunk with one more digit
            if (chunk.IsLess(temp_y)) {
                for (int i = 0; i <= ny + 1; i++) {
                    chunk.n[i] = ans.n[i + nx - ny - 1];
                }
                nc = ny + 1;
            }

            // To keep from testing all 255 possibilities for each "answer
            // digit", we use a simplified binary tree search algorithm.
            ans_digit = 128;
            guess.Clear();
            for (int i = 6; i >= 0; i--) {
                guess.n[0] = (byte) ans_digit;
                // This is why we need more wiggle room... so we don't have
                // to deal with overflows during the guessing of the "answer
                // digit"
                z = Multiply(temp_y, guess);
                if (z.IsGreater(chunk)) {
                    ans_digit = (int) (ans_digit - (Math.pow(2, i)));
                } else {
                    ans_digit = (int) (ans_digit + (Math.pow(2, i)));
                }
            }
            guess.n[0] = (byte) ans_digit;
            z = Multiply(temp_y, guess);
            if (z.IsGreater(chunk)) {
                ans_digit = ans_digit - 1;
            }

            // Generate the intermediate value by shifting the "last guess"
            // multiplier to its proper place (which undoes the effect of the
            // shift that occurred when we developed the chunk).
            guess.Clear();
            guess.n[nx - nc] = (byte) ans_digit;
            z = Multiply(temp_y, guess);

            // Now subtract the intermediate value and continue...
            ans._Subtract(z);
        } while (nx - nc > 0);

        ans.SetSize(real_bitsize);
        if (neg) {
            // So, the rule for "matching the sign of x" always uses the 2's
            // complement mode. I wonder if that is really correct?
            ans.pBIArithMode = ArithMode.TwosComplement;
            ans.ChangeSign();
            ans.pBIArithMode = x.pBIArithMode;
        }
        return ans;
    }

    // Power (raise x to the power of y)
    public static BigInt Power(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.Power(y, 0);
        return ans;
    }

    // Power (raise x to the power of y) with required size
    public static BigInt Power(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.Power(y, size);
        return ans;
    }

    // Power (raise the current value to the power of y)
    public void Power(BigInt y, int size) {
        int real_bitsize;
        boolean over = false;
        BigInt temp_x, temp_y, one;

        temp_x = this.Copy();
        temp_y = y.Copy();

        // set to the correct size first, then add padding
        real_bitsize = (size > 0) ? size
                : ((temp_x.BitSize >= temp_y.BitSize) ? temp_x.BitSize
                        : temp_y.BitSize);
        SetSize(RoundUp(real_bitsize));

        temp_x.SetSize(BitSize);
        temp_y.SetSize(BitSize);
        one = BigInt.One(BitSize);

        // There are a whole bunch of special cases!
        if (temp_y.IsZero()) {
            // Any number raised to the 0th power is 1
            Clear();
            n[0] = 1;
            SetSize(real_bitsize);
            return;
        }
        if (temp_y.Equals(one)) {
            // any number raised to 1 is itself
            SetSize(real_bitsize);
            return;
        }
        if (temp_x.Equals(one)) {
            // any power of 1 is 1
            SetSize(real_bitsize);
            return;
        }
        if (IsZero()
                || (temp_y.SignBit() && (pBIArithMode != ArithMode.Unsigned))) {
            // zero or negative
            SetSize(real_bitsize);
            Clear();
            return;
        }

        // Just multiply x with itself that many times
        temp_y._Subtract(one);
        while (!temp_y.IsZero()) {
            Multiply(temp_x, BitSize);
            if (pOverflow) {
                over = true;
            }
            temp_y._Subtract(one);
        }
        pOverflow = (over | (MaxBit() >= real_bitsize));
        SetSize(real_bitsize);
    }

    // Square Root of x (using Newton's method)
    public static BigInt SquareRoot(BigInt x) {
        BigInt ans = x.Copy();
        ans.SquareRoot(0);
        return ans;
    }

    // Square Root of x (using Newton's method) with required size
    public static BigInt SquareRoot(BigInt x, int size) {
        BigInt ans = x.Copy();
        ans.SquareRoot(size);
        return ans;
    }

    // Square Root of the current value (using Newton's method)
    public void SquareRoot(int size) {
        BigInt temp_x, r;
        int real_bitsize;
        boolean sign;

        sign = SignBit();
        real_bitsize = size > 0 ? size : BitSize;
        SetSize(RoundUp(real_bitsize));

        // a quick sanity check
        if (IsZero() || (pBIArithMode != ArithMode.Unsigned) && sign) {
            pOverflow = true;
            Clear();
            return;
        }

        temp_x = this.Copy();
        r = new BigInt(BitSize, pBIArithMode);
        r.n[0] = 1;
        while (IsGreater(r)) {
            r = Divide(temp_x, this);
            // Using right shift instead of division by 2
            _Add(r);
            RightShift(1, false, BitSize);
        }

        // Carry on remainder (not a perfect square)
        r = Multiply(this, this);
        r._Subtract(temp_x);
        pCarryBit = !r.IsZero();

        // remove the wiggle room (if any)
        SetSize(real_bitsize);
    }

    /*
     * ************************ Bitwise Operators ****************************
     */

    // Perform a Bitwise AND operation of x and y
    public static BigInt BitwiseAnd(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.BitwiseAnd(y, 0);
        return ans;
    }

    // Perform a Bitwise AND operation of x and y with required size
    public static BigInt BitwiseAnd(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.BitwiseAnd(y, size);
        return ans;
    }

    // Perform a Bitwise AND operation
    public void BitwiseAnd(BigInt y, int size) {
        BigInt temp_y;

        // First we have to make a copy of the variables
        temp_y = y.Copy();

        // pick the bit size of the answer
        SetSize((size > 0) ? size : ((BitSize >= temp_y.BitSize) ? BitSize
                : temp_y.BitSize));

        // Resize the y (since the size may have changed)
        temp_y.SetSize(BitSize);

        for (int i = 0; i <= LastByte; i++) {
            this.n[i] = (byte) (this.n[i] & temp_y.n[i]);
        }
    }

    // Perform a Bitwise OR operation of x and y
    public static BigInt BitwiseOr(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.BitwiseOr(y, 0);
        return ans;
    }

    // Perform a Bitwise OR operation of x and y with required size
    public static BigInt BitwiseOr(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.BitwiseOr(y, size);
        return ans;
    }

    // Perform a Bitwise OR operation
    public void BitwiseOr(BigInt y, int size) {
        BigInt temp_y;

        temp_y = y.Copy();

        SetSize((size > 0) ? size : ((BitSize >= temp_y.BitSize) ? BitSize
                : temp_y.BitSize));
        temp_y.SetSize(BitSize);

        for (int i = 0; i <= LastByte; i++) {
            this.n[i] = (byte) (this.n[i] | temp_y.n[i]);
        }
    }

    // Perform a Bitwise NOT operation of x
    public static BigInt OnesComplement(BigInt x) {
        BigInt ans = x.Copy();
        ans.OnesComplement(0);
        return ans;
    }

    // Perform a Bitwise NOT operation of x with required size
    public static BigInt OnesComplement(BigInt x, int size) {
        BigInt ans = x.Copy();
        ans.OnesComplement(size);
        return ans;
    }

    // Perform a Bitwise NOT operation
    public void OnesComplement(int size) {
        if (size > 0) {
            SetSize(size);
        }

        for (int i = 0; i <= LastByte; i++) {
            this.n[i] = (byte) (255 - this.n[i]);
        }
        Mask();
    }

    // Perform a Bitwise Exclusive OR operation on x and y
    public static BigInt Xor(BigInt x, BigInt y) {
        BigInt ans = x.Copy();
        ans.Xor(y, 0);
        return ans;
    }

    // Perform a Bitwise Exclusive OR operation on x and y with required size
    public static BigInt Xor(BigInt x, BigInt y, int size) {
        BigInt ans = x.Copy();
        ans.Xor(y, size);
        return ans;
    }

    // Perform a Bitwise Exclusive OR operation
    public void Xor(BigInt y, int size) {
        BigInt temp_y;

        temp_y = y.Copy();

        SetSize((size > 0) ? size : ((BitSize >= temp_y.BitSize) ? BitSize
                : temp_y.BitSize));
        temp_y.SetSize(BitSize);

        for (int i = 0; (i <= LastByte); i++) {
            this.n[i] = (byte) (this.n[i] ^ temp_y.n[i]);
        }
    }

    // Shift the bit pattern of x to the Left
    public static BigInt LeftShift(BigInt x, int distance) {
        BigInt ans = x.Copy();
        ans.LeftShift(distance, 0);
        return ans;
    }

    // Shift the bit pattern of x to the Left with required size
    public static BigInt LeftShift(BigInt x, int distance, int size) {
        BigInt ans = x.Copy();
        ans.LeftShift(distance, size);
        return ans;
    }

    // Shift the bit pattern to the Left
    public void LeftShift(int distance, int size) {
        int temp, carry;
        BigInt temp_x;

        if (size > 0) {
            SetSize(size);
        }

        // a bit of sanity checking
        if (distance <= 0) {
            return;
        }

        // Shift left is the same as "multiply by 2"
        for (int j = 1; j <= distance; j++) {
            temp_x = this.Copy();

            // Carry is based upon the left most bit
            pCarryBit = SignBit();

            carry = 0;
            for (int i = 0; i <= LastByte; i++) {
                temp = ((int) (temp_x.n[i] & 0xff) * 2) + carry;
                this.n[i] = (byte) (temp % 256);
                carry = temp / 256;
            }
        }
        Mask();
    }

    // Shift the bit pattern of x to the Right
    public static BigInt RightShift(BigInt x, int distance) {
        BigInt ans = x.Copy();
        ans.RightShift(distance, false, 0);
        return ans;
    }

    // Shift the bit pattern of x to the Right with required size
    public static BigInt RightShift(BigInt x, int distance, int size) {
        BigInt ans = x.Copy();
        ans.RightShift(distance, false, size);
        return ans;
    }

    // Shift the bit pattern to the Right
    public void RightShift(int distance, boolean SaveSign, int size) {
        int carry;
        boolean sign_bit;
        BigInt temp_x;

        if (size > 0) {
            SetSize(size);
        }

        // a bit of sanity checking
        if (distance <= 0) {
            return;
        }

        for (int j = 1; j <= distance; j++) {
            temp_x = this.Copy();

            // Carry is based upon the right most bit
            pCarryBit = (temp_x.n[0] & 1) > 0;
            sign_bit = SignBit();

            // Shift right is the same as "divide by 2"
            carry = 0;
            for (int i = LastByte; i >= 0; i--) {
                if (i < LastByte) {
                    if ((temp_x.n[i + 1] & 1) != 0) {
                        carry = 128;
                    } else {
                        carry = 0;
                    }
                }
                this.n[i] = (byte) (((int) (temp_x.n[i] & 0xff) / 2) | carry);
            }
            if (SaveSign & (sign_bit && (pBIArithMode != ArithMode.Unsigned))) {
                // are we doing an Arithmetic Shift Right?
                BitSet((BitSize - 1));
            }
        }
    }

    // Rotate the bit pattern of x to the Right
    public static BigInt RotateRight(BigInt x, int distance, boolean WithCarry,
            boolean StartingCarry, int size) {
        BigInt ans = x.Copy();
        ans.RotateRight(distance, WithCarry, StartingCarry, size);
        return ans;
    }

    // Rotate the bit pattern to the Right
    public void RotateRight(int distance, boolean WithCarry, boolean StartingCarry, int size) {
        int carry;
        BigInt temp_x;
        // v6.0.3 - 5 May 12
        
        if (size > 0) {
            SetSize(size);
        }

        // a bit of sanity checking
        if (distance <= 0) {
            return;
        }
        for (int j = 1; j <= distance; j++) {
            temp_x = this.Copy();

            // Carry is based upon the right most bit
            pCarryBit = (temp_x.n[0] & 1) != 0;

            carry = 0;
            for (int i = LastByte; i >= 0; i--) {
                if (i < LastByte) {
                    if ((temp_x.n[i + 1] & 1) != 0) {
                        carry = 128;
                    } else {
                        carry = 0;
                    }
                }
                this.n[i] = (byte) (((int) (temp_x.n[i] & 0xff) / 2) | carry);
            }
            if (WithCarry) {
                if (StartingCarry) {
                    // add the sign bit back in
                    BitSet(BitSize - 1);
                }
            } else if ((temp_x.n[0] & 1) != 0) {
                // add the bit at position 0 back in
                BitSet(BitSize - 1);
            }
            StartingCarry = pCarryBit;
        }
    }

    // Rotate the bit pattern to the Left
    public static BigInt RotateLeft(BigInt x, int distance, boolean WithCarry,
            boolean StartingCarry, int size) {
        BigInt ans = x.Copy();
        ans.RotateLeft(distance, WithCarry, StartingCarry, size);
        return ans;
    }

    // Rotate the bit pattern to the Left
    public void RotateLeft(int distance, boolean WithCarry, boolean StartingCarry, int size) {
        int temp, carry;
        // v6.0.3 - 5 May 12
        
        if (size > 0) {
            SetSize(size);
        }

        // a bit of sanity checking
        if (distance <= 0) {
            return;
        }

        for (int j = 1; j <= distance; j++) {
            // Carry is based upon the left most bit
            pCarryBit = SignBit();

            // Rotate left is similar to "multiply by 2"
            carry = 0;
            for (int i = 0; i <= LastByte; i++) {
                temp = ((int) (this.n[i] & 0xff) * 2) + carry;
                this.n[i] = (byte) (temp % 256);
                carry = temp / 256;
            }
            if (WithCarry) {
                if (StartingCarry) {
                    // put the carry bit at position 0
                    this.BitSet(0);
                }
            } else if (pCarryBit) {
                // put the sign bit at position 0
                this.BitSet(0);
            }
            StartingCarry = pCarryBit;
        }
        Mask();
    }

    /*
     * ************************ Equality Operators ******************************
     */

    // Is the current BigInt equal to zero?
    public boolean IsZero() {
        for (int i = 0; i <= LastByte; i++) {
            if (this.n[i] != 0) {
                return false;
            }
        }
        // UNDONE: In some situations you might want 1's comp mode 0 = -0
        return true;
    }

    // Test for equality (is x = y)
    public static boolean Equals(BigInt x, BigInt y) {
        if (x == null & y == null) {
            return true;
        }

        if (x == null ^ y == null) {
            return false;
        }

        if (Compare(x, y) == 0) {
            return true;
        }
        return false;
    }

    // Test for equality
    public boolean Equals(BigInt y) {
        if (y == null) {
            return false;
        }

        if (Compare(this, y) == 0) {
            return true;
        }
        return false;
    }

    // Less Than
    public boolean IsLess(BigInt y) {
        if (Compare(this, y) == -1) {
            return true;
        }
        return false;
    }

    // Less Than or Equal
    public boolean IsLessOrEqual(BigInt y) {
        if (Compare(this, y) <= 0) {
            return true;
        }
        return false;
    }

    // Greater Than
    public boolean IsGreater(BigInt y) {
        if (Compare(this, y) == 1) {
            return true;
        }
        return false;
    }

    // Greater Than or Equal
    public boolean IsGreaterOrEqual(BigInt y) {
        if (Compare(this, y) >= 0) {
            return true;
        }
        return false;
    }

    // Compare a BigInt value with another
    public static int Compare(BigInt x, BigInt y) {
        BigInt temp_x, temp_y;

        // make copies since we may change the bit size
        temp_x = x.Copy();
        temp_y = y.Copy();

        if ((temp_x.BitSize > temp_y.BitSize)) {
            temp_y.SetSize(temp_x.BitSize);
        }
        if ((temp_y.BitSize > temp_x.BitSize)) {
            temp_x.SetSize(temp_y.BitSize);
        }

        // Comparing negative number is a bit easier, since we know that any
        // positive number is greater than any negative number
        if (x.pBIArithMode != ArithMode.Unsigned) {
            boolean sign_x = temp_x.SignBit();
            boolean sign_y = temp_y.SignBit();

            // when either is negative (but not both)
            if (sign_x ^ sign_y) {
                if (sign_y) {
                    return 1;
                }
                return -1;
            }
            // UNDONE: In some situations you might want 1's comp mode 0 = -0
        }
        for (int i = temp_x.LastByte; i >= 0; i--) {
            if ((int) (temp_x.n[i] & 0xff) > (int) (temp_y.n[i] & 0xff)) {
                // x > y
                return 1;
            }
            if ((int) (temp_x.n[i] & 0xff) < (int) (temp_y.n[i] & 0xff)) {
                // x < y
                return -1;
            }
        }
        // must be equal
        return 0;
    }

    /*
     * ************************* Import/Export functions *****************************
     */

    // Convert to an integer
    public int ToInteger() {
        BigInt x;

        x = this.Copy();
        x.SetSize(32);
        pLossOfPrecision = !Equals(x);

        // 2's complement mode rules apply!
        return GetInt(x.n);
    }

    // Convert to a Long
    public long ToLong() {
        BigInt x;

        x = this.Copy();
        x.SetSize(64);
        pLossOfPrecision = !Equals(x);

        // 2's complement mode rules apply!
        return GetLong(x.n);
    }

    // Convert to a double
    public double ToDouble() {
        // To Double (actually from long)
        return (double) this.ToLong();
    }

    // Override the default ToString() method
    public String ToString() {
        return ("&H" + ToStringHex());
    }

    // To Hexadecimal string
    public String ToStringHex() {
        int j, ending;
        StringBuilder sb = new StringBuilder();

        // How many digits are possible
        ending = (BitSize - 1) / 4;

        // Instead of using the normal method, we use the GetAt function to
        // extract the digit's value directly from the n() array.
        for (int i = 0; i <= ending; i++) {
            // Sneaky trick to prevent multiplication and division
            j = GetAt((i * 4), 4);
            if (j > 9) {
                // UNDONE: This will probably break globalization
                sb.insert(0, (char) ((int) ('a') + j - 10));
            } else {
                sb.insert(0, j);
            }
        }
        return sb.toString();
    }

    // To Binary string
    public String ToStringBin() {
        return ToStringBin(false);
    }

    // To Binary string
    public String ToStringBin(boolean pad) {
        StringBuilder sb = new StringBuilder();
        int spaces = 0;
        String ans;

        for (int i = LastByte; i >= 0; i--) {
            for (int j = 7; j >= 0; j--) {
                if (((byte) Math.pow(2, j) & n[i]) != 0) {
                    sb.append("1");
                } else {
                    sb.append("0");
                }
            }
            if ((i != 0) && (pad == true)) {
                sb.append(" ");
                spaces++;
            }
        }

        // Trim to current word length, considering the optional spaces
        if (pad) {
            ans = sb.toString().substring(sb.length() - (BitSize + spaces),
                    sb.length());
        } else {
            ans = sb.toString().substring(sb.length() - BitSize, sb.length());
        }
        return ans;
    }

    // To Octal String
    public String ToStringOct() {
        int j, ending;
        StringBuilder sb = new StringBuilder();

        // How many digits are possible
        ending = (BitSize - 1) / 3;

        // Instead of using the normal method, we use the GetAt function to
        // extract the digit's value directly from the n() array.
        for (int i = 0; i <= ending; i++) {
            // Sneaky trick to prevent multiplication and division
            j = GetAt((i * 3), 3);
            sb.insert(0, j);
        }
        return sb.toString();
    }

    // To Decimal String
    public String ToStringDec() {
        int j, ending;
        BigInt tempx, ten, z;
        boolean is_neg;
        StringBuilder sb = new StringBuilder();

        tempx = this.Copy();
        tempx.SetSize(RoundUp(BitSize));
        ten = new BigInt(tempx.BitSize, pBIArithMode);
        is_neg = false;

        switch (pBIArithMode) {
        case Unsigned:
            break;
        // Do nothing
        case OnesComplement:
            if (SignBit()) {
                is_neg = true;
                tempx.OnesComplement(tempx.BitSize);
            }
            break;
        case TwosComplement:
            if (SignBit()) {
                is_neg = true;
                tempx.OnesComplement(tempx.BitSize);
                tempx._Add(BigInt.One(tempx.BitSize));
            }
            break;
        }

        // How many decimal digits are possible
        ending = (int) (Math.floor(this.BitSize / 3.33333333)) + 1;

        // This normal stuff... To get each digit, divide the number by 10
        // and then get the remainder. Repeat.
        ten.n[0] = 10;
        for (int i = 1; i <= ending; i++) {
            // Can't use my sneaky GetAt function... dang!
            z = Remainder(tempx, ten);
            j = z.n[0];
            sb.insert(0, j);
            tempx = Divide(tempx, ten);
        }

        if (is_neg) {
            sb.insert(0, "-");
        }

        return sb.toString();
    }

    /*
     * ************************ Misc Utilities *****************************
     */

    // The number 1 as a Big Integer
    public static BigInt One(int size) {
        BigInt ans = new BigInt(size, ArithMode.TwosComplement);
        ans.n[0] = 1;
        return ans;
    }

    // Split a BigInt into two equal size words (even bit sizes only!)
    public BigInt[] Split() throws Exception {
        int half_bitsize, half_maxbyte;
        BigInt Right_Hand, Left_Hand;
        BigInt[] ans = new BigInt[2];

        // A wee bit of sanity checking
        if ((BitSize % 2) > 0) {
            throw new Exception("Bit size is not divisible by two");
        }

        half_bitsize = (int) (BitSize / 2);
        half_maxbyte = (half_bitsize - 1) / 8;
        Right_Hand = new BigInt(half_bitsize, pBIArithMode);
        Left_Hand = new BigInt(half_bitsize, pBIArithMode);

        // Right size (is easy)
        for (int i = 0; i <= half_maxbyte; i++) {
            Right_Hand.n[i] = this.n[i];
        }
        Right_Hand.Mask();

        // Left side (right shift "half_bitsize" number of times)
        BigInt temp_x = this.Copy();
        temp_x.RightShift(half_bitsize, false, BitSize);

        for (int i = 0; i <= half_maxbyte; i++) {
            Left_Hand.n[i] = temp_x.n[i];
        }
        Left_Hand.Mask();

        ans[0] = Right_Hand;
        ans[1] = Left_Hand;
        return ans;
    }

    // Create a bit mask
    public static BigInt CreateMask(int bits, boolean LeftJustify, int size) {
        BigInt ans = new BigInt(size, ArithMode.Unsigned);
        if (LeftJustify) {
            for (int i = ans.BitSize - 1; i >= (ans.BitSize - bits); i--) {
                ans.BitSet(i);
            }
        } else {
            for (int i = 0; i <= (bits - 1); i++) {
                ans.BitSet(i);
            }
        }
        return ans;
    }

    // Move the bit pattern as many spaces as needed to
    public static BigInt[] LeftJustify(BigInt x) {
        int d;
        BigInt Justified, distance;
        BigInt[] ans = new BigInt[2];

        Justified = x.Copy();
        d = x.BitSize - x.MaxBit() - 1;

        Justified.LeftShift(d, x.BitSize);
        distance = new BigInt(d, x.BitSize, x.pBIArithMode);

        ans[0] = Justified;
        ans[1] = distance;
        return ans;
    }

    // Convert x into its absolute value
    public static BigInt AbsoluteValue(BigInt x) {
        BigInt temp_x = x.Copy();
        temp_x.AbsoluteValue();
        return temp_x;
    }

    // Converts the number into its absolute value
    public void AbsoluteValue() {
        // Is there anything to do?
        if (!this.SignBit()) {
            return;
        }
        switch (pBIArithMode) {
        case Unsigned:
            break;
        case OnesComplement:
            // So, we use 2's complement methodology here, rather
            // than 1's complement. I wonder why...
            _Subtract(BigInt.One(BitSize));
            OnesComplement(BitSize);
            break;
        case TwosComplement:
            // Check for largest possible negative number in order
            // to set the Overflow flag
            BigInt sign = CreateMask(1, true, BitSize);
            if (Equals(sign)) {
                pOverflow = true;
            } else {
                _Subtract(BigInt.One(BitSize));
                OnesComplement(BitSize);
            }
            break;
        }
    }

    // Change the sign of the Big Integer
    public void ChangeSign() {
        if (SignBit()) {
            // is already negative, so go to positive
            switch (pBIArithMode) {
            case Unsigned:
                _Subtract(One(BitSize));
                OnesComplement(BitSize);
                // Not really an error, but we do set the overflow flag
                pOverflow = true;
                break;
            case OnesComplement:
                OnesComplement(BitSize);
                break;
            case TwosComplement:
                _Subtract(One(BitSize));
                OnesComplement(BitSize);
                break;
            }
        } else {
            // is already positive, so go to negative
            switch (pBIArithMode) {
            case Unsigned:
                OnesComplement(BitSize);
                _Add(One(BitSize));
                // Not really an error, but we do set the overflow flag
                pOverflow = true;
                break;
            case OnesComplement:
                OnesComplement(BitSize);
                break;
            case TwosComplement:
                OnesComplement(BitSize);
                _Add(One(BitSize));
                break;
            }
        }
    }

    // Combine two halves into one value.
    public void Combine(BigInt Left_Hand, BigInt Right_Hand) throws Exception {
        BigInt temp;
        int i;

        // A wee bit of sanity checking
        if ((Left_Hand.BitSize != Right_Hand.BitSize)) {
            throw new Exception("Bits sizes do not match");
        }

        SetSize((Left_Hand.BitSize * 2));
        Clear();

        // Right side
        for (i = 0; i <= Right_Hand.MaxByte(); i++) {
            n[i] = Right_Hand.n[i];
        }

        // Left side (shift left "left_hand.bitsize" number of times)
        temp = Left_Hand.Copy();
        temp.SetSize((Left_Hand.BitSize * 2));
        temp.LeftShift(Left_Hand.BitSize, BitSize);

        // combine with the existing
        for (i = 0; i <= MaxByte(); i++) {
            n[i] = (byte) (n[i] | temp.n[i]);
        }
    }

    // Count the number of set bits.
    public void SumBits() {
        BigInt temp;
        BigInt one = BigInt.One(BitSize);

        temp = this.Copy();
        Clear();

        for (int i = LastByte; i >= 0; i--) {
            for (int j = 7; j >= 0; j--) {
                if ((temp.n[i] & (byte) Math.pow(2, j)) != 0) {
                    _Add(one);
                }
            }
        }
    }

    // Clear the bit at the given location
    public void BitClear(int location) {
        int n_byte;
        byte n_bit;
        n_byte = location / 8;

        n_bit = (byte) ~(int) (Math.pow(2, (location % 8)));

        if (n_byte <= LastByte) {
            this.n[n_byte] = (byte) (this.n[n_byte] & n_bit);
        }
    }

    // Set the bit at the given location
    public void BitSet(int location) {
        int n_byte;
        byte n_bit;
        n_byte = location / 8;

        n_bit = (byte) (Math.pow(2, (location % 8)));

        if (n_byte <= LastByte) {
            this.n[n_byte] = (byte) (this.n[n_byte] | n_bit);
        }
    }

    // Test to see if bit at the given location is set or not
    public boolean BitTest(int location) {
        int n_byte;
        byte n_bit;
        n_byte = location / 8;

        n_bit = (byte) (Math.pow(2, (location % 8)));

        if (n_byte <= LastByte) {
            if ((this.n[n_byte] & n_bit) != 0) {
                return true;
            }
        }
        return false;
    }

    /*
     * ************************** Private Methods ****************************
     */

    // Clone the BigInt value
    private BigInt Copy() {
        BigInt ans = new BigInt();

        // start with copying field variables
        ans.BitSize = this.BitSize;
        ans.LastByte = this.LastByte;
        ans.pBIArithMode = this.pBIArithMode;
        ans.pCarryBit = this.pCarryBit;
        ans.pLossOfPrecision = this.pLossOfPrecision;
        ans.pOverflow = this.pOverflow;

        // Now we make a copy of the array
        ans.n = new byte[LastByte + 1];
        for (int i = 0; i <= LastByte; i++) {
            ans.n[i] = n[i];
        }
        return ans;
    }

    // Set/Reset the bit size
    private void SetSize(int size) {
        if (size != BitSize) {
            BigInt temp = null;
            if ((size > BitSize)
                    && (SignBit() && (pBIArithMode != ArithMode.Unsigned))) {
                temp = CreateMask((size - BitSize), true, size);
            }
            LastByte = (size - 1) / 8;
            this.n = ArrayResize(this.n, LastByte + 1);
            BitSize = size;
            Mask();

            // apply the extended sign mask
            if (temp != null) {
                for (int i = 0; i <= LastByte; i++) {
                    this.n[i] = (byte) (this.n[i] | temp.n[i]);
                }
            }
        }
    }

    // Add an extra byte for padding
    private void AddPadding(int size) {
        LastByte = (size - 1) / 8;

        this.n = ArrayResize(this.n, LastByte + 1);
        BitSize = size;
        Mask();
    }

    // Find the location of the most significant bit
    private int MaxBit() {
        for (int i = LastByte; i >= 0; i--) {
            for (int j = 7; j >= 0; j--) {
                if ((n[i] & (byte) Math.pow(2, j)) != 0) {
                    return ((i * 8) + j);
                }
            }
        }
        // Must be zero
        return 0;
    }

    // Reset the BigInt to Zero
    private void Clear() {
        for (int i = 0; i <= LastByte; i++) {
            n[i] = 0;
        }
        // Also reset the status flags
        pCarryBit = false;
        pOverflow = false;
        pLossOfPrecision = false;
    }

    // Returns the location of the most significant digit (byte) in the array
    private int MaxByte() {
        for (int i = LastByte; i >= 0; i--) {
            if (n[i] != 0) {
                return i;
            }
        }
        return 0;
    }

    // Put the integer value at the specified starting bit location within the
    // array
    private void PutAt(int i, int Location) {
        int n_bit, n_byte;
        long temp;

        n_byte = Location / 8;
        n_bit = Location % 8;

        temp = (long) (i * Math.pow(2, n_bit));

        n[n_byte] = (byte) (temp % 256);
        if (temp > 255) {
            if (n_byte < LastByte) {
                n[n_byte + 1] = (byte) (temp / 256);
            } else {
                pOverflow = true;
            }
        }
    }

    // Get an integer value from the specified location (and length) within the
    // array
    private int GetAt(int Location, int Length) {
        int ans, temp, n_bit, n_byte;

        n_byte = Location / 8;
        n_bit = (Location % 8);

        if (n_byte < LastByte) {
            temp = (int) (n[n_byte] & 0xff) + (int) (n[n_byte + 1] & 0xff)
                    * 256;
        } else {
            temp = (int) (n[n_byte] & 0xff);
        }
        ans = temp / (int) Math.pow(2, n_bit) % (int) (Math.pow(2, Length));

        return ans;
    }

    // Mask the current value to the proper number of bits
    private void Mask() {
        byte mask_value;

        // Anything to do?
        if (BitSize == (LastByte + 1) * 8) {
            return;
        }
        mask_value = (byte) ((Math.pow(2, (BitSize - 1) % 8) * 2) - 1);

        n[LastByte] = (byte) (n[LastByte] & mask_value);
    }

    // Check to see if the Sign Bit is set
    private boolean SignBit() {
        byte mask_value;
        mask_value = (byte) (Math.pow(2, (BitSize - 1) % 8));

        if ((n[LastByte] & mask_value) != 0) {
            return true;
        }
        return false;
    }

    // Round up the bitsize to the next byte and add 1 byte
    private static int RoundUp(int bit_size) {
        return (((bit_size - 1) / 8) + 2) * 8;
    }

    // import a byte array into a BigInt
    private void Import(byte[] bytes, int size, boolean negative) {
        int last;

        SetSize(size);

        last = (LastByte > bytes.length - 1) ? bytes.length - 1 : LastByte;

        for (int i = 0; i <= last; i++) {
            this.n[i] = bytes[i];
        }

        // Do we have any leftover bytes?
        pLossOfPrecision = false;
        if (last < bytes.length - 1) {
            for (int i = last + 1; i < bytes.length; i++) {
                if (bytes[i] != 0) {
                    pLossOfPrecision = true;
                    break;
                }
            }
        }

        // How about any leftover bits?
        if (!pLossOfPrecision) {
            pLossOfPrecision = (MaxBit() > BitSize);
        }

        // Fill in the missing sign extension
        if ((bytes.length - 1 < LastByte) && negative) {
            for (int i = bytes.length; i <= LastByte; i++) {
                this.n[i] = (byte) 255;
            }
        }
        Mask();
    }

    // convert a byte array into an integer
    private static int GetInt(byte[] b) {
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
                reverse(b));
        java.io.DataInputStream stream = new java.io.DataInputStream(bais);

        try {
            return stream.readInt();
        } catch (IOException e) {
            // nothing
        }
        return 0;
    }

    // convert a byte array into a long
    private static long GetLong(byte[] b) {
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(
                reverse(b));
        java.io.DataInputStream stream = new java.io.DataInputStream(bais);

        try {
            return stream.readLong();
        } catch (IOException e) {
            // nothing
        }
        return 0;
    }

    // convert an integer into a byte array
    private static byte[] GetBytes(int value) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream stream = new java.io.DataOutputStream(baos);

        try {
            stream.writeInt(value);
            stream.close();
        } catch (IOException e) {
            // nothing
        }
        return reverse(baos.toByteArray());
    }

    // convert a long into a byte array
    private static byte[] GetBytes(long value) {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream stream = new java.io.DataOutputStream(baos);
        try {
            stream.writeLong(value);
        } catch (IOException e) {
            // nothing
        }
        return reverse(baos.toByteArray());
    }

    // reverse the order of the bytes in the array
    private static byte[] reverse(byte[] arr) {
        byte[] rev = new byte[arr.length];

        for (int i = 0; i < arr.length; i++) {
            rev[arr.length - i - 1] = arr[i];
        }
        return rev;
    }

    private byte[] ArrayResize(byte[] b, int size) {
        byte[] ans = new byte[size];

        int len = Math.min(b.length, size);
        System.arraycopy(b, 0, ans, 0, len);
        return ans;
    }
}
