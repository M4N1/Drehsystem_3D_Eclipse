package drehsystem3d;

public class Listener
{
	public interface OnClickListener
	{
		public void onClick(int id);
	}

	public interface OnHoverListener
	{
		public void onHover(int id);
	}

	public interface OnItemClickListener
	{
		public void onItemClick(int itemIdx, String item);
	}

	public interface TextBoxListener
	{
		public void textEdited(int id, String text);

		public void previousTextBox(int id, int cursorPosX);

		public void nextTextBox(int id, int cursorPosX);
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
}
