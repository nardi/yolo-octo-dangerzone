package yolo.octo.dangerzone.core;

public class CircularFloatBuffer {
	private float[] buffer;
	
	public CircularFloatBuffer(int size) {
		buffer = new float[size];
	}

	public int getFrom(int position, float[] data, int offset, int length) {
		int bufferLeft = buffer.length - position;
		if (bufferLeft >= length) {
			System.arraycopy(buffer, position, data, offset, length);
		} else {
			System.arraycopy(buffer, position, data, offset, bufferLeft);
			System.arraycopy(buffer, 0, data, offset + bufferLeft, length - bufferLeft);
		}
		
		return (position + length) % buffer.length;
	}

	public int placeFrom(int position, float[] data, int offset, int length) {
		int bufferLeft = buffer.length - position;
		if (bufferLeft >= length) {
			System.arraycopy(data, offset, buffer, position, length);
		} else {
			System.arraycopy(data, offset, buffer, position, bufferLeft);
			System.arraycopy(data, offset + bufferLeft, buffer, 0, length - bufferLeft);
		}

		return (position + length) % buffer.length;
	}
	
	public int placeFrom(int position, float instantEnergy) {
		buffer[position] = instantEnergy;
		
		return (position + 1) % buffer.length;
	}
	
	public int getLength() {
		return buffer.length;
	}
}
