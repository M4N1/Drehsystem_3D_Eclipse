package drehsystem3d;

import java.util.HashMap;
import java.util.Map;

import drehsystem3d.Listener.UserInputListener;
import processing.core.PApplet;

public class UIHandler implements UserInputListener
{
	private PApplet context;
	private Map<String, View> uiContents;

	public UIHandler(PApplet context)
	{
		this.context = context;
		this.uiContents = new HashMap<>();
	}

	public void addUiElement(String key, View view)
	{
		this.uiContents.put(key, view);
	}

	public void removeUiElement(String key)
	{
		if (!this.uiContents.containsKey(key))
		{
			return;
		}
		this.uiContents.remove(key);
	}

	public View getUiElement(String key)
	{
		return this.uiContents.get(key);
	}

	public void draw()
	{
		for (Map.Entry<String, View> pair : this.uiContents.entrySet())
		{
			pair.getValue().draw();
		}
	}

	@Override
	public void onKeyPressed(int keyCode, char key)
	{
		this.uiContents.forEach((k, v) -> {
			v.onKeyPressed(keyCode, key);
		});
	}

	@Override
	public void onKeyReleased(int keyCode, char key)
	{
		this.uiContents.forEach((k, v) -> {
			v.onKeyReleased(keyCode, key);
		});
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		boolean uiElementClicked = false;
		for (Map.Entry<String, View> entry : this.uiContents.entrySet())
		{
			uiElementClicked = uiElementClicked || entry.getValue().onMousePressed(mouseButton);
		}
		return uiElementClicked;
	}

	@Override
	public void onMouseReleased(int mouseButton)
	{
		this.uiContents.forEach((k, v) -> {
			v.onMouseReleased(mouseButton);
		});
	}
}
