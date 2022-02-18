package dac.rasm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dac.rasm.RiscOpCode.OpCode;
import dac.rasm.RiscOpCode.OpType;


/** 
 * This class knows how to represent a single assembly instruction at the bit level.  All
 * information is passed to the constructor.
 * 
 * @author dcuka
 *
 */
public class RiscInstruction
{
	final static Logger LOG = LoggerFactory.getLogger(RiscInstruction.class);

	OpCode opcode;
	Long instruction = 0L;
	

	public RiscInstruction(OpCode opcode, Integer rd, Integer rs1, Integer rs2, Long imm)
	{
		LOG.debug("Encoding: {}, RD={}, RS1={}, RS2={}, IMM={}", opcode.name(), rd, rs1, rs2, imm);
		encode(opcode, rd, rs1, rs2, imm);
	}
	
	private void encode(OpCode opcode, Integer rd, Integer rs1, Integer rs2, Long imm)
	{
		this.opcode = opcode;
		setBits(0, 6, opcode.optype.op);
		
		switch (opcode.optype.format)
		{
		case R:
			setBits(25, 31, opcode.f7);
			setBits(20, 24, rs2);
			setBits(15, 19, rs1);
			setBits(12, 14, opcode.f3);
			setBits(7, 11, rd);
			break;

		case I:
			if (opcode.optype == OpType.SYSTEM)
			{
				setBits(12, 14, opcode.f3);
				setBits(25, 31, opcode.f7);
				setBits(20, 24, 0b00010);
			}
			else
			{
				setBits(7, 11, rd);
				setBits(12, 14, opcode.f3);
				setBits(15, 19, rs1);
				if (opcode.f7 == null)
				{
//					LOG.warn("Immediate instruction '{}' has no f7", opcode);
					setBits(20, 31, getBits(0, 11, imm));
				}
				else
				{
					if (opcode.f7 == 0)
					{
						setBits(25, 31, getBits(5, 11, imm));
					}
					else
					{
						setBits(25, 31, getBits(5, 11, imm) / opcode.f7);
					}
					setBits(20, 24, getBits(0, 4, imm));
				}
			}
			break;

		case S:
			setBits(25, 31, getBits(5, 11, imm));
			setBits(20, 24, rs2);
			setBits(15, 19, rs1);
			setBits(12, 14, opcode.f3);
			setBits(7, 11, getBits(0, 4, imm));
			break;

		case B:
			setBits(31, 31, getBits(12, 12, imm));
			setBits(25, 30,  getBits(5, 10, imm));
			setBits(20, 24, rs2);
			setBits(15, 19, rs1);
			setBits(12, 14, opcode.f3);
			setBits(8, 11, getBits(1, 4, imm));
			setBits(7, 7, getBits(11, 11, imm));
			// TODO: figure out why this mapping works but does not match spec for B format
//			setBits(7, 11, getBits(1, 4, imm) << 1);
			break;

			
		case U:
			setBits(12, 31, getBits(12, 31, imm));
			setBits(7, 11, rd);
			break;

		case J:
			// imm[20|10:1|11|19:12]
			setBits(31, 31, getBits(20, 20, imm));
			setBits(25, 30, getBits(5, 10, imm));

			setBits(21, 24, getBits(1, 4, imm));
			setBits(20, 20,  getBits(11, 11, imm));
			
			setBits(15, 19, getBits(15, 19, imm));
			setBits(12, 14, getBits(12, 14, imm));
			setBits(7, 11, rd);
			break;

		case X:
			
		default:
			LOG.warn("No RiscInstruction code for this type {}", opcode.optype.format);
			break;

		}
		// for debugging
//		System.err.println(this.toString());
	}
	
	
	public RiscInstruction(OpCode opcode, List<Integer> regs, Long imm)
	{
		this.opcode = opcode;
		// order matters
		Integer rd = null;
		Integer rs1 = null;
		Integer rs2 = null;
		
		switch (opcode.optype.format)
		{
		case B:
			rs1 = (regs.size() > 0) ? regs.remove(0) : null;
			rs2 = (regs.size() > 0) ? regs.remove(0) : null;
			rd = (regs.size() > 0) ? regs.remove(0) : null;
			break;
			
		case I:
		case J:
		case R:
		case S:
		case U:
		case X:
		default:
			rd = (regs.size() > 0) ? regs.remove(0) : null;
			rs1 = (regs.size() > 0) ? regs.remove(0) : null;
			rs2 = (regs.size() > 0) ? regs.remove(0) : null;
			break;
		
		}
		
		encode(opcode, rd, rs1, rs2, imm);

	}
	
	public RiscInstruction(byte[] data)
	{
		setBytes(data);
	}


	public String toString()
	{
		byte[] x = getBytes();

		String hex = String.format("[%02x] [%02x] [%02x] [%02x]",  x[3], x[2], x[1], x[0]);
		
		return String.format("%s",  hex);
	}
	
	public String toBits()
	{
		byte[] x = getBytes();
		return String.format("[%8s] [%8s] [%8s] [%8s]",  Integer.toBinaryString(x[0]), Integer.toBinaryString(x[1]), Integer.toBinaryString(x[2]), Integer.toBinaryString(x[3]));
		
	}
	
	public Long getBits(int startBit, int endBit, Long imm)
	{
		/*
		 * Shift the high order bits off the edge and then shift back
		 */
		long x = (imm << endBit);
		x = x >>> endBit;
		/*
		 * Now shift right to put the start bit into the first bit.
		 */
		x = x >>> startBit;
		return x;
	}
	
	/** 
	 * Take the value and set those bits in the instruction to those in the value.
	 * @param startBit
	 * @param endBit
	 * @param value
	 */
	public void setBits(int startBit, int endBit, long value)
	{
		// create a mask for the bits to keep from value
		long mask = getMask(endBit - startBit + 1);
		
		instruction = instruction | ((mask & value) << startBit);
	}
	
	public void setBytes(byte[] data)
	{
		if (data.length < 4)
		{
			LOG.error("Only found {} bytes instead of 4 for RiscInstruction data.", data.length);
			return;
		}
		
		instruction = 0L;
		for (int i = 3; i >= 0; i--)
		{
			instruction = instruction << 8;
			long x = data[i] & 0x000000ff; 
			instruction += x;
		}
	}
	
	public byte[] getBytes()
	{
		byte opBytes[] = new byte[4];
		
		long x = instruction;
		for (int i = 3; i >= 0; i--)
		{
			opBytes[i] = (byte) (x &0x000000ff);
			x = x >> 8;
		}
		
		return opBytes;
	}

	public String getByteString()
	{
		byte x[] = getBytes();
		return String.format("[%02x] [%02x] [%02x] [%02x]",  x[3], x[2], x[1], x[0]);

	}

	/**
	 * Create a mask of the specified length.
	 * @param length
	 * @return
	 */
	public Long getMask(int length)
	{
		long mask = 1L;
		for (int i=1; i < length; i++)
		{
			mask = mask << 1;
			mask = mask | 1;
		}
		
//		LOG.debug("Created mask {} for {} bits", Long.toBinaryString(mask), length);
		return mask;
	}
	
}