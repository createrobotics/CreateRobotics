package com.workert.robotics.base.roboscriptbytecode;

// These are similar to TokenTypes, and represent different instructions for the VM
public interface OpCode {
    byte OP_CONSTANT = 0;
    byte OP_NULL = 1;
    byte OP_TRUE = 2;
    byte OP_FALSE = 3;
    byte OP_POP = 4;
    byte OP_EQUAL = 5;
    byte OP_NOT_EQUAL = 6;
    byte OP_GREATER = 7;
    byte OP_GREATER_EQUAL = 8;
    byte OP_LESS = 9;
    byte OP_LESS_EQUAL = 10;
    byte OP_ADD = 11;
    byte OP_SUBTRACT = 12;
    byte OP_MULTIPLY = 13;
    byte OP_DIVIDE = 14;
    byte OP_NOT = 15;
    byte OP_NEGATE = 16;
    byte OP_RETURN = 17;
}
