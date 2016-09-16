import java.util.Date;
import java.util.TimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JSlider;

import javax.sound.sampled.Clip;


public class PlayingTimer extends Thread{
	private DateFormat dateFormater = new SimpleDateFormat("HH:mm:ss");	
	private boolean isRunning = false;
	private boolean isPause = false;
	private boolean isReset = false;
	private long startTime;
	private long pauseTime;
	
	private JLabel labelRecordTime;
	private JSlider slider;
	private Clip audioClip;
	
	public void setAudioClip(Clip audioClip){
		this.audioClip = audioClip;
	}

	PlayingTimer(JLabel labelRecordTime, JSlider slider){
		this.labelRecordTime = labelRecordTime;
		this.slider = slider;
	}
	
	public void run(){
		isRunning = true;
		startTime = System.currentTimeMillis();
		
		while (isRunning){
			try{
				Thread.sleep(100);
				if (!isPause){
					if (audioClip != null && audioClip.isRunning()){
						labelRecordTime.setText(toTimeString());
						int currentSecond = (int) audioClip.getMicrosecondPosition() / 1_000_000; 
						slider.setValue(currentSecond);
					}
				} 
				else
					pauseTime += 100;
			} 
			catch (InterruptedException ex){
				if (isReset){
					slider.setValue(0);
					labelRecordTime.setText("00:00:00");
					isRunning = false;		
					break;
				}
			}
		}
	}
	
	void reset(){
		isReset = true;
		isRunning = false;
	}
	
	void pauseTimer(){
		isPause = true;
	}
	
	void resumeTimer(){
		isPause = false;
	}
	
	private String toTimeString(){
		long now = System.currentTimeMillis();
		Date current = new Date(now - startTime - pauseTime);
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeCounter = dateFormater.format(current);
		return timeCounter;
	}
}