package dac.rasm.ext;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScannerTest extends Scanner
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testScan()
	{
		String jump[] = { "jump", "TEST:", "if", "X1", ">=", "X2" };

		String input = String.join(" ", jump);

		List<Token> scan = Scanner.scan(input);

		assertEquals(jump.length + 1, scan.size());
		int i = 0;

		for (String j : jump)
		{
			assertEquals(j, scan.get(i++).text);
		}

		String[] set = { "set", "var", "=", "array", "[", "7", "]" };

		input = String.join(" ", set);

		scan = Scanner.scan(input);

		assertEquals(set.length + 1, scan.size());
		i = 0;

		for (String s : set)
		{
			assertEquals(s, scan.get(i++).text);
		}
	}

}
