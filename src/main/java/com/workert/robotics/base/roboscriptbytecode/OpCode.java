package com.workert.robotics.base.roboscriptbytecode;

public interface OpCode {
    byte OP_CONSTANT = 0;
    byte OP_NULL = 1;
    byte OP_TRUE = 2;
    byte OP_FALSE = 3;
    byte OP_POP = 4;
    byte OP_GET_LOCAL = 5;
    byte OP_SET_LOCAL = 6;
    byte OP_GET_GLOBAL = 7;
    byte OP_DEFINE_GLOBAL = 8;
    byte OP_SET_GLOBAL = 9;
    byte OP_EQUAL = 10;
    byte OP_NOT_EQUAL = 11;
    byte OP_GREATER = 12;
    byte OP_GREATER_EQUAL = 13;
    byte OP_LESS = 14;
    byte OP_LESS_EQUAL = 15;
    byte OP_ADD = 16;
    byte OP_SUBTRACT = 17;
    byte OP_MULTIPLY = 18;
    byte OP_DIVIDE = 19;
    byte OP_NOT = 20;
    byte OP_NEGATE = 21;
    byte OP_JUMP = 22;
    byte OP_JUMP_IF_FALSE = 23;
    byte OP_LOOP = 24;
    byte OP_RETURN = 25;
    byte OP_LOG = 26;
}
