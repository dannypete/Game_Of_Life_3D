/**
 * Daniel Peterson
 * CS 351
 *
 * This class is the "front end" of the Game of Life. It responds to the GameBoard class and displays
 * boxes for cells on the screen.
 */

package Game_Of_Life;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

public class Main extends Application
{

  // Final values for the GUI and handling events
  private final Group mainRoot = new Group();
  private final Xform world = new Xform();
  private final Xform axisGroup = new Xform();
  private final PerspectiveCamera camera = new PerspectiveCamera(true);
  private final Xform cameraXform = new Xform();
  private final Xform cameraXform2 = new Xform();
  private final Xform cameraXform3 = new Xform();
  private static final double CAMERA_INITIAL_DISTANCE = -500;
  private static final double CAMERA_INITIAL_X_ANGLE = 40.0;
  private static final double CAMERA_INITIAL_Y_ANGLE = 320.0;
  private static final double CAMERA_NEAR_CLIP = 0.1;
  private static final double CAMERA_FAR_CLIP = 10000.0;
  private static final double AXIS_LENGTH = 50.0;
  private static final double MOUSE_SPEED = 10;
  private static final double TRACK_SPEED = .1;
  private static final double CONTROL_MULTIPLIER = 0.1;
  private static final double SHIFT_MULTIPLIER = 10.0;
  private static final double ROTATION_SPEED = 2.0;

  // Main scene
  private Scene mainScene;

  // The gameBoard object "backend" of the Game Of Life
  private GameBoard gameBoard;

  // For mouse handling calculations
  private double mousePosX;
  private double mousePosY;
  private double mouseOldX;
  private double mouseOldY;
  private double mouseDeltaX;
  private double mouseDeltaY;

  // Skips the first gameBoard update (it would make first generation on first handle of GameTimer)
  private boolean skip = true;

  // If the user is clicking or dragging (mouse events); used for stopping rotation during that time
  private boolean hasMouseEvent = false;

  // Used to toggle rotate
  private boolean rotate = true;

  // Used to delay 1 second between generations
  private long prevNow = 0;

  // Used to generate a preset board in gameBoard class
  private int presetValue = 0;

  @Override
  public void start (Stage primaryStage) throws Exception
  {
    // I was getting a frozen GUI when using ComboBox. Used the code (below) from the following source:
    // http://stackoverflow.com/questions/31786980/javafx-windows-10-combobox-error
    System.setProperty("glass.accessible.force", "false");

    mainRoot.getChildren().add(world);
    mainRoot.setDepthTest(DepthTest.ENABLE);

    world.setDepthTest(DepthTest.ENABLE);

    // The GUI scene
    mainScene = new Scene(mainRoot, 1024, 768, true);
    mainScene.setFill(Color.GREY);

    buildCamera();
    buildAxes();
    handleMouse(mainScene);
    handleKeyboard(mainScene);

    primaryStage.setTitle("Game Of Life 3-D");
    mainScene.setCamera(camera);

    // The scene where input is entered prior to showing the simulation
    Group inputRoot = new Group();
    Scene inputScene = new Scene(inputRoot);

    final GridPane grid = new GridPane();
    grid.setPadding(new Insets(10, 10, 10, 10));
    grid.setVgap(5);
    grid.setHgap(5);

    inputScene.setRoot(grid);

    // R1
    final TextField r1 = new TextField();
    r1.setPromptText("Enter your value for R1.");
    r1.setPrefColumnCount(10);
    r1.getText();
    GridPane.setConstraints(r1, 0, 0, 2, 1);
    grid.getChildren().add(r1);

    // R2
    final TextField r2 = new TextField();
    r2.setPromptText("Enter your value for R2.");
    GridPane.setConstraints(r2, 0, 1, 2, 1);
    grid.getChildren().add(r2);

    // R3
    final TextField r3 = new TextField();
    r3.setPrefColumnCount(15);
    r3.setPromptText("Enter your value for R3.");
    GridPane.setConstraints(r3, 0, 2, 2, 1);
    grid.getChildren().add(r3);

    // R4
    final TextField r4 = new TextField();
    r4.setPrefColumnCount(15);
    r4.setPromptText("Enter your value for R4.");
    GridPane.setConstraints(r4, 0, 3, 2, 1);
    grid.getChildren().add(r4);

    // Submit values and see simulation
    final Button submit = new Button("Submit");
    GridPane.setConstraints(submit, 1, 4, 1, 1);
    grid.getChildren().add(submit);

    // Select preset
    final ComboBox presets = new ComboBox();
    presets.getItems().addAll("PRESET", "Planes", "Three In Corner", "Center Cube", "No Reason", "Random");
    presets.setValue("PRESET");
    GridPane.setConstraints(presets, 0, 4, 1, 1);
    grid.getChildren().add(presets);
    presets.valueProperty().addListener(new ChangeListener()
    {
      @Override
      public void changed (ObservableValue observable, Object oldValue, Object newValue)
      {
        if (newValue instanceof String)
        {
          if (!newValue.equals("None"))
          {
            switch ((String) newValue)
            {
              case "Planes":
                presetValue = 1;
                r1.setText("8");
                r2.setText("10");
                r3.setText("12");
                r4.setText("6");
                break;
              case "Three In Corner":
                presetValue = 2;
                r1.setText("1");
                r2.setText("5");
                r3.setText("0");
                r4.setText("0");
                break;
              case "Center Cube":
                presetValue = 3;
                r1.setText("3");
                r2.setText("8");
                r3.setText("10");
                r4.setText("3");
                break;
              case "No Reason":
                presetValue = 1;
                r1.setText("3");
                r2.setText("7");
                r3.setText("11");
                r4.setText("8");
                break;
              case "Random":
                presetValue = 4;
                r1.setText("2");
                r2.setText("4");
                r3.setText("8");
                r4.setText("4");
                break;
            }

          }
          else if (!oldValue.equals("None"))
          {
            r1.setDisable(false);
            r2.setDisable(false);
            r3.setDisable(false);
            r4.setDisable(false);

            if (newValue.equals("None"))
            {
              presetValue = 0;
            }
          }
        }
      }
    });

    // Label to show illegal input errors from user
    final Label label = new Label();
    GridPane.setConstraints(label, 0, 5);
    GridPane.setColumnSpan(label, 2);
    grid.getChildren().add(label);

    submit.setOnAction(new EventHandler <ActionEvent>()
    {
      @Override
      public void handle (ActionEvent event)
      {
        if (presets.getValue().equals("PRESET") || r1.getText().equals("") || r2.getText().equals("") ||
                r3.getText().equals("") || r4.getText().equals(""))
        {
          label.setText("Not all fields have values.");
        }
        else
        {
          try
          {
            int numberR1 = Integer.parseInt(r1.getText());
            int numberR2 = Integer.parseInt(r2.getText());
            int numberR3 = Integer.parseInt(r3.getText());
            int numberR4 = Integer.parseInt(r4.getText());

            if (numberR1 > numberR2 || numberR3 < numberR4)
            {
              label.setText("Those values don't make sense.");
            }
            else
            {
              gameBoard = new GameBoard(numberR1, numberR2, numberR3, numberR4, 30);
              addBoxesToWorld();
              gameBoard.preset(presetValue);
              initializeBoxValues();
              primaryStage.setScene(mainScene);
              primaryStage.centerOnScreen();
              AnimationTimer gameLoop = new MainGameLoop();
              gameLoop.start();
            }

          } catch (NumberFormatException e)
          {
            label.setText("Invalid entry or entries.");
          }
        }
      }
    });

    primaryStage.setTitle("Game Of Life 3-D");
    primaryStage.setScene(inputScene);
    primaryStage.show();
  }

  /**
   * Add all of the cells to the world during initialization
   */
  private void addBoxesToWorld ()
  {
    Cell[][][] board = gameBoard.getGameBoard();
    for (int i = 1; i <= gameBoard.getDimensions(); i++)
    {
      for (int j = 1; j <= gameBoard.getDimensions(); j++)
      {
        for (int k = 1; k <= gameBoard.getDimensions(); k++)
        {
          world.getChildren().add(board[i][j][k].getXformContainer());
          board[i][j][k].setCurrColorNum(135);
        }
      }
    }
  }

  /**
   * Initialize the boxes values (size and dimensions)
   */
  private void initializeBoxValues ()
  {
    Cell[][][] board = gameBoard.getGameBoard();
    for (int i = 0; i < gameBoard.getDimensions(); i++)
    {
      for (int j = 0; j < gameBoard.getDimensions(); j++)
      {
        for (int k = 0; k < gameBoard.getDimensions(); k++)
        {
          Cell currCell = board[i][j][k];
          currCell.setBoxDimensions(0);
          if (currCell.isActive())
          {
            currCell.setGrow(true);
            currCell.setCurrColorNum(255);
            currCell.getBox().setVisible(true);
          }
          else
          {
            currCell.setCurrColorNum(135);
            currCell.getBox().setVisible(false);
          }
        }
      }
    }
  }

  /**
   * Update the color and dimensions of the boxes based on the state of the board.
   */
  private void updateBoxes ()
  {
    Cell[][][] board = gameBoard.getGameBoard();

    for (int i = 1; i <= gameBoard.getDimensions(); i++)
    {
      for (int j = 1; j <= gameBoard.getDimensions(); j++)
      {
        for (int k = 1; k <= gameBoard.getDimensions(); k++)
        {
          Cell currCell = board[i][j][k];

          if (currCell.getCurrColorNum() == 255)
            currCell.getBox().setVisible(true);

          if (!currCell.getGrow() && !currCell.getShrink())
            continue;
          else if (currCell.getGrow())
          {
            currCell.setCurrColorNum(currCell.getCurrColorNum() - 10);
            currCell.setBoxDimensions(currCell.getBox().getHeight() + .09);
            if (currCell.getCurrColorNum() < 155)
            {
              currCell.setGrow(false);
              currCell.setBoxDimensions(1);
            }
          }
          else if (currCell.getShrink())
          {
            currCell.setCurrColorNum(currCell.getCurrColorNum() - 10);
            currCell.setBoxDimensions(currCell.getBox().getHeight() - .09);
            if (currCell.getCurrColorNum() < 55)
            {
              currCell.setShrink(false);
              currCell.getBox().setVisible(false);
            }
          }
        }
      }
    }
  }

  /**
   * Build the camera
   */
  private void buildCamera ()
  {
    mainRoot.getChildren().add(cameraXform);
    cameraXform.getChildren().add(cameraXform2);
    cameraXform2.getChildren().add(cameraXform3);
    cameraXform3.getChildren().add(camera);
    cameraXform3.setRotateZ(180.0);

    camera.setNearClip(CAMERA_NEAR_CLIP);
    camera.setFarClip(CAMERA_FAR_CLIP);
    camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
    cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
    cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
  }

  /**
   * Build the axes in case the user wants to see them during simulation
   */
  private void buildAxes ()
  {
    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
    final Box yAxis = new Box(1, AXIS_LENGTH, 1);
    final Box zAxis = new Box(1, 1, AXIS_LENGTH);

    xAxis.setMaterial(redMaterial);
    yAxis.setMaterial(greenMaterial);
    zAxis.setMaterial(blueMaterial);

    axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
    axisGroup.setDepthTest(DepthTest.ENABLE);
    axisGroup.setVisible(false);
    world.getChildren().addAll(axisGroup);
  }

  /**
   * Handle mouse events
   *
   * @param scene The scene where the listeners are added.
   */
  private void handleMouse (Scene scene)
  {

    scene.setOnMousePressed(new EventHandler <MouseEvent>()
    {
      @Override
      public void handle (MouseEvent me)
      {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseOldX = me.getSceneX();
        mouseOldY = me.getSceneY();
        hasMouseEvent = true;
      }
    });
    scene.setOnMouseDragged(new EventHandler <MouseEvent>()
    {
      @Override
      public void handle (MouseEvent me)
      {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        double modifier = 1.0;

        if (me.isControlDown())
        {
          modifier = CONTROL_MULTIPLIER;
        }
        if (me.isShiftDown())
        {
          modifier = SHIFT_MULTIPLIER;
        }
        if (me.isPrimaryButtonDown())
        {
          cameraXform.ry.setAngle(cameraXform.ry.getAngle() -
                  mouseDeltaX * modifier * ROTATION_SPEED);  //
          cameraXform.rx.setAngle(cameraXform.rx.getAngle() +
                  mouseDeltaY * modifier * ROTATION_SPEED);  // -
        }
        else if (me.isSecondaryButtonDown())
        {
          cameraXform2.t.setX(cameraXform2.t.getX() +
                  mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
          cameraXform2.t.setY(cameraXform2.t.getY() +
                  mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
        }
      }
    });
    scene.setOnScroll(new EventHandler <ScrollEvent>()
    {
      @Override
      public void handle (ScrollEvent event)
      {
        double deltaY = event.getDeltaY();

        if (deltaY > 0)
          camera.setTranslateZ(camera.getTranslateZ() + 15);
        else
          camera.setTranslateZ(camera.getTranslateZ() - 15);

      }
    });
    scene.setOnMouseReleased(new EventHandler <MouseEvent>()
    {
      @Override
      public void handle (MouseEvent event)
      {
        //System.out.println("Mouse released");
        hasMouseEvent = false;
      }
    });
  }

  /**
   * Handles keyboard events
   *
   * @param scene The scene where the listeners are added.
   */
  private void handleKeyboard (Scene scene)
  {

    scene.setOnKeyPressed(new EventHandler <KeyEvent>()
    {
      @Override
      public void handle (KeyEvent event)
      {
        switch (event.getCode())
        {
          case Z:
            cameraXform2.t.setX(0.0);
            cameraXform2.t.setY(0.0);
            cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
            cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
            break;
          case X:
            axisGroup.setVisible(!axisGroup.isVisible());
            break;
          case R:
            rotate = !rotate;
            break;
          case W:
            camera.setTranslateZ(camera.getTranslateZ() + 15);
            break;
          case S:
            camera.setTranslateZ(camera.getTranslateZ() - 15);
            break;
        }
      }
    });
  }

  public static void main (String[] args)
  {
    launch(args);
  }

  /**
   * The AnimationTimer which ticks for both the cell color/dimension change and the update of the board.
   */
  private class MainGameLoop extends AnimationTimer
  {
    @Override
    public void handle (long now)
    {

      updateBoxes();

      if (skip)
      {
        prevNow = now;
        skip = false;
      }
      else if (now - prevNow > 1_000_000_000)
      {
        //gameBoard.updateBoard();
        Thread th = new Thread(() -> gameBoard.updateBoard());

        th.start();

        if (!hasMouseEvent && rotate)
        {
          cameraXform.setRotate(cameraXform.getRotateX() + 5, cameraXform.getRotateY() + 5, cameraXform.getRotateZ() + 5);
        }
        prevNow = now;
      }
      else if (!hasMouseEvent && rotate)
        cameraXform.setRotate(cameraXform.getRotateX() + 0.125, cameraXform.getRotateY() + 0.125, cameraXform.getRotateZ() + 0.125);
    }
  }
}