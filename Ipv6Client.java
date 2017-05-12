import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.xml.bind.DatatypeConverter;

/**
 * @author Michael Acosta
 *
 */
public class Ipv6Client {

	public static void main(String[] args) {
		try {
			Socket mySocket = new Socket("codebank.xyz", 38004);
			for (int i = 2; i <= 4096; i *= 2) {
				String s = "";
				s += "0110"; // Version
				s += "0000000000000000000000000000"; // Traffic Class and Flow Label
				s += String.format("%16s",
						Integer.toBinaryString(i)).replace(" ", "0"); // Payload Length
				s += "00010001"; // Next Header
				s += "00010100"; // Hop Limit
				// SourceAddr is all zeros
				byte[] destAddr = InetAddress.getByName("codebank.xyz").getAddress(); // DestinationAddr
				
				byte[] packet = new byte[40 + i]; // Data segment is allocated with all 0s
				
				// Convert binary string to an array of bytes
				int j = 0, k = 0;
				while (j + 8 <= s.length()) {
					packet[k] = (byte)Integer.parseInt(s.substring(j, j + 8), 2);
					j += 8;
					k++;
				}
				
				// Copy destination address array into packet array
				for (int x = 0; x < destAddr.length; x++) {
					packet[36 + x] = destAddr[x];
				}
				
				// Place the 16 bits of 1 into the IPv4 mapped addresses
				for (int y = 0; y < 2; y++) {
					packet[18 + y] = (byte)255;
					packet[34 + y] = (byte)255;
				}
				
				// Write the byte array to the server
				DataOutputStream output = new DataOutputStream(mySocket.getOutputStream());
				output.write(packet);
				
				// Print out the response from the server
				DataInputStream input = new DataInputStream(mySocket.getInputStream());
				byte[] magicNumber = new byte[4];
				input.read(magicNumber, 0, magicNumber.length);
				System.out.println("data length: " + i);
				System.out.println("Response: " +
						DatatypeConverter.printHexBinary(magicNumber) + '\n');
			}
			mySocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}