package dac.rasm;

public class RasmSecurityManager extends SecurityManager
{
	public void checkWrite(String s)
	{
		if (allowFromThisClass("dac.rasm.HexWriter") == true)
		{
			System.out.println("++ Write of " + s + " allowed.");
		}
		else
		{
			System.out.println("-- Write of " + s + " NOT allowed.");
			throw new SecurityException("Cannot write to files from extensions.");
		}
		
	}
	
	public void checkRead(String s)
	{
		System.out.println("#### Allowing read of " + s);
	}
	
	/** Allows an operation only if called from the specified class
	 * 
	 * @param classname
	 * @return
	 */
	protected boolean allowFromThisClass(String classname)
	{
		Class<?>[] c = getClassContext();
		for (int i = 0; i < c.length; i++)
		{
			String name = c[i].getName();
			System.out.println("##### check allow on " + name + " =?= " + classname);
			if (name.equals(classname))
			{
				return true;
			}
		}
		return false;
	}
	
	protected boolean allowSenstiveOperations()
	{
		Class<?>[] c = getClassContext();
		for (int i = 0; i < c.length; i++)
		{
			String name = c[i].getName();
			System.out.println("##### check allow on " + name);
			if (name.contains("dac.rasm.Translator"))
			{
				return false;
			}
		}
		
		return true;
	}
}
