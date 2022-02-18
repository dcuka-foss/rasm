package dac.rasm;

import java.security.SecureClassLoader;

public class ExtClassLoader extends SecureClassLoader
{

	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		return super.loadClass(name);
	}

	public Class findClass(String name)
	{
		byte[] b = loadClassData(name);
		return defineClass(name, b, 0, b.length);
	}

	private byte[] loadClassData(String name)
	{
		// load the untrusted class data here
		return null;
	}
}
