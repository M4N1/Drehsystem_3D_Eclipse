package drehsystem3d;

import java.util.ArrayList;

import drehsystem3d.Listener.UserInputListener;
import processing.core.PApplet;

public class InputHandler implements UserInputListener
{
	private ArrayList<Object> keys;
	private ArrayList<Object> keyCodes;
	private ArrayList<Object> mouseButtons;
	private Key lastPressedKey;

	public InputHandler(PApplet context)
	{
		this.keys = new ArrayList<>();
		this.keyCodes = new ArrayList<>();
		this.mouseButtons = new ArrayList<>();
		this.lastPressedKey = new Key(-1, ' ');
	}

	public boolean isKeyPressed(int keyCode)
	{
		return this.keyCodes.contains(keyCode);
	}

	public boolean isKeyPressed(char key)
	{
		return this.keys.contains(key);
	}

	@Override
	public boolean onKeyPressed(int keyCode, char key)
	{
		this.lastPressedKey = new Key(keyCode, key);
		addItem(this.keyCodes, keyCode);
		addItem(this.keys, key);
		return false;
	}

	@Override
	public boolean onKeyReleased(int keyCode, char key)
	{
		this.lastPressedKey = getPreviousLastKey();
		removeItem(this.keyCodes, keyCode);
		removeItem(this.keys, key);
		return false;
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		addItem(this.mouseButtons, mouseButton);
		return false;
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

	private Key getPreviousLastKey()
	{
		if (this.keyCodes.size() == 0)
		{
			return new Key(-1, ' ');
		}
		int keyCode = (int) getLastElement(this.keyCodes);
		char key = (char) getLastElement(this.keys);
		return new Key(keyCode, key);
	}

	private Object getLastElement(ArrayList<?> list)
	{
		return list.get(list.size() - 1);
	}

	public int getLastKeyCode()
	{
		return this.lastPressedKey.code;
	}

	public char getLastKey()
	{
		return this.lastPressedKey.key;
	}

	private class Key
	{
		public final int code;
		public final char key;

		public Key(int code, char key)
		{
			this.code = code;
			this.key = key;
		}
	}
}
