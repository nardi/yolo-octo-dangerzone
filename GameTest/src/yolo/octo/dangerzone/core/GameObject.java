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
		addObject(go, childObjects.size());
	}
	
	public void addObject(GameObject go, int index) {
		go.setParent(this);
		go.attach();
		childObjects.add(index, go);
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
	
	public void detach() {
		if (parent != null) {
			parent.removeObject(this);
			parentFragment = null;
		} else if (parentFragment != null) {
			parentFragment.removeObject(this);
		}
	}
	
	public void swapFor(GameObject go) {
		if (parent != null) {
			GameObject p = parent;
			int index = p.removeObject(this);
			p.addObject(go, index);
		} else if (parentFragment != null) {
			GameFragment pf = parentFragment;
			int index = pf.removeObject(this);
			pf.addObject(go, index);
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
	
	protected void onAttach() {}
	
	public final void attach() {
		onAttach();
		iterating = true;
		for (GameObject go : childObjects)
			go.attach();
		iterating = false;
		checkAndRemove();
	}
	
	protected void onRun() {}
	
	public final void run() {
		onRun();
		iterating = true;
		for (GameObject go : childObjects)
			go.run();
		iterating = false;
		checkAndRemove();
	}
	
	protected void onHalt() {}
	
	public final void halt() {
		onHalt();
		iterating = true;
		for (GameObject go : childObjects)
			go.halt();
		iterating = false;
		checkAndRemove();
	}
	
	protected void onResize(int width, int height) {}
	
	public final void resize(int width, int height) {
		onResize(width, height);
		iterating = true;
		for (GameObject go : childObjects)
			go.resize(width, height);
		iterating = false;
		checkAndRemove();
	}
}
