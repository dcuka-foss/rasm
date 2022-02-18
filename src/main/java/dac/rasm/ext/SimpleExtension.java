package dac.rasm.ext;

import java.util.List;

import dac.rasm.Instruction;
import dac.rasm.InstructionError;
import dac.rasm.Resolver;

/**
 * This is a parent class for a simple extension that does not need a parser.
 * The keyword for the extension is the only required item. No registers or
 * parameters.
 * 
 * @author dcuka
 *
 */
public abstract class SimpleExtension extends Extension
{

	@Override
	public List<Instruction> processExpr(String input, String first_word, List<String> words, Resolver resolver)
	{
		if (getKeyword().equalsIgnoreCase(first_word) == true)
		{
			return generateInstructions();
		}

		return List.of(new InstructionError().withError("Syntax error on '" + first_word + "'"));
	}

	public String getUsage()
	{
		return getKeyword().toUpperCase();
	}

	public abstract List<Instruction> generateInstructions();

}
