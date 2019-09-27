package drehsystem3d;

import java.util.ArrayList;

import drehsystem3d.Listener.UserInputListener;
import javafx.util.Pair;
import processing.core.PApplet;

public class InputHandler implements UserInputListener
{
	private PApplet context;
	private ArrayList<Object> mouseButtons;
	private ArrayList<Pair<Key, Integer>> keyList;
	private long millisOflastKeyEvent = 0;

	public InputHandler(PApplet context)
	{
		this.context = context;
		this.keyList = new ArrayList<>();
		this.mouseButtons = new ArrayList<>();
	}

	public boolean isKeyPressed(int keyCode)
	{
		for (Pair<Key, Integer> p : this.keyList)
			if (p.getKey().code == keyCode) return true;
		return false;
	}

	public boolean isKeyPressed(char key)
	{
		for (Pair<Key, Integer> p : this.keyList)
			if (p.getKey().key == key) return true;
		return false;
	}
	
	public long millisSinceLastKeyEvent()
	{
		return (this.context.millis() - this.millisOflastKeyEvent);
	}
	
	public boolean millisSinceLastKeyEventElapsed(long elapsed)
	{
		return (millisSinceLastKeyEvent() >= elapsed);
	}

	public ArrayList<Key> keyPressedPermanent()
	{
		ArrayList<Key> permanentKeys = new ArrayList<>();
		for (int i = 0; i < this.keyList.size(); i++)
		{	
			Pair<Key, Integer> p = this.keyList.get(i);
			if (this.context.millis() - p.getValue() > 600)
			{
				permanentKeys.add(p.getKey());
				this.keyList.set(i, new Pair<Key, Integer>(p.getKey(), this.context.millis()-550));
			}
		}
		if (permanentKeys.size() > 0) this.millisOflastKeyEvent = this.context.millis();
		return permanentKeys;
	}
	
	@Override
	public boolean onKeyPressed(int keyCode, char key, boolean repeat)
	{
		if (repeat) return false;
		this.millisOflastKeyEvent = this.context.millis();
		if (!isKeyPressed(key))
		{
			Key k = new Key(keyCode, key);
			this.keyList.add(new Pair<Key, Integer>(k, this.context.millis()));
		}
		return false;
	}

	@Override
	public boolean onKeyReleased(int keyCode, char key)
	{
		Key k = new Key(keyCode, key);
		for (int i = 0; i < this.keyList.size(); i++)
			if (this.keyList.get(i).getKey().equals(k))
				this.keyList.remove(i);
		return false;
	}

	@Override
	public void onMousePressed(int mouseButton)
	{
		addItem(this.mouseButtons, mouseButton);
	}

	@Override
	public void onMouseDragged()
	{
	}

	@Override
	public void onMouseReleased(int mouseButton)
	{
		removeItem(this.mouseButtons, mouseButton);
	}

	private void addItem(ArrayList<Object> list, Object item)
	{
		if (list.contains(item))
		{
			return;
		}
		list.add(item);
	}

	private void removeItem(ArrayList<?> list, Object item)
	{
		if (!list.contains(item))
		{
			return;
		}
		list.remove(list.lastIndexOf(item));
	}

	private Object getLastElement(ArrayList<?> list)
	{
		if (list.size() == 0) return null;
		return list.get(list.size() - 1);
	}

	public int getLastKeyCode()
	{
		return ((Pair<Key, Integer>)getLastElement(this.keyList)).getKey().code;
	}

	public char getLastKey()
	{
		return ((Pair<Key, Integer>)getLastElement(this.keyList)).getKey().key;
	}

	public class Key
	{
		public final int code;
		public final char key;

		public Key(int code, char key)
		{
			this.code = code;
			this.key = key;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj.getClass() != this.getClass()) return false;
			Key other = (Key) obj;
			return (other.code == this.code && other.key == this.key);
		}
		
		@Override
		public String toString()
		{
			return "('" + this.key + "', " + this.code + ")";
		}
	}
}
