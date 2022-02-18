package dac.rasm;

import java.util.HashMap;
import java.util.Map;

import dac.rasm.RiscOpCode.OpCode;
import dac.rasm.RiscOpCode.OpEncoding;

/**
 * This class knows how to represent an instruction that resolves to a single line of assembly code.
 * 
 * @author dcuka
 *
 */
public class InstructionCode extends Instruction
{
	private static final String SPACE = " ";
	public OpCode opcode;

	Map<OpEncoding, Arg> args = new HashMap<>();
	
	public static final OpEncoding[] arg_order = {OpEncoding.RD, OpEncoding.R1, OpEncoding.R2, OpEncoding.IMM };
	
	
	{
		length = 4;
		for (OpEncoding encoding : arg_order)
		{
			args.put(encoding, null);
		}
	}
	
	public String toString()
	{
	
		
		if (opcode == null)
		{
			return "no opcode";
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append(printPc()).append(SPACE);
		
		sb.append(opcode);
		
		String rd = getRd() == null ? "" : getRd().value;
		String rs1 = getRs1() == null ? "" : getRs1().value;
		String rs2 = getRs2() == null ? "" : getRs2().value;
		String imm = getImm() == null ? (getPtr() == null ? null : getPtr().value) : getImm().value;
		String arg_string = "undefined optype in toString(), " + opcode.optype;

		/*
		 * The output format depends on the type of the opcode which determines the order as 
		 * well as which registers or immediate values are printed.
		 */
		switch (opcode.optype)
		{
	
		case BRANCH:
			// TODO: add sign if resolved to a number
			arg_string = String.format("%s, %s, %s", rs1, rs2, imm);
			break;
			
		case OP_IMM:
			arg_string = String.format("%s, %s, %s", rd, rs1, imm);
			break;
			
		case JAL:
			arg_string = String.format("%s, %s", rd, imm);
			break;
			
		case AUIPC:
		case LUI:
			arg_string = String.format("%s, %s", rd, imm);
			break;
			
		case NOP:
			break;
			
		case SYSTEM:
			arg_string = String.format("%s, %s", rd, imm);
			break;
			
 		case LOAD:
			arg_string = String.format("%s, %s(%s)", rd, imm, rs1);
			break;
		
		case STORE:
			arg_string = String.format("%s, %s(%s)", rs2, imm, rs1);
			break;
			
		case OP:
		default:
			arg_string = String.format("%s, %s, %s", rd, rs1, rs2);
			break;
		}
		
		sb.append(SPACE).append(arg_string);
		return sb.toString();
	}
	
	public OpCode getOpcode()
	{
		return opcode;
	}
	
	public void setOpcode(OpCode opcode)
	{
		this.opcode = opcode;
	}

	@Override
	public InstructionType getInstructionType()
	{
		return InstructionType.CODE;
	}

	public String pp()
	{
		return String.format("%-40s//%s", this.toString(), this.input);
	}

	public Arg getRd()
	{
		return args.get(OpEncoding.RD);
	}

	public void setRd(Arg rd)
	{
		args.put(OpEncoding.RD, rd);
	}

	public Arg getRs1()
	{
		return args.get(OpEncoding.R1);
	}

	public void setRs1(Arg rs1)
	{
		args.put(OpEncoding.R1, rs1);
	}

	public Arg getRs2()
	{
		return args.get(OpEncoding.R2);
	}

	public void setRs2(Arg rs2)
	{
		args.put(OpEncoding.R2, rs2);
	}

	public Arg getImm()
	{
		return args.get(OpEncoding.IMM);
	}

	public void setImm(Arg imm)
	{
		args.put(OpEncoding.IMM, imm);
	}

	public void setPtr(Arg imm)
	{
		args.put(OpEncoding.PTR, imm);
	}
	
	public Arg getPtr()
	{
		return args.get(OpEncoding.PTR);
	}

	
	/**
	 * Return the args to an instruction as an array for easier processing.
	 * @return a new array containing the current values of the args some of which may be null.
	 */
	public Map<OpEncoding, Arg> getArgs()
	{
		return args;
	}
}
