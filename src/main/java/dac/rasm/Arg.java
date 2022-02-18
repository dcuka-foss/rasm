package dac.rasm;

/**
 * This class knows how to represent a single argument for an opcode as a register, variable, label, or immediate and 
 * this also knows if the arg has been resolved or not. 
 * @author dcuka
 *
 */
public class Arg
{
	String value;
	ArgType type;
	boolean resolved = false;

	/**
	 * The type of an argument for an instruction.
	 * @author dcuka
	 *
	 */
	public enum ArgType
	{
		REGISTER, LABEL, VARIABLE, IMMEDIATE;
	}

	
	public String toString()
	{
		return String.format("%s (%s)", value, type);
	}
	
	public Arg withVariable(String variable_name)
	{
		this.type = ArgType.VARIABLE;
		this.value = variable_name;
		return this;
		
	}
	
	public Arg withRegister(String register_name)
	{
		this.type = ArgType.REGISTER;
		this.value = register_name;
		return this;
	}
	
	public Arg withLabel(String label_name)
	{
		this.type = ArgType.LABEL;
		this.value = label_name;
		return this;
	}
	
	
	public Arg withImmediate(String immediate)
	{
		this.type = ArgType.IMMEDIATE;
		this.value = immediate;
		resolved = true;
		return this;
	}
	
}

