package server.model;


import java.io.FileInputStream;
import java.io.IOException;

public class VideoStream {

	private FileInputStream fis;

	public VideoStream(String filename) throws Exception {
		fis = new FileInputStream("." + System.getProperty("file.separator") + "videos" + System.getProperty("file.separator") + filename);
	}

	/**
	 * Reads the next frame from the video into the frame parameter
	 * 
	 * @param frame
	 *            where the video frame is read to
	 * @return the total number of bytes read into the frame
	 * @throws IOException
	 */
	public int getnextframe(byte[] frame) throws IOException {
		int length = 0;
		String length_string;
		byte[] frame_length = new byte[5];

		// read current frame length
		fis.read(frame_length, 0, 5);

		// transform frame_length to integer
		length_string = new String(frame_length);
		length = Integer.parseInt(length_string);

		return (fis.read(frame, 0, length));
	}

}