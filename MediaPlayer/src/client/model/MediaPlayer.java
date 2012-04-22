package client.model;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import javax.swing.ImageIcon;

import client.ClientModel;

public class MediaPlayer extends Observable implements Observer {

	public static final int FRAMES_SEC = 30;

	public enum STATE {
		STOP,
		BUFFER,
		PLAY,
		PAUSE
	}

	public enum Update {
		STATE,
		FRAME
	}

	private STATE state;
	private List<ImageIcon> buffer;
	private int currentFrame;
	private Toolkit toolkit;
	private ClientModel rtspClient;
	private int playRate;

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> playHandle;
	private final Runnable play = new Runnable() {
		public void run() {
			nextFrame();
		}
	};

	public MediaPlayer(String videoName, InetAddress serverIp, int rtspServerPort) throws IOException {
		state = STATE.STOP;
		currentFrame = 0;
		toolkit = Toolkit.getDefaultToolkit();
		buffer = new ArrayList<ImageIcon>();
		playRate = 1000 / FRAMES_SEC;
		playHandle = null;
		scheduler = Executors.newScheduledThreadPool(1);
		rtspClient = new ClientModel(videoName, serverIp, rtspServerPort);
		rtspClient.addObserver(this);
	}

	public STATE getState() {
		return state;
	}

	public void setState(STATE state) {
		this.state = state;
		this.setChanged();
		this.notifyObservers(Update.STATE);
	}

	public void play() throws IOException {
		switch (getState()) {
		case STOP:
			rtspClient.setup();
			rtspClient.play();
			setState(STATE.BUFFER);
			break;
		case PAUSE:
			if (bufferedEnough()) {
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

	private void startPlaying() {
		playHandle = scheduler.scheduleAtFixedRate(play, 0, playRate, MILLISECONDS);
		setState(STATE.PLAY);
	}

	private void stopPlaying() {
		if (playHandle != null) {
			playHandle.cancel(true);
		}
	}

	private boolean bufferedEnough() {
		int bufSize = buffer.size();
//		return bufSize > 100 && (double)(bufSize - currentFrame) / bufSize > 0.7;
		return (bufSize - currentFrame) > 150;
	}

	@Override
	public void update(Observable o, Object arg) {
		switch ((ClientModel.Update) arg) {
		case FRAME:
			buffer.add(new ImageIcon(toolkit.createImage(rtspClient.getFrame(), 0, rtspClient.getFrameLength())));
			switch (getState()) {
			case STOP:
			case PAUSE:
				break;
			case PLAY:
				if (!bufferedEnough()) {
					stopPlaying();
					setState(STATE.BUFFER);
				}
				break;
			case BUFFER:
				if (bufferedEnough()) {
					startPlaying();
				}
				break;
			}
			break;
		}
	}

	public ImageIcon getFrame() {
		return buffer.get(currentFrame);
	}

	private void nextFrame() {
		if (currentFrame < buffer.size()) {
			currentFrame++;
			this.setChanged();
			this.notifyObservers(Update.FRAME);
		}
	}

}