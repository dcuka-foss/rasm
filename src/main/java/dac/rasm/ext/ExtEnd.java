package dac.rasm.ext;

import java.util.List;

import dac.rasm.Arg;
import dac.rasm.Instruction;
import dac.rasm.InstructionCode;
import dac.rasm.Resolver;
import dac.rasm.RiscOpCode.OpCode;
import dac.rasm.Translator;

public class ExtEnd extends SimpleExtension
{
	@Override
	public String getKeyword()
	{
		return "end";
	}

	@Override
	public List<Instruction> generateInstructions()
	{
		InstructionCode code = new InstructionCode();

		code.setOpcode(OpCode.JAL);
		code.setRd(new Arg().withRegister(Translator.ZERO));
		code.setImm(new Arg().withImmediate("0"));

		return List.of(code);
	}


	@Override
	public String getDescription()
	{
		return "End a program with an infinite loop.";
	}

}
