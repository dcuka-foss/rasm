package dac.rasm;

/**
 * This class hides the details of representing a comment whether provided as part of the input
 * or generated internally for traceability.
 * 
 * @author dcuka
 *
 */
public class InstructionComment extends Instruction
{
	public static final String COMMENT = "//";
	
	String comment;
	
	public InstructionComment withComment(String comment)
	{
		this.comment = comment;
		return this;
	}
	
	public String toString()
	{
		return COMMENT + " " + comment;
	}

	@Override
	public InstructionType getInstructionType()
	{
		return InstructionType.COMMENT;
	}
}
