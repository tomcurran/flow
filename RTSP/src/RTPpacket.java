package shared;

public class RTPpacket {

	public static int MJPEG_TYPE = 26;		// payload type for MJPEG video
	private static int HEADER_SIZE = 12;	// size of the header

	// Fields that compose the header
	private int headerVersion;
	private int headerPadding;
	private int headerExtension;
	private int headerCC;
	private int headerMarker;
	private int headerPayloadType;
	private int headerSequenceNumber;
	private int headerTimeStamp;
	private int headerSsrc;

	private byte[] header;		// Bitstream of the header
	private byte[] payload;		// Bitstream of the payload
	private int payloadSize;	// size of the payload

	/**
	 * shared with both public constructors
	 */
	private RTPpacket() {
		// fill default fields
		headerVersion = 2;
		headerPadding = 0;
		headerExtension = 0;
		headerCC = 0;
		headerMarker = 0;
		headerSsrc = 0;
	}

	/**
	 * Construct from header fields and payload bitstream
	 */
	public RTPpacket(int payloadType, int frameNumber, int timeStamp, byte[] data, int dataLength) {
		this();

		// fill changing header fields
		headerSequenceNumber = frameNumber;
		headerTimeStamp = timeStamp;
		headerPayloadType = payloadType;

		// build the header bistream
		header = new byte[HEADER_SIZE];
		header[0]  = (byte) (0 | headerVersion << 6 | headerPadding << 5 | headerExtension << 4 | headerCC);
		header[1]  = (byte) (0 | headerMarker << 7 | headerPayloadType);
		header[2]  = (byte)  (headerSequenceNumber >> 8);
		header[3]  = (byte)  (headerSequenceNumber & 0xFF);
		header[4]  = (byte)  (headerTimeStamp >> 24);
		header[5]  = (byte) ((headerTimeStamp >> 16) & 0xFF);
		header[6]  = (byte) ((headerTimeStamp >> 8)  & 0xFF);
		header[7]  = (byte)  (headerTimeStamp        & 0xFF);
		header[8]  = (byte)  (headerSsrc >> 24);
		header[9]  = (byte) ((headerSsrc >> 16) & 0xFF);
		header[10] = (byte) ((headerSsrc >> 8)  & 0xFF);
		header[11] = (byte)  (headerSsrc        & 0xFF);

		// fill the payload bitstream
		payloadSize = dataLength;
		payload = new byte[dataLength];
		// fill payload array of byte from data
		for (int i = 0; i < payloadSize; i++) {
			payload[i] = data[i];
		}
	}

	/**
	 * Construct from the packet bitstream
	 */
	public RTPpacket(byte[] packet, int packetSize) {
		this();

		// check if total packet size is lower than the header size
		if (packetSize >= HEADER_SIZE) {
			// get the header bitsream
			header = new byte[HEADER_SIZE];
			for (int i = 0; i < HEADER_SIZE; i++) {
				header[i] = packet[i];
			}

			// get the payload bitstream
			payloadSize = packetSize - HEADER_SIZE;
			payload = new byte[payloadSize];
			for (int i = HEADER_SIZE; i < packetSize; i++) {
				payload[i - HEADER_SIZE] = packet[i];
			}

			// interpret the changing fields of the header
			headerPayloadType = header[1] & 127;
			headerSequenceNumber = unsignedInt(header[3]) + 256 * unsignedInt(header[2]);
			headerTimeStamp = unsignedInt(header[7]) + 256 * unsignedInt(header[6]) + 65536 * unsignedInt(header[5]) + 16777216 * unsignedInt(header[4]);
		}
	}

	/**
	 * Fills data with the payload bistream of the RTPpacket and returns the size of the payload
	 * @param data 
	 * @return the size of the RTPpacket payload
	 */
	public int getPayload(byte[] data) {
		for (int i = 0; i < payloadSize; i++) {
			data[i] = payload[i];
		}
		return payloadSize;
	}

	/**
	 * Returns the length of the payload
	 * @return the length of the payload
	 */
	public int getPayloadLength() {
		return payloadSize;
	}

	/**
	 * Returns the total length of the RTP packet
	 * @return the total length of the RTP packet
	 */
	public int getLength() {
		return payloadSize + HEADER_SIZE;
	}

	/**
	 * Fills packet with header and payload data and returns packet size
	 * @param packet
	 * @return the total size of the packet (header && payload)
	 */
	public int getPacket(byte[] packet) {
		// construct the packet = header + payload
		for (int i = 0; i < HEADER_SIZE; i++) {
			packet[i] = header[i];
		}
		for (int i = 0; i < payloadSize; i++) {
			packet[i + HEADER_SIZE] = payload[i];
		}
		// return total size of the packet
		return payloadSize + HEADER_SIZE;
	}

	public int getTimeStamp() {
		return headerTimeStamp;
	}

	public int getSequenceNumber() {
		return headerSequenceNumber;
	}

	public int getPayloadType() {
		return headerPayloadType;
	}

	/**
	 * Print headers without the SSRC
	 */
	public void printheader() {
		for (int i = 0; i < (HEADER_SIZE - 4); i++) {
			for (int j = 7; j >= 0; j--) {
				if (((1 << j) & header[i]) != 0) {
					System.out.print("1");
				} else {
					System.out.print("0");
				}
			}
			System.out.print(" ");
		}
		System.out.println();
	}

	/**
	 * Returns the unsigned value of 8-bit integer nb
	 * @param i
	 * @return the unsigned value of 8-bit integer nb
	 */
	public static int unsignedInt(int i) {
		return i >= 0 ? i : 256 + i;
	}

}