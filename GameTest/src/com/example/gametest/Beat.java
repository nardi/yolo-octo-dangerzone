package com.example.gametest;

public class Beat {
	public long startTime;
	public long endTime;
	// intensity kan gebaseerd worden op amplitude, of later ook
	// op de frequentiebanden waar ze in voorkomen bijvoorbeeld
	public double intensity;
	
	public long time() {
		return (startTime + endTime) / 2;
	}
}