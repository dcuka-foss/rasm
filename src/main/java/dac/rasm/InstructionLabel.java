package dac.rasm;

/**
 * This class hides the details of representing a label provided in the input.  Eventually this
 * will get translated to an address based on the PC during assembly.
 * 
 * @author dcuka
 *
 */
public class InstructionLabel extends Instruction
{
	String label;

	public InstructionLabel withLabel(String label)
	{
		this.label = label;
		return this;
	}

	public String toString()
	{
		return "\n" + label;
	}

	@Override
	public InstructionType getInstructionType()
	{
		return InstructionType.LABEL;
	}
}
