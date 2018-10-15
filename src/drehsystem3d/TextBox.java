package drehsystem3d;

import java.util.ArrayList;

import drehsystem3d.Listener.*;

import processing.core.PApplet;
import processing.core.PVector;

class TextBox extends TextView
{ // <>// //<>//
	/**
	 * 
	 */
	ArrayList<Integer> keysPressed = new ArrayList<Integer>();
	boolean mouseDrag = false;
	int markedAreaStart = 0;
	int markedAreaLength = 0;
	int inputType = InputTypes.ALL;
	// int textAlignment = 0;
	TextBoxListener mListener;
	KeyListener keyListener;
	int id = -1;
	int margin = 5;
	int cursorPos = 0;
	int dragCursorPos = 0;
	float cursorPosX;
	float dragCursorPosX;
	PVector pos;
	// int setWidth = 0, setHeight = 0;
	// int viewWidth = 0, viewHeight = 0;
	String hint = "";
	String standardText = "";
	String outputText = "";
	String input = "";
	String text = "";
	int textSize = 20;
	boolean setClicked = false;
	boolean clicked = false;
	int cursorTimer = 0;
	boolean cursorVisible = false;
	PApplet context = null;

	TextBox(PApplet context, int posX, int posY)
	{
		super(context, posX, posY);
		if (context == null)
			return;
		this.context = context;
		pos = new PVector(posX, posY, 0);
	}

	TextBox(PApplet context, int posX, int posY, int w, int h)
	{
		super(context, posX, posY, w, h);
		if (context == null)
			return;
		this.context = context;
		pos = new PVector(posX, posY, 0);
		this.setWidth = w;
		this.setHeight = h;
		calcWidth();
	}

	TextBox(PApplet context, PVector pos)
	{
		super(context, pos);
		if (context == null)
			return;
		this.context = context;
		pos = new PVector(pos.x, pos.y, 0);
	}

	TextBox(PApplet context, PVector pos, int w, int h)
	{
		super(context, pos, w, h);
		if (context == null)
			return;
		this.context = context;
		pos = new PVector(pos.x, pos.y, 0);
		this.setWidth = w;
		this.setHeight = h;
		calcWidth();
	}

	public void setTextBoxListener(TextBoxListener listener)
	{
		this.mListener = listener;
	}

	public void setKeyListener(KeyListener listener)
	{
		this.keyListener = listener;
	}

	public void setHint(String hint)
	{
		this.hint = hint;
		updateText();
	}

	public void setStandardText(String standardText)
	{
		this.standardText = standardText;
		updateText();
	}

	public void setText(String text)
	{
		this.text = text;
		this.input = text;
		updateText();
	}

	public String getText()
	{
		String text = this.clicked ? this.input : this.text;
		if (text.equals("") && !this.standardText.equals(""))
			text = this.standardText;
		return text;
	}

	public void setTextSize(int size)
	{
		this.textSize = size;
		updateText();
	}

	public void setTextAlignment(int alignment)
	{
		if (alignment >= 0 && alignment < 3)
			this.textAlignment = alignment;
	}

	public void setMargin(int margin)
	{
		this.margin = margin;
	}

	public void mousePressedEvent()
	{
		float mX = this.context.mouseX;
		float mY = this.context.mouseY;
		if (mX >= this.pos.x && mX <= this.pos.x + this.viewWidth && mY >= this.pos.y
				&& mY <= this.pos.y + this.viewHeight)
		{
			this.mouseDrag = true;
			this.clicked = true;
			updateText();
			this.cursorPos = calcClosestCharPos(mX);
			this.dragCursorPos = this.cursorPos;
			this.cursorPosX = calcCharPos(this.cursorPos); // (int)(posX+textWidth(outputText.substring(0,
															// this.cursorPos)))
			this.dragCursorPosX = this.cursorPosX;
			resetCursor();
		} else if (this.clicked)
		{
			this.clicked = false;
			this.mouseDrag = false;
			textEdited();
		}
	}

	public void mouseReleasedEvent()
	{
		this.markedAreaStart = this.dragCursorPos < this.cursorPos ? this.dragCursorPos : this.cursorPos;
		this.markedAreaLength = Drehsystem3d.abs(this.dragCursorPos - this.cursorPos);
		// if (this.markedAreaLength > 0 && this.input.length() > 0) {
		// String marked = this.input.substring(this.markedAreaStart,
		// this.markedAreaLength+this.markedAreaStart);
		// println(marked);
		// }
		this.mouseDrag = false;
	}

	public void mouseDraggedEvent()
	{
		if (this.mouseDrag)
		{
			float posX = calcAlignment();
			// String outputText = clicked ? this.input : this.text;
			float min = this.viewWidth;
			for (int i = 0; i < outputText.length() + 1; i++)
			{
				float mX = this.context.mouseX - posX;
				String subString = outputText.substring(0, i);
				float dist = Drehsystem3d.abs(this.context.textWidth(subString) - mX);
				if (dist < min)
				{
					min = dist;
					this.cursorPos = i;
				}
			}
			this.cursorPosX = calcCharPos(this.cursorPos);
		}
	}

	public void handleKeyPressedEvent(int pressedKeyCode, char pressedKey)
	{
		if (this.clicked)
		{
			if (isPrintableChar(pressedKey) && this.markedAreaLength > 0)
			{
				deleteMarkedInputChars();
			}
			switch (pressedKeyCode)
			{
				case 8:
					if (this.markedAreaLength > 0)
						deleteMarkedInputChars();
					else
						deleteInputChar(this.cursorPos, -1);
					break;

				case 10:
					textEdited();
					break;

				case 32:
					if (this.inputType == InputTypes.ALL || this.inputType == InputTypes.STRING)
					{
						addInputChar(this.cursorPos, pressedKey);
					}
					break;

				case 37:
					if (keyIsPressed(16))
					{
						if (this.markedAreaLength == 0)
						{
							if (this.cursorPos > 0)
								this.markedAreaStart = this.cursorPos - 1;
							this.markedAreaLength = 1;
						} else if (this.markedAreaStart == this.cursorPos)
						{
							if (this.markedAreaStart > 0)
							{
								this.markedAreaStart--;
								this.markedAreaLength++;
							}
						} else
						{
							this.markedAreaLength--;
						}
					} else
					{
						this.markedAreaStart = 0;
						this.markedAreaLength = 0;
					}
					updateCursor(-1);
					break;

				case 38:
					textEdited();
					if (this.mListener != null)
						this.mListener.previousTextBox(this.id, calcCharPos(this.cursorPos));
					break;

				case 39:
					if (keyIsPressed(16))
					{
						if (this.markedAreaLength == 0)
						{
							this.markedAreaStart = this.cursorPos;
							this.markedAreaLength = 1;
						} else if (this.markedAreaStart == this.cursorPos)
						{
							this.markedAreaStart++;
							this.markedAreaLength--;
						} else
						{
							if (this.cursorPos < this.input.length())
								this.markedAreaLength++;
						}
					} else
					{
						this.markedAreaStart = 0;
						this.markedAreaLength = 0;
					}
					updateCursor(1);
					break;

				case 40:
					textEdited();
					if (this.mListener != null)
						this.mListener.nextTextBox(this.id, calcCharPos(this.cursorPos));
					break;

				case 127:
					if (this.markedAreaLength > 0)
						deleteMarkedInputChars();
					else
						deleteInputChar(this.cursorPos, 0);
					break;

				default:
					if (isPrintableChar(pressedKey))
					{
						switch (this.inputType)
						{
							case InputTypes.INTEGER:
								try
								{
									Integer.parseInt("" + pressedKey);
								} catch (NumberFormatException e)
								{
									Drehsystem3d.println(e);
									if (!(this.cursorPos == 0 && pressedKey == '-' && !this.input.contains("-")))
										return;
								}
								if (this.cursorPos == 0 && this.input.contains("-"))
									return;
								addInputChar(this.cursorPos, pressedKey);
								break;

							case InputTypes.FLOAT:
								try
								{
									Float.parseFloat("" + pressedKey);
								} catch (NumberFormatException e)
								{
									Drehsystem3d.println(e);
									if (!(!this.input.contains(".") && pressedKey == '.') && pressedKey != '-')
										return;
								}
								if (pressedKey == '-')
								{
									if (this.input.contains("-"))
									{
										deleteInputChar(1, -1);
									} else
									{
										addInputChar(0, pressedKey);
									}
								} else
								{
									if (this.cursorPos == 0 && this.input.contains("-"))
										return;
									addInputChar(this.cursorPos, pressedKey);
								}
								break;

							default:
								addInputChar(this.cursorPos, pressedKey);
						}
					} else
					{
						boolean keyExists = false;
						for (int kc : keysPressed)
						{
							if (kc == pressedKeyCode)
							{
								keyExists = true;
								break;
							}
						}
						if (!keyExists)
							keysPressed.add(pressedKeyCode);
					}
			}
			if (this.keyListener != null)
			{
				this.keyListener.onKeyPressed(pressedKeyCode, pressedKey);
			}
			// println("input:"+this.input);
		}
	}

	public boolean keyIsPressed(int keyCode)
	{
		boolean keyPressed = false;
		for (int kc : keysPressed)
		{
			if (kc == keyCode)
			{
				keyPressed = true;
				break;
			}
		}
		return keyPressed;
	}

	public void handleKeyReleasedEvent(int releasedKeyCode, char releasedKey)
	{
		for (int i = 0; i < keysPressed.size(); i++)
		{
			if (keysPressed.get(i) == releasedKeyCode)
			{
				keysPressed.remove(i);
				return;
			}
		}
	}

	public void addInputChar(int pos, char pressedKey)
	{
		String buffer = "";
		if (pos > 0)
		{
			buffer += this.input.substring(0, pos);
		}
		buffer += pressedKey;
		if (pos < this.input.length())
		{
			buffer += this.input.substring(pos, this.input.length());
		}
		this.input = buffer;
		updateText();
		updateCursor(1);
	}

	public void deleteInputChar(int pos, int dir)
	{
		if (dir != -1 && dir != 0)
			return;
		pos += dir;
		if (this.input.length() > 0)
		{
			if (pos < this.input.length())
			{
				String buffer = "";
				if (pos > 0)
					buffer += this.input.substring(0, pos);
				buffer += this.input.substring(pos + 1, this.input.length());
				this.input = buffer;
			}
			updateText();
			updateCursor(dir);
		}
	}

	public void deleteMarkedInputChars()
	{
		String buffer = "";
		if (this.markedAreaStart > 0)
		{
			buffer += this.input.substring(0, this.markedAreaStart);
		}
		if (this.markedAreaStart + this.markedAreaLength < this.input.length())
		{
			buffer += this.input.substring(this.markedAreaStart + this.markedAreaLength, this.input.length());
		}
		this.input = buffer;
		this.cursorPos = this.markedAreaStart;
		this.markedAreaStart = 0;
		this.markedAreaLength = 0;
		updateText();
		updateCursor(0);
	}

	public void textEdited()
	{
		this.text = this.input;
		this.clicked = false;
		// updateText();
		if (this.inputType == InputTypes.FLOAT)
		{
			if (this.input.length() > 0)
			{
				if (this.input.equals("."))
					this.text = "0.0";
				else if (this.input.charAt(this.input.length() - 1) == '.')
					this.text += "0";

				if (!this.text.contains("."))
				{
					if (this.input.equals("-"))
						this.text = "0.0";
					else
						this.text += ".0";
				} else
				{
					if (this.input.charAt(0) == '.')
					{
						String buffer = this.text;
						this.text = "0" + buffer;
					}
				}
			}
			this.input = this.text;
		} else if (this.inputType == InputTypes.INTEGER)
		{
			if (this.input.length() > 0)
			{
				if (this.input.equals("-"))
					this.text = "0";
			}
			this.input = this.text;
		}
		updateText();
		this.markedAreaStart = 0;
		this.markedAreaLength = 0;
		if (this.mListener != null)
		{
			this.mListener.textEdited(this.id, this.text);
		}
	}

	public void updateText()
	{
		this.outputText = this.clicked ? this.input : this.text;
		if (this.outputText.length() == 0)
		{
			if (!this.standardText.equals("") && !this.clicked)
			{
				// println("std text");
				this.outputText = this.standardText;
				this.input = this.outputText;
			} else if (!this.hint.equals(""))
			{
				this.outputText = this.hint;
			}
		}
		calcWidth();
		calcHeight();
	}

	public @Override void calcWidth()
	{
		this.context.textSize(this.textSize);
		// String shownText = clicked ? this.input : this.text;
		// if (shownText.equals("")) {
		// if (!this.standardText.equals("")) {
		// shownText = this.standardText;
		// } else if (!this.hint.equals("")) {
		// shownText = this.hint;
		// }
		// }
		int nWidth = (int) this.context.textWidth(this.outputText) + this.margin * 2;
		int newWidth = this.setWidth > nWidth ? this.setWidth : nWidth;
		this.viewWidth = newWidth;
		// if (this.width > this.setWidth) {
		// this.setWidth = this.width;
		// }
	}

	public @Override void calcHeight()
	{
		int nHeight = this.textSize + 10;
		int newHeight = this.setWidth > nHeight ? this.setHeight : nHeight;
		this.viewHeight = newHeight;
		// if (this.height > this.setHeight) {
		// this.setHeight = this.height;
		// }
	}

	public void updateCursor(int change)
	{
		if (change == -1 && this.cursorPos > 0)
			this.cursorPos--;
		else if (change == 1 && this.cursorPos < this.input.length())
			this.cursorPos++;
		this.cursorPosX = calcCharPos(this.cursorPos);
		resetCursor();
	}

	public void setInputType(int type)
	{
		this.inputType = type;
	}

	public void resetCursor()
	{
		this.cursorVisible = true;
		this.cursorTimer = context.millis();
	}

	public void setClicked(boolean clicked)
	{
		setClicked(clicked, 0);
	}

	public void setClicked(boolean clicked, int cursorPosX)
	{
		this.setClicked = clicked;
		if (this.setClicked)
		{
			this.cursorPos = calcClosestCharPos(cursorPosX);
			updateCursor(0);
		}
	}

	// boolean isClicked() {
	// return this.clicked;
	// }

	public void showCursor()
	{
		if (this.setClicked)
		{
			this.clicked = true;
			this.setClicked = false;
		}
		if (this.clicked)
		{
			if (context.millis() - cursorTimer >= 500)
			{
				cursorVisible = !cursorVisible;
				cursorTimer = context.millis();
			}
			float offset = (this.viewHeight - this.textSize) / 2;
			float y1 = this.pos.y + offset - 1;
			float y2 = this.pos.y + this.viewHeight - offset + 1;
			if (cursorVisible)
			{
				this.context.line(this.cursorPosX, y1, this.cursorPosX, y2);
			}
			if (this.mouseDrag)
			{
				this.context.fill(255, 255, 255, 100);
				this.context.noStroke();
				this.context.rect(this.cursorPosX, y1, this.dragCursorPosX - this.cursorPosX, y2 - y1);
			} else if (this.markedAreaLength > 0)
			{
				this.context.fill(255, 255, 255, 100);
				this.context.noStroke();
				int start = calcCharPos(this.markedAreaStart);
				int stop = calcCharPos(this.markedAreaLength + this.markedAreaStart) - start;
				this.context.rect(start, y1, stop, y2 - y1);
			}
		}
	}

	public int calcCharPos(int idx)
	{
		this.context.textSize(this.textSize);
		// String outputText = this.clicked ? this.input : this.text;
		String displayedText = getEditableText();
		if (displayedText.length() == 0 || idx < 0 || idx > displayedText.length())
			return (int) calcAlignment(displayedText);
		return (int) (calcAlignment() + this.context.textWidth(displayedText.substring(0, idx)));
	}

	public int calcClosestCharPos(float x)
	{
		int idx = -1;
		String displayedText = getEditableText(); // this.outputText;
		float posX = calcAlignment(displayedText);
		// String outputText = this.clicked ? this.input : this.text;
		float min = this.viewWidth;
		this.context.textSize(this.textSize);
		Drehsystem3d.println();
		for (int i = 0; i < displayedText.length() + 1; i++)
		{
			String subString = displayedText.substring(0, i);
			float dist = Drehsystem3d.abs(this.context.textWidth(subString) - (x - posX));
			if (dist < min)
			{
				min = dist;
				idx = i;
			}
			Drehsystem3d.println("dist:" + dist);
			Drehsystem3d.println("min:" + min);
			Drehsystem3d.println("idx:" + idx);
		}
		return idx;
	}

	public float calcAlignment()
	{
		return calcAlignment(this.outputText);
	}

	public float calcAlignment(String displayedText)
	{
		this.context.textSize(this.textSize);
		// String outputText = this.clicked ? this.input : this.text;
		// if (outputText.equals("")) {
		// if (!this.standardText.equals("")) outputText = this.standardText;
		// else if (!this.hint.equals("")) outputText = this.hint;
		// }
		float posX = this.pos.x;
		float offset = 0;
		switch (this.textAlignment)
		{
			case TextView.TEXTALIGNMENT_LEFT:
				posX += this.margin;
				break;

			case TextView.TEXTALIGNMENT_RIGHT:
				offset = this.viewWidth - this.context.textWidth(displayedText) - this.margin;
				if (offset < this.margin)
					offset = this.margin;
				posX += offset;
				break;

			case TextView.TEXTALIGNMENT_CENTER:
				offset = (this.viewWidth - this.context.textWidth(displayedText)) / 2;
				if (offset < this.margin)
					offset = this.margin;
				posX += offset;
				break;
		}
		return posX;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public String getEditableText()
	{
		return this.outputText.equals(this.hint) ? "" : this.outputText;
	}

	public boolean isPrintableChar(char c)
	{
		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
		return (!Character.isISOControl(c)) && block != null && block != Character.UnicodeBlock.SPECIALS;
	}

	public void draw()
	{
		super.draw();
		this.context.noFill();
		this.context.stroke(255);
		this.context.strokeWeight(1);
		this.context.textSize(this.textSize);
		this.context.rect(this.pos.x, this.pos.y, this.viewWidth, this.viewHeight);
		// outputText = this.clicked ? this.input : this.text;
		float offset = (this.viewHeight - this.textSize) / 2 + 2;
		float posX = calcAlignment();
		if (offset < 0)
			offset = 0;
		// if (!this.clicked && outputText.equals("")) {
		// if (!this.hint.equals("")) {
		// outputText = this.hint;
		// this.context.fill(100);
		// } else if (!this.standardText.equals("")) {
		// outputText = this.standardText;
		// this.context.fill(255);
		// }
		// } else {
		// this.context.fill(255);
		// }
		if (this.outputText.equals(this.hint))
			this.context.fill(100);
		else
			this.context.fill(255);
		this.context.text(outputText, posX, this.pos.y + this.viewHeight - offset);
		showCursor();
	}
}