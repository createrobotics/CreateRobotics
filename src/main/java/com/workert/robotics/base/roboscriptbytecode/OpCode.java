package com.workert.robotics.base.roboscriptbytecode;

public interface OpCode {
    byte OP_CONSTANT = 0;
    byte OP_NULL = 1;
    byte OP_TRUE = 2;
    byte OP_FALSE = 3;
    byte OP_POP = 4;
    byte OP_GET_GLOBAL = 5;
    byte OP_DEFINE_GLOBAL = 6;
    byte OP_SET_GLOBAL = 7;
    byte OP_EQUAL = 8;
    byte OP_NOT_EQUAL = 9;
    byte OP_GREATER = 10;
    byte OP_GREATER_EQUAL = 11;
    byte OP_LESS = 12;
    byte OP_LESS_EQUAL = 13;
    byte OP_ADD = 14;
    byte OP_SUBTRACT = 15;
    byte OP_MULTIPLY = 16;
    byte OP_DIVIDE = 17;
    byte OP_NOT = 18;
    byte OP_NEGATE = 19;
    byte OP_RETURN = 20;
    byte OP_LOG = 21;
}
