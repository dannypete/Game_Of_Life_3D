/**
 * Daniel Peterson
 * CS 351
 *
 * This class describes the object that makes up the board in the GameBoard class. These cells have several variables,
 * but the main one, active, describes if the cell is alive or not.
 */

package Game_Of_Life;

import javafx.scene.DepthTest;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class Cell
{

  // Is the cell alive
  private boolean active;

  // The cell's JavaFX box
  private Box box;

  // The box's container. Xform has some handy methods for rotation and translation, which is why they are used.
  private Xform xformContainer;

  // Is the cell new and should be bright and growing in the GUI
  private boolean grow;

  // Is the cell dying and should be dimming and shrinking in the GUI
  private boolean shrink;

  // The int 0-255 of the cell's box
  private int currColorNum;

  // The amount of neighbors the cell currently has
  private int neighbors;

  /**
   * Creates a new Cell object. The cells make up the gameBoard and are a compilation of data used to keep track of
   * the state of the cell both "behind the scenes"/logically and graphically in the GUI.
   */
  public Cell ()
  {
    active = false;
    grow = false;
    shrink = false;

    currColorNum = 0;
    neighbors = 0;

    box = new Box(1, 1, 1);
    box.setVisible(false);
    xformContainer = new Xform();

    xformContainer.setDepthTest(DepthTest.ENABLE);

    xformContainer.getChildren().add(box);
  }

  /**
   * Gets the cell's Xform. All of the cell's boxes go into Xforms because Xforms have very handy methods for
   * rotation, translation, etc.
   *
   * @return The cell's Xform box container.
   */
  public Xform getXformContainer ()
  {
    return xformContainer;
  }

  /**
   * Gets the cell's current status.
   *
   * @return The cell's status (true is alive and false is dead).
   */
  public boolean isActive ()
  {
    return active;
  }

  /**
   * Sets the cell's current status (true is alive and false is dead).
   *
   * @param value The value to set the cell's status to.
   */
  public void setActive (boolean value)
  {
    active = value;
  }

  /**
   * Gets the cell's GUI box to alter its properties.
   *
   * @return The cell's JavaFX box.
   */
  public Box getBox ()
  {
    return box;
  }

  /**
   * Gets the value of the cell's "is this a new cell that should be bright and grow box size" boolean.
   *
   * @return The cell's "should I be bright and grow box size" boolean.
   */
  public boolean getGrow ()
  {
    return grow;
  }

  /**
   * Sets the cell's "is this a new cell that should be bright and grow box size" boolean.
   *
   * @param value The cell's "should I be bright and grow box size" new value.
   */
  public void setGrow (boolean value)
  {
    grow = value;
  }

  /**
   * Gets the value of the cell's "is this a dying cell that should be dim and shrink in box size" boolean.
   *
   * @return The cell's "is this a dying cell that should be dim and shrink in box size" boolean.
   */
  public boolean getShrink ()
  {
    return shrink;
  }

  /**
   * Sets the value of the cell's "is this a dying cell that should be dim and shrink in box size" boolean.
   *
   * @param value The new value of the cell's "is this a dying cell that should be dim and shrink in box size" boolean.
   */
  public void setShrink (boolean value)
  {
    shrink = value;
  }

  /**
   * Returns the cell's current color number. Only one number changes for the Color.rgb() method, so I only need to
   * return the changing value. The rest are static values.
   * @return
   */
  public int getCurrColorNum ()
  {
    return currColorNum;
  }

  /**
   * Sets the cell box's color. I set two of the Color.rgb() values to the same value for a more pronounced change when
   * the cell is being born or dying. Also sets the box's color.
   *
   * @param value The new value for the box's color.
   */
  public void setCurrColorNum (int value)
  {
    currColorNum = value;
    box.setMaterial(new PhongMaterial(Color.rgb(value, value, 0, 1)));
  }

  public void setBoxDimensions (double value)
  {
    box.setHeight(value);
    box.setWidth(value);
    box.setDepth(value);
  }

  /**
   * Gets the current cell's number of current neighbors for calculations.
   *
   * @return The amount of neighbors of the current cell.
   */
  public int getNeighbors ()
  {
    return neighbors;
  }

  /**
   * Sets the cell's current neighbors during calculations.
   *
   * @param value The cell's amount of current neighbors.
   */
  public void setNeighbors (int value)
  {
    neighbors = value;
  }
}
