package rasm;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

import dac.rasm.Resolver;

class ResolverTest
{
	private static final String PI = "pi";
	private static final String PI_COLON = "PI:";


	@Test
	void testLabel()
	{
		Resolver r = new Resolver();
		
		r.createLabel(PI, 314L);
		
		assertEquals(r.labelEntries().size(), 1);
		
		assertEquals(r.resolveAbsoluteLabel(PI_COLON), Long.valueOf(314));
		assertEquals(r.resolveRelativeLabel(PI_COLON, 100L), Long.valueOf(214));
	}
	
	@Test
	void testGetRegisterNumber()
	{
		assertEquals(Resolver.resolveRegister("X1"), Integer.valueOf(1));
		assertEquals(Resolver.resolveRegister("X2"), Integer.valueOf(2));
		assertEquals(Resolver.resolveRegister("X3"), Integer.valueOf(3));
		assertEquals(Resolver.resolveRegister("X15"), Integer.valueOf(15));
		assertEquals(Resolver.resolveRegister("1x"), null);
	}
}
