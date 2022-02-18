package dac.rasm;

import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dac.rasm.RiscOpCode.OpEncoding;

/**
 * This class knows how to assemble a single Instruction (abstract form) into a RiscInstruction (bits and bytes).
 * 
 * @author dcuka
 *
 */
public class Assembler
{
	final static Logger LOG = LoggerFactory.getLogger(Assembler.class);

	public static RiscInstruction assemble(Instruction instruction, Resolver resolver)
	{
		RiscInstruction risc = null;
		switch (instruction.getInstructionType())
		{
		case CODE:
			InstructionCode code = (InstructionCode) instruction;
			Integer rd = null;
			Integer rs1 = null; 
			Integer rs2 = null;
			Long imm = null;

			for (Entry<OpEncoding, Arg> arg_entry : code.getArgs().entrySet())
			{
				Arg arg = arg_entry.getValue();
				if (arg == null)
				{
					continue;
				}
				switch (arg_entry.getKey())
				{
				case ADDR:
					imm = resolver.resolveAbsoluteLabel(arg.value);
					break;

				case IMM:
					imm = resolver.resolveAbsoluteLabel(arg.value);
					break;

				case PTR:
					imm = resolver.resolveRelativeLabel(arg.value, code.pc);
					break;

				case RD:
					rd = resolver.resolveRegister(arg.value);
					break;
					
				case R1:
					rs1 = resolver.resolveRegister(arg.value);
					break;
					
				case R2:
					rs2 = resolver.resolveRegister(arg.value);
					break;

				case STRING:
				default:
					LOG.error("cannot handle encoding {} for instruction {}", arg_entry, instruction);
					break;
				}

			}
			risc = new RiscInstruction(code.opcode, rd, rs1, rs2, imm);
			break;

		case DATA:
			InstructionData data_instruction = (InstructionData) instruction;
			risc = new RiscInstruction(data_instruction.data);
			break;

		case COMMENT:
		case LABEL:
		case STRING:
			return null;

		}

		return risc;

	}
}
