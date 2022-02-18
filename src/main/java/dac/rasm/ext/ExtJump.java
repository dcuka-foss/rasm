package dac.rasm.ext;

import java.util.List;
import java.util.regex.Pattern;

import dac.rasm.Arg;
import dac.rasm.Instruction;
import dac.rasm.InstructionCode;
import dac.rasm.InstructionError;
import dac.rasm.Resolver;
import dac.rasm.RiscOpCode.OpCode;
import dac.rasm.ext.Parser.TokenType;

public class ExtJump extends RegexExtension
{

	
	public static final String RE_JUMP_VAR = String.format("jump (%s) if (%s) (%s) (%s)", RE_LABEL, RE_VAR, RE_COND, RE_VAR);
	public static final String RE_JUMP_IMM = String.format("jump (%s) if (%s) (%s) (%s)", RE_LABEL, RE_VAR, RE_COND, RE_IMM);
	List<String> patterns = List.of(RE_JUMP_VAR, RE_JUMP_IMM);
	Pattern pattern = Pattern.compile(String.join("|", patterns), Pattern.CASE_INSENSITIVE);
	
	List<TokenType> grammar = List.of(TokenType.WORD, TokenType.WORD, TokenType.WORD, TokenType.WORD, TokenType.COMPARE, TokenType.WORD);
	
	Token x = new Token("jump");
	
	Grammar jump = new Grammar("jump").T("jump").F("label").T("if").F("rs1").F("cond").F("rs2");

	Grammar g_var = new Grammar("g_var").T("jump").F("label").T("if").F("rs1").O("cond").F("rs2");
	Grammar g_imm = new Grammar("g_imm").T("jump").F("label").T("if").F("rs1").O("cond").I("rs2");
	@Override
	public String getKeyword()
	{
		return "jump";
	}	

	
	
	/**
	 * jump LABEL if R1 COND R2
	 * 
	 * @param args
	 * @return
	 */

	@Override
	public List<Instruction> processExpr(String input, String first_word, List<String> words, Resolver resolver)
	{
		
		input = input.strip();
		LOG.debug("Grammar check of {} versus {}", input, grammar);
		Parse parse = parse(input, grammar);
		
		if (parse.isFailed() == true)
		{		
			return List.of(new InstructionError().withError(parse.getError().toString()));
		}
		
		List<Token> tokens = parse.tokens;
		
		String label=tokens.get(1).text;
		String r1=tokens.get(3).text;
		String cond=tokens.get(4).text;
		String r2=tokens.get(5).text;
		

		InstructionCode code = new InstructionCode();
		String error = "";

		try
		{
			/*
			 * BEQ BNE BLT BGE BLTU BGEU
			 */
			switch (cond)
			{
			case "==":
			case "=":
				code.opcode = OpCode.BEQ;
				break;

			case "!=":
			case "<>":
				code.opcode = OpCode.BNE;
				break;

			case "<":
				code.opcode = OpCode.BLT;
				break;

			case ">=":
			case "=>":
				code.opcode = OpCode.BGE;
				break;

			case ">":
			default:
				LOG.error("invalid condition '{}' in jump", cond);
				error += String.format("\nInvalid condition '%s'", cond);
			}

			code.setRs1(new Arg().withRegister(resolver.resolveVariable(r1)));
			code.setRs2(new Arg().withRegister(resolver.resolveVariable(r2)));
			code.setPtr(new Arg().withLabel(label));
		}
		catch (Exception e)
		{
			return List.of(new InstructionError().withError("Invalid Jump: " + words + error));
		}

		return List.of(code);
	}


}
