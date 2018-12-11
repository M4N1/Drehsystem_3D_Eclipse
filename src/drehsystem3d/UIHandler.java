package drehsystem3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import drehsystem3d.Listener.UserInputListener;
import drehsystem3d.Listener.WindowResizeListener;
import processing.core.PApplet;

public class UIHandler implements UserInputListener, WindowResizeListener
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
	public boolean onKeyPressed(int keyCode, char key)
	{
		boolean uiElementClicked = false;
		for (Map.Entry<String, View> entry : this.uiContents.entrySet())
		{
			uiElementClicked = uiElementClicked || entry.getValue().onKeyPressed(keyCode, key);
		}
		return uiElementClicked;
	}

	@Override
	public boolean onKeyReleased(int keyCode, char key)
	{
		boolean uiElementClicked = false;
		for (Map.Entry<String, View> entry : this.uiContents.entrySet())
		{
			uiElementClicked = uiElementClicked || entry.getValue().onKeyReleased(keyCode, key);
		}
		return uiElementClicked;
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		boolean uiElementClicked = false;
		for (Map.Entry<String, View> entry : this.uiContents.entrySet())
		{
			View element = entry.getValue();
			boolean elementClicked = element.onMousePressed(mouseButton);
			if (elementClicked)
			{
				Global.logger.log(Level.FINE, "View item clicked", new Object[] {entry.getKey(), element.id, element.getClass().getName()});
			}
			
			uiElementClicked = uiElementClicked || elementClicked;
		}
		return uiElementClicked;
	}

	@Override
	public void onMouseDragged()
	{
		this.uiContents.forEach((k, v) -> {
			v.onMouseDragged();
		});
	}

	@Override
	public void onMouseReleased(int mouseButton)
	{
		this.uiContents.forEach((k, v) -> {
			v.onMouseReleased(mouseButton);
		});
	}

	@Override
	public void onWindowResize(int widthOld, int heightOld, int widthNew, int heightNew) {
		this.uiContents.forEach((k, v) -> {
			v.onWindowResize(widthOld, heightOld, widthNew, heightNew);
		});
	}
	
	public Checkbox addCheckBox(String name, String desc)
	{
		return addCheckBox(name, desc, false, (ArrayList<Checkbox>) null);
	}

	public Checkbox addCheckBox(String name, String desc, boolean checked)
	{
		return addCheckBox(name, desc, checked, (ArrayList<Checkbox>) null);
	}

	public Checkbox addCheckBox(String name, String desc, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<>();
		group.add(member);
		return addCheckBox(name, desc, false, group);
	}

	public Checkbox addCheckBox(String name, String desc, ArrayList<Checkbox> group)
	{
		return addCheckBox(name, desc, false, group);
	}

	public Checkbox addCheckBox(String name, String desc, boolean checked, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<>();
		group.add(member);
		return addCheckBox(name, desc, checked, group);
	}

	public Checkbox addCheckBox(String name, String desc, boolean checked, ArrayList<Checkbox> group)
	{
		Checkbox c = new Checkbox(this.context, desc, group);
		c.setChecked(checked);
		c.setSize(20, 20);
		c.setMargin(5);
		this.addUiElement(name, c);
		return c;
	}
}
