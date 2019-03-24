package com.jovial.jrpn;

// A "Return Packet" that contains all of the information
// required by the GUI
public class DisplayPacket {

	private String pDisplayText;
	private String pAlternateText;
	private boolean pF_Annunciator;
	private boolean pG_Annunciator;
	private boolean pCarry_Annunciator;
	private boolean pOverflow_Annunciator;
	private boolean pPrgm_Annunciator;
	private int pDelay;
	private boolean pMenuNeedsUpdating;
	private boolean pBeep;
	private StartType pStart;

	// Which type of program are we starting
	public enum StartType {
		None, RunProgram, RunLine
	}

	// Default constructor
	public DisplayPacket() {
		pDisplayText = "";
		pAlternateText = "";
		pF_Annunciator = false;
		pG_Annunciator = false;
		pCarry_Annunciator = false;
		pOverflow_Annunciator = false;
		pPrgm_Annunciator = false;
		pDelay = 0;
		pMenuNeedsUpdating = false;
		pBeep = false;
		pStart = StartType.None;
	}

	// The main text for the display
	public String getDisplayText() {
		return pDisplayText;
	}

	public void setDisplayText(String dt) {
		pDisplayText = dt;
	}

	// The alternate text for the display (typically an error message or pause)
	public String getAlternateText() {
		return pAlternateText;
	}

	public void setAlternateText(String at) {
		pAlternateText = at;
	}

	// Should the F Annunciator be "lit"
	public boolean isF_Annunciator() {
		return pF_Annunciator;
	}

	public void setF_Annunciator(boolean f) {
		pF_Annunciator = f;
	}

	// Should the G Annunciator be "lit"
	public boolean isG_Annunciator() {
		return pG_Annunciator;
	}

	public void setG_Annunciator(boolean g) {
		pG_Annunciator = g;
	}

	// Should the Carry Annunciator be "lit"
	public boolean isCarry_Annunciator() {
		return pCarry_Annunciator;
	}

	public void setCarry_Annunciator(boolean c) {
		pCarry_Annunciator = c;
	}

	// Should the Overflow Annunciator be "lit"
	public boolean isOverflow_Annunciator() {
		return pOverflow_Annunciator;
	}

	public void setOverflow_Annunciator(boolean o) {
		pOverflow_Annunciator = o;
	}

	// Should the Program Annunciator be "lit"
	public boolean isPrgm_Annunciator() {
		return pPrgm_Annunciator;
	}

	public void setPrgm_Annunciator(boolean p) {
		pPrgm_Annunciator = p;
	}

	// The delay (in milliseconds) for the paused display
	public int getDelay() {
		return pDelay;
	}

	public void setDelay(int d) {
		pDelay = d;
	}

	// Did the calculator engine indicate that the menu needs to be updated?
	public boolean isMenuNeedsUpdating() {
		return pMenuNeedsUpdating;
	}

	public void setMenuNeedsUpdating(boolean mnu) {
		pMenuNeedsUpdating = mnu;
	}

	// Did the calculator engine ask for a beep?
	public boolean isBeep() {
		return pBeep;
	}

	public void setBeep(boolean b) {
		pBeep = b;
	}

	// Did the calculator engine indicate that a program should start?
	public StartType getStart() {
		return pStart;
	}

	public void setStart(StartType st) {
		pStart = st;
	}
}
