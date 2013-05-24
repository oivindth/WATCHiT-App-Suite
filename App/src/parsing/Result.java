package parsing;

import java.util.List;

public class Result implements Comparable<Result> {
	
	private String user;
	private String date;
	private List<Step> steps;
	private int totalTime;
	private String timeToDisplay;
	
	public Result() {
		
	}
	
	public Result(String user, String date, List<Step> steps, int totalTime) {
		this.user = user;
		this.date = date;
		this.steps = steps;
		this.totalTime = totalTime;
		timeToDisplay = Parser.convertSecondsToString(this.totalTime);
	}
	
	public void setTimeToDisplay (String t) {
		timeToDisplay = t;
	}
	public String getTimeToDisplay () {
		return timeToDisplay;
	}
	
	@Override
	public int compareTo(Result another) {
		if  (this.totalTime > another.totalTime) {
			return 1;
		} else if (this.totalTime < another.totalTime) {
			return -1;
		} 
		return 0;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}
	
}
