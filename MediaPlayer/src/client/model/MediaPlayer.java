package client.model;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import server.rtsp.model.RTPpacket;
import client.rtsp.model.ClientModel;
import client.statistics.StatisticsModel;

public class MediaPlayer extends Observable implements Observer {

	public enum STATE {
		STOP,
		BUFFER,
		PLAY,
		PAUSE, 
		RETURN
	}

	private STATE state;
	private List<RTPpacket> buffer;
	private int currentFrame;
	private ClientModel rtspClient;
	private InetAddress serverIp;
	private int rtspServerPort;
	private StatisticsModel statsLogger;
	private LibraryEntry libraryEntry;

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> playHandle;
	private final Runnable play = new Runnable() {
		public void run() {
			nextFrame();
		}
	};

	public MediaPlayer(InetAddress serverIp, int rtspServerPort, StatisticsModel statsLogger) throws IOException {
		state = STATE.STOP;
		currentFrame = 0;
		buffer = new ArrayList<RTPpacket>();
		playHandle = null;
		scheduler = Executors.newScheduledThreadPool(1);
		this.serverIp = serverIp;
		this.rtspServerPort = rtspServerPort;
		this.statsLogger = statsLogger;
		
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		System.out.println("Setting state to: " + state);
		this.state = state;
		this.setChanged();
		this.notifyObservers(Update.STATE);
	}

	public byte[] getFrame() {
		RTPpacket packet = buffer.get(currentFrame);
		if (packet == null) {
			return new byte[0];
		}
		byte[] frame = new byte[packet.getPayloadLength()];
		packet.getPayload(frame);
		return frame;
	}

	public int getFrameLength() {
		RTPpacket packet = buffer.get(currentFrame);
		if (packet == null) {
			return 0;
		}
		return packet.getPayloadLength();
	}

	public RTPpacket getPacket() {
		return buffer.get(currentFrame);
	}

	public void play() throws IOException {
		System.out.println("Starting play...");
		switch (getState()) {
		case STOP:
			rtspClient.setup();
			rtspClient.play();
			setState(STATE.BUFFER);
			break;
		case PAUSE:
			if (bufferLowerBound()) {
				startPlaying();
			} else {
				setState(STATE.BUFFER);
			}
			break;
		case PLAY:
			if (bufferLowerBound()) {
				System.out.println("In PLAY case");
				startPlaying();
			} else {
				setState(STATE.BUFFER);
			}
			break;
		case BUFFER:
			break;
		}
	}

	public void pause() {
		switch (getState()) {
		case STOP:
			stopPlaying();
			setState(STATE.STOP);
			break;
		case PAUSE:
			break;
		case PLAY:
			stopPlaying();
			setState(STATE.PAUSE);
			break;
		case BUFFER:
			setState(STATE.PAUSE);
			break;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((ClientModel.Update) arg) {
		case FRAME:
			buffer.add(rtspClient.getPacket());
			switch (getState()) {
			case STOP:
				stopPlaying();
				break;
			case PAUSE:
				break;
			case PLAY:
				if (bufferLowerBound()) {
					stopPlaying();
					setState(STATE.BUFFER);
				}
				break;
			case BUFFER:
				if (bufferUpperBound()) {
					startPlaying();
				}
				break;
			}
			break;
		}
	}

	private void nextFrame() {
		if (currentFrame < buffer.size()) {
			currentFrame++;
			this.setChanged();
			this.notifyObservers(Update.FRAME);
			statsLogger.logFrameExitFromBuffer();
		}
	}

	private void startPlaying() {
		playHandle = scheduler.scheduleAtFixedRate(play, 0, Integer.parseInt(libraryEntry.getPeriod()), MILLISECONDS);
		setState(STATE.PLAY);
	}

	public void stopPlaying() {
		if (playHandle != null) {
			playHandle.cancel(true);
		}
	}

	private boolean bufferUpperBound() {
		int bufSize = buffer.size();
		int vidLen = Integer.parseInt(libraryEntry.getLength());
		return bufSize == vidLen || (double)(buffer.size() - currentFrame) / vidLen > 0.2;
	}

	private boolean bufferLowerBound() {
		int vidLen = Integer.parseInt(libraryEntry.getLength());
		return (double)(buffer.size() - currentFrame) / vidLen < 0.05;
	}
	
	public void openMedia(LibraryEntry media){
		this.libraryEntry = media;
		try {
			rtspClient = new ClientModel(media.getLocation(), serverIp, rtspServerPort, statsLogger);
			rtspClient.addObserver(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}