public class RTPpacket {

	private static int HEADER_SIZE = 12; // size of the RTP header:

	// Fields that compose the RTP header
	private int Version;
	private int Padding;
	private int Extension;
	private int CC;
	private int Marker;
	private int PayloadType;
	private int SequenceNumber;
	private int TimeStamp;
	private int Ssrc;
	
	private byte[] header;		// Bitstream of the RTP header

	private int payload_size;	// size of the RTP payload
	private byte[] payload;		// Bitstream of the RTP payload

	/**
	 * Construct from header fields and payload bitstream
	 * @param PType
	 * @param Framenb
	 * @param Time
	 * @param data
	 * @param data_length
	 */
	public RTPpacket(int PType, int Framenb, int Time, byte[] data,
			int data_length) {
		// fill by default header fields:
		Version = 2;
		Padding = 0;
		Extension = 0;
		CC = 0;
		Marker = 0;
		Ssrc = 0;

		// fill changing header fields:
		SequenceNumber = Framenb;
		TimeStamp = Time;
		PayloadType = PType;

		// build the header bistream:
		header = new byte[HEADER_SIZE];
		// fill the header array of byte with RTP header fields
		header[0] = (byte) (0 | Version << 6 | Padding << 5 | Extension << 4 | CC);
		header[1] = (byte) (0 | Marker << 7 | PayloadType);
		header[2] = (byte) (SequenceNumber >> 8);
		header[3] = (byte) (SequenceNumber & 0xFF);
		header[4] = (byte) (TimeStamp >> 24);
		header[5] = (byte) ((TimeStamp >> 16) & 0xFF);
		header[6] = (byte) ((TimeStamp >> 8) & 0xFF);
		header[7] = (byte) (TimeStamp  & 0xFF);
		header[8] = (byte) (Ssrc >> 24);
		header[9] = (byte) ((Ssrc >> 16) & 0xFF);
		header[10] = (byte) ((Ssrc >> 8) & 0xFF);
		header[11] = (byte) (Ssrc  & 0xFF);

		// fill the payload bitstream
		payload_size = data_length;
		payload = new byte[data_length];
		// fill payload array of byte from data (given in parameter of the constructor)
		for (int i = 0; i < payload_size; i++) {
			payload[i] = data[i];
		}

	}

	/**
	 * Construct from the packet bitstream
	 * @param packet
	 * @param packet_size
	 */
	public RTPpacket(byte[] packet, int packet_size) {
		// fill default fields:
		Version = 2;
		Padding = 0;
		Extension = 0;
		CC = 0;
		Marker = 0;
		Ssrc = 0;

		// check if total packet size is lower than the header size
		if (packet_size >= HEADER_SIZE) {
			// get the header bitsream:
			header = new byte[HEADER_SIZE];
			for (int i = 0; i < HEADER_SIZE; i++) {
				header[i] = packet[i];
			}

			// get the payload bitstream:
			payload_size = packet_size - HEADER_SIZE;
			payload = new byte[payload_size];
			for (int i = HEADER_SIZE; i < packet_size; i++) {
				payload[i - HEADER_SIZE] = packet[i];
			}

			// interpret the changing fields of the header:
			PayloadType = header[1] & 127;
			SequenceNumber = unsigned_int(header[3]) + 256
					* unsigned_int(header[2]);
			TimeStamp = unsigned_int(header[7]) + 256 * unsigned_int(header[6])
					+ 65536 * unsigned_int(header[5]) + 16777216
					* unsigned_int(header[4]);
		}
	}

	/**
	 * Fills data with the payload bistream of the RTPpacket and returns the size of the payload
	 * @param data 
	 * @return the size of the RTPpacket payload
	 */
	public int getPayload(byte[] data) {

		for (int i = 0; i < payload_size; i++)
			data[i] = payload[i];

		return (payload_size);
	}

	/**
	 * Returns the length of the payload
	 * @return the length of the payload
	 */
	public int getPayloadLength() {
		return payload_size;
	}

	/**
	 * Returns the total length of the RTP packet
	 * @return the total length of the RTP packet
	 */
	public int getLength() {
		return payload_size + HEADER_SIZE;
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
		for (int i = 0; i < payload_size; i++) {
			packet[i + HEADER_SIZE] = payload[i];
		}

		// return total size of the packet
		return (payload_size + HEADER_SIZE);
	}

	public int getTimeStamp() {
		return TimeStamp;
	}

	public int getSequenceNumber() {
		return SequenceNumber;
	}

	public int getPayloadType() {
		return PayloadType;
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
	 * @param nb
	 * @return the unsigned value of 8-bit integer nb
	 */
	public static int unsigned_int(int nb) {
		if (nb >= 0) {
			return (nb);
		} else {
			return (256 + nb);
		}
	}

}