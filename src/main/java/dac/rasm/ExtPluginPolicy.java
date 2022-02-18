package dac.rasm;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;

/**
 * This class hides the details of the extension system for plugins to add
 * keywords.
 * 
 * @author dcuka
 *
 */
public class ExtPluginPolicy extends Policy
{

	/**
	 * Deny permissions to anything loaded externally.
	 */
	public PermissionCollection getPermissions(CodeSource codeSource)
	{
		Permissions p = new Permissions();
		if (codeSource.getLocation().toString().contains("Extension") == false)
		{
			p.add(new AllPermission());
		}
		p.add(new AllPermission());

		return p;
	}

	public void refresh()
	{
	}
}
