package drehsystem3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import drehsystem3d.Listener.InputBoxListener;
import drehsystem3d.Listener.OnClickListener;
import drehsystem3d.Listener.OnItemClickListener;
import drehsystem3d.Listener.TextBoxListener;
import drehsystem3d.Listener.UserInputListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class Drehsystem3d extends PApplet
{

	final static boolean output = true;
	long startTime;
	long ellapsedTime;
	long time = 0;
	boolean pressed = false;
	long lastKeyEvent = 0;
	int currWindowWidth;
	int currWindowHeight;
	TextBoxListener textEditedListener;
	MenuItem menuItem;
	ArrayList<GraphApplet> applets;
	UIHandler uiHandler;
	int tbStartX;
	int tbStartY;
	int tbWidth;
	InputHandler inputHandler;
	ArrayList<Point> points = new ArrayList<>();
	Point pointToAdd;
	int nameCounter = 65;
	PGraphics xySurface, yzSurface, xzSurface;
	int checkBoxY = 140;
	int checkBoxOffset = 30;
	Checkbox cLines;
	Checkbox cVelocity;
	Checkbox cAcceleration;
	Checkbox cOutput;
	Checkbox cPath;
	float size = 20;
	float scale = 0.2f;
	float scaleD = 40;
	long lastTime = 0;
	float speed = 1.0f;
	boolean setup = true;
	boolean inputWindowOpened = false;

	PVector mouseReference = new PVector(0, 0, 0);
	boolean leftButtonPressed = false;
	boolean centerButtonPressed = false;
	boolean rightButtonPressed = false;
	boolean rotation = false;
	boolean zooming = false;
	boolean translation = false;

	CameraController cameraController;

	boolean removePoints = false;
	ArrayList<UserInputListener> userInputListeners;

	HashMap<Integer, Integer[]> objects = new HashMap<>();
	int idCount = 0;
	Integer[] colorCount = { 100, 0, 0 };
	PGraphics detectionCanvas;

	boolean reset = false;
	boolean stopped = false;
	boolean clearPath = false;

	@Override
	public void settings()
	{
		size(1500, 800, P3D);
	}

	@Override
	public void setup()
	{
		Logger.setLogStatus(true);
		Logger.setLogStoreStatus(false);
		this.detectionCanvas = createGraphics(this.width, this.height, P3D);
		this.currWindowWidth = this.width;
		this.currWindowHeight = this.height;
		this.surface.setResizable(true);
		this.tbStartX = 50;
		this.tbStartY = this.height - 450;
		this.tbWidth = 120;
		this.applets = new ArrayList<>();
		this.cameraController = new CameraController(this, new float[] { 0, 0, 0 }, 1,
				new PVector(this.width / 2, this.height / 2, 0));

		this.userInputListeners = new ArrayList<>();
		this.inputHandler = new InputHandler(this);
		this.uiHandler = new UIHandler(this);
		this.userInputListeners.add(this.inputHandler);
		this.userInputListeners.add(this.uiHandler);

		background(0);

		this.xySurface = createGraphics(100, 100, P2D);
		this.xySurface.beginDraw();
		this.xySurface.background(0, 0, 255, 50);
		this.xySurface.endDraw();

		this.yzSurface = createGraphics(100, 100, P2D);
		this.yzSurface.beginDraw();
		this.yzSurface.background(0, 255, 0, 50);
		this.yzSurface.endDraw();

		this.xzSurface = createGraphics(100, 100, P2D);
		this.xzSurface.beginDraw();
		this.xzSurface.background(255, 0, 0, 50);
		this.xzSurface.endDraw();

		// Point point;
		addNewPoint(null, new PVector(0, 0, 0), new PVector(0, 0, 0), 0);
		addNewPoint(getLastPoint(), new PVector(0, -3, 0), new PVector(0, 0, 50), 0);
		addNewPoint(getLastPoint(), new PVector(0, -2, 0), new PVector(400, 0, 0), 0);

		setupUI();

		this.lastTime = millis();
		this.startTime = millis();
		this.ellapsedTime = 0;
		this.stopped = true;
		this.setup = false;
	}

	private void setupUI()
	{
		this.cLines = addCheckBox("lines", true);
		this.cVelocity = addCheckBox("v", true);
		this.cAcceleration = addCheckBox("acc", false);
		this.cOutput = addCheckBox("out", false);
		this.cPath = addCheckBox("path", true);

		this.uiHandler.addUiElement("cLines", this.cLines);
		this.uiHandler.addUiElement("cVelocity", this.cVelocity);
		this.uiHandler.addUiElement("cAcceleration", this.cAcceleration);
		this.uiHandler.addUiElement("cOutput", this.cOutput);
		this.uiHandler.addUiElement("cPath", this.cPath);

		Button bReset, bStart, bClearPath, bAlign;

		bReset = new Button(this, this.tbStartX - 40, this.tbStartY, 120, 50, "Remove All");
		bReset.setMargin(10);
		bReset.setBackground(0);
		bReset.setTextColor(255);
		bReset.setCornerRadius(15);
		bReset.setStrokeWeight(2);
		bReset.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				Drehsystem3d.this.removePoints = true;
			}
		});
		this.uiHandler.addUiElement("bReset", bReset);

		bStart = new Button(this);
		bStart.alignBottom(bReset);
		bStart.setSize(120, 50);
		bStart.setText("Start Pos");
		bStart.setMargin(10);
		bStart.setBackground(0);
		bStart.setTextColor(255);
		bStart.setCornerRadius(15);
		bStart.setStrokeWeight(2);
		bStart.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				Drehsystem3d.this.reset = true;
			}
		});
		this.uiHandler.addUiElement("bStart", bStart);

		bClearPath = new Button(this);
		bClearPath.alignBottom(bStart);
		bClearPath.setSize(120, 50);
		bClearPath.setText("Clear Path");
		bClearPath.setMargin(10);
		bClearPath.setBackground(0);
		bClearPath.setTextColor(255);
		bClearPath.setCornerRadius(15);
		bClearPath.setStrokeWeight(2);
		bClearPath.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				Drehsystem3d.this.clearPath = true;
			}
		});
		this.uiHandler.addUiElement("bClearPath", bClearPath);

		bAlign = new Button(this);
		bAlign.alignBottom(bClearPath);
		bAlign.setSize(120, 50);
		bAlign.setText("Align");
		bAlign.setMargin(10);
		bAlign.setBackground(0);
		bAlign.setTextColor(255);
		bAlign.setCornerRadius(15);
		bAlign.setStrokeWeight(2);
		bAlign.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				Drehsystem3d.this.cameraController.resetCamera();
			}
		});
		bAlign.setId(1);
		this.uiHandler.addUiElement("bAlign", bAlign);
		
		Toast toast = new Toast(this, this, "Welcome!", Toast.DURATION_LONG);
		this.uiHandler.addUiElement("WelcomeToast", toast);
		
		/*TextView testView = new TextView(this, width/2, height/2);
		testView.setBackground(255);
		testView.setTextColor(0);
		testView.setText("Hello World!");
		testView.setPaddingY(20);
		testView.setMargin(20);
		testView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(int id) {
				Logger.log("Hello World clicked!");				
			}
		});
		this.uiHandler.addUiElement("testView", testView);
		
		View view = getDummy("Left");
		view.alignLeft(testView);
		view.setVerticalAlignment(View.AlignmentVertical.BOTTOM);
		
		view = getDummy("Right");
		view.alignRight(testView);
		view.setVerticalAlignment(View.AlignmentVertical.TOP);
		
		view = getDummy("Top");
		view.alignTop(testView);
		view.setHorizontalAlignment(View.AlignmentHorizontal.LEFT);
		
		view = getDummy("Bottom");
		view.alignBottom(testView);
		view.setHorizontalAlignment(View.AlignmentHorizontal.RIGHT);*/
	}
	
	private TextView getDummy(String text)
	{
		TextView textView = new TextView(this, width/2, 10);
		textView.setBackground(255);
		textView.setTextColor(0);
		textView.setText(text);
		textView.setPadding(5);
		textView.setMargin(20);
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(int id) {
				Logger.log(this, "'" + text + "' clicked");		
			}
		});
		this.uiHandler.addUiElement(text + "View", textView);
		return textView;
	}

	public void update()
	{
		handleKeyPressedPermanent();
		if (!this.setup)
		{
			if (this.reset)
			{
				Logger.log(this, "reset update");
			}
			float dTime = (millis() - this.lastTime);

			this.ellapsedTime += dTime * this.speed;

			for (Point p : this.points)
			{
				p.initPos(p.setPos);
			}
			for (Point p : this.points)
			{
				p.update();
			}
			updateGraphApplets();
			this.lastTime = millis();
		}
	}

	private void handleKeyPressedPermanent()
	{
		if (this.keyPressed && (millis() - this.lastKeyEvent > 100)
				&& (this.inputHandler.getLastKeyCode() == 139 || this.inputHandler.getLastKeyCode() == 93
						|| this.inputHandler.getLastKeyCode() == 140 || this.inputHandler.getLastKeyCode() == 47))
		{
			handleKeyPressedEvent(this.inputHandler.getLastKeyCode(), this.inputHandler.getLastKey());
		}
	}

	private void updateGraphApplets()
	{
		for (GraphApplet sa : this.applets)
		{
			for (Point p : this.points)
			{
				if (p.getName().equals(sa.getName()))
				{
					PVector pos = new PVector(p.pos.x, p.pos.y, p.pos.z);
					float v = pos.sub(p.v).mag();
					pos = new PVector(p.pos.x, p.pos.y, p.pos.z);
					float a = pos.sub(p.a).mag();
					sa.addPoint("v", v);
					sa.addPoint("a", a);
				}
			}
		}
	}

	@Override
	public void draw()
	{
		addBufferedPoint();
		handlePathUpdates();
		noLights();
		pushMatrix();
		if (!(this.currWindowWidth == this.width && this.currWindowHeight == this.height))
		{
			handleWindowResizeEvent();
		}

		this.cameraController.calcCameraAdjustment();

		background(0);

		if (this.reset)
		{
			resetToStartPosition();
		}

		setButtonVisibility();
		
		this.uiHandler.draw();

		drawSimulation();

		popMatrix();

		drawTextElements();
		drawMenuItem();
		// if (this.mousePressed)
		// {
		// background(0);
		// translate(0, 0, 0);
		// image(this.detectionCanvas, 0, 0);
		// }
	}
	
	private void setButtonVisibility()
	{
		Button align = (Button) uiHandler.getUiElement("bAlign");
		boolean bAlignVisible = !(
					cameraController.getAngle()[0] == 0 
				&& 	cameraController.getAngle()[1] == 0 
				&& 	cameraController.getPos().x == width/2 
				&&  cameraController.getPos().y == height/2
		);
		align.setVisibility(bAlignVisible);
	}

	private void drawSimulation()
	{
		pushMatrix();
		this.cameraController.adjustCamera(this.getGraphics());

		if (!this.stopped)
		{
			update();
		}

		drawCoordinateSufaces();

		resetDetectionCanvas();
		for (Point p : this.points)
		{
			drawPoint(p);
			updateDetectionCanvas(p);
		}

		drawPointPaths();

		popMatrix();
	}

	private void resetToStartPosition()
	{
		for (Point p : this.points)
		{
			p.moveToStart();
		}
		this.stopped = true;
		this.reset = false;
	}

	private void addBufferedPoint()
	{
		if (this.pointToAdd != null)
		{
			addNewPoint(this.pointToAdd.parent, this.pointToAdd.setPos, this.pointToAdd.setW, this.pointToAdd.setAlpha);
			this.pointToAdd = null;
		}
	}

	private void handlePathUpdates()
	{
		if (this.removePoints)
		{
			this.points = new ArrayList<>();
			this.nameCounter = 65;
			addNewPoint(null, new PVector(0, 0, 0), new PVector(0, 0, 0), 0);
			this.removePoints = false;
		}
		else if (this.clearPath)
		{
			for (Point p : this.points)
			{
				p.clearPath();
			}
			this.clearPath = false;
		}
	}

	private void handleWindowResizeEvent()
	{
		this.cameraController.onWindowResize(this.currWindowWidth, this.currWindowHeight, this.width, this.height);
		this.cameraController.setInitPos(width/2, height/2);
		this.uiHandler.onWindowResize(this.currWindowWidth, this.currWindowHeight, this.width, this.height);
		this.currWindowWidth = this.width;
		this.currWindowHeight = this.height;
		this.detectionCanvas = createGraphics(this.width, this.height, P3D);
		erasePath();
	}

	private void drawCoordinateSufaces()
	{
		stroke(255, 0, 0);
		image(this.xySurface, -50, -50);
		pushMatrix();
		rotateY(HALF_PI);
		image(this.yzSurface, -50, -50);
		popMatrix();

		pushMatrix();
		rotateX(HALF_PI);
		rotateZ(HALF_PI);
		image(this.xzSurface, -50, -50);
		popMatrix();
	}

	private void resetDetectionCanvas()
	{
		this.detectionCanvas.beginDraw();
		this.detectionCanvas.background(0);
		this.detectionCanvas.endDraw();
	}

	private void drawPoint(Point p)
	{
		p.setVisibilityL(this.cLines.isChecked());
		p.setVisibilityV(this.cVelocity.isChecked());
		p.setVisibilityA(this.cAcceleration.isChecked());
		p.draw();
	}

	private void updateDetectionCanvas(Point p)
	{
		Integer[] colorValue = this.objects.get(p.getId());
		this.detectionCanvas.fill(colorValue[0], colorValue[1], colorValue[2]);

		this.detectionCanvas.beginDraw();
		this.cameraController.adjustCamera(this.detectionCanvas);
		this.detectionCanvas.noStroke();
		this.detectionCanvas.pushMatrix();
		this.detectionCanvas.translate(p.pos.x * this.scaleD, p.pos.y * this.scaleD, p.pos.z * this.scaleD);
		this.detectionCanvas.sphere(10);
		this.detectionCanvas.popMatrix();
		this.detectionCanvas.endDraw();
	}

	private void drawPointPaths()
	{
		if (!this.cPath.isChecked() || this.points.size() <= 1)
		{
			return;
		}

		for (Point p : this.points)
		{
			if (p.getPathVisibility())
			{
				int[] c = p.getPathColor();
				stroke(c[0], c[1], c[2]);
				ArrayList<PVector> path = p.getPath();
				if (path != null && path.size() > 1)
				{
					for (int i = 0; i < path.size() - 1; i++)
					{
						PVector lastPos = path.get(i);
						PVector pos = path.get(i + 1);
						line(lastPos.x * this.scaleD, lastPos.y * this.scaleD, lastPos.z * this.scaleD,
								pos.x * this.scaleD, pos.y * this.scaleD, pos.z * this.scaleD);
					}
				}
			}
		}
	}

	private void drawTextElements()
	{
		hint(DISABLE_DEPTH_TEST);
		pushMatrix();
		noLights();
		fill(255);
		stroke(255);
		textSize(20);
		text("X:" + this.mouseX, 40, this.height - 60);
		text("Y:" + this.mouseY, 40, this.height - 40);
		text("Elapsed time:" + (int) (this.ellapsedTime / 1000), 40, this.height - 20);

		textSize(25);
		if (output)
		{
			fill(255);
			stroke(255);

			text("Scale:", 20, 40);
			text("1m/s : " + this.scale + "px\n1m : " + this.scaleD + "px", 120, 40);

			if (this.stopped)
			{
				text("paused", 10, this.height - 100);
			}

			String speedOutput = "Speed: " + "x" + this.speed;
			text(speedOutput, this.width / 2 - textWidth(speedOutput) / 2, this.height - 20);
		}
		popMatrix();
		hint(ENABLE_DEPTH_TEST);
	}

	private void drawMenuItem()
	{
		if (this.menuItem != null)
		{
			hint(DISABLE_DEPTH_TEST);
			this.menuItem.draw();
			hint(ENABLE_DEPTH_TEST);
		}
	}

	@Override
	public void mousePressed()
	{
		Logger.log(this, "Mouse pressed : '" + this.mouseButton + "'");
		boolean itemClicked = false;
		for (UserInputListener l : this.userInputListeners)
		{
			itemClicked = itemClicked || l.onMousePressed(this.mouseButton);
		}
		if (this.menuItem != null)
		{
			itemClicked = itemClicked || this.menuItem.onMousePressed(this.mouseButton);
		}

		if (itemClicked)
		{
			return;
		}
		
		if (this.mouseButton == LEFT)
		{
			this.leftButtonPressed = true;
		}
		else if (this.mouseButton == CENTER)
		{
			this.centerButtonPressed = true;
		}
		else if (this.mouseButton == RIGHT)
		{
			this.rightButtonPressed = true;
			openMenuContextIfObjectIsClicked();
		}

		for (GraphApplet sa : this.applets)
		{
			if (sa.waitingForExit())
			{
				this.cOutput.setChecked(false);
				sa.exited();
			}
			if (this.cOutput.isChecked() && !sa.isVisible() && !sa.exited)
			{
				sa.resume();
			}
			else if (!this.cOutput.isChecked() && sa.isVisible())
			{
				sa.pause();
			}
		}

		if (tranlationConditionsFullfilled() && !this.translation)
		{
			this.cameraController.setNewAdjustment(CameraController.Adjustment.TRANSLATION);
			this.translation = true;
		}
		else if (rotationConditionsFullfilled() && !this.rotation)
		{
			this.cameraController.setNewAdjustment(CameraController.Adjustment.ROTATION);
			this.rotation = true;
		}
		else if (zoomingConditionFillfilled() && !this.zooming)
		{
			this.cameraController.setNewAdjustment(CameraController.Adjustment.ZOOM);
			this.zooming = true;
		}
	}

	private boolean tranlationConditionsFullfilled()
	{
		return this.leftButtonPressed;
	}

	private boolean rotationConditionsFullfilled()
	{
		return (this.centerButtonPressed && this.rightButtonPressed && !this.translation);
	}

	private boolean zoomingConditionFillfilled()
	{
		return (this.centerButtonPressed && !this.translation && !this.rotation);
	}

	private void openMenuContextIfObjectIsClicked()
	{
		int objectId = getPressedObjectId();
		if (objectId != -1)
		{
			for (Point p : this.points)
			{
				if (p.getId() == objectId)
				{
					Logger.log(this, "point " + p.getName() + " pressed");
					openMenuContext(p);
				}
			}
		}
	}

	private void openMenuContext(Point point)
	{
		final String[] possibleValues = new String[] { "Add Point", "Change value", "Graph", "Hide Path", "Draw Path",
				"Remove", "Change Color" };

		final String[] values;
		if (point.parent != null)
		{
			values = new String[] { possibleValues[0], possibleValues[1], possibleValues[2],
					(point.visibilityPath ? possibleValues[3] : possibleValues[4]), possibleValues[5],
					possibleValues[6] };
		}
		else
		{
			values = new String[] { possibleValues[0] };
		}

		PVector screenPos = getScreenPos(point);

		this.menuItem = new MenuItem(this, screenPos.x, screenPos.y, "Title", values);
		this.menuItem.setOnItemClickListener(new PointMenuItemClickListener(point, possibleValues));
	}

	private PVector getScreenPos(Point point)
	{
		PVector pos = new PVector(0, 0, 0);
		PVector scaledPos = new PVector(point.pos.x, point.pos.y, point.pos.z);
		scaledPos = scaledPos.mult(this.scaleD);

		pushMatrix();
		this.cameraController.adjustCamera(this.getGraphics());
		translate(scaledPos.x, scaledPos.y, scaledPos.z);
		pos.x = screenX(0, 0, 0);
		pos.y = screenY(0, 0, 0);
		popMatrix();
		return pos;
	}

	public class PointMenuItemClickListener implements OnItemClickListener
	{
		private Point point;
		private String[] possibleValues;

		public PointMenuItemClickListener(Point point, String[] possibleValues)
		{
			this.point = point;
			this.possibleValues = possibleValues;
		}

		@Override
		public void onItemClick(int itemIdx, String item)
		{
			final String[] inputBoxValues = new String[] { "x", "y", "z", "ωx", "ωy", "ωz", "α" };
			final float[] maxLimits = new float[] { 15, 15, 100, 400, 400, 400, 200 };
			final float[] minLimits = new float[] { -Drehsystem3d.this.width / 2, -Drehsystem3d.this.height / 2, -100,
					-400, -400, -400, -200 };

			// Add point.
			if (item.equals(this.possibleValues[0]))
			{
				openInputWindow("Input", inputBoxValues, getPointValuesAsString(), InputTypes.FLOAT, maxLimits,
						minLimits, new InputBoxListener()
						{
							@Override
							public void finishedEditing(String... data)
							{
								Drehsystem3d.this.inputWindowOpened = false;
								addPoint(PointMenuItemClickListener.this.point, data);
							}

							@Override
							public void onExit()
							{
								Drehsystem3d.this.inputWindowOpened = false;
							}
						});
			}
			else if (item.equals(this.possibleValues[1]))
			{
				openInputWindow("Change " + this.point.getName(), inputBoxValues, getPointValuesAsString(this.point),
						InputTypes.FLOAT, maxLimits, minLimits, new InputBoxListener()
						{
							@Override
							public void finishedEditing(String... data)
							{
								Drehsystem3d.this.inputWindowOpened = false;
								changePoint(PointMenuItemClickListener.this.point, data);
							}

							@Override
							public void onExit()
							{
								Drehsystem3d.this.inputWindowOpened = false;
							}
						});
			}
			else if (item.equals(this.possibleValues[2]))
			{
				openGraphContext(this.point);
			}
			else if (item.equals(this.possibleValues[3]) || item.equals(this.possibleValues[4]))
			{
				changePathVisibility(this.point);
			}
			else if (item.equals(this.possibleValues[5]))
			{
				Drehsystem3d.this.points.remove(this.point);
			}

			else if (item.equals(this.possibleValues[6]))
			{
				openColorSelectionInput();
			}
		}

		private void changePathVisibility(Point point)
		{
			boolean newVisiblityPath = !point.visibilityPath;
			point.drawPath(newVisiblityPath);
		}

		private void openColorSelectionInput()
		{
			String[] standardValues = new String[3];
			int[] c = this.point.getPathColor();
			for (int i = 0; i < standardValues.length; i++)
			{
				standardValues[i] = Integer.toString(c[i]);
			}
			openColorInputWindow("Pathcolor " + this.point.getName(), standardValues,
					new InputBoxListener()
					{
						@Override
						public void finishedEditing(String... data)
						{
							Drehsystem3d.this.inputWindowOpened = false;
							int r = data[0] == "" ? 0 : Integer.parseInt(data[0]);
							int g = data[1] == "" ? 0 : Integer.parseInt(data[1]);
							int b = data[2] == "" ? 0 : Integer.parseInt(data[2]);
							PointMenuItemClickListener.this.point.setPathColor(new int[] { r, g, b });
							PointMenuItemClickListener.this.point.drawPath();
						}

						@Override
						public void onExit()
						{
							Drehsystem3d.this.inputWindowOpened = false;
						}
					});
		}

		private void openGraphContext(Point point)
		{
			String name = point.getName();
			boolean graphAlreadyExists = false;
			for (GraphApplet a : Drehsystem3d.this.applets)
			{
				if (a.getName().equals(name))
				{
					graphAlreadyExists = true;
					break;
				}
			}
			if (!graphAlreadyExists)
			{
				GraphApplet sa = new GraphApplet(Drehsystem3d.this, name);
				sa.resume();
				sa.createDataSet("v", 0, 0, 255);
				sa.createDataSet("a", 255, 0, 0);
				Drehsystem3d.this.cOutput.setChecked(true);
				Drehsystem3d.this.applets.add(sa);
			}
		};
	}

	private void openInputWindow(String name, String[] values, String[] standardValues, int inputType,
			float[] maxLimits, float[] minLimits, InputBoxListener listener)
	{
		if (!this.inputWindowOpened)
		{
			InputBox ib = new InputBox(name, values, standardValues);
			ib.setInputType(inputType);
			ib.setMaxLimits(maxLimits);
			ib.setMinLimits(minLimits);
			ib.setOnEditingFinishedListener(listener);
			ib.run();
			Drehsystem3d.this.inputWindowOpened = true;
		}
	}
	
	private void openColorInputWindow(String name, String[] standardValues, InputBoxListener listener)
	{
		if (!this.inputWindowOpened)
		{
			String[] values = new String[] { "r", "g", "b" };
			float[] maxLimits = new float[] { 255, 255, 255 };
			float[] minLimits = new float[] { 0, 0, 0 };
			ColorInputBox ib = new ColorInputBox(name, values, standardValues);
			ib.setInputType(InputTypes.INTEGER);
			ib.setMaxLimits(maxLimits);
			ib.setMinLimits(minLimits);
			ib.setOnEditingFinishedListener(listener);
			ib.run();
			Drehsystem3d.this.inputWindowOpened = true;
		}
	}

	private String[] getPointValuesAsString()
	{
		String[] values = new String[7];
		for (int i = 0; i < values.length; i++)
		{
			values[i] = "0.0";
		}
		return values;
	}

	private String[] getPointValuesAsString(Point point)
	{
		if (point == null)
		{
			return null;
		}
		return new String[] { Float.toString(point.setPos.x), Float.toString(point.setPos.y * -1),
				Float.toString(point.setPos.z), Float.toString(point.setW.x), Float.toString(point.setW.y),
				Float.toString(point.setW.z), Float.toString(point.setAlpha) };
	}

	private boolean addPoint(Point parent, String... data)
	{
		Drehsystem3d.this.pointToAdd = new Point(Drehsystem3d.this, Drehsystem3d.this.idCount, parent,
				new PVector(0, 0, 0), new PVector(0, 0, 0), 0);
		return changePoint(Drehsystem3d.this.pointToAdd, data);
	}

	private boolean changePoint(Point point, String... data)
	{
		if (data.length != 7)
		{
			return false;
		}

		float x = getValueOrZero(data[0]);
		float y = getValueOrZero(data[1]) * -1;
		float z = getValueOrZero(data[2]);
		float wx = getValueOrZero(data[3]);
		float wy = getValueOrZero(data[4]);
		float wz = getValueOrZero(data[5]);
		float alpha = getValueOrZero(data[6]);

		if (x == 0 && y == 0 && z == 0)
		{
			Drehsystem3d.this.pointToAdd = null;
			return false;
		}
		Drehsystem3d.this.reset = true;
		resetPoints();
		point.setPos(new PVector(x, y, z));
		point.setW(new PVector(wx, wy, wz));
		point.setAlpha(alpha);
		return true;
	}

	private void resetPoints()
	{
		for (Point p : Drehsystem3d.this.points)
		{
			p.reset();
		}
	}

	private float getValueOrZero(String s)
	{
		return s == "" ? 0.0f : Float.parseFloat(s);
	}

	private int getPressedObjectId()
	{
		this.detectionCanvas.loadPixels();
		int objectId = -1;
		Iterator<Map.Entry<Integer, Integer[]>> it = this.objects.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, Integer[]> pair = it.next();
			Integer[] colorValue = pair.getValue();
			int c = this.detectionCanvas.pixels[this.mouseX + this.mouseY * this.width];
			if (color(colorValue[0], colorValue[1], colorValue[2]) == c)
			{
				objectId = pair.getKey();
				break;
			}
		}
		return objectId;
	}

	@Override
	public void mouseReleased()
	{
		for (UserInputListener l : this.userInputListeners)
		{
			l.onMouseReleased(this.mouseButton);
		}
		if (this.mouseButton == LEFT)
		{
			this.leftButtonPressed = false;
		}
		else if (this.mouseButton == CENTER)
		{
			this.centerButtonPressed = false;
		}
		else if (this.mouseButton == RIGHT)
		{
			this.rightButtonPressed = false;
		}

		if (this.rotation || this.translation || this.zooming)
		{
			this.cameraController.removeAdjustment();
			this.rotation = false;
			this.translation = false;
			this.zooming = false;
		}

		if (zoomingConditionFillfilled())
		{
			this.cameraController.setNewAdjustment(CameraController.Adjustment.ZOOM);
			this.zooming = true;
		}
	}

	@Override
	public void mouseDragged()
	{
		this.inputHandler.onMouseDragged();
		this.uiHandler.onMouseDragged();
	}

	@Override
	public void keyPressed()
	{
		Logger.log(this, "Key was pressed ('" + this.key + "', " + this.keyCode + ")");
		boolean uiElementClicked = this.uiHandler.onKeyPressed(this.keyCode, this.key);
		if (!uiElementClicked)
		{
			handleKeyPressedEvent(this.keyCode, this.key);
		}
	}

	public void handleKeyPressedEvent(int pressedKeyCode, char pressedKey)
	{
		for (UserInputListener l : this.userInputListeners)
		{
			l.onKeyPressed(this.keyCode, this.key);
		}
		this.lastKeyEvent = millis();
		switch (pressedKeyCode)
		{
			case 139:
			case 93:
				if (this.inputHandler.isKeyPressed(16) && this.inputHandler.isKeyPressed(17))
				{
					this.scale += 0.2f;
					updateDrawScale();
				}
				else if (this.inputHandler.isKeyPressed(17))
				{
					this.scaleD++;
					updateDrawScaleD();
				}
				else
				{
					if ((Math.abs(this.speed) >= 0 && Math.abs(this.speed) < 1))
					{
						this.speed = this.speed + 0.5f;
					}
					else
					{
						this.speed++;
					}
					updateDrawSpeed();
				}
				break;

			case 140:
			case 47:
				if (this.inputHandler.isKeyPressed(16) && this.inputHandler.isKeyPressed(17))
				{
					if (this.scale > 0)
					{
						if (this.scale <= 0.2f)
						{
							this.scale -= 0.1f;
						}
						else
						{
							this.scale -= 0.2f;
						}
						updateDrawScale();
					}
				}
				else if (this.inputHandler.isKeyPressed(17))
				{
					if (this.scaleD > 0)
					{
						this.scaleD--;
						updateDrawScaleD();
					}
				}
				else
				{
					if ((Math.abs(this.speed) >= 0 && Math.abs(this.speed) < 1))
					{
						this.speed = this.speed - 0.5f;
					}
					else
					{
						this.speed--;
					}
					updateDrawSpeed();
				}
				break;
		}
		switch (pressedKey)
		{
			case '1':
				this.speed = 1;
				updateDrawSpeed();
				break;

			case '0':
				this.speed = 1;
				updateDrawSpeed();
				break;

			case 'p':
				this.cPath.setChecked(!this.cPath.isChecked());
				break;

			case 'o':
				this.cOutput.setChecked(!this.cOutput.isChecked());
				break;

			case 'v':
				this.cVelocity.setChecked(!this.cVelocity.isChecked());
				break;

			case 'a':
				this.cAcceleration.setChecked(!this.cAcceleration.isChecked());
				break;

			case ' ':
				stopOrResume();
				this.pressed = true;
				break;
		}
	}

	@Override
	public void keyReleased()
	{
		boolean uiElementClicked = this.uiHandler.onKeyReleased(this.keyCode, this.key);

		if (!uiElementClicked)
		{
			handleKeyReleasedEvent(this.keyCode, this.key);
		}
	}

	public void handleKeyReleasedEvent(int pressedKeyCode, char pressedKey)
	{
		for (UserInputListener l : this.userInputListeners)
		{
			l.onKeyReleased(this.keyCode, this.key);
		}
	}

	public void updateDrawSpeed()
	{
		for (Point p : this.points)
		{
			p.setDrawSpeed(this.speed);
		}
	}

	public void updateDrawScale()
	{
		for (Point p : this.points)
		{
			p.setScale(this.scale);
		}
	}

	public void updateDrawScaleD()
	{
		for (Point p : this.points)
		{
			p.setScaleD(this.scaleD);
		}
	}

	public void stopOrResume()
	{
		this.stopped = !this.stopped;
		if (this.stopped)
		{
			this.ellapsedTime += (millis() - this.lastTime) * this.speed;
		}
		this.lastTime = millis();
		this.startTime = millis();
		for (Point p : this.points)
		{
			if (this.stopped)
			{
				p.stopTime();
			}
			else
			{
				p.startTime();
			}

		}
	}

	public void erasePath()
	{
	}

	public Point getPoint(int idx)
	{
		if (idx < 0 || idx > this.points.size())
		{
			return null;
		}
		return this.points.get(idx);
	}

	public Point getLastPoint()
	{
		if (this.points.size() == 0)
		{
			return null;
		}
		return this.points.get(this.points.size() - 1);
	}

	public Point getPreviousPoint(Point p)
	{
		if (this.points.size() == 0)
		{
			return null;
		}
		for (int i = 0; i < this.points.size(); i++)
		{
			if (this.points.get(i) == p)
			{
				return getPoint(i - 1);
			}
		}
		return null;
	}

	public Point getNextPoint(Point p)
	{
		if (this.points.size() == 0)
		{
			return null;
		}
		for (int i = 0; i < this.points.size(); i++)
		{
			if (this.points.get(i) == p)
			{
				return getPoint(i + 1);
			}
		}
		return null;
	}

	public Point addNewPoint(Point parent, float a, float[] angle, PVector w, float alpha)
	{
		this.points.add(
				new Point(this, this.idCount, "" + PApplet.parseChar(this.nameCounter++), parent, a, angle, w, alpha));
		Point point = this.points.get(this.points.size() - 1);
		point.setScale(this.scale);
		point.setScaleD(this.scaleD);
		point.setDrawSpeed(this.speed);
		this.objects.put(this.idCount++, this.colorCount);
		this.colorCount = new Integer[] { this.colorCount[0], this.colorCount[1], this.colorCount[2] };
		this.colorCount[0] += 1;
		if (this.colorCount[0] > 255)
		{
			this.colorCount[0] = 0;
			this.colorCount[1] += 1;
		}
		if (this.colorCount[1] > 255)
		{
			this.colorCount[1] = 0;
			this.colorCount[2] += 1;
		}
		if (this.nameCounter > 91)
		{
			this.nameCounter = 65;
		}
		return point;
	}

	public Point addNewPoint(Point parent, PVector pos, PVector w, float alpha)
	{
		this.points
				.add(new Point(this, this.idCount, "" + PApplet.parseChar(this.nameCounter++), parent, pos, w, alpha));
		Point point = this.points.get(this.points.size() - 1);
		point.setScale(this.scale);
		point.setScaleD(this.scaleD);
		point.setDrawSpeed(this.speed);
		this.objects.put(this.idCount++, this.colorCount);
		if (parent != null)
		{
			parent.addChild(point);
		}
		this.colorCount = new Integer[] { this.colorCount[0], this.colorCount[1], this.colorCount[2] };
		this.colorCount[0] += 1;
		if (this.colorCount[0] > 255)
		{
			this.colorCount[0] = 0;
			this.colorCount[1] += 1;
		}
		if (this.colorCount[1] > 255)
		{
			this.colorCount[1] = 0;
			this.colorCount[2] += 1;
		}
		if (this.nameCounter >= 91)
		{
			this.nameCounter = 65;
		}
		return point;
	}

	public Checkbox addCheckBox(String title)
	{
		return addCheckBox(title, false, (ArrayList<Checkbox>) null);
	}

	public Checkbox addCheckBox(String title, boolean checked)
	{
		return addCheckBox(title, checked, (ArrayList<Checkbox>) null);
	}

	public Checkbox addCheckBox(String title, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<>();
		group.add(member);
		return addCheckBox(title, false, group);
	}

	public Checkbox addCheckBox(String title, ArrayList<Checkbox> group)
	{
		return addCheckBox(title, false, group);
	}

	public Checkbox addCheckBox(String title, boolean checked, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<>();
		group.add(member);
		return addCheckBox(title, checked, group);
	}

	public Checkbox addCheckBox(String title, boolean checked, ArrayList<Checkbox> group)
	{
		Checkbox c = new Checkbox(this, 20, this.checkBoxY, 20, title, group);
		c.setChecked(checked);
		// this.checkboxes.add(c);
		this.checkBoxY += this.checkBoxOffset;
		return c;
	}

	static public void main(String[] passedArgs)
	{
		String[] appletArgs = new String[] { "drehsystem3d.Drehsystem3d" };
		if (passedArgs != null)
		{
			PApplet.main(concat(appletArgs, passedArgs));
		}
		else
		{
			PApplet.main(appletArgs);
		}
	}
}
