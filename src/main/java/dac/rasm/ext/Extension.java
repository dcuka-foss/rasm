package dac.rasm.ext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dac.rasm.Instruction;
import dac.rasm.Resolver;
import dac.rasm.ext.Parser.TokenType;

/*
 * This class represents an extension which adds a single keyword to be processed.  The keyword is used to register the extension and when invoked
 * the caller will provide the input line in both raw and semi-processed states along with the resolver for variables and labels.  The result
 * is a list of instructions for the operation.
 */
public abstract class Extension
{
	final static Logger LOG = LoggerFactory.getLogger(Extension.class);

	// for convenience if needed
	public static final String RE_LABEL = "[A-Z][A-Z0-9_]*:";
	public static final String RE_VAR = "[A-Z][A-Z0-9_]*";
	public static final String RE_COND = "==|!=|>=|<=|>|<";
	public static final String RE_OP = "[+-]";
	public static final String RE_IMM = "-?[0-9]+";


	public abstract List<Instruction> processExpr(String input, String first_word, List<String> words,
			Resolver resolver);

	public abstract String getKeyword();

	public abstract String getDescription();

	public abstract String getUsage();


	protected void writeTest()
	{

		String content = "write test for " + this.getClass().getName();
		Path path = Paths.get("write-test.txt");
		try
		{
			Files.writeString(path, content, StandardCharsets.UTF_8);
		}
		catch (IOException e)
		{
			LOG.error("{} threw IOException {}", content, e.getMessage());
			e.printStackTrace();
		}
	}
}
