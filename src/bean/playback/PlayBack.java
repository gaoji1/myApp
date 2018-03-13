package bean.playback;

import java.util.Date;

public class PlayBack {

	int id;
	String uName;
	Date liveTime;
	String fileName;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getuName() {
		return uName;
	}
	public void setuName(String uName) {
		this.uName = uName;
	}
	public Date getLiveTime() {
		return liveTime;
	}
	public void setLiveTime(Date liveTime) {
		this.liveTime = liveTime;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
