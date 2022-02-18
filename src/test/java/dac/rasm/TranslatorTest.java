package dac.rasm;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import dac.rasm.Arg.ArgType;

class TranslatorTest
{

	Resolver resolver = new Resolver();
	Translator translator = new Translator(resolver);
	
	@Test
	void testExtendedTranslate()
	{
		var code_list = translator.translate(0, "nop", List.of(), "nop");
		InstructionCode x = (InstructionCode) code_list.get(0);
		
		assertEquals(RiscOpCode.OpCode.ADDI,  x.opcode);
		
		code_list = translator.translate(0, "end", List.of(), "end");
		x = (InstructionCode) code_list.get(0);
		
		assertEquals(RiscOpCode.OpCode.JAL,  x.opcode);
	}

	@Test
	void testParseOperand()
	{
		InstructionCode x = translator.parseOperand("addi", List.of("X1", "X2", "25"));
		
		assertEquals(RiscOpCode.OpCode.ADDI , x.opcode);
		
		assertEquals("X1", x.args.get(0).value);
		assertEquals(ArgType.REGISTER, x.args.get(0).type);
		
	}
}
