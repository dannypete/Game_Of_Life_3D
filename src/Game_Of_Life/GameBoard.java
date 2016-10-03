/**
 * Daniel Peterson
 * CS 351
 *
 * This is the "back end" of the Game of Life. It has presets for the board and updates the board.
 */

package Game_Of_Life;

import java.util.Random;

public class GameBoard
{
  private static final double OFFSET = 1;

  private Cell gameBoard[][][];

  // Upper bound for new life
  private int r2;

  // Lower bound for new life
  private int r1;

  // Lower bound for dying cell
  private int r4;

  // Upper bound for killing cell
  private int r3;

  // The cubed dimensions of the game board grid
  private int dimensions;

  /**
   * Creates a new gameBoard object. This is the object responsible for the logic
   * behind this Game of Life program.
   *
   * @param r1         New appears if equal or more than r1.
   * @param r2         New appears if equal or less than r2.
   * @param r3         Cell dies if more than r3.
   * @param r4         Cell dies if less than r4.
   * @param dimensions Cubic dimensions of the gameboard.
   */
  public GameBoard (int r1, int r2, int r3, int r4, int dimensions)
  {

    this.r1 = r1;
    this.r2 = r2;
    this.r3 = r3;
    this.r4 = r4;
    this.dimensions = dimensions;

    gameBoard = new Cell[dimensions + 2][dimensions + 2][dimensions + 2];
    for (int i = 0; i < dimensions + 2; i++)
    {
      for (int j = 0; j < dimensions + 2; j++)
      {
        for (int k = 0; k < dimensions + 2; k++)
        {
          gameBoard[i][j][k] = new Cell();
          gameBoard[i][j][k].getXformContainer().setTranslate(
                  i - dimensions + (i * OFFSET) - 1,
                  j - dimensions + (j * OFFSET) - 1,
                  k - dimensions + (k * OFFSET) - 1);
        }
      }
    }
  }

  /**
   * Calculates the amount of neighbors each cell has.
   */
  private void calculateNeighbors ()
  {

    for (int i = 1; i <= dimensions; i++)
    {
      for (int j = 1; j <= dimensions; j++)
      {
        for (int k = 1; k <= dimensions; k++)
        {
          gameBoard[i][j][k].setNeighbors(0);
          for (int m = i - 1; m <= i + 1; m++)
          {
            for (int n = j - 1; n <= j + 1; n++)
            {
              for (int p = k - 1; p <= k + 1; p++)
              {
                if (m == i && n == j && p == k)
                {
                  continue;
                }
                else if (gameBoard[m][n][p].isActive())
                  gameBoard[i][j][k].setNeighbors(gameBoard[i][j][k].getNeighbors()+1);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Called to produce the next generation of cells. This method updates logic as well as some values used
   * by the GUI.
   *
   * A new cell is born if r1 <= cell's neighbors <= r2;
   * a cell dies if its neighbors > r3 or < r4.
   */
  public void updateBoard ()
  {
    calculateNeighbors();

    for (int i = 1; i <= dimensions; i++)
    {
      for (int j = 1; j <= dimensions; j++)
      {
        for (int k = 1; k <= dimensions; k++)
        {
          Cell currCell = gameBoard[i][j][k];
          if (!currCell.isActive())
          {
            if (gameBoard[i][j][k].getNeighbors() <= this.r2 && gameBoard[i][j][k].getNeighbors() >= this.r1)
            {
              currCell.setActive(true);
              currCell.setGrow(true);
              currCell.setCurrColorNum(255);
              currCell.setBoxDimensions(0);
            }
          }
          else if (currCell.isActive())
          {
            if (gameBoard[i][j][k].getNeighbors() < this.r4 || gameBoard[i][j][k].getNeighbors() > this.r3)
            {
              currCell.setActive(false);
              currCell.setShrink(true);
            }
          }
          else
          {
            System.out.println("Cell is not active... and not not active...");
          }
        }
      }
    }
  }

  /**
   * Sets the state of the gameBoard to one of the defined presets.
   *
   * @param value The preset to use - there are 4 valid presets to use (0-3, with 0 being an empty board).
   */
  public void preset (int value)
  {
    switch (value)
    {
      case 0:
        break;
      case 1:
        for (int i = 1; i <= dimensions; i++)
        {
          for (int j = 1; j <= dimensions; j++)
          {
            for (int k = 1; k <= dimensions; k++)
            {
              if (k % 2 == 0)
                gameBoard[i][j][k].setActive(true);
              else
                gameBoard[i][j][k].setActive(false);
            }
          }
        }
        break;
      case 2:
        gameBoard[1][1][1].setActive(true);
        gameBoard[2][1][1].setActive(true);
        gameBoard[1][2][1].setActive(true);
        break;
      case 3:
        gameBoard[15][15][15].setActive(true);
        gameBoard[15][15][16].setActive(true);
        gameBoard[15][16][15].setActive(true);
        gameBoard[15][16][16].setActive(true);

        gameBoard[16][15][15].setActive(true);
        gameBoard[16][15][16].setActive(true);
        gameBoard[16][16][15].setActive(true);
        gameBoard[16][16][16].setActive(true);
        break;
      case 4:
        Random rand = new Random();
        for (int i = 1; i <= dimensions; i++)
        {
          for (int j = 1; j <= dimensions; j++)
          {
            for (int k = 1; k <= dimensions; k++)
            {
              if (rand.nextInt(8) % 8 == 0)
              {
                gameBoard[i][j][k].setActive(true);
              }
            }
          }
        }
        break;
      default:
        System.out.println("What happened here? Invalid preset value.");
    }
  }

  /**
   * Gets the dimensions of the gameBoard grid.
   *
   * @return The int value of the board's beight/width/depth (its a cube).
   */
  public int getDimensions ()
  {
    return dimensions;
  }

  /**
   * Gets the gameBoard. Called by the GUI to show/color the cells.
   *
   * @return The 3D Cell array of the board.
   */
  public Cell[][][] getGameBoard ()
  {
    return gameBoard;
  }

}
