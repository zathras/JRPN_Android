package com.emmetgray.wrpn;

// The main calculator stack
public class CStack {
    private Register[] reg = new Register[4];

    // The default constructor
    public CStack() {
        for (int i = 0; i <= 3; i++) {
            reg[i] = new Register();
        }
    }

    // Build a new stack with the given Big Integer size
    public CStack(int size, CalcState.CalcArithMode mode) {
        for (int i = 0; i <= 3; i++) {
            reg[i] = new Register(size, mode);
        }
    }

    // Peek at the value at the "bottom" of the stack
    public Register Peek() {
        return reg[0];
    }

    // Get a Register value off the stack
    public Register Pop() {
        Register prev;

        prev = reg[0];
        // drop the stack
        // Note: the top of the stack is preserved
        for (int i = 0; i <= 2; i++) {
            reg[i] = reg[i + 1];
        }
        // Make "deep copy" so 2 and 3 aren't pointing to the same
        // register (since register is a reference type).
        reg[3] = reg[3].Copy();
        return prev;
    }

    // Push a value onto the stack
    public void Push(Register val) {
        // push a value on the stack
        // Note: the top of the stack is lost
        for (int i = 2; i >= 0; i--) {
            reg[i + 1] = reg[i];
        }
        reg[0] = val;
    }

    // The X register
    public Register getX() {
        return reg[0];
    }

    public void setX(Register x) {
        reg[0] = x;
    }

    // The Y register
    public Register getY() {
        return reg[1];
    }

    public void setY(Register y) {
        reg[1] = y;
    }

    // The Z register
    public Register getZ() {
        return reg[2];
    }

    public void setZ(Register z) {
        reg[2] = z;
    }

    // The T register
    public Register getT() {
        return reg[3];
    }

    public void setT(Register t) {
        reg[3] = t;
    }

    // Copy the stack to an ordinary array
    public Register[] ToArray() {
        Register[] ans = new Register[4];
        for (int i = 0; i < 4; i++) {
            ans[3 - i] = reg[i];
        }
        return ans;
    }
}
