package drehsystem3d;

import java.util.logging.Level;

import drehsystem3d.Listener.WindowResizeListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class CameraController implements WindowResizeListener
{
	private PApplet context;
	private PVector mouseReference = new PVector(0, 0, 0);
	private PVector pos;
	private PVector lastSetPos;

	private PVector initialPos;
	private float[] initialAngle;
	private float initialZoom;

	private float[] currentAngle = new float[] { 0, 0, 0 };
	private float[] angle = new float[] { 0, 0, 0 };
	private float[] lastSetAngle = new float[] { 0, 0, 0 };
	private float zoom = 1;
	private float setZoom = 1;
	private Adjustment adjustment = Adjustment.NONE;

	public enum Adjustment
	{
		NONE, TRANSLATION, ROTATION, ZOOM
	}

	public CameraController(PApplet context, float[] initialAngle, float initialZoom, PVector initialPos)
	{
		this.context = context;

		this.initialAngle = getValues(initialAngle);
		this.initialZoom = initialZoom;
		this.initialPos = initialPos;

		this.angle = getValues(initialAngle);
		this.currentAngle = getValues(this.angle);
		this.lastSetAngle = getValues(this.angle);

		this.zoom = initialZoom;
		this.setZoom = initialZoom;

		this.pos = initialPos;
		this.lastSetPos = initialPos;
	}

	public void resetCamera()
	{
		this.angle = getValues(this.initialAngle);
		this.currentAngle = getValues(this.angle);
		this.lastSetAngle = getValues(this.angle);

		this.zoom = this.initialZoom;
		this.setZoom = this.initialZoom;

		this.pos = this.initialPos.copy();
		this.lastSetPos = this.initialPos.copy();
		
		Global.logger.log(Level.FINE, "Camera reset");
		Global.logger.log(Level.FINE, "Angle", new Object[] {this.angle[0], this.angle[1]});
		Global.logger.log(Level.FINE, "Zoom", this.zoom);
		Global.logger.log(Level.FINE, "Pos", this.pos);
	}

	@Override
	public void onWindowResize(int widthOld, int heightOld, int widthNew, int heightNew)
	{
		this.initialPos.x = this.initialPos.x * widthNew / widthOld;
		this.initialPos.y = this.initialPos.y * heightNew / heightOld;
		this.lastSetPos.x = this.lastSetPos.x * widthNew / widthOld;
		this.lastSetPos.y = this.lastSetPos.y * heightNew / heightOld;

		this.pos.x = this.pos.x * widthNew / widthOld;
		this.pos.y = this.pos.y * heightNew / heightNew;
	}
	
	public void setInitPos(int x, int y)
	{
		this.initialPos = new PVector(x, y, 0);
	}
	
	public void setInitPos(PVector pos)
	{
		this.initialPos = pos.copy();
	}
	
	public PVector getPos()
	{
		return this.pos;
	}

	public void setZoom(float zoom)
	{
		this.zoom = zoom;
	}

	public float getZoom()
	{
		return this.zoom;
	}

	public void setAngle(float[] angle)
	{
		this.lastSetAngle = this.angle;
		this.angle = angle;
		this.currentAngle = angle;
	}

	public float[] getAngle()
	{
		return this.angle;
	}

	public float[] getCurrentAngle()
	{
		return this.currentAngle;
	}

	public float[] getLastSetAngle()
	{
		return this.lastSetAngle;
	}

	public void calcCameraAdjustment()
	{
		if (this.adjustment == Adjustment.ROTATION)
		{
			float maxAngle = PApplet.PI;
			this.angle[0] = PApplet.map(this.context.mouseX - this.mouseReference.x, -this.context.width,
					this.context.width, -maxAngle, maxAngle) * 2 + this.lastSetAngle[0];
			this.angle[1] = PApplet.map(this.context.mouseY - this.mouseReference.y, -this.context.height,
					this.context.height, maxAngle, -maxAngle) * 2 + this.lastSetAngle[1];

			this.angle[0] = trimAngle(this.angle[0]);
			this.angle[1] = trimAngle(this.angle[1]);
		}
		else if (this.adjustment == Adjustment.TRANSLATION)
		{
			this.pos = new PVector(this.lastSetPos.x + (this.context.mouseX - this.mouseReference.x),
					this.lastSetPos.y + (this.context.mouseY - this.mouseReference.y), 0);
		}
		else if (this.adjustment == Adjustment.ZOOM)
		{
			this.zoom = this.setZoom - (this.context.mouseY - this.mouseReference.y) / 50;
		}
	}

	private float trimAngle(float angle)
	{
		return angle + ((Math.abs(angle) > PApplet.PI) ? (Math.signum(angle) * PApplet.TWO_PI) : 0);
	}

	public void adjustCamera(PGraphics canvas)
	{
		this.currentAngle = getValues(this.angle);

		canvas.translate(this.pos.x, this.pos.y, 0);
		canvas.scale(this.zoom);
		canvas.rotateY(this.angle[0]);
		canvas.rotateX(this.angle[1]);
	}

	public void setNewAdjustment(Adjustment adjustment)
	{
		setMouseReference();
		this.adjustment = adjustment;
	}

	public void removeAdjustment()
	{
		this.adjustment = Adjustment.NONE;
		this.lastSetAngle = getValues(this.angle);
		this.lastSetPos = new PVector(this.pos.x, this.pos.y, 0);
		this.setZoom = this.zoom;
	}

	private void setMouseReference()
	{
		this.mouseReference = new PVector(this.context.mouseX, this.context.mouseY, 0);
	}

	private float[] getValues(float[] valuesArray)
	{
		float[] values = new float[valuesArray.length];
		for (int i = 0; i < valuesArray.length; i++)
		{
			values[i] = valuesArray[i];
		}
		return values;
	}
}
