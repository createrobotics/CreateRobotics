package com.workert.robotics.base.roboscriptbytecode;

// These are similar to TokenTypes, and represent different instructions for the VM
public interface OpCode {
    byte OP_CONSTANT = 0;
    byte OP_EQUAL = 1;
    byte OP_NOT_EQUAL = 2;
    byte OP_GREATER = 3;
    byte OP_GREATER_EQUAL = 4;
    byte OP_LESS = 5;
    byte OP_LESS_EQUAL = 6;
    byte OP_ADD = 7;
    byte OP_SUBTRACT = 8;
    byte OP_MULTIPLY = 9;
    byte OP_DIVIDE = 10;
    byte OP_NOT = 11;
    byte OP_NEGATE = 12;
    byte OP_RETURN = 13;
}
