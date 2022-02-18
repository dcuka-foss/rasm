package dac.rasm;

/**
 * This class represents an instruction that could not be translated.
 * 
 * @author dcuka
 *
 */
public class InstructionError extends InstructionCode
{

	String error;

	{
		length = 0;
	}
	
	@Override
	public InstructionType getInstructionType()
	{
		return InstructionType.COMMENT;
	}

	public InstructionError withError(String error_message)
	{
		this.error = error_message;
		return this;
	}

	public String toString()
	{
		return String.format("ERROR:%s on input [%s]", error, input);
	}
}
