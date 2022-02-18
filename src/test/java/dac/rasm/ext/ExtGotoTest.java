package dac.rasm.ext;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtGotoTest
{

	@BeforeEach
	void setUp() throws Exception
	{
	}

	@Test
	void testParse()
	{
		ExtGoto e = new ExtGoto();
		List<Token> scan = Scanner.scan("goto 123");
		Parse p = e.parse(scan, e.g);

		System.out.println(p);

		assertFalse(p.valid);

		scan = Scanner.scan("goto exit:");
		p = e.parse(scan, e.g);
		
		System.out.println(p);

		assertTrue(p.valid);

		;

	}

}
