package com.example.gametest;

public abstract class GameObject implements BaseGameObject {
	private BaseGameObject parent = null;
	
	public GameObject(BaseGameObject parent) {
		this.parent = parent;
	}
	
	public BaseGameObject getParent() {
		return parent;
	}
}
