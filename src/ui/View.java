package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import drehsystem3d.Listener.KeyListener;
import drehsystem3d.Listener.OnClickListener;
import drehsystem3d.Listener.OnHoverListener;
import drehsystem3d.Listener.UserInputListener;
import drehsystem3d.Listener.WindowResizeListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public abstract class View implements UserInputListener, KeyListener, WindowResizeListener
{
	public enum AlignmentHorizontal
	{
		MANUALL,
		CENTER,
		LEFT,
		RIGHT
	}
		
	public enum AlignmentVertical
	{
		MANUALL,
		CENTER,
		TOP,
		BOTTOM
	}
	
	public static int instanceCounter = 0;
	private static List<View> viewInstances = new ArrayList<View>();
	
	protected int drawPriority = 1;
	
	protected View container = null;
	protected int id = -1;
	protected final String name;
	
	protected OnClickListener onClickListener;
	protected OnHoverListener onHoverListener;
	
	protected Runnable onHoverAction;
	protected Runnable onHoverEndAction;
	
	protected boolean clicked = false;
	protected boolean visible = true;
	protected boolean hovered = false;
	
	protected PVector pos = new PVector(0, 0, 0);
	protected int viewWidth = 0, viewHeight = 0;
	protected int width = 0, height = 0;
	
	protected Color backgroundColor = new Color(0, 0, 0, 255);
	
	protected PApplet context = null;
	protected PGraphics canvas = null;
	
	private Neighbor neighbor = new Neighbor();
	private final Spacing margin = new Spacing();
	private AlignmentHorizontal horizontalAlignment = AlignmentHorizontal.MANUALL;
	private AlignmentVertical verticalAlignment = AlignmentVertical.MANUALL;
	
	public View(PApplet context, String name)
	{
		this.context = context;
		this.name = name;
		
		setup();
	}
	
	public View(PApplet context, String name, float x, float y)
	{
		this.context = context;
		this.name = name;
		this.pos = new PVector(x, y, 0);
		
		setup();
	}

	public View(PApplet context, String name, float x, float y, int w, int h)
	{
		this.context = context;
		this.name = name;
		this.pos = new PVector(x, y, 0);
		this.setSize(w, h);
		
		setup();
	}

	public View(PApplet context, String name, PVector pos)
	{
		this.context = context;
		this.name = name;
		this.pos = pos.copy();
		
		setup();
	}

	public View(PApplet context, String name, PVector pos, int w, int h)
	{
		this.context = context;
		this.name = name;
		this.pos = pos.copy();
		this.setSize(w, h);
		
		setup();
	}
	
	private void setup()
	{
		this.canvas = this.context.getGraphics();
		View.registerInstance(this);
	}
	
	public void setCanvas(PGraphics canvas)
	{
		this.canvas = canvas;
	}
	
	public void setDrawPriority(int priority)
	{
		this.drawPriority = priority;
	}
	
	public int getDrawPriority()
	{
		return this.drawPriority;
	}
	
	public PGraphics getCanvas()
	{
		return this.canvas;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setParent(View v)
	{
		this.container = v;
	}
	
	private static void registerInstance(View newInstance)
	{
		View.viewInstances.add(newInstance);
	}
	
	private static boolean nameExists(String name)
	{
		for (View v : View.viewInstances)
		{
			if (v.name.equals(name)) return true;
		}
		return false;
	}
	
	public static boolean unregisterInstance(String name)
	{
		for (View v : View.viewInstances)
		{
			if (v.name.equals(name)) 
			{
				return unregisterInstance(v);
			}
		}
		return false;
	}
	
	public static boolean unregisterInstance(View instanceToRemove)
	{
		return View.viewInstances.remove(instanceToRemove);
	}
	
	public static View getViewByName(String name)
	{
		for (View v : viewInstances)
		{
			if (v.name.equals(name)) return v;
		}
		return null;
	}
	
	public static View getViewById(int id)
	{
		for (View v : viewInstances)
		{
			if (v.id == id) return v;
		}
		return null;
	}
	
	protected class Spacing
	{
		private int x = 0;
		private int y = 0;
		
		public void setSpacing(int spacing)
		{
			this.x = spacing;
			this.y = spacing;
		}
		
		public void setSpacing(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public void setSpacingX(int x)
		{
			this.x = x;
		}
		
		public void setSpacingY(int y)
		{
			this.y = y;
		}
		
		public int getSpacingX()
		{
			return x;
		}
		
		public int getSpacingY()
		{
			return y;
		}
	}
	
	protected class Neighbor
	{
		public final static int NONE = 0;
		public final static int TOP = 1;
		public final static int RIGHT = 2;
		public final static int BOTTOM = 3;
		public final static int LEFT = 4;
		
		private View neighbor = null;
		private int directionOfNeighbor = NONE;
		
		public void setTopNeighbor(View v)
		{
			setNewNeighbor(v, TOP);
		}
		
		public void setRightNeighbor(View v)
		{
			setNewNeighbor(v, RIGHT);
		}
		
		public void setBottomNeighbor(View v)
		{
			setNewNeighbor(v, BOTTOM);
		}
		
		public void setLeftNeighbor(View v)
		{
			setNewNeighbor(v, LEFT);
		}
		
		private void setNewNeighbor(View v, int dir)
		{
			this.neighbor = v;
			this.directionOfNeighbor = dir;
		}
		
		public void removeNeighbor()
		{
			this.neighbor = null;
			this.directionOfNeighbor = NONE;
		}
		
		public int exists()
		{
			return this.directionOfNeighbor;
		}
		
		public View getNeighbor()
		{
			return this.neighbor;
		}
	}

	public void setVisibility(boolean visible)
	{
		this.visible = visible;
	}

	public boolean getVisibility()
	{
		return this.visible;
	}

	public void setPos(PVector pos)
	{
		this.pos = new PVector(pos.x, pos.y, pos.z);
	}

	public void setBackgroundColor(int color)
	{
		this.backgroundColor.setColor(color);
	}
	
	public void setBackgroundColor(Color color)
	{
		this.backgroundColor = color.clone();
	}

	public void setBackgroundgAlpha(int alpha)
	{
		this.backgroundColor.a = alpha;
	}

	public void setSize(int w, int h)
	{
		this.width = w;
		this.viewWidth = w + this.getMarginX();
		
		this.height = h;
		this.viewHeight = h + this.getMarginY();
	}
	
	public void setWidth(int w)
	{
		this.width = w;
		this.viewWidth = w + this.getMarginX();
	}

	public void setHeight(int h)
	{
		this.height = h;
		this.viewHeight = h + this.getMarginY();
	}

	private void calcPosX()
	{
		int neighborDir = this.neighbor.exists();
		View other = this.neighbor.getNeighbor();
		final Map<View.AlignmentHorizontal, Runnable> actions = new HashMap<>();
		
		switch (neighborDir)
		{
			case Neighbor.RIGHT:
				this.pos.x = other.pos.x - other.margin.getSpacingX() - this.viewWidth - this.margin.getSpacingX();
				break;
				
			case Neighbor.LEFT:
				this.pos.x = other.pos.x + other.viewWidth + other.margin.getSpacingX() + this.margin.getSpacingX();
				break;
				
			case Neighbor.TOP:
			case Neighbor.BOTTOM:
				actions.put(View.AlignmentHorizontal.CENTER, () -> {this.pos.x = other.pos.x + (other.viewWidth - this.viewWidth) / 2;});
				actions.put(View.AlignmentHorizontal.LEFT, () -> {this.pos.x = other.pos.x;});
				actions.put(View.AlignmentHorizontal.RIGHT, () -> {this.pos.x = other.pos.x + other.viewWidth - this.viewWidth;});
				break;
				
			default:
				actions.put(View.AlignmentHorizontal.CENTER, () -> {this.pos.x = (this.context.width - this.viewWidth) / 2;});
				actions.put(View.AlignmentHorizontal.LEFT, () -> {this.pos.x = this.margin.getSpacingX();});
				actions.put(View.AlignmentHorizontal.RIGHT, () -> {this.pos.x = this.context.width - this.viewWidth - this.margin.getSpacingX();});
		}
		
		Runnable action = actions.get(this.horizontalAlignment);
		
		if (action != null)
		{
			action.run();
		}
	}

	private void calcPosY()
	{
		int neighborDir = this.neighbor.exists();
		View other = this.neighbor.getNeighbor();
		final Map<View.AlignmentVertical, Runnable> actions = new HashMap<>();
		
		switch(neighborDir)
		{
			case Neighbor.TOP:
				this.pos.y = other.pos.y + other.viewHeight + other.margin.getSpacingY() + this.margin.getSpacingY();
				break;
				
			case Neighbor.BOTTOM:
				this.pos.y = other.pos.y - other.margin.getSpacingY() - this.viewHeight - this.margin.getSpacingY();
				break;
				
			case Neighbor.LEFT:
			case Neighbor.RIGHT:
				actions.put(View.AlignmentVertical.CENTER, () -> {this.pos.y = other.pos.y + (other.viewHeight - this.viewHeight) / 2;});
				actions.put(View.AlignmentVertical.TOP, () -> {this.pos.y = other.pos.y;});
				actions.put(View.AlignmentVertical.BOTTOM, () -> {this.pos.y = other.pos.y + other.viewHeight - this.viewHeight;});
				break;
				
			default:
				actions.put(View.AlignmentVertical.CENTER, () -> {this.pos.y = (this.context.height - this.viewHeight) / 2;});
				actions.put(View.AlignmentVertical.TOP, () -> {this.pos.y = this.margin.getSpacingY();});
				actions.put(View.AlignmentVertical.BOTTOM, () -> {this.pos.y = this.context.height - this.viewHeight - this.margin.getSpacingY();});	
		}
		
		Runnable action = actions.get(this.verticalAlignment);
		
		if (action != null)
		{
			action.run();
		}
	}

	public void alignTop(View v)
	{
		neighbor.setBottomNeighbor(v);
		changeToNeighborttachedPlacement();
	}
	
	public void alignRight(View v)
	{
		neighbor.setLeftNeighbor(v);
		changeToNeighborttachedPlacement();
	}
	
	public void alignBottom(View v)
	{
		neighbor.setTopNeighbor(v);
		changeToNeighborttachedPlacement();
	}
	
	public void alignLeft(View v)
	{
		neighbor.setRightNeighbor(v);
		changeToNeighborttachedPlacement();
	}
	
	private void changeToNeighborttachedPlacement()
	{
		if (this.horizontalAlignment == AlignmentHorizontal.MANUALL)
		{
			this.horizontalAlignment = AlignmentHorizontal.LEFT;
		}
		if (this.verticalAlignment == AlignmentVertical.MANUALL)
		{
			this.verticalAlignment = AlignmentVertical.CENTER;
		}
		calcPosX();
		calcPosY();
	}
	
	public void setHorizontalAlignment(AlignmentHorizontal alignment)
	{
		this.horizontalAlignment = alignment;
		calcPosX();
	}

	public void setVerticalAlignment(AlignmentVertical alignment)
	{
		this.verticalAlignment = alignment;
		calcPosY();
	}

	public void setMargin(int spacing)
	{
		this.margin.setSpacing(spacing);
	}
	
	public void setMarginX(int x)
	{
		this.margin.setSpacingX(x);
	}
	
	public void setMarginY(int y)
	{
		this.margin.setSpacingY(y);
	}
	
	public void setMargin(int x, int y)
	{
		this.margin.setSpacing(x, y);
	}
	
	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public void setOnClickListener(OnClickListener listener)
	{
		this.onClickListener = listener;
	}

	public void setOnHoverListener(OnHoverListener listener)
	{
		this.onHoverListener = listener;
	}

	public void setOnHoverAction(Runnable action)
	{
		this.onHoverAction = action;
	}
	
	public void setOnHoverEndAction(Runnable action)
	{
		this.onHoverEndAction = action;
	}
	
	public PVector getPos()
	{
		return this.container == null ? this.pos.copy() : this.pos.copy().add(this.container.getPos());
	}

	public boolean isClicked()
	{
		return isHovered();
	}

	public boolean isHovered()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		PVector pos = getPos();
		return (mX > pos.x && mX <= pos.x + this.width && mY > pos.y
				&& mY <= pos.y + this.height);
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		if (this.visible && isClicked())
		{
			this.clicked = true;
			if (this.onClickListener != null)
			{
				this.onClickListener.onClick(this);
			}
		}
		else if (this.clicked)
		{
			this.clicked = false;
		}
		return this.clicked;
	}

	@Override
	public void onMouseDragged()
	{
	}

	@Override
	public void onMouseReleased(int mouseButton)
	{
	}

	@Override
	public boolean onKeyPressed(int keyCode, char key)
	{
		return this.clicked;
	}

	@Override
	public boolean onKeyReleased(int keyCode, char key)
	{
		return this.clicked;
	}

	public int getWidth()
	{
		return this.width;
	}
	
	public int getViewWidth()
	{
		return this.viewWidth;
	}

	public int getHeight()
	{
		return this.height;
	}
	
	public int getViewHeight()
	{
		return this.viewHeight;
	}
	
	public int getMarginX()
	{
		return this.margin.getSpacingX();
	}
	
	public int getMarginY()
	{
		return this.margin.getSpacingY();
	}
	
	@Override
	public void onWindowResize(int widthOld, int heightOld, int widthNew, int heightNew) {
		this.pos.x = this.pos.x * widthNew / widthOld;
		this.pos.y = this.pos.y * heightNew / heightOld;
	}

	public void draw()
	{
		draw(this.canvas);
	}
	
	public void draw(PGraphics canvas)
	{
		this.canvas = canvas;
		calcPosX();
		calcPosY();
		boolean previouslyHovered = this.hovered;
		this.hovered = isHovered();
		if (this.hovered)
		{
			if (this.onHoverListener != null)
			{
				this.onHoverListener.onHover(this);
			}
			if (this.onHoverAction != null)
			{
				this.onHoverAction.run();
			}
		}
		else if (previouslyHovered && this.onHoverEndAction != null)
		{
			this.onHoverEndAction.run();
		}
	}
}
