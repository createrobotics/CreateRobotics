package com.workert.robotics.base.roboscriptbytecode;

// These are similar to TokenTypes, and represent different instructions for the VM
public interface OpCode {
    byte OP_CONSTANT = 0;
    byte OP_POP = 1;
    byte OP_EQUAL = 2;
    byte OP_NOT_EQUAL = 3;
    byte OP_GREATER = 4;
    byte OP_GREATER_EQUAL = 5;
    byte OP_LESS = 6;
    byte OP_LESS_EQUAL = 7;
    byte OP_ADD = 8;
    byte OP_SUBTRACT = 9;
    byte OP_MULTIPLY = 10;
    byte OP_DIVIDE = 11;
    byte OP_NOT = 12;
    byte OP_NEGATE = 13;
    byte OP_RETURN = 14;
}
