package com.workert.robotics.base.roboscript.ingame;
public interface ConsoleOutputProvider {

	String getConsoleOutput();

	RunningState getRunningState();

	enum RunningState {
		RUNNING,
		STOPPED,
		ENERGY_REQUIREMENT_NOT_MET
	}
}
