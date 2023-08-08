package com.workert.robotics.base.roboscript;
public class ComputerSignal {
	final Object[] args;
	final String name;

	ComputerSignal(String name, Object[] args) {
		this.args = args;
		this.name = name;
	}
}
