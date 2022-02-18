package dac.rasm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import dac.rasm.RiscOpCode.OpCode;

class RiscInstructionTest
{
	@Test
	void testToString()
	{
		RiscInstruction code = new RiscInstruction(OpCode.BEQ, null, 1, 2, 30L);
		assertEquals(code.getByteString(), "[63] [8f] [20] [00]");
		
		code = new RiscInstruction(OpCode.ANDI, 4, 4, null, 28L);
		assertEquals(code.getByteString(), "[13] [72] [c2] [01]");

		code = new RiscInstruction(OpCode.BLT, null, 1, 2, -48L);
		assertEquals(code.getByteString(), "[e3] [c8] [20] [fc]");
	}
}
