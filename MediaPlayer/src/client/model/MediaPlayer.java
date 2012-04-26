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
import client.statistics.InboundLoggingController;

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
	private int playRate;
	private int videoLength;
	private InetAddress serverIp;
	private int rtspServerPort;

	private InboundLoggingController logger;
	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> playHandle;
	private final Runnable play = new Runnable() {
		public void run() {
			nextFrame();
		}
	};

	public MediaPlayer(InetAddress serverIp, int rtspServerPort) throws IOException {
		state = STATE.STOP;
		currentFrame = 0;
		buffer = new ArrayList<RTPpacket>();
		videoLength = 500;
		playRate = 1000 / 20;
		playHandle = null;
		scheduler = Executors.newScheduledThreadPool(1);
		this.serverIp = serverIp;
		this.rtspServerPort = rtspServerPort;
		logger = InboundLoggingController.getInstance();
		
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
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
			logger.logFramePlayed();
		}
	}

	private void startPlaying() {
		playHandle = scheduler.scheduleAtFixedRate(play, 0, playRate, MILLISECONDS);
		setState(STATE.PLAY);
	}

	public void stopPlaying() {
		if (playHandle != null) {
			playHandle.cancel(true);
		}
	}

	private boolean bufferUpperBound() {
		int bufSize = buffer.size();
		return bufSize == videoLength || (double)(buffer.size() - currentFrame) / videoLength > 0.2;
	}

	private boolean bufferLowerBound() {
		return (double)(buffer.size() - currentFrame) / videoLength < 0.05;
	}
	
	public void openMedia(String videoName){
		System.out.println("MEDIA TO OPEN: " + videoName);
		try {
			rtspClient = new ClientModel(videoName, serverIp, rtspServerPort);
			rtspClient.addObserver(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}