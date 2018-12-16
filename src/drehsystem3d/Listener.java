package drehsystem3d;

public class Listener
{
	public interface OnClickListener
	{
		public void onClick(View v);
	}

	public interface OnHoverListener
	{
		public void onHover(View v);
	}

	public interface OnItemClickListener
	{
		public void onItemClick(int itemIdx, String item);
	}

	public interface TextBoxListener
	{
		public void textEditingFinished(TextBox textBox, String text);
		
		public void textEdited(TextBox textBox, String text);

		public void previousTextBox(TextBox textBox, int cursorPosX);

		public void nextTextBox(TextBox textBox, int cursorPosX);
	}

	public interface UIListener
	{
		public void drawUI();
	}

	public interface OnAnimationFinishedListener
	{
		public void onAnimationFinished();
	}

	public interface InputBoxListener
	{
		public void finishedEditing(String... data);

		public void onExit();
	}

	public interface UserInputListener
	{
		public boolean onKeyPressed(int keyCode, char key);

		public boolean onKeyReleased(int keyCode, char key);

		public boolean onMousePressed(int mouseButton);

		public void onMouseDragged();

		public void onMouseReleased(int mouseButton);
	}

	public interface KeyListener
	{
		public boolean onKeyPressed(int keyCode, char key);

		public boolean onKeyReleased(int keyCode, char key);
	}

	public interface WindowResizeListener
	{
		public void onWindowResize(int widthOld, int heightOld, int widthNew, int heightNew);
	}
}
