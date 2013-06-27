package yolo.octo.dangerzone.lvlgen;

public class Score {
	private int score;
	
	public Score() {
		score = 0;
		//Collectable.setScore(this);
	}
	
	public void addScore(int score) {
		this.score += score;
	}
	
	public int getScore() {
		return score;
	}
	
	
}
