package drehsystem3d;

import java.util.ArrayList;

import drehsystem3d.Listener.*;

import processing.core.PApplet;

public class InputBox extends PApplet implements TextBoxListener, OnClickListener, OnAnimationFinishedListener {
	/**
	 * 
	 */
	private Drehsystem3d InputBox;
	ArrayList<TextView> textviews = new ArrayList<TextView>();
	ArrayList<TextBox> textboxes = new ArrayList<TextBox>();
	Button bSubmit;
	String[] values;
	String[] standardValues;
	String[] hintValues;
	float[] limitsMax;
	float[] limitsMin;
	boolean visible = true;
	boolean exit = false;
	String title = "";
	int windowWidth;
	int windowHeight;
	int xStart = 0;
	int yStart = 0;
	int xMax = 0;
	int yMax = 0;
	int padding = 10;
	int tvWidth = 100;
	int tvHeight = 50;
	int tbWidth = 150;
	int tbHeight = 50;
	int tvWidthMax = 0;
	int tbWidthMax = 0;
	int counter = 1;
	int inputType = InputTypes.FLOAT;
	InputBoxListener mListener;

	InputBox(Drehsystem3d drehsystem3d, String title, String[] values) {
		InputBox = drehsystem3d;
		init(title, values, null, false);
	}

	InputBox(Drehsystem3d drehsystem3d, String title, String[] values, String[] standardValues) {
		InputBox = drehsystem3d;
		init(title, values, standardValues, true);
	}

	InputBox(Drehsystem3d drehsystem3d, String title, String[] values, String[] descValues, boolean stdValue) {
		InputBox = drehsystem3d;
		init(title, values, descValues, stdValue);
	}

	private void init(String title, String[] values, String[] descValues, boolean stdValue) {
		this.values = values;
		if (descValues != null) {
			if (descValues.length == values.length) {
				if (stdValue)
					this.standardValues = descValues;
				else
					this.hintValues = descValues;
			}
		}
		String[] args = { "InputBox" };
		this.title = title;
		PApplet.runSketch(args, this);
		surface.setTitle(this.title);
	}

	public void settings() {
		size(this.tvWidth + this.tbWidth + 20, 200);
		this.windowWidth = this.width;
		this.windowHeight = this.height;
	}

	public void setup() {
		surface.setTitle(this.title);
		this.xStart = this.padding;
		this.yStart = this.padding;
		// println("width:"+this.width);
		// println("height:"+this.height);
		for (int i = 0; i < values.length; i++) {
			addTextView(values[i]);
			addTextBox();
		}
		for (TextView tv : textviews)
			tv.setWidth(tvWidthMax);
		for (TextBox tb : textboxes)
			tb.setWidth(tbWidthMax);
		bSubmit = new Button(this, xStart, yStart, 100, 50, "Submit");
		bSubmit.setBackground(255);
		bSubmit.setTextColor(0);
		bSubmit.setTextSize(25);
		bSubmit.setPadding(10);
		bSubmit.setHorizontalAlignment(TextView.ALIGNMENT_CENTER);
		bSubmit.setVerticalAlignment(TextView.ALIGNMENT_BOTTOM);
		bSubmit.setOnClickListener(this);
		bSubmit.setOnAnimationFinishedListener(this);
		this.yMax = this.yStart + this.tbHeight + this.padding;
		this.xMax += this.tvWidth + 2 * this.padding;
		if (this.xMax > this.windowWidth) {
			this.windowWidth = this.xMax;
		}
		if (this.yMax > this.windowHeight) {
			this.windowHeight = this.yMax;
		}
		surface.setSize(this.windowWidth, this.windowHeight);
	}

	public void setMaxLimits(float limit) {
		float[] limits = new float[values.length];
		for (int i = 0; i < values.length; i++) {
			limits[i] = limit;
		}
		setMaxLimits(limits);
	}

	public void setMaxLimits(float[] limits) {
		if (limits.length != values.length)
			return;
		this.limitsMax = limits;
	}

	public void setMinLimits(float limit) {
		float[] limits = new float[values.length];
		for (int i = 0; i < values.length; i++) {
			limits[i] = limit;
		}
		setMinLimits(limits);
	}

	public void setMinLimits(float[] limits) {
		this.limitsMin = limits;
	}

	public void setInputType(int type) {
		this.inputType = type;
	}

	public void addTextView(String text) {
		TextView tv = new TextView(this, this.xStart, this.yStart, this.tvWidth, this.tvHeight);
		tv.setMargin(5);
		tv.setText(text);
		tv.setTextSize(30);
		tv.setTextAlignment(TextView.TEXTALIGNMENT_CENTER);
		if (tv.viewWidth > tvWidthMax)
			tvWidthMax = tv.viewWidth;
		this.textviews.add(tv);
	}

	public void addTextBox() {
		final TextBox tb = new TextBox(InputBox, this, this.xStart + this.tvWidth, this.yStart, this.tbWidth,
				this.tbHeight);
		tb.setMargin(5);
		tb.setTextSize(30);
		tb.setId(this.counter);
		if (this.standardValues != null) {
			tb.setStandardText(this.standardValues[this.counter - 1]);
		} else if (this.hintValues != null) {
			tb.setHint(this.hintValues[this.counter - 1]);
		}

		// else {
		// tb.setStandardText("0.0");
		// }
		tb.setInputType(this.inputType);
		tb.setTextAlignment(TextView.TEXTALIGNMENT_RIGHT);
		// tb.setHint("Enter value");
		tb.setTextBoxListener(new TextBoxListener() {
			@Override
			public void textEdited(int id, String text) {
				println("Text Edited");
				if (tb.inputType == InputTypes.FLOAT) {
					float value;
					try {
						value = Float.parseFloat(text);
						if (limitsMax != null && limitsMax.length > id - 1) {
							float maxValue = limitsMax[id - 1];
							// println("maxValue:"+maxValue);
							if (value > maxValue) {
								tb.setText(Float.toString(maxValue));
								return;
							}
							float minValue = limitsMin[id - 1];
							// println("minValue:"+minValue);
							if (value < minValue) {
								tb.setText(Float.toString(minValue));
								return;
							}
						}
					} catch (NumberFormatException e) {
						println(e);
						tb.setText(Float.toString(limitsMax[id - 1]));
					}
				} else if (tb.inputType == InputTypes.INTEGER) {
					int value;
					try {
						value = Integer.parseInt(text);
						if (limitsMax != null && limitsMax.length > id - 1) {
							int maxValue = (int) limitsMax[id - 1];
							// println("maxValue:"+maxValue);
							if (value > maxValue) {
								tb.setText(Integer.toString(maxValue));
								return;
							}
							int minValue = (int) limitsMin[id - 1];
							// println("minValue:"+minValue);
							if (value < minValue) {
								tb.setText(Integer.toString(minValue));
								return;
							}
						}
					} catch (NumberFormatException e) {
						println(e);
						tb.setText(Integer.toString((int) limitsMax[id - 1]));
					}
				}
			}

			@Override
			public void previousTextBox(int id, int cursorPosX) {
				// println("\nnextTextBox");
				// println("id:"+id);
				// for (TextBox textBox : textboxes) {
				// println("tb id:" + textBox.getId());
				// }
				for (int i = 0; i < textboxes.size(); i++) {
					if (textboxes.get(i).getId() == id) {
						int next = i == 0 ? textboxes.size() - 1 : i - 1;
						textboxes.get(next).setClicked(true, cursorPosX);
					}
				}
			}

			@Override
			public void nextTextBox(int id, int cursorPosX) {
				// println("\nnextTextBox");
				// println("id:"+id);
				// for (TextBox textBox : textboxes) {
				// println("tb id:" + textBox.getId());
				// }

				for (int i = 0; i < textboxes.size(); i++) {
					if (textboxes.get(i).getId() == id) {
						int next = (i + 1) % textboxes.size();
						textboxes.get(next).setClicked(true, cursorPosX);
						println("i:" + i);
						println("tb id:" + id);
						println("size:" + textboxes.size());
						println("next:" + next);
					}
				}
			}
		});
		tb.setKeyListener(new KeyListener() {
			@Override
			public void onKeyPressed(int pressedKeyCode, char pressedKey) {
				if (pressedKeyCode == 10) {
					// println("\nnextTextBox");
					tb.clicked = false;
					int id = tb.getId();
					// println("id:"+id);
					// for (TextBox textBox : textboxes) {
					// println("tb id:" + textBox.getId());
					// }

					for (int i = 0; i < textboxes.size(); i++) {
						if (textboxes.get(i).getId() == id) {
							int next = i + 1;
							if (next > textboxes.size() - 1) {
								finish();
								return;
							}
							textboxes.get(next).setClicked(true, tb.calcCharPos(tb.cursorPos));
							println("i:" + i);
							println("tb id:" + id);
							println("size:" + textboxes.size());
							println("next:" + next);
							break;
						}
					}
				}
			}

			@Override
			public void onKeyReleased(int pressedKeyCode, char pressedKey) {
			}
		});
		if (tb.viewWidth > this.xMax) {
			this.xMax = tb.viewWidth;
		}
		if (tb.viewWidth > tbWidthMax)
			tbWidthMax = tb.viewWidth;
		this.textboxes.add(tb);
		this.yStart += this.tbHeight + 10;
		this.counter++;
	}

	public void setOnEditingFinishedListener(InputBoxListener listener) {
		this.mListener = listener;
	}

	public void mousePressed() {
		bSubmit.mousePressedEvent();
		for (TextBox tb : textboxes) {
			tb.mousePressedEvent();
		}
	}

	public void mouseReleased() {
		for (TextBox tb : textboxes) {
			tb.mouseReleasedEvent();
		}
	}

	public void mouseDragged() {
		for (TextBox tb : textboxes) {
			tb.mouseDraggedEvent();
		}
	}

	public void keyPressed() {
		for (TextBox tb : textboxes) {
			tb.handleKeyPressedEvent(keyCode, key);
		}
	}

	public void keyReleased() {
		for (TextBox tb : textboxes) {
			tb.handleKeyReleasedEvent(keyCode, key);
		}
	}

	public void draw() {
		background(0);
		for (TextView tv : this.textviews) {
			tv.draw();
		}
		for (TextBox tb : this.textboxes) {
			tb.draw();
		}
		bSubmit.draw();

		// noFill();
		// stroke(255);
		// strokeWeight(1);
		// rect(0, 0, 100, 100);
	}

	@Override
	public void exit() {
		this.getSurface().setVisible(false);
		this.visible = false;
		this.exit = true;
		if (this.mListener != null)
			this.mListener.onExit();
		this.noLoop();
	}

	public void exited() {
		this.exit = false;
	}

	public void pause() {
		this.getSurface().setVisible(false);
		this.visible = false;
		this.noLoop();
	}

	public void resume() {
		this.getSurface().setVisible(true);
		this.visible = true;
		this.exit = false;
		this.loop();
	}

	public boolean isVisible() {
		return this.visible;
	}

	public boolean waitingForExit() {
		return this.exit;
	}

	public String getName() {
		return this.title;
	}

	public void finish() {
		String[] data = new String[this.textboxes.size()];
		for (int i = 0; i < this.textboxes.size(); i++) {
			data[i] = this.textboxes.get(i).getText();
		}
		if (this.mListener != null)
			this.mListener.finishedEditing(data);
		exit();
	}

	@Override
	public void textEdited(int id, String text) {
	}

	@Override
	public void previousTextBox(int id, int cursorPosX) {
	}

	@Override
	public void nextTextBox(int id, int cursorPosX) {
	}

	@Override
	public void onClick(int id) {
	}

	@Override
	public void onAnimationFinished() {
		finish();
	}
}