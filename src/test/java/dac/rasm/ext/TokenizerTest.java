package dac.rasm.ext;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

class TokenizerTest
{

	@Test
	void testTokenize()
	{
		
		List<Token> t3 = Tokenizer.simpleTokenize("X_POS >= 1230");
		System.err.println(t3);
		
		assertEquals(3, t3.size());

		List<Token> t1 = Tokenizer.simpleTokenize("goto label");
		System.err.println(t1);
		
		assertEquals(2, t1.size());
		
		List<Token> t2 = Tokenizer.simpleTokenize("set x=x+1");
		System.err.println(t2);
		
		assertEquals(6, t2.size());
	}

}
