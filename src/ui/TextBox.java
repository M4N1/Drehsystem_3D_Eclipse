package ui;

import java.util.ArrayList;
import java.util.logging.Level;

import drehsystem3d.Drehsystem3d;
import drehsystem3d.Global;
import drehsystem3d.Listener.KeyListener;
import drehsystem3d.Listener.TextBoxListener;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class TextBox extends TextView
{
	
	protected ArrayList<Integer> keysPressed = new ArrayList<>();
	protected boolean mouseDrag = false;
	protected int markedAreaStart = 0;
	protected int markedAreaLength = 0;
	protected int inputType = InputTypes.ALL;
	protected TextBoxListener mListener;
	protected KeyListener keyListener;
	protected int cursorPos = 0;
	protected int dragCursorPos = 0;
	protected float cursorPosX;
	protected float dragCursorPosX;
	protected String hint = "";
	protected String standardText = "";
	protected String outputText = "";
	protected String input = "";
	protected boolean setClicked = false;
	protected int cursorTimer = 0;
	protected boolean cursorVisible = false;

	public TextBox(PApplet context, String name, int posX, int posY)
	{
		super(context, name, posX, posY);
	}

	public TextBox(PApplet context, String name, int posX, int posY, int w, int h)
	{
		super(context, name, posX, posY, w, h);
		//calcWidth();
	}

	public TextBox(PApplet context, String name, PVector pos)
	{
		super(context, name, pos);
	}

	public TextBox(PApplet context, String name, PVector pos, int w, int h)
	{
		super(context, name, pos, w, h);
		//calcWidth();
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

	@Override
	public void setText(String text)
	{
		this.text = text;
		this.input = text;
		updateText();
	}

	@Override
	public String getText()
	{
		Global.logger.log(Level.FINER, "getText", new Object[] {this.name, this.clicked, "'"+this.input+"'", "'"+this.text+"'"});
		String text = this.clicked ? this.input : this.text;
		if (text.equals("") && !this.standardText.equals(""))
		{
			text = this.standardText;
		}
		return text;
	}

	@Override
	public void setTextSize(int size)
	{
		this.textSize = size;
		updateText();
	}

	@Override
	public boolean onMousePressed(int mouseButton)
	{
		if (this.isHovered())
		{
			this.mouseDrag = true;
			this.clicked = true;
			updateText();
			this.cursorPos = calcClosestCharPos(this.context.mouseX);
			this.dragCursorPos = this.cursorPos;
			this.cursorPosX = calcCharPos(this.cursorPos);
			this.dragCursorPosX = this.cursorPosX;
			resetCursor();
		}
		else if (this.clicked)
		{
			this.clicked = false;
			this.mouseDrag = false;
			textEdited();
		}
		return super.onMousePressed(mouseButton);
	}

	@Override
	public void onMouseReleased(int mouseButton)
	{
		super.onMouseReleased(mouseButton);
		this.markedAreaStart = this.dragCursorPos < this.cursorPos ? this.dragCursorPos : this.cursorPos;
		this.markedAreaLength = Drehsystem3d.abs(this.dragCursorPos - this.cursorPos);
		this.mouseDrag = false;
	}

	@Override
	public void onMouseDragged()
	{
		super.onMouseDragged();
		if (this.mouseDrag)
		{
			float posX = calcAlignmentX();
			float min = this.width;
			for (int i = 0; i < this.outputText.length() + 1; i++)
			{
				float mX = this.context.mouseX - posX;
				String subString = this.outputText.substring(0, i);
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

	@Override
	public boolean onKeyPressed(int pressedKeyCode, char pressedKey)
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
					{
						deleteMarkedInputChars();
					}
					else
					{
						deleteInputChar(this.cursorPos, -1);
					}
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
							{
								this.markedAreaStart = this.cursorPos - 1;
							}
							this.markedAreaLength = 1;
						}
						else if (this.markedAreaStart == this.cursorPos)
						{
							if (this.markedAreaStart > 0)
							{
								this.markedAreaStart--;
								this.markedAreaLength++;
							}
						}
						else
						{
							this.markedAreaLength--;
						}
					}
					else
					{
						this.markedAreaStart = 0;
						this.markedAreaLength = 0;
					}
					updateCursor(-1);
					break;

				case 38:
					textEdited();
					if (this.mListener != null)
					{
						this.mListener.previousTextBox(this, calcCharPos(this.cursorPos));
					}
					break;

				case 39:
					if (keyIsPressed(16))
					{
						if (this.markedAreaLength == 0)
						{
							this.markedAreaStart = this.cursorPos;
							this.markedAreaLength = 1;
						}
						else if (this.markedAreaStart == this.cursorPos)
						{
							this.markedAreaStart++;
							this.markedAreaLength--;
						}
						else
						{
							if (this.cursorPos < this.input.length())
							{
								this.markedAreaLength++;
							}
						}
					}
					else
					{
						this.markedAreaStart = 0;
						this.markedAreaLength = 0;
					}
					updateCursor(1);
					break;

				case 40:
					textEdited();
					if (this.mListener != null)
					{
						this.mListener.nextTextBox(this, calcCharPos(this.cursorPos));
					}
					break;

				case 127:
					if (this.markedAreaLength > 0)
					{
						deleteMarkedInputChars();
					}
					else
					{
						deleteInputChar(this.cursorPos, 0);
					}
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
								}
								catch (NumberFormatException e)
								{
									Global.logger.log(Level.SEVERE, e.getStackTrace().toString());
									if (!(this.cursorPos == 0 && pressedKey == '-' && !this.input.contains("-")))
									{
										break;
									}
								}
								if (this.cursorPos == 0 && this.input.contains("-"))
								{
									break;
								}
								addInputChar(this.cursorPos, pressedKey);
								break;

							case InputTypes.FLOAT:
								try
								{
									Float.parseFloat("" + pressedKey);
								}
								catch (NumberFormatException e)
								{
									Global.logger.log(Level.SEVERE, e.getStackTrace().toString());
									if (!(!this.input.contains(".") && pressedKey == '.') && pressedKey != '-')
									{
										break;
									}
								}
								if (pressedKey == '-')
								{
									if (this.input.contains("-"))
									{
										deleteInputChar(1, -1);
									}
									else
									{
										addInputChar(0, pressedKey);
									}
								}
								else
								{
									if (this.cursorPos == 0 && this.input.contains("-"))
									{
										break;
									}
									addInputChar(this.cursorPos, pressedKey);
								}
								break;

							default:
								addInputChar(this.cursorPos, pressedKey);
						}
					}
					else
					{
						boolean keyExists = false;
						for (int kc : this.keysPressed)
						{
							if (kc == pressedKeyCode)
							{
								keyExists = true;
								break;
							}
						}
						if (!keyExists)
						{
							this.keysPressed.add(pressedKeyCode);
						}
					}
			}
			if (this.keyListener != null)
			{
				this.keyListener.onKeyPressed(pressedKeyCode, pressedKey);
			}
		}
		return this.clicked;
	}

	public boolean keyIsPressed(int keyCode)
	{
		boolean keyPressed = false;
		for (int kc : this.keysPressed)
		{
			if (kc == keyCode)
			{
				keyPressed = true;
				break;
			}
		}
		return keyPressed;
	}

	@Override
	public boolean onKeyReleased(int releasedKeyCode, char releasedKey)
	{
		for (int i = 0; i < this.keysPressed.size(); i++)
		{
			if (this.keysPressed.get(i) == releasedKeyCode)
			{
				this.keysPressed.remove(i);
				break;
			}
		}
		return super.onKeyReleased(releasedKeyCode, releasedKey);
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
		{
			return;
		}
		pos += dir;
		if (this.input.length() > 0)
		{
			if (pos < this.input.length())
			{
				String buffer = "";
				if (pos > 0)
				{
					buffer += this.input.substring(0, pos);
				}
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
		if (this.inputType == InputTypes.FLOAT)
		{
			if (this.input.length() > 0)
			{
				if (this.input.equals("."))
				{
					this.text = "0.0";
				}
				else if (this.input.charAt(this.input.length() - 1) == '.')
				{
					this.text += "0";
				}

				if (!this.text.contains("."))
				{
					if (this.input.equals("-"))
					{
						this.text = "0.0";
					}
					else
					{
						this.text += ".0";
					}
				}
				else
				{
					if (this.input.charAt(0) == '.')
					{
						String buffer = this.text;
						this.text = "0" + buffer;
					}
				}
			}
			this.input = this.text;
		}
		else if (this.inputType == InputTypes.INTEGER)
		{
			if (this.input.length() > 0)
			{
				if (this.input.equals("-"))
				{
					this.text = "0";
				}
			}
			this.input = this.text;
		}
		updateText();
		this.markedAreaStart = 0;
		this.markedAreaLength = 0;
		if (this.mListener != null)
		{
			this.mListener.textEditingFinished(this, this.text);
		}
		Global.logger.log(Level.FINER, "textEdited", new Object[] {this.name, this.text});
	}

	public void updateText()
	{
		this.outputText = this.clicked ? this.input : this.text;
		if (this.outputText.length() == 0)
		{
			if (!this.standardText.equals("") && !this.clicked)
			{
				this.outputText = this.standardText;
				this.input = this.outputText;
			}
			else if (!this.hint.equals(""))
			{
				this.outputText = this.hint;
			}
		}
		else if (this.inputType == InputTypes.INTEGER || this.inputType == InputTypes.FLOAT)
		{
			
		}
		
		if (this.mListener != null)
		{
			this.mListener.textEdited(this, outputText);
		}
		calcWidth();
		calcHeight();
	}

	public void updateCursor(int change)
	{
		if (change == -1 && this.cursorPos > 0)
		{
			this.cursorPos--;
		}
		else if (change == 1 && this.cursorPos < this.input.length())
		{
			this.cursorPos++;
		}
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
		this.cursorTimer = this.context.millis();
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

	public void showCursor()
	{
		if (this.setClicked)
		{
			this.clicked = true;
			this.setClicked = false;
		}
		if (this.clicked)
		{
			if (this.context.millis() - this.cursorTimer >= 500)
			{
				this.cursorVisible = !this.cursorVisible;
				this.cursorTimer = this.context.millis();
			}
			float offset = (this.height - this.textSize) / 2;
			float posY = getActualPos().y;
			float y1 = posY + offset - 1;
			float y2 = posY + this.height - offset + 1;
			if (this.cursorVisible)
			{
				this.canvas.line(this.cursorPosX, y1, this.cursorPosX, y2);
			}
			if (this.mouseDrag)
			{
				this.canvas.fill(255, 255, 255, 100);
				this.canvas.noStroke();
				this.canvas.rect(this.cursorPosX, y1, this.dragCursorPosX - this.cursorPosX, y2 - y1);
			}
			else if (this.markedAreaLength > 0)
			{
				this.canvas.fill(255, 255, 255, 100);
				this.canvas.noStroke();
				int start = calcCharPos(this.markedAreaStart);
				int stop = calcCharPos(this.markedAreaLength + this.markedAreaStart) - start;
				this.canvas.rect(start, y1, stop, y2 - y1);
			}
		}
	}

	public int calcCharPos(int idx)
	{
		this.canvas.textSize(this.textSize);
		String displayedText = getEditableText();
		if (displayedText.length() == 0 || idx < 0 || idx > displayedText.length())
		{
			return (int) calcAlignmentX(displayedText);
		}
		return (int) (calcAlignmentX() + this.canvas.textWidth(displayedText.substring(0, idx)));
	}

	public int calcClosestCharPos(float x)
	{
		int idx = -1;
		String displayedText = getEditableText();
		float posX = calcAlignmentX(displayedText);
		float min = this.width;
		this.canvas.textSize(this.textSize);
		for (int i = 0; i < displayedText.length() + 1; i++)
		{
			String subString = displayedText.substring(0, i);
			float dist = Drehsystem3d.abs(this.canvas.textWidth(subString) - (x - posX));
			if (dist < min)
			{
				min = dist;
				idx = i;
			}
		}
		return idx;
	}

	@Override
	protected float calcAlignmentX()
	{
		return calcAlignmentX(this.outputText);
	}

	/*private float calcAlignmentX(String displayedText)
	{
		this.canvas.textSize(this.textSize);
		float posX = getPos().x;
		float offset = 0;
		int paddingSpacingX = this.padding.getSpacingX();
		switch (this.textAlignment)
		{
			case LEFT:
				posX += this.getMarginX();
				break;

			case RIGHT:
				offset = this.width - this.canvas.textWidth(displayedText) - paddingSpacingX;
				if (offset < paddingSpacingX)
				{
					offset = paddingSpacingX;
				}
				posX += offset;
				break;

			case CENTER:
				offset = (this.width - this.canvas.textWidth(displayedText)) / 2;
				if (offset < paddingSpacingX)
				{
					offset = paddingSpacingX;
				}
				posX += offset;
				break;
		}
		return posX;
	}*/

	@Override
	public void setId(int id)
	{
		this.id = id;
	}

	@Override
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

	@Override
	public void draw(PGraphics canvas)
	{
		super.draw(canvas);
		/*this.canvas.noFill();
		this.canvas.stroke(255);
		this.canvas.strokeWeight(1);
		this.canvas.textSize(this.textSize);
		this.canvas.rect(this.pos.x, this.pos.y, this.width, this.height);*/
		float offset = (this.height - this.textSize) / 2 + 2;
		float posX = calcAlignmentX();
		float posY = getActualPos().y + this.height - offset;
		if (offset < 0)
		{
			offset = 0;
		}
		this.canvas.beginDraw();
		if (this.outputText.equals(this.hint))
		{
			this.canvas.fill(100);
		}
		else
		{
			this.canvas.fill(this.textColor.r, this.textColor.g, this.textColor.b, this.textColor.a);
		}
		this.canvas.text(this.outputText, posX, posY);
		showCursor();
		this.canvas.endDraw();
	}
}