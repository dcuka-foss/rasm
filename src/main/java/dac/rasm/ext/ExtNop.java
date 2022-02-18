package dac.rasm.ext;

import java.util.List;

import dac.rasm.Arg;
import dac.rasm.Instruction;
import dac.rasm.InstructionCode;
import dac.rasm.Resolver;
import dac.rasm.RiscOpCode.OpCode;
import dac.rasm.Translator;

public class ExtNop extends SimpleExtension
{
	@Override
	public String getKeyword()
	{
		return "nop";
	}

	@Override
	public List<Instruction> generateInstructions()
	{
		InstructionCode code = new InstructionCode();

		code.setOpcode(OpCode.ADDI);
		code.setRd(new Arg().withRegister(Translator.ZERO));
		code.setRs1(new Arg().withRegister(Translator.ZERO));
		code.setImm(new Arg().withImmediate("0"));

		return List.of(code);
	}

	@Override
	public String getDescription()
	{
		return "Generate instructions that do nothing but that consume an instruction cycle.";
	}

}
