package dac.rasm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dac.rasm.Arg.ArgType;
import dac.rasm.RiscOpCode.OpEncoding;

/**
 * This class knows how to resolve data into values suitable for Instructions.
 * This can include converting from one form to another.
 * 
 * @author dcuka
 *
 */
public class Resolver
{
	final static Logger LOG = LoggerFactory.getLogger(Resolver.class);

	final static String ADDR_FORMAT = "0x%08x";

	Map<String, String> vars = new HashMap<>();
//	Map<String, String> defined_vars = new HashMap<>();
	Map<String, Long> labels = new HashMap<>();

	public void createVariable(String variable, String value)
	{
		String var_name = variable.toLowerCase();
		if (vars.containsKey(var_name) == true)
		{
			LOG.error("Variable '{}' already defined, with value '{}'", var_name, vars.get(var_name));
			return;
		}

		LOG.debug("Added variable {} as reference to register {}.", var_name, value);
		vars.put(var_name, value);
	}

	public String resolveVariable(String variable)
	{
		String var_name = variable.toLowerCase();
		if (vars.containsKey(var_name) == false)
		{
//			LOG.warn("Variable '{}' not defined", var_name);
			return var_name;
		}

		return vars.get(var_name);
	}


	public static Integer resolveRegister(String register)
	{
		Integer register_number = null;
		String r = register.replaceAll("^[xX]", "");

		try
		{
			register_number = Integer.valueOf(r);
		}
		catch (NumberFormatException nfe)
		{
			LOG.error("Not a register number '{}'", register);
			return null;
		}

		return register_number;
	}

	public void createLabel(String label, Long pc)
	{
		String label_name = label.toUpperCase();
//		if (label_name.endsWith(":") == false)
//		{
//			label_name = label_name + ":";
//		}

		// strip off :
		label_name = label_name.replace(":", "");
		
		if (labels.containsKey(label_name) == true)
		{
			LOG.error("Label '{}' already defined, with value '{}'", label_name,
					String.format(ADDR_FORMAT, labels.get(label_name)));

		}

		LOG.debug("Added label {} as reference to memory {}.", label_name, String.format(ADDR_FORMAT, pc));
		labels.put(label_name, pc);
	}

	public Long resolveAbsoluteLabel(String label)
	{
		String label_name = label.toUpperCase();
		label_name = label_name.replace(":", "");

		if (labels.containsKey(label_name) == false)
		{
			try
			{
				return Long.decode(label);
			}
			catch (Exception e)
			{
				LOG.warn("Value {} is not a label  and not a number.", label_name);
				return null;
			}
		}

		return labels.get(label_name);
	}

	public Long resolveRelativeLabel(String label, Long pc)
	{
		String label_name = label.toUpperCase();
		label_name = label_name.replace(":", "");

		long jump_distance = 0L;
		if (labels.containsKey(label_name) == true)
		{
			Long label_pc = labels.get(label_name);
			jump_distance = label_pc - pc;
		}
		else
		{
			try
			{
				jump_distance = Long.decode(label);
			}
			catch (Exception e)
			{
				LOG.error("Value {} is not a label or long.", label);
				throw new RuntimeException("stop here");
			}
		}

		LOG.debug("Resolved label {} to relative jump {}", label_name, jump_distance);
		return jump_distance;
//		new_value = String.format("%+d", jump_distance);

	}

	public Set<Entry<String, String>> varEntries()
	{
		return vars.entrySet();
	}

	public Set<Entry<String, Long>> labelEntries()
	{
		return labels.entrySet();
	}

	public Long resolveImmediate(String value)
	{
		if (isLabel(value) == true)
		{
			
		}
		
		return Long.valueOf(value);
	}

	
	/**
	 * During translation we do not have forward references so after the first pass, go back and find 
	 * intructions that need labels resolved and well, resolve them.
	 * 
	 * @param instruction
	 */
	public void resolve(Instruction instruction)
	{
		if (instruction instanceof InstructionCode == true)
		{
			InstructionCode code = (InstructionCode) instruction;
			LOG.error("dont forget to resolve labels -- figure this out");
			
			/*
			 * Look at each arg and its target encoding to figure out 
			 * which are absolute and which are relative.
			 */
			for (Entry<OpEncoding, Arg> arg_entry : code.args.entrySet())
			{
				Arg arg = arg_entry.getValue();
				if (arg != null && arg.type == ArgType.LABEL)
				{
					String new_value = "unresolved";
					OpEncoding encoding = arg_entry.getKey();
					switch (encoding)
					{
					case ADDR:
					case IMM:
						new_value = resolveAbsoluteLabel(arg.value).toString();
						break;

					case PTR:
						Long relative_distance = resolveRelativeLabel(arg.value.toString(), code.getPc());
						new_value = ((relative_distance>=0) ? "+" : "") + relative_distance;
						break;

					case R1:
					case R2:
					case RD:
					case STRING:
					default:
						LOG.error("Unhandled arg type, encoding {} with label {}", encoding, arg);
						break;
					
					}
					LOG.debug("resolving encoding {} for label {} to {}", encoding, arg, new_value);
					arg.value = new_value;
				}
				
			}
			
		}
		else if (instruction instanceof InstructionData == true)
		{
			LOG.error("figure this out");
		}
		else
		{
			LOG.debug("Nothing to resolve for instruction: {}", instruction);
		}
		return;
	}

	public boolean isLabel(String label)
	{
		String label_name = label.toUpperCase();
		return labels.containsKey(label_name);
	}

	public boolean isImmediate(String string)
	{
		try
		{
			Long.decode(string);

			return true;
		}
		catch (Exception e)
		{
			return false;
		}

	}
}
