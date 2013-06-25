package yolo.octo.dangerzone.core;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class GameObject implements Drawable, Updateable, Touchable {
	private GameFragment parentFragment = null;
	private GameObject parent = null;
	private List<GameObject> childObjects = new ArrayList<GameObject>();
	private boolean iterating = false;
	/*
	 * You can't remove an object from a List while iterating over it,
	 * so a record is kept instead and we remove these objects later,
	 * so GameObjects can remove themselves and each other.
	 */
	private List<GameObject> objectsToRemove = new ArrayList<GameObject>();
	
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
		if (iterating)
			objectsToRemove.add(go);
		else
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
	
	private void checkAndRemove() {
		if (!objectsToRemove.isEmpty()) {
			for (GameObject go : objectsToRemove)
				childObjects.remove(go);
			objectsToRemove.clear();
		}
	}
	
	protected void preUpdate(long dt) {}
	protected void onUpdate(long dt) {}
	protected void postUpdate(long dt) {}
	
	public final void update(long dt) {
		preUpdate(dt);
		onUpdate(dt);
		iterating = true;
		for (GameObject go : childObjects)
			go.update(dt);
		iterating = false;
		checkAndRemove();
		postUpdate(dt);
	}

	protected void preDraw(Canvas canvas) {}
	protected void onDraw(Canvas canvas) {}
	protected void postDraw(Canvas canvas) {}
	
	public final void draw(Canvas canvas) {
		preDraw(canvas);
		onDraw(canvas);
		iterating = true;
		for (GameObject go : childObjects)
			go.draw(canvas);
		iterating = false;
		checkAndRemove();
		postDraw(canvas);
	}
	
	protected boolean onTouch(View v, MotionEvent me) {
		return false;
	}
	
	public final boolean touch(View v, MotionEvent me) {
		boolean eventUsed = false;
		eventUsed |= onTouch(v, me);
		iterating = true;
		for (GameObject go : childObjects)
			eventUsed |= go.touch(v, me);
		iterating = false;
		checkAndRemove();
		return eventUsed;
	}
}