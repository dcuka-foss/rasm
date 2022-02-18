package dac.rasm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class hides the details of a single instruction that comes from the input.  This is an abstraction
 * of instructions which could include labels, sections and other pseudo-instruction information.  This
 * holds the unassembled original human readable input provided by the user but structures and classifies
 * each line to aid in down-stream processing.
 * 
 * @author dcuka
 *
 */
public abstract class Instruction
{
	final static Logger LOG = LoggerFactory.getLogger(Instruction.class);

	public static final String SPACE = " ";
	
	String input;
	Long pc;
	int	length = 0;
	
	
	
	public enum InstructionType
	{
		COMMENT, STRING, CODE, DATA, LABEL
	}
	
	public abstract InstructionType getInstructionType();
	
	
	/**
	 * Override this for any instruction that produces bits as part of the assembly.
	 * @return
	 */
	public RiscInstruction assemble()
	{
		return null;
	}

	public String getInput()
	{
		return input;
	
	}

	public void setInput(String input)
	{
		this.input = input;
	
	}

	public Long getPc()
	{
		return pc;
	
	}

	public void setPc(Long pc)
	{
		this.pc = pc;
	
	}

	public String printPc()
	{
		return String.format("\t%08x", pc);
	}

	protected  boolean isLabel()
	{
		return getInstructionType() == InstructionType.LABEL;
	}


	public boolean isComment()
	{
		return getInstructionType() == InstructionType.COMMENT;
	}

}
