package drehsystem3d;

import java.util.ArrayList;

import processing.core.PVector;

public class Point {
	/**
	 * 
	 */
	private Drehsystem3d Point;
	Point parent = null;
	private final int id;
	private boolean newPosReceived = false;
	PVector setPos;
	private PVector absSetPos;
	private PVector lastPos;
	PVector pos;
	private final int size = 10;
	private PVector lastV = null;
	PVector a = null;
	PVector v = null;
	PVector setW = null;
	private PVector w = null;
	private PVector wAbs = null;
	float setAlpha = 0;
	private float alpha = 0;
	private float[] phi = new float[] { 0, 0, 0 };
	private float drawSpeed = 1;
	private long lastTime = 0;
	private float scale = 1;
	private float scaleD = 40;
	private boolean setup = true;
	private boolean reset = false;
	String name = "";
	private boolean visibilityL = true;
	private boolean visibilityV = true;
	private boolean visibilityA = false;
	boolean visibilityPath = false;
	private boolean finishedPath = false;
	private ArrayList<Point> childs = new ArrayList<Point>();
	private ArrayList<PVector> path = new ArrayList<PVector>();
	private int[] pathColor = { 255, 255, 255 };
	private int pathEntryCount = 0;

	Point(Drehsystem3d drehsystem3d, int id, Point parent, float amp, float[] angle, PVector w, float alpha) {
		Point = drehsystem3d;
		this.id = id;
		init("", parent, getPosFromAngle(amp, angle), w, alpha);
	}

	Point(Drehsystem3d drehsystem3d, int id, String name, Point parent, float amp, float[] angle, PVector w,
			float alpha) {
		Point = drehsystem3d;
		this.id = id;
		init(name, parent, getPosFromAngle(amp, angle), w, alpha);
	}

	public PVector getPosFromAngle(float amp, float[] angle) {
		if (angle.length != 2)
			return null;
		float x = amp * Drehsystem3d.cos(angle[0]) * Drehsystem3d.cos(angle[1]);
		float y = amp * Drehsystem3d.sin(angle[1]);
		float z = amp * Drehsystem3d.sin(angle[0]) * Drehsystem3d.cos(angle[1]);
		return new PVector(x, y, z);
	}

	Point(Drehsystem3d drehsystem3d, int id, Point parent, PVector pos, PVector w, float alpha) {
		Point = drehsystem3d;
		this.id = id;
		init("", parent, pos, w, alpha);
	}

	Point(Drehsystem3d drehsystem3d, int id, String name, Point parent, PVector pos, PVector w, float alpha) {
		Point = drehsystem3d;
		this.id = id;
		init(name, parent, pos, w, alpha);
	}

	public int getId() {
		return this.id;
	}

	public void setPos(float x, float y, float z) {
		setPos(new PVector(x, y, z));
	}

	public void setPos(PVector pos) {
		this.setPos = new PVector(pos.x, pos.y, pos.z);
		newPosReceived = true;
	}

	public void setW(PVector w) {
		this.setW = getVector(w);
	}

	public void setAlpha(float alpha) {
		this.setAlpha = alpha;
	}

	public String getName() {
		return this.name;
	}

	public void drawPath() {
		drawPath(true);
	}

	public void drawPath(boolean visible) {
		this.visibilityPath = visible;
	}

	public boolean getPathVisibility() {
		return this.visibilityPath;
	}

	public void setPathColor(int[] c) {
		this.pathColor = c;
	}

	public int[] getPathColor() {
		return this.pathColor;
	}

	private void init(String name, Point parent, PVector pos, PVector w, float alpha) {
		Drehsystem3d.println("\n\n" + name);
		this.name = name;
		this.parent = parent;
		this.setPos = new PVector(pos.x, pos.y, pos.z);
		// if (this.parent != null) this.absSetPos = new
		// PVector(this.setPos.x+this.parent.pos.x,
		// this.setPos.y+this.parent.pos.y, this.setPos.z+this.parent.pos.z);
		this.setW = new PVector(w.x, w.y, w.z);
		this.setAlpha = alpha;
		this.lastV = new PVector(0, 0, 0);
		this.v = new PVector(0, 0, 0);
		this.a = new PVector(0, 0, 0);
		calcPos();
	}

	private void initPos(PVector pos) {
		if (this.parent != null)
			this.absSetPos = new PVector(pos.x + this.parent.pos.x, pos.y + this.parent.pos.y,
					pos.z + this.parent.pos.z);
		else
			this.absSetPos = new PVector(pos.x, pos.y, pos.z);
		this.lastPos = new PVector(pos.x, pos.y, pos.z);
		this.pos = new PVector(pos.x, pos.y, pos.z);
		// this.amp = sqrt(pos.x*pos.x+pos.y*pos.y);
		// this.amp = this.pos.mag();
		this.phi[0] = 0;
		this.phi[1] = 0;
		if (!(this.pos.x == 0 && this.pos.z == 0)) {
			// if (this.pos.x != 0) {
			Drehsystem3d.println("phiXY atan:" + Drehsystem3d.atan(this.pos.z / this.pos.x));
			this.phi[0] = Drehsystem3d.map(Drehsystem3d.atan(this.pos.z / this.pos.x), -Drehsystem3d.PI,
					Drehsystem3d.PI, -180, 180);
			Drehsystem3d.println("phiXY mapped:" + this.phi[0]);
			// if (this.pos.x < 0 || this.pos.z < 0) {
			// this.phi[0] += 180;
			// }
			// }
		}
		if (!(this.pos.z == 0 && this.pos.y == 0)) {
			// if (this.pos.z != 0) {
			Drehsystem3d.println("phiYZ atan:" + Drehsystem3d.atan(this.pos.y / this.pos.z));
			this.phi[1] = Drehsystem3d.map(Drehsystem3d.atan(this.pos.y / this.pos.z), -Drehsystem3d.PI,
					Drehsystem3d.PI, -180, 180);
			Drehsystem3d.println("phiYZ mapped:" + this.phi[1]);
			// if (this.pos.z < 0 || this.pos.y < 0) {
			// this.phi[1] += 180;
			// }
			// }
		}
		Drehsystem3d.println("Initialised position:" + this.pos);
		Drehsystem3d.println("Initialised angle:[ " + this.phi[0] + ", " + this.phi[1] + " ]");
		Drehsystem3d.println("Abs Set position:" + this.absSetPos);
	}

	public void calcPos() {
		Drehsystem3d.println("\n\n");
		this.w = new PVector(this.setW.x, this.setW.y, this.setW.z);
		this.alpha = this.setAlpha;
		// println(this.name);
		initPos(this.setPos);
		if (this.parent != null) {
			PVector parentPos = new PVector(this.parent.pos.x, this.parent.pos.y, this.parent.pos.z);
			this.lastPos.add(parentPos);
			this.pos.add(parentPos);
			this.wAbs = new PVector(this.parent.wAbs.x + this.w.x, this.parent.wAbs.y + this.w.y,
					this.parent.wAbs.z + this.w.z);
		} else {
			this.wAbs = new PVector(w.x, w.y, w.z);
		}
		// println("x:" + this.pos.x);
		// println("y:" + this.pos.y);
		// println("phi:" + this.phi);
		Drehsystem3d.println("Position before update:" + this.pos);
		update();
		Drehsystem3d.println("Position calculation finished:" + this.pos);
	}

	public void moveToStart() {
		this.reset = true;
		this.pathEntryCount = 0;
		resetTime();
		calcPos();
	}

	public void reset() {
		initPos(this.setPos);
		newPosReceived = false;
		moveToStart();
		this.path = new ArrayList<PVector>();
		this.finishedPath = false;
	}

	public void setDrawSpeed(float speed) {
		this.drawSpeed = speed;
	}

	public void setVisibilityL(boolean visible) {
		this.visibilityL = visible;
	}

	public void setVisibilityV(boolean visible) {
		this.visibilityV = visible;
	}

	public void setVisibilityA(boolean visible) {
		this.visibilityA = visible;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public void setScaleD(float scale) {
		this.scaleD = scale;
	}

	public void clearPath() {
		this.path = new ArrayList<PVector>();
		this.pathEntryCount = 0;
		this.finishedPath = false;
	}

	public void update() {
		Drehsystem3d.println("\n" + this.name);
		// this.time += (millis()-this.lastTime)*this.drawSpeed;
		float dTime = (Point.millis() - this.lastTime) * (float) this.drawSpeed;
		if (this.setup || this.reset)
			dTime = 0;
		if (dTime != 0 || this.setup || this.reset) {
			this.lastPos = getVector(this.pos);
			this.lastV = getVector(this.v);
			this.w.x += this.alpha * this.drawSpeed * dTime / 1000;
			this.w.y += this.alpha * this.drawSpeed * dTime / 1000;
			this.w.z += this.alpha * this.drawSpeed * dTime / 1000;
			Drehsystem3d.println("abs pos:" + this.pos);
			PVector position = getVector(this.pos);
			if (this.parent != null) {
				this.wAbs = new PVector(this.parent.wAbs.x + this.w.x, this.parent.wAbs.y + this.w.y,
						this.parent.wAbs.z + this.w.z);
				position = position.sub(this.parent.pos);
			} else {
				this.wAbs = getVector(this.w);
			}

			Drehsystem3d.println("pos:" + position);
			this.pos = rotateV(this.w, position, dTime);

			if (this.parent != null) {
				PVector p = getVector(this.pos).mult(-1);
				this.v = this.w.cross(p);
				this.pos = this.pos.add(this.parent.pos);
				Point lastParent = this.parent;
				for (;;) {
					Point parent = lastParent.parent;
					if (parent == null) {
						break;
					} else {
						p = getVector(this.pos);
						this.v = this.v.add(lastParent.w.cross(p.sub(parent.pos).mult(-1)));
						lastParent = parent;
					}
				}
				if (!this.setup && !this.reset) {
					PVector velocity = new PVector(this.v.x, this.v.y, this.v.z);
					PVector pos = getVector(this.pos);
					this.a = pos.sub(velocity).sub(this.lastPos.sub(this.lastV)).mult(1000 / dTime);
					this.a.add(this.pos);
				} else {
					this.a = new PVector(0, 0, 0);
				}
			}
			Drehsystem3d.println("pos:" + this.pos);
			Drehsystem3d.println("last pos:" + this.lastPos);

			if (this.visibilityPath && !this.reset) {
				boolean distanceCheck = false;
				if (this.pathEntryCount < this.path.size()) {
					path.set(this.pathEntryCount, new PVector(this.pos.x, this.pos.y, this.pos.z));
					Drehsystem3d.println("Override entry");
				} else {
					path.add(new PVector(this.pos.x, this.pos.y, this.pos.z));
					final int minData = 100;
					if (path.size() > 5000 || finishedPath)
						path.remove(0);
					if (this.path.size() > minData) {
						distanceCheck = true;
						for (int i = 0; i < 6; i++) {
							PVector p1 = this.path.get(this.path.size() - (minData / 2 - 1) - i);
							PVector p2 = this.path.get(i);
							float d = Drehsystem3d.dist(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
							if (d > 0.7f) {
								distanceCheck = false;
								break;
							}
						}
					}
					if (distanceCheck) {
						this.finishedPath = true;
					}
				}
				this.pathEntryCount++;
			}
			this.lastTime = Point.millis();
			this.setup = false;
			this.reset = false;
		}
		Drehsystem3d.println("\n");
	}

	public boolean mousePressedEvent(float mX, float mY) {
		float d = Drehsystem3d.dist(mX, mY, this.pos.x * scaleD, this.pos.y * scaleD);
		// println("d:"+d);
		if (d <= this.size / 2)
			return true;
		return false;
	}

	public ArrayList<PVector> getPath() {
		return this.path;
	}

	public void draw() {
		if (newPosReceived) {
			initPos(this.setPos);
			newPosReceived = false;
		}
		Point.fill(255);
		Point.stroke(255);
		Point.strokeWeight(1);
		PVector scaledPos = new PVector(this.pos.x, this.pos.y, this.pos.z);
		scaledPos = scaledPos.mult(scaleD);

		if (this.visibilityL) {
			if (this.parent != null) {
				PVector scaledParentPos = new PVector(this.parent.pos.x, this.parent.pos.y, this.parent.pos.z);
				scaledParentPos = scaledParentPos.mult(scaleD);
				Point.line(scaledParentPos.x, scaledParentPos.y, scaledParentPos.z, scaledPos.x, scaledPos.y,
						scaledPos.z);
			}
		}

		Point.strokeWeight(2);
		if (this.visibilityV) {
			Point.stroke(0, 0, 255);
			Point.line(scaledPos.x, scaledPos.y, scaledPos.z, scaledPos.x + this.v.x * scale,
					scaledPos.y + this.v.y * scale, scaledPos.z + this.v.z * scale);
		}
		if (this.visibilityA) {
			Point.stroke(255, 0, 0);
			Point.line(scaledPos.x, scaledPos.y, scaledPos.z, scaledPos.x + this.a.x * scale,
					scaledPos.y + this.a.y * scale, scaledPos.z + this.a.z * scale);
		}
		if (this.parent != null) {
			Point.stroke(51);
			Point.strokeWeight(4);
			PVector start = getVector(this.parent.pos);
			// stroke(255,255,0);
			// line(start.x*scaleD, start.y*scaleD, start.z*scaleD,
			// (this.lastPos.x), (this.lastPos.y), (this.lastPos.z));
			Point.stroke(255, 0, 0);
			Point.line(start.x * scaleD, start.y * scaleD, start.z * scaleD, start.x * scaleD + this.w.x,
					start.y * scaleD + this.w.y, start.z * scaleD + this.w.z);
		}
		Point.pushMatrix();
		Point.translate(scaledPos.x, scaledPos.y, scaledPos.z);
		if (this.visibilityPath) {
			Point.fill(this.pathColor[0], this.pathColor[1], this.pathColor[2]);
		} else {
			Point.fill(255, 255, 255, 50);
		}
		Point.lights();
		Point.noStroke();
		try {
			Point.sphere(10);
		} catch (Exception e) {

		}
		Point.popMatrix();
		Point.hint(Drehsystem3d.DISABLE_DEPTH_TEST);
		Point.fill(255);
		if (!this.name.equals("")) {
			Point.text(this.name, scaledPos.x + this.size, scaledPos.y + this.size, scaledPos.z + this.size);
		}
		Point.hint(Drehsystem3d.ENABLE_DEPTH_TEST);
	}

	public void resetTime() {
		Point.startTime = Point.millis();
		lastTime = Point.millis();
	}

	public void setName(String name) {
		this.name = name;
	}

	public PVector getVector(PVector v) {
		return new PVector(v.x, v.y, v.z);
	}

	public PVector rotateV(PVector a, PVector vector, float dTime) {
		final int dAlpha = 10;
		PVector result = new PVector(0, 0, 0);
		Drehsystem3d.println("\nrotating " + vector + " around " + a);
		PVector position = getVector(vector);
		PVector axis = getVector(a).normalize();
		PVector pn = axis.cross(position);
		if (this.parent != null) {
			Point.stroke(51);
			Point.line(this.parent.pos.x * scaleD, this.parent.pos.y * scaleD, this.parent.pos.z * scaleD,
					(pn.x + this.parent.pos.x) * scaleD, (pn.y + this.parent.pos.y) * scaleD,
					(pn.z + this.parent.pos.z) * scaleD);
		}
		// for (int t=0; t<dTime; t++) {
		axis = getVector(a).normalize();
		pn = axis.cross(pn).mult(-1);
		axis = getVector(a);
		float angle = a.mag() * dTime / 1000 * Drehsystem3d.PI / 180;
		Drehsystem3d.println("rotating by:" + angle + " degrees");
		Drehsystem3d.println("mag start:" + vector.mag());
		PVector offset = position.sub(pn);
		position = getVector(vector);

		Point.strokeWeight(2);
		Drehsystem3d.println("pn before rotation:" + pn);

		ArrayList<PVector> positions = new ArrayList<PVector>();
		for (int i = 0; i < this.childs.size(); i++) {
			Point child = this.childs.get(i);
			PVector relPos = getVector(child.pos);
			if (this.parent != null) {
				relPos.sub(this.parent.pos);
			}
			Drehsystem3d.println("child " + child.getName() + " pos:" + relPos);
			positions.add(relPos);
		}
		float alphaX = Drehsystem3d.atan(w.y / w.z);
		if (w.y != 0) {
			float alpha = alphaX;
			Drehsystem3d.println("angle x:" + alpha);
			axis = rotateVX(axis, alpha);
			pn = rotateVX(pn, alpha);
			for (int i = 0; i < positions.size(); i++) {
				PVector childPos = positions.get(i);
				childs.get(i).w = rotateVX(childs.get(i).w, alpha);
				positions.set(i, rotateVX(childPos, alpha));
			}
		}
		float alphaY = -Drehsystem3d.atan(axis.x / axis.z);
		Drehsystem3d.println("Axis:" + axis);
		if (w.x != 0) {
			float alpha = alphaY;
			Drehsystem3d.println("angle y:" + alpha);
			axis = rotateVY(axis, alpha);
			pn = rotateVY(pn, alpha);
			for (int i = 0; i < positions.size(); i++) {
				PVector childPos = positions.get(i);
				childs.get(i).w = rotateVY(childs.get(i).w, alpha);
				positions.set(i, rotateVY(childPos, alpha));
			}
		}
		if (axis.z > 0)
			angle *= -1;
		Drehsystem3d.println("Axis:" + axis);
		Drehsystem3d.println("pn:" + pn);
		pn = rotateVZ(pn, angle);
		if (this.parent != null) {
			Point.strokeWeight(4);
			PVector start = getVector(this.parent.pos);
			Point.stroke(255, 0, 255);
			Point.line(start.x * scaleD, start.y * scaleD, start.z * scaleD, start.x * scaleD + axis.x,
					start.y * scaleD + axis.y, start.z * scaleD + axis.z);
			// stroke(51);
			// line(start.x*scaleD, start.y*scaleD, start.z*scaleD,
			// (start.x+offset.x)*scaleD, (start.y+offset.y)*scaleD,
			// (start.z+offset.z)*scaleD);
			// line((start.x+offset.x)*scaleD, (start.y+offset.y)*scaleD,
			// (start.z+offset.z)*scaleD, (start.x+pn.x)*scaleD,
			// (start.y+pn.y)*scaleD, (start.z+pn.z)*scaleD);
		}
		for (int i = 0; i < positions.size(); i++) {
			PVector childPos = positions.get(i);
			childs.get(i).w = rotateVZ(childs.get(i).w, angle);
			positions.set(i, rotateVZ(childPos, angle));
		}

		if (w.x != 0) {
			float alpha = -alphaY;
			Drehsystem3d.println("angle y:" + alpha);
			axis = rotateVY(axis, alpha);
			pn = rotateVY(pn, alpha);
			for (int i = 0; i < positions.size(); i++) {
				PVector childPos = positions.get(i);
				childs.get(i).w = rotateVY(childs.get(i).w, alpha);
				positions.set(i, rotateVY(childPos, alpha));
			}
		}
		if (w.y != 0) {
			float alpha = -alphaX;
			Drehsystem3d.println("angle x:" + alpha);
			axis = rotateVX(axis, alpha);
			pn = rotateVX(pn, alpha);
			for (int i = 0; i < positions.size(); i++) {
				PVector childPos = positions.get(i);
				childs.get(i).w = rotateVX(childs.get(i).w, alpha);
				positions.set(i, rotateVX(childPos, alpha));
			}
		}
		result = pn.add(offset);
		for (int i = 0; i < positions.size(); i++) {
			PVector newPos = getVector(positions.get(i));
			Drehsystem3d.println("child " + this.childs.get(i).getName() + " pos without parent:" + newPos);
			if (this.parent != null) {
				newPos.add(this.parent.pos);
			}
			this.childs.get(i).updatePos(newPos);
			Drehsystem3d.println("child " + this.childs.get(i).getName() + " new pos:" + this.childs.get(i).getPos());
		}
		if (this.parent != null) {
			Point.strokeWeight(4);
			PVector start = getVector(this.parent.pos);
			Point.stroke(51);
			Point.line(start.x * scaleD, start.y * scaleD, start.z * scaleD, (start.x + offset.x) * scaleD,
					(start.y + offset.y) * scaleD, (start.z + offset.z) * scaleD);
			Point.line((start.x + offset.x) * scaleD, (start.y + offset.y) * scaleD, (start.z + offset.z) * scaleD,
					(start.x + pn.x) * scaleD, (start.y + pn.y) * scaleD, (start.z + pn.z) * scaleD);
		}
		Drehsystem3d.println("offset:" + offset);
		Drehsystem3d.println("result:" + result);
		Drehsystem3d.println("result mag:" + result.mag());
		// dTime -= dAlpha;
		// if (dTime > 0) {
		// return rotateV(axis, result, dTime);
		// }
		// }
		return result;
	}

	public void addChild(Point p) {
		this.childs.add(p);
		if (this.parent != null)
			this.parent.addChild(p);
	}

	public void removeChilds() {
		this.childs = new ArrayList<Point>();
	}

	public boolean removeChild(Point p) {
		if (!this.childs.contains(p))
			return false;
		this.childs.remove(p);
		return true;
	}

	public PVector rotateVX(PVector p, float alpha) {
		Drehsystem3d.println("rotating around x");
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = x;
		p.y = y * Drehsystem3d.cos(alpha) - z * Drehsystem3d.sin(alpha);
		p.z = y * Drehsystem3d.sin(alpha) + z * Drehsystem3d.cos(alpha);
		return getVector(p);
	}

	public PVector rotateVY(PVector p, float alpha) {
		Drehsystem3d.println("rotating around y");
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = z * Drehsystem3d.sin(alpha) + x * Drehsystem3d.cos(alpha);
		p.y = y;
		p.z = z * Drehsystem3d.cos(alpha) - x * Drehsystem3d.sin(alpha);
		return getVector(p);
	}

	public PVector rotateVZ(PVector p, float alpha) {
		Drehsystem3d.println("rotating around z");
		float x = p.x;
		float y = p.y;
		float z = p.z;
		p.x = x * Drehsystem3d.cos(alpha) - y * Drehsystem3d.sin(alpha);
		p.y = x * Drehsystem3d.sin(alpha) + y * Drehsystem3d.cos(alpha);
		p.z = z;
		return getVector(p);
	}

	public void updatePos(PVector pos) {
		this.pos = pos;
	}

	public PVector getPos() {
		return this.pos;
	}
}