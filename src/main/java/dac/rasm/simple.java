package dac.rasm;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class simple implements Runnable
{
	final static Logger LOG = LoggerFactory.getLogger(simple.class);
	private String progname = "hw.prog";
	public static final Integer PC_WIDTH = 4;
	Mode mode = Mode.PROG;
	Map<String, String> vars = new HashMap<>();
	Map<String, String> defined_vars = new HashMap<>();
	Map<String, Integer> labels = new HashMap<>();
	List<Instruction> instructions = new ArrayList<>();
	List<RiscInstruction> assembler = new ArrayList<>();
	
	private int line_number = 0;
	public static final String KEYWORD_CODE = "code:";
	public static final String KEYWORD_DECLARE = "declare:";
	public static final String KEYWORD_PROG = "prog:";

	Resolver resolver = new Resolver();
	private Translator translator = new Translator(resolver);

	public enum Mode
	{
		PROG, DECLARE, CODE
	}

	{
		defined_vars.put("TEXTIO",  "0xb0000000");
		defined_vars.put("CONSOLE", "0xc0000000");
		defined_vars.put("BITMAP",  "0x00000C00");
		
		resolver.createLabel("TEXTIO",  0xb0000000L);
		resolver.createLabel("CONSOLE", 0xc0000000L);
		resolver.createLabel("BITMAP",  0x00000C00L);
		
		resolver.createVariable("zero", "X0");
		resolver.createVariable("sp", "X2");
	}
	
	
	@Override
	public void run()
	{
		try
		{
			int pass = 1;
			List<String> lines = Files.readAllLines(Paths.get(progname));
			long pc = 0;
			
			for (String line : lines)
			{
				line_number++;
				List<String> words = getWords(line);
				
				LOG.debug("PASS #{} -- Processing {}, pc={},  input: {}", pass, mode, pc, words);
				
				if (words.size() == 0)
				{
					continue;
				}

				Mode starting_mode = mode;
				switch (mode)
				{
				case PROG:
					processProgLine(words);
					break;

				case DECLARE:
					processDeclareLine(words);
					break;

				case CODE:

					List<Instruction> code_to_add = processCodeLine(pc, words, line);
					if (code_to_add == null || code_to_add.isEmpty() == true)
					{
						LOG.error("Failed to convert '{}', resolve this error or program will not be valid", line);
					}
					else
					{
						for (Instruction single_instruction : code_to_add)
						{
							pc = pc + single_instruction.length;
							LOG.debug("PC: {}\tINSTRUCTION: {}", pc, single_instruction);
							instructions.add(single_instruction);
						}
					}
				}

				if (mode != starting_mode)
				{
					LOG.debug("Switched to {} mode.", mode);

				}

			}


			System.out.println("\nVARIABLES:");
			for (Entry<String, String> var : resolver.varEntries())
			{
				System.out.format("\t%-10s\t%s\n", var.getKey(), var.getValue());

			}

			System.out.println("\nLABELS:");
			for (Entry<String, Long> label : resolver.labelEntries())
			{
				System.out.format("\t%-10s\t0x%08x\n", label.getKey(), label.getValue());

			}
			
			// resolve forward labels
			pass = 2;
			for (Instruction instruction : instructions)
			{
				resolver.resolve(instruction);
			}
			
			System.out.println("\nPRE-PROCESS CODE:");
			for (Instruction code : instructions)
			{
				System.out.println(String.format("%-50s// %s", code, code.getInput()));
			}
			
			pass = 3;
			Map<Instruction, RiscInstruction> pp_code = new HashMap<>();
			
			System.out.println("\nASSEMBLE:");
			for (Instruction code : instructions)
			{
				try
				{
					RiscInstruction risc = Assembler.assemble(code, resolver);
					System.out.println(risc + "\t" + code);
					LOG.debug("Assembled {} into {}", code, risc);
					if (risc != null)
					{
						assembler.add(risc);
						pp_code.put(code, risc);
					}

				}
				catch (Exception e)
				{
					LOG.error("Failed to assemble: {}", code);
					e.printStackTrace();
				}
			}
			
			System.out.println("\nPRINTOUT:");
			for (Instruction code : instructions)
			{
				RiscInstruction risc = pp_code.get(code);
				if (risc == null)
				{
					if (code.getInput() == null)
					{
						System.out.format("%-30s\n", code);
					}
					else
					{
						System.out.format("%-30s\t; %s\n", code, code.getInput());
					}
				}
				else
				{
					System.out.format("  %20s\t%-30s\t; %s\n", risc, code, code.getInput());
				}
			}
			
			pass=4;
			System.out.println("\nHEX:");
			for (RiscInstruction hex : assembler)
			{
				System.out.println(hex);
			}
			
			System.out.println("\nFILE:");
			System.out.println(HexWriter.format(assembler));
			
			String filename = "a.hex";
			if (filename != null)
			{
				LOG.info("Writing hex program to '{}'...", filename);
				HexWriter.write(assembler, filename);
			}

		}
		catch (IOException e)
		{
			e.printStackTrace();

		}

	}


	/**
	 * Process a STR: directive into a number of instructions
	 * @param pc
	 * @param line
	 * @return
	 */
	private List<Instruction> processString(long pc, String line)
	{
		String f[] = line.split(":");
		String text = f[1].trim().replaceAll("^\"", "").replaceAll("\"$", "");
		
		List<Instruction> strings = new ArrayList<>();
		int start = 0;
		int end = Math.min(4,  text.length());
		while (start < end)
		{
			InstructionData data = new InstructionData();
			String chunk = text.substring(start, end);
			data.withData(chunk);
			data.setInput(chunk);
			data.setPc(pc);
			strings.add(data);
			start = end;
			end = Math.min(end + 4, text.length());
			pc += PC_WIDTH;

		}
		return strings;
	}

	

	private void processDeclareLine(List<String> words)
	{
		String first = words.remove(0);
		switch (first.toLowerCase())
		{
		case KEYWORD_CODE:
			mode = Mode.CODE;
			break;

		case KEYWORD_PROG:
		case KEYWORD_DECLARE:
			LOG.error("Invalid directive '{}'", first);
			break;

		default:
			// process variable
			if (words.size() < 1)
			{
				break;

			}

			first = first.toUpperCase();
			String second = words.remove(0).toUpperCase();
			resolver.createVariable(first, second);
			
			vars.put(first, second);
			LOG.debug("Added variable {} as reference to register {}.", first, second);

		}

	}

	public enum ArgFormat
	{
		TRIPLE, DOUBLE, OFFSET
	}
	
	private List<Instruction> processCodeLine(long pc, List<String> words, String line)
	{
		List<Instruction> code_list = new ArrayList<>();
		String first_word = words.remove(0);
		
		switch (first_word.toLowerCase())
		{
		case KEYWORD_PROG:
		case KEYWORD_DECLARE:
		case KEYWORD_CODE:
			LOG.error("Invalid directive '{}'", first_word);
			break;

		case "string:":
				code_list.addAll(processString(pc, line));
				break;
				
		default:
			
			// a colon would indicate a label. 
			if (first_word.contains(":") == true)
			{
				code_list.add(createLabelInstruction(first_word, pc));
			}
			else if (first_word.startsWith("//") == true)
			{
				code_list.add(new InstructionComment().withComment(line));
			}
			else
			{
				code_list = translator.translate(pc, first_word, words, line);
			}
		}
		
		return code_list;
	}

	private Instruction createLabelInstruction(String first_word, Long pc)
	{
		resolver.createLabel(first_word, pc);
		Instruction label_instruction = new InstructionLabel().withLabel(first_word);
		label_instruction.setPc(pc);
		return label_instruction;
	}

 
	/**
	 * Return a list of words from an input string.
	 * @param input
	 * @return
	 */
	private List<String> getWords(String input)
	{
		List<String> words = new ArrayList<>();
		String f[] = input.split("\\s+|[,]");
		for (String w : f)
		{
			switch (w)
			{
			case ";":
				return words;
				
			default:
				if (w.isBlank() == false)
				{
					words.add(w);
					
				}
					
			}

		}

		return words;

	}
	
	
	private String getVariable(String var_name)
	{
		if (vars.containsKey(var_name) == true)
		{
			return vars.get(var_name);

		}

		LOG.error("Invalid variable '{}' at line {}.", var_name, line_number);
		return null;

	}

	private Integer getLabelPc(String label)
	{
		if (labels.containsKey(label) == true)
		{
			return labels.get(label);

		}

		if (labels.containsKey(label + ":") == true)
		{
			return labels.get(label + ":");

		}

		LOG.error("Invalid label '{}' at line {}.", label, line_number);
		return null;

	}

	private void processProgLine(List<String> words)
	{
		switch (words.get(0).toLowerCase())
		{
		case KEYWORD_PROG:
			break;

		case KEYWORD_DECLARE:
			mode = Mode.DECLARE;
			break;

		case KEYWORD_CODE:
			mode = Mode.CODE;
			break;

		default:
			LOG.error("Invalid directive '{}'", words.get(0));

		}

	}

	public static void main(String args[])
	{
//		Policy policy = Policy.getPolicy();
//
//		ExtPluginPolicy mypolicy = new ExtPluginPolicy();
//		Policy.setPolicy(mypolicy);
//		Policy.setPolicy(mypolicy);
			System.setSecurityManager(new RasmSecurityManager());

		simple s = new simple();
		int failures = 0;
		for (int i = 0; i < args.length; i++)
		{
			switch (args[i])
			{
			case "-f":
			case "--filename":
				s.setProgname(args[++i]);
				break;

			case "+x":
				String ext_name = args[++i];
				try
				{
					s.translator.addExtension(ext_name);
				}
				catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
						| SecurityException e)
				{
					LOG.error("Failed to load '{}'.", ext_name, e);
					failures++;
				}
				break;

			// add multiple extensions
			case "+ext":
				Path path = Paths.get(args[++i]);
				try
				{
					List<String> lines = Files.readAllLines(path);
					for (String ext_name_line : lines)
					{
						ext_name_line=  ext_name_line.trim();
						if (ext_name_line.isBlank() == false)
						{
							s.translator.addExtension(ext_name_line);
						}
					}
				}
				catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e)
				{
					LOG.error("Failure while reading or processing '{}'", path, e);
					failures++;
				}
				break;
				
			default:
				LOG.warn("Invalid arg '{}', ignored.", args[i]);
				break;

			}

		}

		if (failures > 0)
		{
			LOG.error("Aborting due to {} command line errors.", failures);
		}
		else
		{
			s.run();
		}

	}

	public enum FormatType
	{
		TRIPLE, INDEXED, COMMENT, STRING, DOUBLE
	}

	

	public String getProgname()
	{
		return progname;

	}

	public void setProgname(String progname)
	{
		this.progname = progname;

	}
	
	
}

