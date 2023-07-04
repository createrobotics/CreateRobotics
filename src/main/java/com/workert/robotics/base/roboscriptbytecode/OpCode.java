package com.workert.robotics.base.roboscriptbytecode;

// These are similar to TokenTypes, and represent different instructions for the VM
public interface OpCode {
    byte OP_CONSTANT = 0;
    byte OP_NEGATE = 1;
    byte OP_RETURN = 2;
}
