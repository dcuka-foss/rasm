package dac.rasm;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class knows how to write a set of Risc instructions out to a .hex file
 * suitable as input into the simulator.
 * 
 * @author dcuka
 *
 */
public class HexWriter
{
	final static Logger LOG = LoggerFactory.getLogger(HexWriter.class);


	public static final String SOL = ":";
	public static final String EOL = "\n";
	public static final String EOF = "00000001FF";

	public static final int bytesPerLine = 16;

	StringWriter writer = null;


	/**
	 * Return a string version of the hex file suitable for writing to a file.
	 * 
	 * @param program
	 * @param writer
	 * @return
	 * @throws IOException
	 */
	public static String format(List<RiscInstruction> program) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		long addr = 0L;

		// track number of bytes per line
		int count = 0;
		byte buffer[] = new byte[bytesPerLine];

		for (RiscInstruction risc : program)
		{
			byte[] data = risc.getBytes();
			for (int i = 0; i < data.length; i++)
			{
				buffer[count++] = data[data.length - i  - 1];
			}

			// check if buffer full and trigger line output
			if (count >= bytesPerLine)
			{
				sb.append(formatData(count, addr, buffer)).append(EOL);
				//reset
				count = 0;
				buffer = new byte[bytesPerLine];
				addr += bytesPerLine;
			}

		}

		if (count > 0)
		{
			sb.append(formatData(bytesPerLine, addr, buffer)).append(EOL);
		}
		sb.append(SOL).append(EOF).append(EOL);

		return sb.toString();
	}


	protected static String formatData(int count, long addr, byte[] buffer)
	{
//        let checksum = count + (lineAddr >> 8) + (lineAddr & 0xFF);

		long checksum = count + (addr >> 8) + (addr & 0xFF);
		
		StringBuilder sb = new StringBuilder();
		LOG.debug("Formatting line count={}, addr={}, data={}, checksum={}", count, addr, buffer, checksum);
		
		sb.append(SOL);
		sb.append(String.format("%02x", 0xFF & count));
		sb.append(String.format("%04x", 0xFFFF & addr));
		sb.append("00");
		for (int i = 0; i < buffer.length; i++)
		{
			sb.append(String.format("%02x", 0xFF & buffer[i]));
			checksum += Byte.toUnsignedInt(buffer[i]);
		}
		int ck = slice(-checksum, 7, 0);
		
		sb.append(String.format("%02x", 0xFF & ck));

		LOG.debug("OUTPUT: {}", sb.toString());
		return sb.toString();
	}


	private static int slice(long checksum, int left, int right)
	{
		int pos = 0;
		int word = (int) checksum;
	    final int sl = 31 - left;
	    // remove the bits to the left
	    word <<= sl;
	    
	    // remove bits to the right
	    word >>>= right + sl;
	    
	    return word << pos;

	}

	
	public static void write(List<RiscInstruction> program, String filename) throws IOException
	{
		String content = format(program);
		Path path = Paths.get(filename);
		Files.writeString(path, content, StandardCharsets.UTF_8);
	}


}
