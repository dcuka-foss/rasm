package dac.rasm;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dac.rasm.RiscOpCode.OpCode;
import dac.rasm.RiscOpCode.OpEncoding;
import dac.rasm.ext.ExtEnd;
import dac.rasm.ext.ExtGoto;
import dac.rasm.ext.ExtJump;
import dac.rasm.ext.ExtNop;
import dac.rasm.ext.ExtSet;
import dac.rasm.ext.Extension;

/**
 * This class knows how to translate text and arguments into an Instruction.
 * 
 * @author dcuka
 *
 */
public class Translator
{
	final static Logger LOG = LoggerFactory.getLogger(Translator.class);

	public static final String ZERO = "X0";
	Resolver resolver = null;

	private Map<String, Extension> extensions = new HashMap<>();
	
	{
		// TODO: write a separate restricted class loader for extensions
//		ExtClassLoader extclassloader = new ExtClassLoader();
		
		addExtension(new ExtNop());
		addExtension(new ExtEnd());
		addExtension(new ExtJump());
		addExtension(new ExtSet());
		addExtension(new ExtGoto());
	}
	
	public Translator(Resolver resolver)
	{
		this.resolver = resolver;
	}

	private void addExtension(Extension ext)
	{
		extensions.put(ext.getKeyword(), ext);
	}
	
	/**
	 * Add an extension by class name.  Make sure the class is in the classpath.
	 * @param classname
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void addExtension(String classname) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		ClassLoader loader = this.getClass().getClassLoader();
		Class ext_class = loader.loadClass(classname);
		Extension ext = (Extension) ext_class.getDeclaredConstructor().newInstance();
		addExtension(ext);
		LOG.info("Added keyword extension: {}", ext.getKeyword());
	}

	public List<Instruction> translate(long pc, String first_word, List<String> args, String line)
	{
		List<Instruction> code_list = new ArrayList<>();
		InstructionCode code = new InstructionCode();
		/**
		 * Check for extended instructions first.
		 */
		if (RiscOpCode.isOpCode(first_word) == true)
		{
			code = parseOperand(first_word, args);
			code_list.add(code);
		}
		else if (extensions.containsKey(first_word.toLowerCase()) == true)
		{
			try
			{
				LOG.debug("Parsing through extension: {}", first_word);
				code_list = extensions.get(first_word.toLowerCase()).processExpr(line, first_word, args, resolver);
			}
			catch (SecurityException se)
			{
				LOG.error("security violation in extension for '{}': {}", first_word, se.getMessage());
				code = new InstructionError().withError("Security error: " + first_word);
				code_list.add(code);
			}
		}
		else
		{
			code = new InstructionError().withError("Syntax error: " + first_word);
			code_list.add(code);
		}

		for (var x : code_list)
		{
			if (x.getInput() == null || x.getInput().isEmpty())
			{
				x.setInput(line);
			}
			x.setPc(pc);
			pc += x.length;
		}
		LOG.debug("++ Instruction: for input '{} {}'\n\t{}", first_word, args, code.pp());
		return code_list;
	}



	/**
	 * Parse the operand and its arguments into a code instruction. Use the
	 * OpEncoding to determine what should be set.
	 * 
	 * @param string
	 * @param words
	 * @return
	 */
	protected InstructionCode parseOperand(String opcode_string, List<String> args)
	{
		if (RiscOpCode.validCodes.containsKey(opcode_string.toUpperCase()) == false)
		{
			return new InstructionError().withError("Invalid opcode: '" + opcode_string + "'");
		}
		OpCode opcode = OpCode.valueOf(opcode_string.toUpperCase());
		if (args.size() != opcode.encoding.size())
		{
			LOG.error("arg count mismatch for {}: require {} but have {}", opcode_string, opcode.encoding, args);
			return new InstructionError().withError(
					String.format("arg count mismatch for %s, expecting %s but got [%s]", opcode_string, 
							opcode.encoding, args));
		}

		InstructionCode code = new InstructionCode();
		code.setOpcode(opcode);

		for (int i = 0; i < args.size(); i++)
		{
			String arg = args.get(i);
			OpEncoding encoding = opcode.encoding.get(i);

			switch (encoding)
			{
			case RD:
				// register could be variable or register name
				code.setRd(new Arg().withRegister(resolver.resolveVariable(arg)));
				break;
				
			case R1:
				code.setRs1(new Arg().withRegister(resolver.resolveVariable(arg)));
				break;
				
			case R2:
				code.setRs2(new Arg().withRegister(resolver.resolveVariable(arg)));
				break;
				
			case PTR:
				// could be signed number or label
				code.setPtr(new Arg().withLabel(arg));
				// TODO: Change Value to store the encoding instead of new ones.
				break;
				
			case ADDR:
				// 12(x2)
				String g[] = arg.split("[\\[\\(\\)\\]]");
				code.setImm(new Arg().withImmediate(g[1]));
				code.setRs1(new Arg().withRegister(resolver.resolveVariable(g[0])));
				break;

			case IMM:
				code.setImm(new Arg().withImmediate(arg));
				// absolute signed number or label (not relative to PC)

				break;

			case STRING:
				// not sure if needed
				// no action
				LOG.error("Should not be processsing string in Translator {} {}", opcode_string, args);
				break;

			}
			LOG.debug("\tdecode {}: arg #{} {} into {}", opcode_string, i, arg, code.args.get(i));
		}
		return code;

	}

}
