package dac.rasm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dac.rasm.simple.FormatType;

/**
 * This class hides the details of the specific op-codes and their encoding requirements.
 * @author dcuka
 *
 */
public class RiscOpCode
{

	/**
	 * Auxiliary map that stores the opcodes as strings which is easily searched
	 * to confirm a string is valid before mapping.
	 */
	public static Map<String, OpCode> validCodes = new HashMap<>();
	
	static {
		for (OpCode op : OpCode.values())
		{
			validCodes.put(op.name(), op);
//			System.err.println("added valid code " + op.name());
		}
	}
	
	public static boolean isOpCode(String op_code_string)
	{
		return RiscOpCode.validCodes.containsKey(op_code_string.toUpperCase());
	}
	
	public enum OpCodeFormat
	{
		I, U, S, R, B, J, X;
	}
	
	/*
	Name 	opcode 	Format
	LOAD 	0000011 	I
	OP-IMM 	0010011 	I
	AUIPC 	0010111 	U
	STORE 	0100011 	S
	OP 	0110011 	R
	LUI 	0110111 	U
	BRANCH 	1100011 	B
	JALR 	1100111 	I
	JAL 	1101111 	J
	SYSTEM 	1110011 	I
	*/
	public enum OpType
	{
		LOAD(0B0000011, OpCodeFormat.I), OP_IMM(0B0010011, OpCodeFormat.I), AUIPC(0B0010111, OpCodeFormat.U),
		STORE(0B0100011, OpCodeFormat.S), OP(0B0110011, OpCodeFormat.R), LUI(0B0110111, OpCodeFormat.U),
		BRANCH(0B1100011, OpCodeFormat.B), JALR(0B1100111, OpCodeFormat.I), JAL(0B1101111, OpCodeFormat.J),
		SYSTEM(0B1110011, OpCodeFormat.I), NOP(0, OpCodeFormat.X);

		Byte op;
		OpCodeFormat format;
		
		OpType(int op, OpCodeFormat format)
		{
			this.op = (byte) op;
			this.format = format;
		}
	}
	
	/*
	 * RD, R1, R2 are all registers.
	 * IMM resolves to a number
	 * PTR resolves to a number relative to the PC
	 * ADDR i think might be a PTR
	 * STRING is just string.
	 */
	public enum OpEncoding
	{
		RD, R1, R2, IMM, PTR, ADDR, STRING
	}
	

	
	/*
	 * Instruction 	opcode 	funct3 	funct7 	rs2
	 * LUI 	LUI 	— 	— 	—
AUIPC	AUIPC 	— 	— 	—
JAL 	JAL 	— 	— 	—
JALR 	JALR 	000 	— 	—
BEQ 	BRANCH 	000 	— 	—
BNE 	BRANCH 	001 	— 	—
BLT 	BRANCH 	100 	— 	—
BGE 	BRANCH 	101 	— 	—
BLTU 	BRANCH 	110 	— 	—
BGEU 	BRANCH 	111 	— 	—
LB 	LOAD 	000 	— 	—
LH 	LOAD 	001 	— 	—
LW 	LOAD 	010 	— 	—
LBU 	LOAD 	100 	— 	—
LHU 	LOAD 	101 	— 	—
SB 	STORE 	000 	— 	—
SH 	STORE 	001 	— 	—
SW 	STORE 	010 	— 	—
ADDI 	OP-IMM 	000 	— 	—
SLLI 	OP-IMM 	001 	0000000 	—
SLTI 	OP-IMM 	010 	— 	—
SLTIU	OP-IMM 	011 	— 	—
XORI 	OP-IMM 	100 	— 	—
SRLI 	OP-IMM 	101 	0000000 	—
SRAI 	OP-IMM 	101 	0100000 	—
ORI 	OP-IMM 	110 	— 	—
ANDI 	OP-IMM 	111 	— 	—
ADD 	OP 	000 	0000000 	—
SUB 	OP 	000 	0100000 	—
SLL 	OP 	001 	0000000 	—
SLT 	OP 	010 	0000000 	—
SLTU 	OP 	011 	0000000 	—
XOR 	OP 	100 	0000000 	—
SRL 	OP 	101 	0000000 	—
SRA 	OP 	101 	0100000 	—
OR 	OP 	110 	0000000 	—
AND 	OP 	111 	0000000 	—
MRET 	SYSTEM 	000 	0011000 	00010
	 */

	
	public enum OpCode
	{
		LUI(OpType.LUI, 		null, null, 		List.of(OpEncoding.RD, OpEncoding.IMM)),
		AUIPC(OpType.AUIPC, 	null, null,  	List.of(OpEncoding.RD, OpEncoding.IMM)),
		JAL(OpType.JAL, 		null, null,			List.of(OpEncoding.RD, OpEncoding.PTR)),
		JALR(OpType.JALR, 		0b000, null, 		List.of(OpEncoding.RD, OpEncoding.ADDR)),
		BEQ(OpType.BRANCH, 		0b000, null, 		List.of(OpEncoding.R1, OpEncoding.R2, OpEncoding.PTR)),
		BNE(OpType.BRANCH, 		0b001, null,		List.of(OpEncoding.R1, OpEncoding.R2, OpEncoding.PTR)),
		BLT(OpType.BRANCH, 		0b100, null,		List.of(OpEncoding.R1, OpEncoding.R2, OpEncoding.PTR)),
		BGE(OpType.BRANCH, 		0b101, null,		List.of(OpEncoding.R1, OpEncoding.R2, OpEncoding.PTR)),
		BLTU(OpType.BRANCH, 	0b110, null,		List.of(OpEncoding.R1, OpEncoding.R2, OpEncoding.PTR)),
		BGEU(OpType.BRANCH,		0b111, null,		List.of(OpEncoding.R1, OpEncoding.R2, OpEncoding.PTR)),
		LB(OpType.LOAD, 		0b000, null,		List.of(OpEncoding.RD, OpEncoding.ADDR)),
		LH(OpType.LOAD, 		0b001, null,		List.of(OpEncoding.RD, OpEncoding.ADDR)),
		LW (OpType.LOAD, 		0b010, null,		List.of(OpEncoding.RD, OpEncoding.ADDR)),
		LBU(OpType.LOAD, 		0b100, null,		List.of(OpEncoding.RD, OpEncoding.ADDR)),
		LHU(OpType.LOAD, 		0b101, null,		List.of(OpEncoding.RD, OpEncoding.ADDR)),
		SB(OpType.STORE, 		0b000, null,		List.of(OpEncoding.R2, OpEncoding.ADDR)),
		SH(OpType.STORE, 		0b001, null,		List.of(OpEncoding.R2, OpEncoding.ADDR)),
		SW(OpType.STORE, 		0b010, null,		List.of(OpEncoding.R2, OpEncoding.ADDR)),
		ADDI(OpType.OP_IMM,		0b000, null,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		SLLI(OpType.OP_IMM, 	0b001, 	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		SLTI(OpType.OP_IMM, 	0b010, null,			List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		SLTIU(OpType.OP_IMM, 	0b011, null,			List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		XORI(OpType.OP_IMM, 	0b100, null,			List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		SRLI(OpType.OP_IMM, 	0b101,	0b0000000 ,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		SRAI(OpType.OP_IMM, 	0b101,	0b0100000 ,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		ORI(OpType.OP_IMM, 		0b110, null,			List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		ANDI(OpType.OP_IMM, 	0b111, null,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.IMM)),
		ADD(OpType.OP,		 	0b000,	0b0000000 	,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		SUB (OpType.OP, 		0b000,	0b0100000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		SLL (OpType.OP, 		0b001,	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		SLT (OpType.OP, 		0b010,	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		SLTU (OpType.OP, 		0b011,	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		XOR (OpType.OP, 		0b100,	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		SRL (OpType.OP, 		0b101,	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		SRA (OpType.OP, 		0b101,	0b0100000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		OR (OpType.OP, 			0b110, 	0b0000000,			List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		AND (OpType.OP, 		0b111,	0b0000000,		List.of(OpEncoding.RD, OpEncoding.R1, OpEncoding.R2)),
		MRET (OpType.SYSTEM, 	0b000,	0b0011000, 	List.of()),  // 	rs2 00010
		COMMENT(OpType.NOP, 	null, null, 			List.of(OpEncoding.STRING)),
		ERROR(OpType.NOP, 		null, null,				List.of(OpEncoding.STRING)),
		LABEL(OpType.NOP, 		null, null, 				List.of(OpEncoding.STRING)); 
		
		
		public OpType optype;
		public Integer f3;
		public Integer f7;
		public List<OpEncoding> encoding;
		public FormatType format;
		
		
		OpCode(OpType optype, Integer f3, Integer f7, List<OpEncoding> opEncoding)
		{
			this.optype = optype;
			this.f3 = f3;
			this.f7 = f7;
			this.encoding = opEncoding;
			switch (encoding.size())
			{
			case 0:
			case 1:
				format = FormatType.STRING;
				break;
				
			case 2:
				format = FormatType.DOUBLE;
				break;
				
			case 3:
				format = FormatType.TRIPLE;
				break;
				
			}

		}

		public String toString()
		{
			return this.name().toLowerCase();
		}
	}
	
}
