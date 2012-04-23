package server.model;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class VideoStream {

	private FileInputStream fis;
	private String fileName;

	public VideoStream(String filename) throws RuntimeException {
		fileName = filename;
		try {
			fis = new FileInputStream(filename);
		} catch (FileNotFoundException fex) {
			throw new RuntimeException("Problem accessing source file ("+fileName+")");
		}
	}

	/**
	 * Reads the next frame from the video into the frame parameter
	 * 
	 * @param frame
	 *            where the video frame is read to
	 * @return the total number of bytes read into the frame
	 * @throws IOException
	 */
	public int getnextframe(byte[] frame){
		int length = 0;
		String length_string;
		byte[] frame_length = new byte[5];
		
		try {
			
			// read current frame length
			fis.read(frame_length, 0, 5);
	
			// transform frame_length to integer
			length_string = new String(frame_length);
			length = Integer.parseInt(length_string);
	
			return (fis.read(frame, 0, length));
		
		} catch (IOException ioex) {
			throw new RuntimeException("Problem accessing source file ("+fileName+")");
		}
	}

}