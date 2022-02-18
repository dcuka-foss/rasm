package dac.rasm.ext;

import java.util.List;

import dac.rasm.Instruction;
import dac.rasm.Resolver;

public abstract class RegexExtension extends Extension
{

	@Override
	public List<Instruction> processExpr(String input, String first_word, List<String> words, Resolver resolver)
	{
		List<Token> scan = Scanner.scan(input);
		
		// TODO Auto-generated method stub
		return null;
	}



}
