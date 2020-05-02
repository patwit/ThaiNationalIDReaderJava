import java.io.UnsupportedEncodingException;

import javax.smartcardio.CardException;
import javax.swing.SwingUtilities;

public class KKUThaiIDReader {
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				KKUThaiIDReaderGUI reader 
					= new KKUThaiIDReaderGUI("Thai National ID Card Reader");
				reader.createAndShowGUI();
				reader.runProgram();
			}
		});
	}
}
