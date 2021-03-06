/* The Floorbuffer-class is a ring-buffer that manages the points visible 
 * on the screen.
 */

package yolo.octo.dangerzone.lvlgen;
import android.graphics.PointF;
import android.view.View;

public class FloorBuffer {
	private int index;
	private int bufferSize;
	private int pointCounter = 0;
	private int offset;
	private float[] points;
	private float[] buffer;
	private PointF[] tempBuffer;

	
	/* Initialiseer de waardes voor de buffer */
	public FloorBuffer(float[] points, int offset) {
		this.points = points;
		index = 0;
		bufferSize = 400;
		buffer = new float[bufferSize];
		tempBuffer = new PointF[bufferSize];
		this.offset = offset + bufferSize / 4 - 1;
		
		fillBuffer();
	}
	
	
	/* Initialiseer de buffer met de eerste >bufferSize< aantal waardes.
	 * Als er minder waardes dan dit zijn, wordt er een plat vlak gegenereerd.
	 */
	private void fillBuffer() {
		int toSkip = Math.min(bufferSize, offset);
		offset -= toSkip;
		for (int i = toSkip; i < bufferSize; i++) {
			if (pointCounter < points.length) {
				buffer[i] = points[pointCounter];
				pointCounter++;
				
			}
			else {
				buffer[i] = 0;
			}
		}
	}
	
	
	/* Vervangt het meest linker punt met het nieuwe, meest rechter punt. */
	public void update() {

		if (offset > 0 || pointCounter >= points.length) {
			buffer[index] = 0;
			if (offset > 0)
				offset--;
		}
		else {
			buffer[index] = points[pointCounter];
			pointCounter++;
		}
		
		index = (index + 1) % bufferSize;
	}
	
	/* This update() is calle din order to consecutively execute update(),
	 * for example when we need to skip a few framses.
	 */
	public void update(int skip){
		for(int i = 0; i < skip; i++){
			update();
		}
	}
	
	/* Geeft de buffer terug voor de teken klasse, in de volgorde van aller linker 
	 * punt op scherm naar aller rechter punt. 
	 */
	public PointF[] getBuffer() {
		for (int i = index, j = 0; j < bufferSize; i++, j++) {
			i %= bufferSize;
			tempBuffer[j] = new PointF();
			tempBuffer[j].y = buffer[i];	
			tempBuffer[j].x = j;
		}
		
		return tempBuffer;
	}
	
	
	/* Gets the height on the player's position
	 */
	public float getHeight(View v) {
		int width = v.getWidth();
		int location =(int) ((width/4.0 * 399.0) / width);
		float yValue = (float) (buffer[(index + location) % 400] * 100);
		return yValue;
	}
}
