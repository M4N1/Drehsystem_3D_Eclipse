package drehsystem3d;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.Iterator;
import java.util.Map;

import drehsystem3d.Listener.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Drehsystem3d extends PApplet
{

	final static boolean output = true;
	long startTime;
	long time = 0;
	boolean pressed = false;
	int min = 0;
	char lastPressedKey = ' ';
	int lastPressedKeyCode = -1;
	long lastKeyEvent = 0;
	int currWindowWidth;
	int currWindowHeight;
	TextBoxListener textEditedListener;
	TextView tvW;
	MenuItem menuItem;
	ArrayList<GraphApplet> applets;
	ArrayList<TextView> textviews;
	ArrayList<TextBox> textboxes;
	ArrayList<Button> buttons;
	int tbStartX;
	int tbStartY;
	int tbWidth;
	ArrayList<Character> keys;
	ArrayList<Integer> keyCodes;
	ArrayList<Integer> mouseButtons;
	ArrayList<Point> points = new ArrayList<Point>();
	Point pointToAdd;
	int nameCounter = 65;
	PGraphics xySurface, yzSurface, xzSurface;
	ArrayList<Checkbox> checkboxes;
	TextBox tbW;
	Button bReset, bStart, bClearPath, bAlign;
	int checkBoxY = 140;
	int checkBoxOffset = 30;
	Checkbox cLines;
	Checkbox cVelocity;
	Checkbox cAcceleration;
	Checkbox cOutput;
	Checkbox cPath;
	PVector[] lastPos;
	PVector[] v;
	float size = 20;
	float scale = 0.2f;
	float scaleD = 40;
	PVector[] w = { new PVector(0, 0, 0), new PVector(0, 0, 150), new PVector(0, 0, 100) };
	float[] phi = { 0, 0, 0 };
	float[] A = { 0, 2, 5 };
	float[] a = { 0, 0, 0 };
	long lastTime = 0;
	float startPhi = 0;
	float speed = 1.0f;
	boolean setup = true;
	boolean inputWindowOpened = false;
	PVector mouseReference = new PVector(0, 0, 0);
	boolean centerButtonPressed = false;
	boolean rightButtonPressed = false;
	boolean rotation = false;
	boolean zooming = false;
	boolean translation = false;
	float[] currentAngle = new float[] { 0, 0, 0 };
	float[] angle = new float[] { 0, 0, 0 };
	float[] lastSetAngle = new float[] { 0, 0, 0 };
	float zoom = 1;
	float setZoom = 1;
	PVector pos;
	PVector lastSetPos;
	boolean removePoints = false;

	HashMap<Integer, Integer[]> objects = new HashMap<Integer, Integer[]>();
	int idCount = 0;
	Integer[] colorCount = { 100, 0, 0 };
	PGraphics detectionCanvas;

	boolean reset = false;
	boolean stopped = false;
	boolean clearPath = false;

	Toast toast;

	public void settings()
	{
		size(1500, 800, P3D);
	}

	public void setup()
	{

		pos = new PVector(width / 2, height / 2, 0);
		lastSetPos = new PVector(width / 2, height / 2, 0);
		detectionCanvas = createGraphics(width, height, P3D);
		this.currWindowWidth = this.width;
		this.currWindowHeight = this.height;
		surface.setResizable(true);
		tbStartX = 50;
		tbStartY = height - 450;
		tbWidth = 120;
		applets = new ArrayList<GraphApplet>();
		buttons = new ArrayList<Button>();
		textviews = new ArrayList<TextView>();
		textboxes = new ArrayList<TextBox>();
		checkboxes = new ArrayList<Checkbox>();
		keys = new ArrayList<Character>();
		keyCodes = new ArrayList<Integer>();
		mouseButtons = new ArrayList<Integer>();

		if (!output)
			min = 1;
		background(0);

		cLines = addCheckBox("lines", true);
		cVelocity = addCheckBox("v", true);
		cAcceleration = addCheckBox("acc", false);
		cOutput = addCheckBox("out", false);
		cPath = addCheckBox("path", true);

		xySurface = createGraphics(100, 100, P2D);
		xySurface.beginDraw();
		xySurface.background(0, 0, 255, 50);
		xySurface.endDraw();

		yzSurface = createGraphics(100, 100, P2D);
		yzSurface.beginDraw();
		yzSurface.background(0, 255, 0, 50);
		yzSurface.endDraw();

		xzSurface = createGraphics(100, 100, P2D);
		xzSurface.beginDraw();
		xzSurface.background(255, 0, 0, 50);
		xzSurface.endDraw();

		float yOff = 0;
		Point point;
		// addNewPoint(null, new PVector(0, 0, 0), new PVector(0, 0, 0), 0);
		// addNewPoint(getLastPoint(), new PVector(0, -5, 0), new PVector(0, 0,
		// -100), 0);
		// point = addNewPoint(getLastPoint(), new PVector(0, -4, 0), new
		// PVector(100, 0, 300), 0);
		// getLastPoint().setPathColor(new int[]{255,255,0});
		// getLastPoint().drawPath();
		// addNewPoint(point, new PVector(0, 0, 4), new PVector(0, 0, 0), 0);
		// getLastPoint().setPathColor(new int[]{255,0,255});
		// getLastPoint().drawPath();
		// addNewPoint(point, new PVector(0, 0, -4), new PVector(0, 0, 0), 0);
		// getLastPoint().setPathColor(new int[]{0,255,255});
		// getLastPoint().drawPath();
		addNewPoint(null, new PVector(0, 0, 0), new PVector(0, 0, 0), 0);
		addNewPoint(getLastPoint(), new PVector(0, -5, 0), new PVector(0, 0, 0), 0);
		point = addNewPoint(getLastPoint(), new PVector(0, -4, 0), new PVector(100, 100, 100), 0);
		addNewPoint(getLastPoint(), new PVector(0, -4, 0), new PVector(-100, -100, -100), 0);
		// getLastPoint().setPathColor(new int[]{255,255,0});
		// getLastPoint().drawPath();
		// addNewPoint(point, new PVector(0, 0, 4), new PVector(0, 0, 0), 0);
		// getLastPoint().setPathColor(new int[]{255,0,255});
		// getLastPoint().drawPath();
		// addNewPoint(point, new PVector(0, 0, -4), new PVector(0, 0, 0), 0);
		// getLastPoint().setPathColor(new int[]{0,255,255});
		// getLastPoint().drawPath();
		// addNewPoint(point, new PVector(-4, 0, 0), new PVector(0, 0, 100), 0);

		this.bReset = new Button(this, this.tbStartX - 40, this.tbStartY + yOff, 120, 50, "Remove All");
		this.bReset.setBackground(0);
		this.bReset.setTextColor(255);
		this.bReset.setCornerRadius(15);
		this.bReset.setTextAlignment(15);
		this.bReset.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				removePoints = true;
			}
		});
		buttons.add(this.bReset);
		yOff += 70;

		this.bStart = new Button(this, this.tbStartX - 40, this.tbStartY + yOff, 120, 50, "Start Pos");
		this.bStart.setBackground(0);
		this.bStart.setTextColor(255);
		this.bStart.setCornerRadius(15);
		this.bStart.setTextAlignment(15);
		this.bStart.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				reset = true;
			}
		});
		buttons.add(this.bStart);
		yOff += 70;

		this.bClearPath = new Button(this, this.tbStartX - 40, this.tbStartY + yOff, 120, 50, "Clear Path");
		this.bClearPath.setBackground(0);
		this.bClearPath.setTextColor(255);
		this.bClearPath.setCornerRadius(15);
		this.bClearPath.setTextAlignment(15);
		this.bClearPath.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				clearPath = true;
			}
		});
		buttons.add(this.bClearPath);
		yOff += 70;

		this.bAlign = new Button(this, this.tbStartX - 40, this.tbStartY + yOff, 120, 50, "Align");
		this.bAlign.setBackground(0);
		this.bAlign.setTextColor(255);
		this.bAlign.setCornerRadius(15);
		this.bAlign.setTextAlignment(15);
		this.bAlign.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(int id)
			{
				angle = new float[] { 0, 0, 0 };
				lastSetAngle = new float[] { 0, 0, 0 };
				pos = new PVector(width / 2, height / 2, 0);
				lastSetPos = new PVector(width / 2, height / 2, 0);
				zoom = 1;
				setZoom = 1;
			}
		});
		this.bAlign.setId(1);
		buttons.add(this.bAlign);

		// angle = new float[]{0, 0, 0};
		// lastSetAngle = new float[]{0, 0, 0};
		println("\n");
		for (Point p : points)
		{
			println(p.name);
			println(p.pos + "\n");
		}

		lastTime = millis();
		startTime = millis();
		stopped = true;
		setup = false;

		toast = new Toast(this, this, "Welcome!", Toast.DURATION_LONG);

		// sa = new SecondApplet();
		// sa.pause();
		// sa.createDataSet("v", 0, 0, 255);
		// sa.createDataSet("a", 255, 0, 0);
	}

	public void update()
	{
		if (!this.setup)
		{
			if (this.reset)
				println("reset update");
			for (Point p : points)
			{
				p.update();
			}
			for (GraphApplet sa : applets)
			{
				for (Point p : points)
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
	}

	public void draw()
	{
		if (keyPressed && (millis() - lastKeyEvent > 100) && (lastPressedKeyCode == 139 || lastPressedKeyCode == 93
				|| lastPressedKeyCode == 140 || lastPressedKeyCode == 47))
		{
			handleKeyPressedEvent(lastPressedKeyCode, lastPressedKey);
		}
		if (this.pointToAdd != null)
		{
			// Point addNewPoint(Point parent, PVector pos, PVector w, float
			// alpha)
			addNewPoint(this.pointToAdd.parent, this.pointToAdd.setPos, this.pointToAdd.setW, this.pointToAdd.setAlpha);
			this.pointToAdd = null;
		}
		if (removePoints)
		{
			points = new ArrayList<Point>();
			this.nameCounter = 65;
			addNewPoint(null, new PVector(0, 0, 0), new PVector(0, 0, 0), 0);
			removePoints = false;
		} else if (clearPath)
		{
			for (Point p : points)
			{
				p.clearPath();
			}
			clearPath = false;
		}
		noLights();
		pushMatrix();
		if (rotation)
		{
			float maxAngle = PI;
			angle[0] = map((float) (mouseX - mouseReference.x), -width, width, -maxAngle, maxAngle) * 2
					+ lastSetAngle[0]; // mouseReference.x
			angle[1] = map((float) (mouseY - mouseReference.y), -height, height, maxAngle, -maxAngle) * 2
					+ lastSetAngle[1]; // mouseReference.y
			if (angle[0] < -PI)
				angle[0] += TWO_PI;
			else if (angle[0] > PI)
				angle[0] -= TWO_PI;
			if (angle[1] < -PI)
				angle[1] += TWO_PI;
			else if (angle[1] > PI)
				angle[1] -= TWO_PI;
			println("rotation y:" + angle[0]);
			println("rotation x:" + angle[1]);
			// if (angle[0] < -PI || angle[0] > PI) angle[0] *= -1;
			// if (angle[1] < -PI || angle[1] > PI) angle[1] *= -1;
			// println("angle x:" + angle[1]);
			// println("angle y:" + angle[0]);
			// println("last angle x:" + lastSetAngle[1]);
			// println("last angle y:" + lastSetAngle[0]);
		} else if (translation)
		{
			pos = new PVector(lastSetPos.x + (mouseX - mouseReference.x), lastSetPos.y + (mouseY - mouseReference.y),
					0);
		}
		boolean windowResized = !(this.currWindowWidth == this.width && this.currWindowHeight == this.height);
		if (windowResized)
		{
			toast.windowResized(this.currWindowWidth, this.currWindowHeight);
			this.currWindowWidth = this.width;
			this.currWindowHeight = this.height;
			this.pos = new PVector(width / 2, height / 2, 0);
			this.lastSetPos = new PVector(width / 2, height / 2, 0);
			detectionCanvas = createGraphics(width, height, P3D);
			erasePath();
		}
		background(0);
		detectionCanvas.beginDraw();
		detectionCanvas.background(0);
		detectionCanvas.endDraw();
		if (this.reset)
		{
			// angle = new float[]{0, 0, 0};
			// lastSetAngle = new float[]{0, 0, 0};
			for (Point p : points)
			{
				p.moveToStart();
				// println("\nlastPos:" + p.lastPos);
				// println("pos:" + p.pos);
			}
			this.stopped = true;
			this.reset = false;
		}
		// image(canvas, 0, 0);
		toast.draw();

		for (Button b : buttons)
		{
			if (b.id == 1)
			{
				boolean visible = !(this.currentAngle[0] == 0 && this.currentAngle[1] == 0 && this.currentAngle[2] == 0
						&& this.pos.x == width / 2 && this.pos.y == height / 2 && this.zoom == 1);
				b.setVisibility(visible);
			}
			b.draw();
		}

		for (Checkbox c : checkboxes)
			c.draw();

		for (TextView tv : textviews)
			tv.draw();

		for (TextBox tb : textboxes)
			tb.draw();

		translate(pos.x, pos.y, 0);

		pushMatrix();
		// rotateY(angle[0]*angle[1]/HALF_PI+angle[0]);
		// rotateZ(-angle[1]*(PI-angle[0])/HALF_PI+angle[1]);
		// rotateX(-angle[1]*angle[0]/HALF_PI+angle[1]);
		// PVector v1 = new PVector(0, cos(currentAngle[0])+1, 0);
		// PVector v2 = new PVector(cos(currentAngle[1])+1, 0, 0);
		// PVector v3 = new PVector(0, 0, cos(currentAngle[2])+1);
		// detectionCanvas.beginDraw();
		// detectionCanvas.background(0);
		// detectionCanvas.translate(width/2, height/2);
		// detectionCanvas.rotate(angle[0], 0, cos(currentAngle[0])+1, 0);
		// detectionCanvas.rotate((currentAngle[0] > HALF_PI || currentAngle[0]
		// < -HALF_PI ? -1 : 1) * angle[1], cos(currentAngle[1])+1, 0, 0);
		// detectionCanvas.rotate(angle[2], 0, 0, cos(currentAngle[2])+1);
		// detectionCanvas.endDraw();
		// zoom = setZoom - (mouseY - mouseReference.y) / 50;
		zoom = zooming ? setZoom - (mouseY - mouseReference.y) / 50 : setZoom;
		scale(zoom);
		// rotate(angle[0], 0, cos(currentAngle[0]), 0);
		rotateY(angle[0]);
		currentAngle[0] = angle[0];

		// rotate((currentAngle[0] > HALF_PI || currentAngle[0] < -HALF_PI ? -1
		// : 1) * angle[1], cos(currentAngle[1]), 0, 0);
		rotateX(angle[1]);
		currentAngle[1] = angle[1];

		// rotate(angle[2], 0, 0, cos(currentAngle[2]));
		currentAngle[2] = angle[2];

		if (!this.stopped)
		{
			update();
			// this.stopped = true;
		}

		// println("x:" + v1.x);
		// println("y:" + v1.y);
		// println("z:" + v1.z);
		// currentAngle[0] += angle[0]*angle[1]/HALF_PI+angle[0];
		// currentAngle[1] += -angle[1]*angle[0]/HALF_PI+angle[1];
		// currentAngle[2] += -angle[1]*(PI-angle[0])/HALF_PI+angle[1];
		stroke(255, 0, 0);
		// line(0, 0, v1.x*100, v1.y*100);
		// line(0, 0, v2.x*100, v2.y*100);
		// line(0, 0, v3.x*100, v3.y*100);
		image(xySurface, -50, -50);
		pushMatrix();
		rotateY(HALF_PI);
		image(yzSurface, -50, -50);
		popMatrix();
		pushMatrix();
		rotateX(HALF_PI);
		rotateZ(HALF_PI);
		image(xzSurface, -50, -50);
		popMatrix();
		for (Point p : points)
		{
			p.setVisibilityL(cLines.isChecked());
			p.setVisibilityV(cVelocity.isChecked());
			p.setVisibilityA(cAcceleration.isChecked());
			p.draw();
			int id = p.getId();
			// println("point id:" + id);
			Integer[] colorValue = objects.get(id);
			detectionCanvas.fill(colorValue[0], colorValue[1], colorValue[2]); // colorValue
			// detectionCanvas.fill(255); //colorValue
			detectionCanvas.beginDraw();
			detectionCanvas.noStroke();
			detectionCanvas.pushMatrix();
			detectionCanvas.translate(pos.x, pos.y);
			detectionCanvas.scale(zoom);
			detectionCanvas.rotate(angle[0], 0, cos(currentAngle[0]), 0);
			detectionCanvas.rotate((currentAngle[0] > HALF_PI || currentAngle[0] < -HALF_PI ? -1 : 1) * angle[1],
					cos(currentAngle[1]), 0, 0);
			detectionCanvas.rotate(angle[2], 0, 0, cos(currentAngle[2]));
			detectionCanvas.translate(p.pos.x * scaleD, p.pos.y * scaleD, p.pos.z * scaleD);
			detectionCanvas.sphere(10);
			detectionCanvas.popMatrix();
			detectionCanvas.endDraw();
		}

		if (cPath.isChecked() && !windowResized)
		{
			if (points.size() > 1)
			{
				for (int i = 0; i < points.size(); i++)
				{
					Point p = points.get(i);
					// if (!(p.lastPos.x == p.pos.x && p.lastPos.y == p.pos.y))
					if (p.getPathVisibility())
					{
						int[] c = p.getPathColor();
						stroke(c[0], c[1], c[2]);
						ArrayList<PVector> path = p.getPath();
						if (path != null && path.size() > 1)
						{
							for (int j = 0; j < path.size() - 1; j++)
							{
								PVector lastPos = path.get(j);
								PVector pos = path.get(j + 1);
								line(lastPos.x * scaleD, lastPos.y * scaleD, lastPos.z * scaleD, pos.x * scaleD,
										pos.y * scaleD, pos.z * scaleD);
							}
						}
					}
				}
			}
		}

		popMatrix();
		popMatrix();
		hint(DISABLE_DEPTH_TEST);
		pushMatrix();
		translate(width / 2, height / 2);
		noLights();
		fill(255);
		stroke(255);
		textSize(20);
		text("X:" + mouseX, -width / 2 + 40, height / 2 - 60);
		text("Y:" + mouseY, -width / 2 + 40, height / 2 - 40);
		text("Elapsed time:" + (millis() - this.startTime), -width / 2 + 40, height / 2 - 20);

		// text("ReferenceX:"+mouseReference.x, -width/2+120, height/2-60);
		// text("ReferenceY:"+mouseReference.y, -width/2+120, height/2-40);

		// text("AngleX:"+angle[1], -width/2+300, height/2-60);
		// text("AngleY:"+angle[0], -width/2+300, height/2-40);
		// text("Last key:" + lastPressedKeyCode + " / '" + lastPressedKey +
		// "'", -width/2+400, height/2-20);

		textSize(25);
		if (output)
		{
			fill(255);
			stroke(255);

			text("Scale:", -width / 2 + 20, -height / 2 + 40);
			text("1m/s : " + scale + "px\n1m : " + scaleD + "px", -width / 2 + 120, -height / 2 + 40);

			if (stopped)
				text("paused", -width / 2 + 10, height / 2 - 100);

			String speedOutput = "Speed: " + "x" + speed;
			text(speedOutput, -textWidth(speedOutput) / 2, height / 2 - 20);
			// text(speedOutput, -width/2+700, height/2-20);
			/*
			 * if (cOutput.isChecked()) { String v1 = ((v[1].mag() < 100) ?
			 * ("  " + Integer.toString((int)(v[1].mag()+0.5))) :
			 * (Integer.toString((int)(v[1].mag()+0.5)))); String v2 =
			 * ((v[3].mag() < 100) ? ("  " +
			 * Integer.toString((int)(v[3].mag()+0.5))) :
			 * (Integer.toString((int)(v[3].mag()+0.5)))); String v3 =
			 * ((v[2].mag() < 100) ? ("  " +
			 * Integer.toString((int)(v[2].mag()+0.5))) :
			 * (Integer.toString((int)(v[2].mag()+0.5)))); text("v_oa: " + v1 +
			 * " m/s", -width/2+10, height/2-100); text("v_ap: " + v2 + " m/s",
			 * -width/2+10, height/2-60); text("v_op: " + v3 + " m/s",
			 * -width/2+10, height/2-20);
			 * String w1 = w[1].z < 10 ? ("  " + String.format("%.02f", w[1].z))
			 * : String.format("%.02f", w[1].z); String w2 = w[2].z < 10 ? ("  "
			 * + String.format("%.02f", w[2].z)) : String.format("%.02f",
			 * w[2].z);
			 * text("omega_oa: " + w1 + " 1/s", -width/2+275, height/2-100);
			 * text("omega_ap: " + w2 + " 1/s", -width/2+275, height/2-60);
			 * float timeS = (float)time/1000; text("Time: " +
			 * String.format("%.02f", timeS) + " s", -width/2+275, height/2-20);
			 * text( "alpha_oa: " + a[1] + " 1/s^2", -width/2+700,
			 * height/2-100); text("alpha_ap: " + a[2] + " 1/s^2", -width/2+700,
			 * height/2-60); }
			 */
		}
		popMatrix();
		if (menuItem != null)
			menuItem.draw();
		// image(detectionCanvas,0,0);
		hint(ENABLE_DEPTH_TEST);
	}

	public void mousePressed()
	{
		if (!mouseButtons.contains(mouseButton))
			mouseButtons.add(mouseButton);
		println("mouse pressed : '" + mouseButton + "'");
		toast.onMousePressed();
		for (Button b : buttons)
			b.mousePressedEvent();
		if (menuItem != null)
			if (menuItem.onMousePressed())
				return;
		if (mouseButton == LEFT)
		{
			mouseReference = new PVector(mouseX, mouseY, 0);
			translation = true;
		} else if (mouseButton == CENTER)
		{
			centerButtonPressed = true;
		} else if (mouseButton == RIGHT)
		{
			rightButtonPressed = true;
			detectionCanvas.loadPixels();
			int objectId = -1;
			Iterator<Map.Entry<Integer, Integer[]>> it = objects.entrySet().iterator();
			while (it.hasNext())
			{
				Map.Entry<Integer, Integer[]> pair = it.next();
				Integer[] colorValue = pair.getValue();
				int c = detectionCanvas.pixels[mouseX + mouseY * width];
				if (color(colorValue[0], colorValue[1], colorValue[2]) == c)
				{
					objectId = (int) pair.getKey();
					break;
				}
			}
			if (objectId != -1)
			{
				for (Point p : points)
				{
					final Point point = p;
					if (point.getId() == objectId)
					{
						println("point " + p.getName() + " pressed");
						final String[] possibleValues = new String[] { "Add Point", "Change value", "Graph",
								"Hide Path", "Draw Path", "Remove", "Change Color" };

						final String[] values;
						if (point.parent != null)
						{
							values = new String[] { possibleValues[0], possibleValues[1], possibleValues[2],
									(point.visibilityPath ? possibleValues[3] : possibleValues[4]), possibleValues[5],
									possibleValues[6] };
						} else
						{
							values = new String[] { possibleValues[0] };
						}
						PVector scaledPos = new PVector(point.pos.x, point.pos.y, point.pos.z);
						scaledPos = scaledPos.mult(scaleD);
						pushMatrix();
						translate(pos.x, pos.y);
						scale(zoom);
						rotate(angle[0], 0, cos(currentAngle[0]) + 1, 0);
						rotate((currentAngle[0] > HALF_PI || currentAngle[0] < -HALF_PI ? -1 : 1) * angle[1],
								cos(currentAngle[1]) + 1, 0, 0);
						rotate(angle[2], 0, 0, cos(currentAngle[2]) + 1);
						translate(scaledPos.x, scaledPos.y, scaledPos.z);
						float x = screenX(0, 0, 0);
						float y = screenY(0, 0, 0);
						popMatrix();
						println("MenuX:" + x);
						println("MenuY:" + y);
						menuItem = new MenuItem(this, x, y, "Title", values);
						menuItem.setOnItemClickListener(new OnItemClickListener()
						{
							@Override
							public void onItemClick(int itemIdx, String item)
							{
								InputBox ib;

								// Add point.
								if (item.equals(possibleValues[0]))
								{
									ib = new InputBox("Input",
											new String[] { "x", "y", "z", "wx", "wy", "wz", "alpha" },
											new String[] { "0.0", "0.0", "0.0", "0.0", "0.0", "0.0", "0.0" });
									ib.setMaxLimits(new float[] { 15, 15, 100, 400, 400, 400, 200 });
									ib.setMinLimits(
											new float[] { -width / 2, -height / 2, -100, -400, -400, -400, -200 });
									ib.setOnEditingFinishedListener(new InputBoxListener()
									{
										@Override
										public void finishedEditing(String... data)
										{
											inputWindowOpened = false;
											if (data.length == 7)
											{
												float x = data[0] == "" ? 0.0f : Float.parseFloat(data[0]);
												float y = data[1] == "" ? 0.0f : Float.parseFloat(data[1]);
												float z = data[2] == "" ? 0.0f : Float.parseFloat(data[2]);
												float wx = data[3] == "" ? 0.0f : Float.parseFloat(data[3]);
												float wy = data[4] == "" ? 0.0f : Float.parseFloat(data[4]);
												float wz = data[5] == "" ? 0.0f : Float.parseFloat(data[5]);
												float alpha = data[6] == "" ? 0.0f : Float.parseFloat(data[6]);
												println("x:" + x);
												println("y:" + y);
												println("z:" + z);
												println("wx:" + wx);
												println("wy:" + wy);
												println("wz:" + wz);
												println("alpha:" + alpha);
												reset = true;
												if (!(x == 0 && y == 0 && z == 0))
												{
													for (Point p : points)
													{
														p.reset();
													}
													pointToAdd = new Point(Drehsystem3d.this, idCount, point,
															new PVector(x, y, z), new PVector(wx, wy, wz), alpha);
													reset = true;
												}
											}
										}

										@Override
										public void onExit()
										{
											inputWindowOpened = false;
										}
									});
									inputWindowOpened = true;
								} else if (item.equals(possibleValues[1]))
								{
									if (!inputWindowOpened)
									{
										String[] values = new String[] { "x", "y", "z", "wx", "wy", "wz", "alpha" };
										String[] standardValues = new String[] { Float.toString(point.setPos.x),
												Float.toString(point.setPos.y), Float.toString(point.setPos.z),
												Float.toString(point.setW.x), Float.toString(point.setW.y),
												Float.toString(point.setW.z), Float.toString(point.setAlpha) };
										ib = new InputBox("Change " + point.getName(), values, standardValues);
										ib.setMaxLimits(new float[] { 15, 15, 100, 400, 400, 400, 200 });
										ib.setMinLimits(
												new float[] { -width / 2, -height / 2, -100, -400, -400, -400, -200 });
										ib.setOnEditingFinishedListener(new InputBoxListener()
										{
											@Override
											public void finishedEditing(String... data)
											{
												inputWindowOpened = false;
												if (data.length == 7)
												{
													float x = data[0] == "" ? 0.0f : Float.parseFloat(data[0]);
													float y = data[1] == "" ? 0.0f : Float.parseFloat(data[1]);
													float z = data[2] == "" ? 0.0f : Float.parseFloat(data[2]);
													float wx = data[3] == "" ? 0.0f : Float.parseFloat(data[3]);
													float wy = data[4] == "" ? 0.0f : Float.parseFloat(data[4]);
													float wz = data[5] == "" ? 0.0f : Float.parseFloat(data[5]);
													float alpha = data[6] == "" ? 0.0f : Float.parseFloat(data[6]);
													println("x:" + x);
													println("y:" + y);
													println("z:" + z);
													println("wx:" + wx);
													println("wy:" + wy);
													println("wz:" + wz);
													println("alpha:" + alpha);
													if (!(x == 0 && y == 0 && z == 0))
													{
														point.setPos(new PVector(x, y, z));
														point.setW(new PVector(wx, wy, wz));
														point.setAlpha(alpha);
														for (Point p : points)
														{
															p.reset();
														}
														reset = true;
													}
												}
											}

											@Override
											public void onExit()
											{
												inputWindowOpened = false;
											}
										});
										inputWindowOpened = true;
									}
								} else if (item.equals(possibleValues[2]))
								{
									String name = point.getName();
									boolean exists = false;
									for (GraphApplet a : applets)
									{
										if (a.getName().equals(name))
										{
											exists = true;
											break;
										}
									}
									if (!exists)
									{
										GraphApplet sa = new GraphApplet(Drehsystem3d.this, name);
										sa.resume();
										sa.createDataSet("v", 0, 0, 255);
										sa.createDataSet("a", 255, 0, 0);
										cOutput.setChecked(true);
										applets.add(sa);
									}
								} else if (item.equals(possibleValues[3]) || item.equals(possibleValues[4]))
								{
									boolean newVisiblityPath = !point.visibilityPath;
									point.drawPath(newVisiblityPath);
									if (!newVisiblityPath)
									{
										erasePath();
									}
								} else if (item.equals(possibleValues[5]))
								{
									points.remove(point);
								}

								else if (item.equals(possibleValues[6]))
								{
									if (!inputWindowOpened)
									{
										String[] values = new String[] { "r", "g", "b" };
										String[] standardValues = new String[3];
										int[] c = point.getPathColor();
										for (int i = 0; i < standardValues.length; i++)
										{
											standardValues[i] = Integer.toString(c[i]);
										}
										ib = new InputBox("Pathcolor " + point.getName(), values, standardValues);
										ib.setInputType(InputTypes.INTEGER);
										ib.setMaxLimits(new float[] { 255, 255, 255 });
										ib.setMinLimits(new float[] { 0, 0, 0 });
										ib.setOnEditingFinishedListener(new InputBoxListener()
										{
											@Override
											public void finishedEditing(String... data)
											{
												int necessaryDataLength = 3;
												inputWindowOpened = false;
												if (data.length == necessaryDataLength)
												{
													int r = data[0] == "" ? 0 : Integer.parseInt(data[0]);
													int g = data[1] == "" ? 0 : Integer.parseInt(data[1]);
													int b = data[2] == "" ? 0 : Integer.parseInt(data[2]);
													println("\nr:" + r);
													println("g:" + g);
													println("b:" + b);
													point.setPathColor(new int[] { r, g, b });
													point.drawPath();
												}
											}

											@Override
											public void onExit()
											{
												inputWindowOpened = false;
											}
										});
										inputWindowOpened = true;
									}
								}
							}
						});
					}
				}
			}
		}

		for (Checkbox c : checkboxes)
		{
			if (c.mousePressedEvent())
			{
				println("pressed " + c.text);
			}
		}

		for (TextBox tb : textboxes)
		{
			tb.mousePressedEvent();
		}

		for (GraphApplet sa : applets)
		{
			if (sa.waitingForExit())
			{
				cOutput.setChecked(false);
				sa.exited();
			}
			if (cOutput.isChecked() && !sa.isVisible() && !sa.exited)
			{
				sa.resume();
			} else if (!cOutput.isChecked() && sa.isVisible())
			{
				sa.pause();
			}
		}

		if (centerButtonPressed && rightButtonPressed && !rotation)
		{
			mouseReference = new PVector(mouseX, mouseY, 0);
			rotation = true;
			setZoom = zoom;
			zooming = false;
		} else if (centerButtonPressed && !zooming)
		{
			mouseReference = new PVector(mouseX, mouseY, 0);
			zooming = true;
		}
	}

	public void mouseReleased()
	{
		println("mouse released:" + mouseButton);
		if (rotation)
		{
			lastSetAngle = new float[] { angle[0], angle[1], angle[2] };
			println("last angle x:" + lastSetAngle[1]);
			println("last angle y:" + lastSetAngle[0]);
			rotation = false;
			if (mouseButton != CENTER)
			{
				mouseReference = new PVector(mouseX, mouseY, 0);
				zooming = true;
			}
		} else if (zooming)
		{
			setZoom = zoom;
			zooming = false;
		} else if (translation)
		{
			lastSetPos = new PVector(pos.x, pos.y, 0);
			translation = false;
		}

		if (mouseButton == CENTER)
		{
			centerButtonPressed = false;
		} else if (mouseButton == RIGHT)
		{
			rightButtonPressed = false;
		}
		toast.onMouseReleased();
		for (TextBox tb : textboxes)
		{
			tb.mouseReleasedEvent();
		}
	}

	public void mouseDragged()
	{
		for (TextBox tb : textboxes)
		{
			tb.mouseDraggedEvent();
		}
	}

	public void keyPressed()
	{
		println("key pressed");
		lastPressedKey = key;
		lastPressedKeyCode = keyCode;
		boolean tbClicked = false;
		for (TextBox tb : textboxes)
		{
			tb.handleKeyPressedEvent(keyCode, key);
			if (tb.isClicked())
				tbClicked = true;
		}
		if (!tbClicked)
			handleKeyPressedEvent(keyCode, key);
	}

	public void handleKeyPressedEvent(int pressedKeyCode, char pressedKey)
	{
		lastKeyEvent = millis();
		if (!keyCodes.contains(pressedKeyCode))
		{
			keys.add(pressedKey);
			keyCodes.add(pressedKeyCode);
			println("keyCode:" + pressedKeyCode);
			println("key:'" + pressedKey + "'");
		}
		switch (pressedKeyCode)
		{
			case 139:
			case 93:
				if (isKeyPressed(16) && isKeyPressed(17))
				{
					this.scale += 0.2f;
					updateDrawScale();
				} else if (isKeyPressed(17))
				{
					this.scaleD++;
					updateDrawScaleD();
				} else
				{
					if ((speed >= 0 && speed < 1) || (speed < -0.5f && speed >= -1))
						speed = speed + 0.5f;
					else
						speed++;
					updateDrawSpeed();
				}
				break;

			case 140:
			case 47:
				if (isKeyPressed(16) && isKeyPressed(17))
				{
					if (this.scale > 0)
					{
						if (this.scale <= 0.2f)
							this.scale -= 0.1f;
						else
							this.scale -= 0.2f;
						updateDrawScale();
					}
				} else if (isKeyPressed(17))
				{
					if (this.scaleD > 0)
					{
						this.scaleD--;
						updateDrawScaleD();
					}
				} else
				{
					if ((speed > 0.5f && speed <= 1) || (speed <= -0.5f && speed > -1))
						speed = speed - 0.5f;
					else
						speed--;
					updateDrawSpeed();
				}
				break;
		}
		switch (pressedKey)
		{
			case '1':
				speed = 1;
				updateDrawSpeed();
				break;

			case '0':
				speed = 1;
				updateDrawSpeed();
				break;

			case 'p':
				cPath.setChecked(!cPath.isChecked());
				break;

			case 'o':
				cOutput.setChecked(!cOutput.isChecked());
				break;

			case 'v':
				cVelocity.setChecked(!cVelocity.isChecked());
				break;

			case 'a':
				cAcceleration.setChecked(!cAcceleration.isChecked());
				break;

			case ' ':
				stop();
				pressed = true;
				break;
		}
	}

	public void keyReleased()
	{
		boolean tbClicked = false;
		for (TextBox tb : textboxes)
		{
			tb.handleKeyReleasedEvent(keyCode, key);
			if (tb.isClicked())
				tbClicked = true;
		}
		if (!tbClicked)
			handleKeyReleasedEvent(keyCode, key);
	}

	public void handleKeyReleasedEvent(int pressedKeyCode, char pressedKey)
	{
		for (int i = 0; i < keyCodes.size(); i++)
		{
			int maxId = keyCodes.size() - 1;
			if (pressedKeyCode == keyCodes.get(i))
			{
				println("Removed:" + keyCodes.get(i) + "\t'" + keys.get(i) + "'");
				keyCodes.remove(i);
				keys.remove(i);
				if (i == maxId)
				{
					if (keyCodes.size() > 0)
					{
						lastPressedKeyCode = keyCodes.get(keyCodes.size() - 1);
						lastPressedKey = keys.get(keyCodes.size() - 1);
					} else
					{
						lastPressedKeyCode = -1;
						lastPressedKey = ' ';
					}
				}
			}
		}
	}

	public boolean isKeyPressed(int pressedKeyCode)
	{
		for (int i = 0; i < keyCodes.size(); i++)
		{
			if (keyCodes.get(i) == pressedKeyCode)
			{
				return true;
			}
		}
		return false;
	}

	public void updateDrawSpeed()
	{
		for (Point p : points)
		{
			p.setDrawSpeed(this.speed);
		}
	}

	public void updateDrawScale()
	{
		for (Point p : points)
		{
			p.setScale(this.scale);
		}
	}

	public void updateDrawScaleD()
	{
		for (Point p : points)
		{
			p.setScaleD(this.scaleD);
		}
	}

	public void stop()
	{
		stop(!stopped);
	}

	public void stop(boolean state)
	{
		stopped = state;
		lastTime = millis();
		startTime = millis();
		for (Point p : points)
		{
			p.resetTime();
		}
	}

	public void erasePath()
	{
	}

	public Point getPoint(int idx)
	{
		if (idx < 0 || idx > points.size())
			return null;
		return points.get(idx);
	}

	public Point getLastPoint()
	{
		if (points.size() == 0)
			return null;
		return points.get(points.size() - 1);
	}

	public Point getPreviousPoint(Point p)
	{
		if (points.size() == 0)
			return null;
		for (int i = 0; i < points.size(); i++)
		{
			if (points.get(i) == p)
				return getPoint(i - 1);
		}
		return null;
	}

	public Point getNextPoint(Point p)
	{
		if (points.size() == 0)
			return null;
		for (int i = 0; i < points.size(); i++)
		{
			if (points.get(i) == p)
				return getPoint(i + 1);
		}
		return null;
	}

	public Point addNewPoint(Point parent, float a, float[] angle, PVector w, float alpha)
	{
		points.add(new Point(this, idCount, "" + PApplet.parseChar(this.nameCounter++), parent, a, angle, w, alpha));
		Point point = points.get(points.size() - 1);
		point.setScale(this.scale);
		point.setScaleD(this.scaleD);
		point.setDrawSpeed(this.speed);
		objects.put(idCount++, colorCount);
		colorCount = new Integer[] { colorCount[0], colorCount[1], colorCount[2] };
		colorCount[0] += 1;
		if (colorCount[0] > 255)
		{
			colorCount[0] = 0;
			colorCount[1] += 1;
		}
		if (colorCount[1] > 255)
		{
			colorCount[1] = 0;
			colorCount[2] += 1;
		}
		if (nameCounter > 91)
			nameCounter = 65;
		println("Added Point " + point.getName());
		return point;
	}

	public Point addNewPoint(Point parent, PVector pos, PVector w, float alpha)
	{
		points.add(new Point(this, idCount, "" + PApplet.parseChar(this.nameCounter++), parent, pos, w, alpha));
		Point point = points.get(points.size() - 1);
		point.setScale(this.scale);
		point.setScaleD(this.scaleD);
		point.setDrawSpeed(this.speed);
		objects.put(idCount++, colorCount);
		if (parent != null)
		{
			parent.addChild(point);
		}
		colorCount = new Integer[] { colorCount[0], colorCount[1], colorCount[2] };
		colorCount[0] += 1;
		if (colorCount[0] > 255)
		{
			colorCount[0] = 0;
			colorCount[1] += 1;
		}
		if (colorCount[1] > 255)
		{
			colorCount[1] = 0;
			colorCount[2] += 1;
		}
		if (nameCounter >= 91)
		{
			nameCounter = 65;
		}
		println("Added Point " + point.getName());
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
		ArrayList<Checkbox> group = new ArrayList<Checkbox>();
		group.add(member);
		return addCheckBox(title, false, group);
	}

	public Checkbox addCheckBox(String title, ArrayList<Checkbox> group)
	{
		return addCheckBox(title, false, group);
	}

	public Checkbox addCheckBox(String title, boolean checked, Checkbox member)
	{
		ArrayList<Checkbox> group = new ArrayList<Checkbox>();
		group.add(member);
		return addCheckBox(title, checked, group);
	}

	public Checkbox addCheckBox(String title, boolean checked, ArrayList<Checkbox> group)
	{
		Checkbox c = new Checkbox(this, 20, this.checkBoxY, 20, title, group);
		c.setChecked(checked);
		checkboxes.add(c);
		this.checkBoxY += this.checkBoxOffset;
		return c;
	}

	static public void main(String[] passedArgs)
	{
		String[] appletArgs = new String[] { "drehsystem3d.Drehsystem3d" };
		if (passedArgs != null)
		{
			PApplet.main(concat(appletArgs, passedArgs));
		} else
		{
			PApplet.main(appletArgs);
		}
	}
}
