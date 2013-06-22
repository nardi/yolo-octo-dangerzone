package com.example.gametest;

public class CircularDoubleBuffer {
	private double[] buffer;
	
	public CircularDoubleBuffer(int size) {
		buffer = new double[size];
	}

	public int getFrom(int position, double[] data, int offset, int length) {
		int bufferLeft = buffer.length - position;
		if (bufferLeft >= length) {
			System.arraycopy(buffer, position, data, offset, length);
		} else {
			System.arraycopy(buffer, position, data, offset, bufferLeft);
			System.arraycopy(buffer, 0, data, offset + bufferLeft, length - bufferLeft);
		}
		
		return (position + length) % buffer.length;
	}

	public int placeFrom(int position, short[] data, int offset, int length) {
		int bufferLeft = buffer.length - position;
		if (bufferLeft >= length) {
			System.arraycopy(data, offset, buffer, position, length);
		} else {
			System.arraycopy(data, offset, buffer, position, bufferLeft);
			System.arraycopy(data, offset + bufferLeft, buffer, 0, length - bufferLeft);
		}

		return (position + length) % buffer.length;
	}
	
	public int getLength() {
		return buffer.length;
	}
}
