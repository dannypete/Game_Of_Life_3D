# Game of Life 3D

This program simulates cell life. The idea is that if certain space is too cramped or too "rural", a live cell will
die. Conversely, if a space has an amount certain bounding of neighbors, new life will be born.

## Citations

- [Project Desription](https://www.cs.unm.edu/~joel/cs351/notes/CS-351-03-GameOfLife3D.pdf)
- [Xform and some Application Code](https://docs.oracle.com/javase/8/javafx/graphics-tutorial/sampleapp3d.htm)
- [Help with ComboBox Issues](http://stackoverflow.com/questions/31786980/javafx-windows-10-combobox-error)

## Miscellaneous

### Controls:

- Left click and drag: Rotate the cube
- Right click: moves the cube about the window
- Scroll: zoom
- Z: reset cube to original position
- X: toggle axes visiblity
- R: toggle rotation
- W: zoom in
- S: zoom out

### Code Details

- A cell comes to life if its neighbor count is => R1 and =< R2.
- A cell dies if its neighbor count is > R3 or < R4.
- The presets have defined R1, R2, R3, and R4, but they can be changed in the input.
- The main method is in Main.java.
