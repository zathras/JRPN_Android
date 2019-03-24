package com.jovial.jrpn;

// A storage register.  Really just a container class to
// hold both the double and BigInt values.
public class Register {

	private double pFVal;
	private BigInt pBiVal;

	// Default constructor
	public Register() {
		pFVal = 0;
		pBiVal = new BigInt(32, BigInt.ArithMode.TwosComplement);
	}

	// Create a new Register given the Big Integer size and Arithmetic Mode
	public Register(int size, CalcState.CalcArithMode mode) {
		pFVal = 0;
		BigInt.ArithMode bimode = BigInt.ArithMode.toArithMode(mode.index());
		pBiVal = new BigInt(size, bimode);
	}

	// The float value inside the register container
	public Double getFVal() {
		return pFVal;
	}

	public void setFVal(double d) {
		pFVal = d;
	}

	// The Big Integer value inside the Register
	public BigInt getBiVal() {
		return pBiVal;
	}

	public void setBiVal(BigInt bi) {
		pBiVal = bi;
	}

	// Make a "deep" copy of a register
	public Register Copy() {
		Register temp = new Register();
		temp.pFVal = pFVal;
		temp.pBiVal = new BigInt(pBiVal, pBiVal.getWordSize(),
				pBiVal.getBIArithMode());
		return temp;
	}
}
