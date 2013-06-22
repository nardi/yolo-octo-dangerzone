package com.example.gametest;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class GameObject implements Drawable, Updateable, Touchable {
	private GameFragment parentFragment = null;
	private GameObject parent = null;
	private List<GameObject> childObjects = new ArrayList<GameObject>();
	
	void setParent(GameObject parent) {
		setParentFragment(parent.getParentFragment());
		this.parent = parent;
	}
	
	public GameObject getParent() {
		return parent;
	}
	
	void setParentFragment(GameFragment parentFragment) {
		this.parentFragment = parentFragment;
		this.parent = null;
	}
	
	public GameFragment getParentFragment() {
		return parentFragment;
	}

	public void addObject(GameObject go) {
		childObjects.add(go);
		go.setParent(this);
	}
	
	public void addObject(GameObject go, int index) {
		childObjects.add(index, go);
		go.setParent(this);
	}

	public int removeObject(GameObject go) {
		int index = childObjects.indexOf(go);
		childObjects.remove(go);
		if (go.getParent() == this)
			go.setParent(null);
		return index;
	}
	
	public void detatch() {
		if (parent != null) {
			parent.removeObject(this);
			parentFragment = null;
		} else if (parentFragment != null) {
			parentFragment.removeObject(this);
		}
	}
	
	public void swapFor(GameObject go) {
		if (parent != null) {
			int index = parent.removeObject(this);
			parent.addObject(go, index);
		} else if (parentFragment != null) {
			int index = parentFragment.removeObject(this);
			parentFragment.addObject(go, index);
		}
	}
	
	protected void preUpdate(long dt) {}
	protected void onUpdate(long dt) {}
	protected void postUpdate(long dt) {}
	
	public final void update(long dt) {
		preUpdate(dt);
		onUpdate(dt);
		for (GameObject go : childObjects)
			go.update(dt);
		postUpdate(dt);
	}

	protected void preDraw(Canvas canvas) {}
	protected void onDraw(Canvas canvas) {}
	protected void postDraw(Canvas canvas) {}
	
	public final void draw(Canvas canvas) {
		preDraw(canvas);
		onDraw(canvas);
		for (GameObject go : childObjects)
			go.draw(canvas);
		postDraw(canvas);
	}
	
	protected boolean onTouch(View v, MotionEvent me) {
		return false;
	}
	
	public final boolean touch(View v, MotionEvent me) {
		boolean eventUsed = false;
		eventUsed |= onTouch(v, me);
		for (GameObject go : childObjects)
			eventUsed |= go.touch(v, me);
		return eventUsed;
	}
}
