package ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import drehsystem3d.Global;
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
	
	public enum Positioning
	{
		RELATIVE,
		ABSOLUTE
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
	protected boolean previouslyHovered = false;
	
	protected PVector pos = new PVector(0, 0, 0);
	protected int viewWidth = 0, viewHeight = 0;
	protected int width = 0, height = 0;
	protected Positioning positioning = Positioning.ABSOLUTE;
	
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
		this.width = w;
		this.viewWidth = w;
		this.height = h;
		this.viewHeight = h;
		
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
		this.width = w;
		this.viewWidth = w;
		this.height = h;
		this.viewHeight = h;
		
		setup();
	}
	
	private void setup()
	{
		this.canvas = this.context.getGraphics();
		View.registerInstance(this);
	}
	
	public void setPositioning(Positioning positioning)
	{
		this.positioning = positioning;
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
	
	public void setContainer(View v)
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
		private static final int TOP = 0;
		private static final int RIGHT = 1;
		private static final int BOTTOM = 2;
		private static final int LEFT = 3;
		
		private int[] spacing = new int[4];
		
		public void set(int spacing)
		{
			for (int i = TOP; i <= LEFT; i++)
			{
				this.spacing[i] = spacing;
			}
		}
		
		public void set(int x, int y)
		{
			this.setX(x);
			this.setY(y);
		}
		
		public void setTop(int value)
		{
			this.spacing[TOP] = value;
		}
		
		public void setRight(int value)
		{
			this.spacing[RIGHT] = value;
		}
		
		public void setBottom(int value)
		{
			this.spacing[BOTTOM] = value;
		}
		
		public void setLeft(int value)
		{
			this.spacing[LEFT] = value;
		}
		
		public void setX(int x)
		{
			this.spacing[RIGHT] = x;
			this.spacing[LEFT] = x;
		}
		
		public void setY(int y)
		{
			this.spacing[TOP] = y;
			this.spacing[BOTTOM] = y;
		}
		
		public int getTop()
		{
			return this.spacing[TOP];
		}
		
		public int getRight()
		{
			return this.spacing[RIGHT];
		}
		
		public int getBottom()
		{
			return this.spacing[BOTTOM];
		}
		
		public int getLeft()
		{
			return this.spacing[LEFT];
		}
		
		public int getX()
		{
			return getRight() + getLeft();
		}

		public int getY()
		{
			return getTop() + getBottom();
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
	
	public void setPos(float x, float y)
	{
		this.pos = new PVector(x, y);
	}

	public void setPos(PVector pos)
	{
		this.pos = pos.copy();
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
		setWidth(w);
		setHeight(h);
	}
	
	public void setWidth(int w)
	{
		this.width = w;
		updateViewWidth();
	}

	public void setHeight(int h)
	{
		this.height = h;
		updateViewHeight();
	}

	private void calcPosX()
	{
		int neighborDir = this.neighbor.exists();
		View other = this.neighbor.getNeighbor();
		final Map<View.AlignmentHorizontal, Runnable> actions = new HashMap<>();
		
		switch (neighborDir)
		{
			case Neighbor.RIGHT:
				this.pos.x = other.pos.x - this.viewWidth - 1;
				break;
				
			case Neighbor.LEFT:
				this.pos.x = other.pos.x + other.viewWidth + 1;
				break;
				
			case Neighbor.TOP:
			case Neighbor.BOTTOM:
				actions.put(View.AlignmentHorizontal.CENTER, () -> {this.pos.x = other.pos.x + (other.viewWidth - this.viewWidth) / 2;});
				actions.put(View.AlignmentHorizontal.LEFT, () -> {this.pos.x = other.pos.x;});
				actions.put(View.AlignmentHorizontal.RIGHT, () -> {this.pos.x = other.pos.x + other.viewWidth - this.viewWidth;});
				break;
				
			default:
				actions.put(View.AlignmentHorizontal.CENTER, () -> {this.pos.x = (this.context.width - this.viewWidth) / 2;});
				actions.put(View.AlignmentHorizontal.LEFT, () -> {this.pos.x = 0;});
				actions.put(View.AlignmentHorizontal.RIGHT, () -> {this.pos.x = this.context.width - this.viewWidth;});
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
				this.pos.y = other.pos.y + other.viewHeight + 1;
				break;
				
			case Neighbor.BOTTOM:
				this.pos.y = other.pos.y - this.viewHeight - 1;
				break;
				
			case Neighbor.LEFT:
			case Neighbor.RIGHT:
				actions.put(View.AlignmentVertical.CENTER, () -> {this.pos.y = other.pos.y + (other.viewHeight - this.viewHeight) / 2;});
				actions.put(View.AlignmentVertical.TOP, () -> {this.pos.y = other.pos.y;});
				actions.put(View.AlignmentVertical.BOTTOM, () -> {this.pos.y = other.pos.y + other.viewHeight - this.viewHeight;});
				break;
				
			default:
				actions.put(View.AlignmentVertical.CENTER, () -> {this.pos.y = (this.context.height - this.viewHeight) / 2;});
				actions.put(View.AlignmentVertical.TOP, () -> {this.pos.y = 0;});
				actions.put(View.AlignmentVertical.BOTTOM, () -> {this.pos.y = this.context.height - this.viewHeight;});
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
		this.margin.set(spacing);
		updateViewSize();
	}
	
	public void setMarginTop(int value)
	{
		this.margin.setTop(value);
		updateViewHeight();
	}
	
	public void setMarginRight(int value)
	{
		this.margin.setRight(value);
		updateViewWidth();
	}
	
	public void setMarginBottom(int value)
	{
		this.margin.setBottom(value);
		updateViewHeight();
	}
	
	public void setMarginLeft(int value)
	{
		this.margin.setLeft(value);
		updateViewWidth();
	}
	
	public void setMarginX(int x)
	{
		this.margin.setX(x);
		updateViewWidth();
	}
	
	public void setMarginY(int y)
	{
		this.margin.setY(y);
		updateViewHeight();
	}
	
	public void setMargin(int x, int y)
	{
		this.margin.set(x, y);
		updateViewSize();
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
	
	public PVector getViewPos()
	{
		return this.positioning == Positioning.ABSOLUTE ? getAbsPos() : getRelPos();
	}
	
	public PVector getActualPos()
	{
		return this.positioning == Positioning.ABSOLUTE ? getActualAbsPos() : getActualRelPos();
	}
	
	public PVector getAbsPos()
	{
		return this.container == null ? this.pos.copy() : this.pos.copy().add(this.container.getAbsPos());
	}
	
	public PVector getRelPos()
	{
		return this.pos.copy();
	}
	
	public PVector getActualAbsPos()
	{
		return getAbsPos().add(new PVector(this.getMarginLeft(), this.getMarginTop()));
	}
	
	public PVector getActualRelPos()
	{
		return getRelPos().add(new PVector(this.getMarginLeft(), this.getMarginTop()));
	}
	
	public PVector getRelPos(PVector pos)
	{
		return this.container == null ? pos.copy() : pos.copy().sub(this.container.getAbsPos());
	}

	public boolean isHovered()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		PVector pos = getActualAbsPos();
		return (this.visible && mX >= pos.x && mX <= pos.x + this.width && mY >= pos.y
				&& mY <= pos.y + this.height);
	}

	public View isPressed()
	{
		this.clicked = false;
		return isHovered() ? this : null;
	}
	
	@Override
	public void onMousePressed(int mouseButton)
	{
		this.clicked = true;
		if (this.onClickListener != null)
		{
			Global.logger.log(Level.FINE, "onMousePressed", new Object[] {this.name, this.id});
			this.onClickListener.onClick(this);
		}
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
	public boolean onKeyPressed(int keyCode, char key, boolean repeat)
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
	
	public int getMarginTop()
	{
		return this.margin.getTop();
	}
	
	public int getMarginRight()
	{
		return this.margin.getRight();
	}
	
	public int getMarginBottom()
	{
		return this.margin.getBottom();
	}
	
	public int getMarginLeft()
	{
		return this.margin.getLeft();
	}
	
	public int getMarginX()
	{
		return this.margin.getX();
	}
	
	public int getMarginY()
	{
		return this.margin.getY();
	}
	
	@Override
	public void onWindowResize(int widthOld, int heightOld, int widthNew, int heightNew) {
		this.pos.x = this.pos.x * widthNew / widthOld;
		this.pos.y = this.pos.y * heightNew / heightOld;
	}
	
	private void updateViewWidth()
	{
		this.viewWidth = this.width + this.margin.getX();
	}

	private void updateViewHeight()
	{
		this.viewHeight = this.height + this.margin.getY();
	}
	
	private void updateViewSize()
	{
		this.viewWidth = this.width + this.margin.getX();
		this.viewHeight = this.height + this.margin.getY();
	}
	
	public void onHoverAction()
	{
		Global.logger.log(
			Level.FINER, 
			"View on hover", 
			new Object[] {
					this.name, 
					this.pos, 
					this.width, 
					this.height, 
					this.viewWidth, 
					this.viewHeight
			}
		);
		if (this.onHoverListener != null) this.onHoverListener.onHover(this);
		if (this.onHoverAction != null) this.onHoverAction.run();
	}
	
	public View updateHoverState()
	{
		this.previouslyHovered = this.hovered;
		this.hovered = isHovered();
		if (!this.hovered && this.previouslyHovered)
		{
			if (this.onHoverListener != null) this.onHoverListener.onHoverEnd(this);
			if (this.onHoverEndAction != null) this.onHoverEndAction.run();
		}
		return this.hovered ? this : null;
	}
	
	private void drawViewbox()
	{
		if (Global.SHOW_VIEWBOXES && this.visible)
		{
			PVector pos;
			this.canvas.beginDraw();
			this.canvas.noFill();
			this.canvas.strokeWeight(2);
			
			pos = this.getActualPos();
			this.canvas.stroke(255, 0, 0);
			this.canvas.rect(pos.x, pos.y, this.width, this.height);
			
			pos = this.getViewPos();
			if (this.hovered)
			{
				this.canvas.stroke(255, 255, 0);
				this.canvas.strokeWeight(3);
			}
			else
			{
				this.canvas.stroke(255, 0, 0);
				this.canvas.strokeWeight(2);
			}
			this.canvas.rect(pos.x, pos.y, this.viewWidth, this.viewHeight);
			
			this.canvas.endDraw();
		}
	}
	
	public void update()
	{
		calcPosX();
		calcPosY();
		drawViewbox();
	}
	
	public void draw(PGraphics canvas)
	{
		this.canvas = canvas;
		draw();
	}
	
	public abstract void draw();
}
