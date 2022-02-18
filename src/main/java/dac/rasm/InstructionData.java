package dac.rasm;

/**
 * This class hides the details of representing a set of data bytes that will be loaded into
 * memory as part of the assembly process.
 * 
 * @author dcuka
 *
 */
public class InstructionData extends Instruction
{
	byte data[] = new byte[4];
	
	{
		length = 4;
	}
	
	public InstructionData withData(byte[] data)
	{
		this.data = data;
		return this;
	}
	
	public InstructionData withData(byte d1, byte d2, byte d3, byte d4)
	{
		this.data[0] = d1;
		this.data[1] = d2;
		this.data[2] = d3;
		this.data[3] = d4;
		return this;
	}
	 
	public InstructionData withData(String chunk)
	{
		byte[] x = chunk.getBytes();
		
		if (x.length > 4)
		{
			LOG.warn("Chunk length {} > 4 for '{}', extra ignored", x.length, chunk);
		}
		
		for (int i=0; i < data.length; i++)
		{
			data[i] = (i < x.length) ? x[i] : 0;
		}
		return this;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(printPc()).append(SPACE);
		for (int i=0; i < data.length; i++)
		{
			sb.append(String.format("[%02x] ", data[i]));
		}
		
		return sb.toString();
	}
	
	@Override
	public RiscInstruction assemble()
	{
		return null;
	}

	@Override
	public InstructionType getInstructionType()
	{
		return InstructionType.DATA;
	}

}
