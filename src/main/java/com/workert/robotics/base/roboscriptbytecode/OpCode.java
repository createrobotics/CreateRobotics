package com.workert.robotics.base.roboscriptbytecode;

// These are similar to TokenTypes, and represent different instructions for the VM
interface OpCode {
	byte OP_CONSTANT = 0;
	byte OP_ADD = 1;
	byte OP_SUBTRACT = 2;
	byte OP_MULTIPLY = 3;
	byte OP_DIVIDE = 4;
	byte OP_NEGATE = 5;
	byte OP_RETURN = 6;
}
