/* The Score class handles the player's score.
 */

package yolo.octo.dangerzone.lvlgen;

public class Score {
	private int score;
	
	/* The constructor, it initializes the Score object and makes it call-able
	 * for the Collectible class.
	 */
	public Score() {
		score = 0;
		Collectible.setScore(this);
	}
	
	/* Adds a certain amount to the current score. 
	 * (Can also be negative)
	 */
	public void addScore(int score) {
		this.score += score;
	}
	
	/* Returns the current score.
	 */
	public int getScore() {
		return score;
	}
	
	
}
