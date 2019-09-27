package drehsystem3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

import drehsystem3d.Listener.UserInputListener;
import drehsystem3d.Listener.WindowResizeListener;
import processing.core.PApplet;
import ui.Checkbox;
import ui.View;

public class UIHandler implements UserInputListener, WindowResizeListener
{
	private PApplet context;
	private List<View> uiContents;
	private Set<View> contentsToManage;

	public UIHandler(PApplet context)
	{
		this.context = context;
		this.uiContents = new ArrayList<>();
		this.contentsToManage = new TreeSet<>(new Comparator<View>() {

			@Override
			public int compare(View v1, View v2)
			{
				if (v1.equals(v2)) return 0;
				int priority = v1.getDrawPriority() - v2.getDrawPriority();
				return priority == 0 ? 1 : priority;
			}
		});
	}

	public boolean addUiElement(View view)
	{
		return addUiElement(view, true);
	}
	
	/**
	 * 
	 * @param view The element to add
	 * @param manage Let the UIHandler manage events of the given view
	 * @return True when successful
	 */
	public boolean addUiElement(View view, boolean manage)
	{
		Global.logger.log(Level.FINE, "Add item to ui stack.", new Object[] {view, view.getName()});
		if (this.uiContents.contains(view))
			Global.logger.log(Level.FINE, "Item already exists.", new Object[] {view, view.getName()});
		boolean success = this.uiContents.add(view);
		if (success && manage)
		{
			this.contentsToManage.add(view);
			Global.logger.log(Level.FINE, "Add item to manager stack.", new Object[] {view, view.getName()});
		}
		return success;
	}

	public boolean removeUiElement(String name)
	{
		View v = getUiElement(name);
		if (v == null) return false;
		boolean success = this.uiContents.remove(v);
		if (success) this.contentsToManage.remove(v);
		return success;
	}

	public View getUiElement(String name)
	{
		for (View v : this.uiContents)
			if (v.getName().equals(name)) return v;
		return null;
	}

	public void draw()
	{
		if (Global.logger.isLoggable(Level.FINEST))
			System.out.print("\n");
		View hoveredView = null;
		for (View c : this.contentsToManage)
		{
			View current = c.updateHoverState();
			hoveredView = current != null ? current : hoveredView;
		}
		if (hoveredView != null) hoveredView.onHoverAction();
		for (View v : this.contentsToManage)
		{
			v.draw();
			Global.logger.log(Level.FINEST, "UI draw event", v.getName());
		}
	}

	@Override
	public boolean onKeyPressed(int keyCode, char key, boolean repeat)
	{
		boolean uiElementClicked = false;
		for (View v : this.contentsToManage)
		{
			uiElementClicked = uiElementClicked || v.onKeyPressed(keyCode, key, repeat);
		}
		return uiElementClicked;
	}

	@Override
	public boolean onKeyReleased(int keyCode, char key)
	{
		boolean uiElementClicked = false;
		for (View v : this.contentsToManage)
		{
			uiElementClicked = uiElementClicked || v.onKeyReleased(keyCode, key);
		}
		return uiElementClicked;
	}

	@Override
	public void onMousePressed(int mouseButton)
	{
		View viewPressed = null;
		for(View v : this.contentsToManage)
		{
			View current = v.isPressed();
			viewPressed = current != null ? current : viewPressed;
		}
		if (viewPressed != null) {
			Global.logger.log(Level.FINE, "View Pressed", new Object[] {viewPressed.getName(), viewPressed.getId()});
			viewPressed.onMousePressed(mouseButton);
		}
	}

	@Override
	public void onMouseDragged()
	{
		this.contentsToManage.forEach((v) -> {
			v.onMouseDragged();
		});
	}

	@Override
	public void onMouseReleased(int mouseButton)
	{
		this.contentsToManage.forEach((v) -> {
			v.onMouseReleased(mouseButton);
		});
	}

	@Override
	public void onWindowResize(int widthOld, int heightOld, int widthNew, int heightNew) {
		this.contentsToManage.forEach((v) -> {
			v.onWindowResize(widthOld, heightOld, widthNew, heightNew);
		});
	}
	
	public Checkbox setupCheckBox(String name, String desc)
	{
		return setupCheckBox(name, desc, false, (ArrayList<Checkbox>) null);
	}

	public Checkbox setupCheckBox(String name, String desc, boolean checked)
	{
		return setupCheckBox(name, desc, checked, (ArrayList<Checkbox>) null);
	}

	public Checkbox setupCheckBox(String name, String desc, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<>();
		group.add(member);
		return setupCheckBox(name, desc, false, group);
	}

	public Checkbox setupCheckBox(String name, String desc, ArrayList<Checkbox> group)
	{
		return setupCheckBox(name, desc, false, group);
	}

	public Checkbox setupCheckBox(String name, String desc, boolean checked, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<>();
		group.add(member);
		return setupCheckBox(name, desc, checked, group);
	}

	public Checkbox setupCheckBox(String name, String desc, boolean checked, ArrayList<Checkbox> group)
	{
		Checkbox c = new Checkbox(this.context, name, desc, group);
		c.setChecked(checked);
		c.setSize(20, 20);
		c.setMargin(5);
		return c;
	}
}
