import java.util.*;
import java.lang.Math;
class FinalProject extends App {
    //Define variables
    Random rand = new Random();
    Hexagon[][] hexGrid;
    int mouseX;
    int mouseY;
    boolean win;
    int moves;
    boolean lost;
    int score = 0;
    int highScore = 0;
    int scale;
    int numDiags;
    int numDrawn;
    int size;
    int walls;
    int numHexes;
    boolean start = true;
    boolean wallsSet;
    
    
    void setup() {
        win = false;
        lost = false;
        wallsSet = false;
        moves = 0;
        numHexes = 0;
        scale = 3;
        //Subtract size to allow for adjustable difficulty
        numDiags = 6 - size;
        numDrawn = 9 - size;
        drawFirstHexGrid();
        setMouseStart();
    }
    
    void loop() {
        //Deals with if we are showing the start menu or the main game
        if (start) {
            if (startMenu()) {
                reset();
                start = false;
            } 
        } else if (!wallsSet) {
            //Makes sure the walls are randomized properly, will happen at beginning of each game
            setWallsStart();
            wallsSet = true;
        } else {
            //Detects if user wants to go back to start
            if (keyPressed('W')) {
                start = true;
            }
            drawHexGrid();
            
            //Reset if we lose and end function
            if (lost) {
                score = 0;
                lose();
                return;
            }
            
            //Reset if we win and end function
            if (win) {
                won(); 
                return;
            }
            
            //If the user presses the mouse
            if (mousePressed) {
                double closest = 64.0;
                int closex = 0;
                int closey = 0;
                //Check which hexagon is closest, have to do this because contains for hexagons is weird
                for (int x = 0; x < hexGrid.length; x++) {
                    for (int y = 0; y < hexGrid[x].length; y++) {
                        if (hexGrid[x][y] != null) {
                            double prox = hexGrid[x][y].proximity(mousePosition);
                            //Makes sure we are actually close enough to be in a hexagon
                            if (prox < (10.0 * scale) && prox < closest){
                                closex = x;
                                closey = y;
                                closest = prox;
                            }
                        }
                    }
                }
                //If we're in a hexagon, and that hexagon isnt a wall or mouse, make the hex a wall and move the mouse
                if (closest < 64.0) {
                    if (!hexGrid[closex][closey].wall && (closex!=mouseX || closey != mouseY)) {
                        hexGrid[closex][closey].wall = true;
                        mouseMove();
                    }
                }
                
            }
        }
    }
    
    boolean startMenu() {
        drawString("Tap Hexagons to Trap the Mouse", new Vector2(0.0,28.0), Vector3.white, 20, true);
        drawString("Press W to Return to Start Menu", new Vector2(0.0,24.0), Vector3.white, 20, true);
        
        //Changes color of text based on where rectangle is
        Vector3 size0col = Vector3.white;
        Vector3 size1col = Vector3.white;
        Vector3 size2col = Vector3.white;
        Vector3 size3col = Vector3.white;
        if (size == 0) {
            drawCenterRectangle(new Vector2(-20.0,12.0), new Vector2(7.0,4.0), Vector3.green);
            size0col = Vector3.black;
        } else if (size == 1) {
            drawCenterRectangle(new Vector2(-8.0,12.0), new Vector2(10.0,4.0), Vector3.green);
            size1col = Vector3.black;
        } else if (size == 2) {
            drawCenterRectangle(new Vector2(4.0,12.0), new Vector2(7.0,4.0), Vector3.green);
            size2col = Vector3.black;
        } else if (size == 3) {
            drawCenterRectangle(new Vector2(20.0,12.0), new Vector2(16.0,4.0), Vector3.green);
            size3col = Vector3.black;
        }
        drawString("Size:", new Vector2(0.0,16.0), Vector3.white, 20, true);
        drawString("Easy", new Vector2(-20.0,12.0), size0col, 20, true);
        drawString("Medium", new Vector2(-8.0,12.0), size1col, 20, true);
        drawString("Hard", new Vector2(4.0,12.0), size2col, 20, true);
        drawString("Impossible", new Vector2(20.0,12.0), size3col, 20, true);
        
        //Changes color of text based on where rectangle is
        Vector3 walls0col = Vector3.white;
        Vector3 walls1col = Vector3.white;
        Vector3 walls2col = Vector3.white;
        Vector3 walls3col = Vector3.white;
        if (walls == 0) {
            drawCenterRectangle(new Vector2(-20.0,-12.0), new Vector2(7.0,4.0), Vector3.green);
            walls0col = Vector3.black;
        } else if (walls == 1) {
            drawCenterRectangle(new Vector2(-8.0,-12.0), new Vector2(10.0,4.0), Vector3.green);
            walls1col = Vector3.black;
        } else if (walls == 2) {
            drawCenterRectangle(new Vector2(4.0,-12.0), new Vector2(7.0,4.0), Vector3.green);
            walls2col = Vector3.black;
        } else if (walls == 3) {
            drawCenterRectangle(new Vector2(20.0,-12.0), new Vector2(16.0,4.0), Vector3.green);
            walls3col = Vector3.black;
        }
        drawString("Walls:", new Vector2(0.0,-8.0), Vector3.white, 20, true);
        drawString("Easy", new Vector2(-20.0,-12.0), walls0col, 20, true);
        drawString("Medium", new Vector2(-8.0,-12.0), walls1col, 20, true);
        drawString("Hard", new Vector2(4.0,-12.0), walls2col, 20, true);
        drawString("Impossible", new Vector2(20.0,-12.0), walls3col, 20, true);
        
        drawCenterRectangle(new Vector2(0.0,-28.0), new Vector2(8.0,4.0), Vector3.green);
        drawString("Start", new Vector2(0.0,-28.0), Vector3.black, 20, true);
        
        //Draws the mouse in the center of the screen
        drawMouse (new Hexagon(new Vector2(256.0,256.0),12));
        
        if (keyPressed(' ')) {
            return true;
        }
        if (mousePressed) {
            //If in start box, end menu screen
            if (mousePosition.x < 4.0 && mousePosition.x > -4.0 && mousePosition.y < -26.0 && mousePosition.y > -30.0) {
                return true;
            }
            
            //Check if user clicked a size box
            if (mousePosition.y < 14.0 && mousePosition.y > 10.0) {
                if (mousePosition.x > -23.5 && mousePosition.x < -16.5) {
                    size = 0;
                } else if (mousePosition.x > -13.0 && mousePosition.x < -3.0) {
                    size = 1;
                } else if (mousePosition.x > -0.5 && mousePosition.x < 7.5) {
                    size = 2;
                } else if (mousePosition.x > 12.0 && mousePosition.x < 28.0) {
                    size = 3;
                }
            }
            
            //Check if user clicked a walls box
            if (mousePosition.y > -14.0 && mousePosition.y < -10.0) {
                if (mousePosition.x > -23.5 && mousePosition.x < -16.5) {
                    walls = 0;
                } else if (mousePosition.x > -13.0 && mousePosition.x < -3.0) {
                    walls = 1;
                } else if (mousePosition.x > -0.5 && mousePosition.x < 7.5) {
                    walls = 2;
                } else if (mousePosition.x > 12.0 && mousePosition.x < 28.0) {
                    walls = 3;
                }
            }
        }
        return false;
    }
    
    void lose() {
        //Put up text, keep grid the same until user clicks
        drawString("You", new Vector2(26.0,28.0), Vector3.red, 20, true);
        drawString("Lost", new Vector2(26.0,24.0), Vector3.red, 20, true);
        drawString("Score: ", new Vector2(26.0,16.0), Vector3.red, 20, true);
        drawString(Integer.toString(score), new Vector2(26.0,12.0), Vector3.red, 20, true);
        
        //When user clicks, reset the board
        if (mousePressed || keyPressed(' ')) {
            lost = false;
            reset();
        }
    }
    
    void won() {
        //Put up text, keep grid the same until user clicks
        drawString("You", new Vector2(26.0,28.0), Vector3.green, 20, true);
        drawString("Win", new Vector2(26.0,24.0), Vector3.green, 20, true);
        drawString("Score: ", new Vector2(26.0,16.0), Vector3.green, 20, true);
        drawString(Integer.toString(score), new Vector2(26.0,12.0), Vector3.green, 20, true);
        
        //When user clicks, reset the board
        if (mousePressed || keyPressed(' ')) {
            win = false;
            reset();
        }
    }
    
    void drawFirstHexGrid() {
        
        
        //Create an array of arrays with appropriate numbers (inner array will be hexagons, outer array will be diagonals  
        hexGrid = new Hexagon[numDiags+(numDrawn/2)*2][numDrawn];
        
        //Draw top of the grid
        double x = 50.0 + (_xChangeHex(scale) * 2)*numDrawn/2;
        double y = 50.0;
        int numToDraw = numDrawn%2;
        int count = 0;
        while (numToDraw < numDrawn) {
            Hexagon newHex = new Hexagon(new Vector2(x,y), scale);
            numHexes++;
            
            //Calls function that takes one hexagon and draws a full diagonal, does some math to cut off some hexagons (to get a real grid)
            _drawHexGridHelper(newHex,numToDraw,scale,count,numDrawn- count*2, numDrawn % 2);
            x = x - _xChangeHex(scale) * 2;
            numToDraw = numToDraw + 2;
            count++;
        }
        
        
        //Draw main base of the grid
        x = 50.0;
        y = 50.0;
        for (int i = 0; i < numDiags; i++) {
            Hexagon newHex = new Hexagon(new Vector2(x,y), scale);
            //Calls function that takes one hexagon and draws a full diagonal
            _drawHexGridHelper(newHex,numDrawn,scale,i + (numDrawn/2),0,0);
            y = y + _yChangeHex(scale) * 2;
        }
        
        //draw bottom of the grid
        numToDraw = numDrawn - 2;
        count = 0;
        while (numToDraw > 0) {
            Hexagon newHex = new Hexagon(new Vector2(x,y), scale);
            //Calls function that takes one hexagon and draws a full diagonal, does some math to cut off some hexagons (to get a real grid)
            _drawHexGridHelper(newHex,numToDraw,scale,numDiags+(numDrawn/2)+count,0,0);
            y = y + _yChangeHex(scale) * 2;
            numToDraw -= 2;
            count++;
        }
        
    }
    
    
    void drawHexGrid() {
        if (!win && !lost) {
            //Displays normal text during normal gameplay
            drawString("Score: ", new Vector2(26.0,28.0), Vector3.white, 20, true);
            drawString(Integer.toString(score), new Vector2(26.0,24.0), Vector3.white, 20, true);
            drawString("Moves: ", new Vector2(26.0,16.0), Vector3.white, 20, true);
            drawString(Integer.toString(moves), new Vector2(26.0,12.0), Vector3.white, 20, true);
            drawString("High", new Vector2(24.0,4.0), Vector3.white, 20, true);
            drawString("Score: ", new Vector2(26.0,0.0), Vector3.white, 20, true);
            drawString(Integer.toString(highScore), new Vector2(26.0,-4.0), Vector3.white, 20, true);
        }
        
        //Draws all hexagons that aren't null (some can be null since we don't draw all in certain diagonals)
        for (int x = 0; x < hexGrid.length; x++) {
            for (int y = 0; y < hexGrid[x].length; y++) {
                if (hexGrid[x][y] != null) {
                    drawCenterHexagon(hexGrid[x][y]);
                }
            }
        }
    }
    
    void mouseMove() {
        //Pick a random direction to move
        int dir = rand.nextInt(6);
        boolean moved = false;
        for (int i = 0; i < 6; i++) {
            //Tries to move towards an edge, if first random didn't succeed try all "intelligent" directions until success
            if (_mouseMoveHelperSmart((dir + i) % 6)) {
                moved = true;
                moves +=1;
                drawHexGrid();
                return;
            }
        }
        for (int i = 0; i < 6; i++) {
            //If moving in "intelligent" directions didn't allow a move, see if any moves are possible
            if (_mouseMoveHelperStupid((dir + i) % 6)) {
                moved = true;
                moves +=1;
                drawHexGrid();
                break;
            }
        }
        if (!moved) {
            //If the mouse wasn't able to move, the user trapped it and won!
            win = true;
            score++;
            if (score > highScore) {highScore = score;}
        }
    }
    
    boolean _mouseMoveHelperSmart(int dir) {
        //Check if the mouse is on the beginning or end of a diagonal, and can win
        if ((mouseY == 0 || mouseY == hexGrid[mouseX].length - 1)) {
            lost = true;
            hexGrid[mouseX][mouseY].mouse = false;
            return true;
        }
        int nonNullBegin = -1;
        //Checksf we are in an offset case
        if (hexGrid[mouseX][0] == null) {
            for (int i = 0; i < hexGrid[mouseX].length; i++) {
                if (hexGrid[mouseX][i]!=null) {
                    nonNullBegin = i;
                    break;
                }
            }
            //If we're in an offset case, and the mouse is in the first or second position (since offsets are at the top), mouse wins
            if (nonNullBegin!=-1 && (mouseY==nonNullBegin || mouseY==nonNullBegin+1)) {
                lost = true;
                hexGrid[mouseX][mouseY].mouse = false;
                return true;
            }
        }
        //Checks if the second hex in a diagonal is on the edge or not, if it is and mouse is there it wins
        if (mouseY == 1 && hexGrid[mouseX-1][1]==null) {
            lost = true;
            hexGrid[mouseX][mouseY].mouse = false;
            return true;
        }
        //Checks for back offset case
        int nonNullEnd = -1;
        if (hexGrid[mouseX][hexGrid[mouseX].length-1] == null) {
            
            for (int i = hexGrid[mouseX].length-1; i > -1; i--) {
                if (hexGrid[mouseX][i]!=null) {
                    nonNullEnd = i;
                    break;
                }
            }
            //If in backoffset case case and mouse is at end of non nulls (or one in), mouse wins
            if (nonNullEnd!=-1 && (mouseY==nonNullEnd || mouseY==nonNullEnd-1)) {
                lost = true;
                hexGrid[mouseX][mouseY].mouse = false;
                return true;
            }
        }
        
        //Checks if second to last hex in a diagonal is on the edge or not, if it is and mouse is there it wins
        if (mouseY == hexGrid[mouseX].length-2 && hexGrid[mouseX+1][hexGrid[mouseX+1].length-2]==null) {
            lost = true;
            hexGrid[mouseX][mouseY].mouse = false;
            return true;
        }
        
        //Creates an adjustment so the mouse doesn't get trapped in an endless loop
        int adjust = 2;
        
        //Different possible directions based on what random direction was chosen
        if (dir == 0) {
            if (mouseX > 0) {
                if (hexGrid[mouseX-1][mouseY]!=null && !hexGrid[mouseX-1][mouseY].wall) {
                    //If direction mouse is going is closer to the edge then not, go in that direction (with some roughness)
                    if (mouseX <= hexGrid.length - mouseX + adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    }
                }
            }
        } else if (dir == 1) {
            if (mouseX > 0 && mouseY < hexGrid[mouseX-1].length - 1) {
                if (hexGrid[mouseX-1][mouseY+1]!=null && !hexGrid[mouseX-1][mouseY+1].wall) {
                    //If direction mouse is going is closer to the edge then not, go in that direction (with some roughness)
                    //Other cases for if our array has nulls/is offset
                    if (nonNullBegin == -1 && nonNullEnd == -1 && mouseX <= hexGrid.length - mouseX + adjust && mouseY >= hexGrid[mouseX].length - mouseY - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX--;
                        mouseY++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullBegin != -1 && mouseX <= hexGrid.length - mouseX +adjust && mouseY - nonNullBegin >= hexGrid[mouseX].length - mouseY -nonNullBegin - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX--;
                        mouseY++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullEnd != -1 && mouseX <= hexGrid.length - mouseX + adjust && mouseY >= nonNullEnd - mouseY -adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX--;
                        mouseY++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    }
                }
            }
        } else if (dir == 2) {
            if (mouseY > 0) {
                if (hexGrid[mouseX][mouseY-1]!=null && !hexGrid[mouseX][mouseY-1].wall) {
                    //If direction mouse is going is closer to the edge then not, go in that direction (with some roughness)
                    //Other cases for if our array has nulls/is offset
                    if (nonNullBegin == -1 && nonNullEnd == -1 && mouseY >= hexGrid[mouseX].length - mouseY - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseY--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullBegin != -1 && mouseY - nonNullBegin >= hexGrid[mouseX].length - mouseY - nonNullBegin - adjust ) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseY--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullEnd != -1 && mouseY >= nonNullEnd - mouseY - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseY--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    }
                }
            }
        } else if (dir == 3) {
            if (mouseY < hexGrid[mouseX].length - 1) {
                if (hexGrid[mouseX][mouseY+1]!=null && !hexGrid[mouseX][mouseY+1].wall) {
                    //If direction mouse is going is closer to the edge then not, go in that direction (with some roughness)
                    //Other cases for if our array has nulls/is offset
                    if (nonNullBegin == -1 && nonNullEnd == -1 && mouseY <= hexGrid[mouseX].length - mouseY + adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseY++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullBegin == -1 && mouseY - nonNullBegin <= hexGrid[mouseX].length - mouseY - nonNullBegin+adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseY++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullEnd != -1 && mouseY <= nonNullEnd - mouseY+adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseY++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    }
                }
            }
        } else if (dir == 4) {
            if (mouseX < hexGrid.length - 1 && mouseY > 0) {
                if (hexGrid[mouseX+1][mouseY-1]!=null && !hexGrid[mouseX+1][mouseY-1].wall) {
                    //If direction mouse is going is closer to the edge then not, go in that direction (with some roughness)
                    //Other cases for if our array has nulls/is offset
                    if (nonNullBegin == -1 && nonNullEnd == -1 && mouseX >= hexGrid.length - mouseX - adjust && mouseY >= hexGrid[mouseX].length - mouseY - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX++;
                        mouseY--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullBegin != -1 && mouseY - nonNullBegin >= hexGrid[mouseX].length - mouseY - nonNullBegin - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX++;
                        mouseY--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    } else if (nonNullEnd != -1 && mouseY >= nonNullEnd - mouseY - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX++;
                        mouseY--;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    }
                }
            }
        } else if (dir == 5) {
            if (mouseX < hexGrid.length - 1) {
                //If direction mouse is going is closer to the edge then not, go in that direction (with some roughness)
                if (hexGrid[mouseX+1][mouseY]!=null && !hexGrid[mouseX+1][mouseY].wall) {
                    if (mouseX >= hexGrid.length - mouseX - adjust) {
                        hexGrid[mouseX][mouseY].mouse = false;
                        mouseX++;
                        hexGrid[mouseX][mouseY].mouse = true;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    boolean _mouseMoveHelperStupid (int dir) {
        //Simply moves according to direction told, regardless of if this is a direction closer to the edge
        if (dir == 0) {
            if (mouseX > 0) {
                if (hexGrid[mouseX-1][mouseY]!=null && !hexGrid[mouseX-1][mouseY].wall) {
                    hexGrid[mouseX][mouseY].mouse = false;
                    mouseX--;
                    hexGrid[mouseX][mouseY].mouse = true;
                    return true;
                }
            }
        } else if (dir == 1) {
            if (mouseX > 0 && mouseY < hexGrid[mouseX-1].length - 1) {
                if (hexGrid[mouseX-1][mouseY+1]!=null && !hexGrid[mouseX-1][mouseY+1].wall) {
                    hexGrid[mouseX][mouseY].mouse = false;
                    mouseX--;
                    mouseY++;
                    hexGrid[mouseX][mouseY].mouse = true;
                    return true;
                }
            }
        } else if (dir == 2) {
            if (mouseY > 0) {
                if (hexGrid[mouseX][mouseY-1]!=null && !hexGrid[mouseX][mouseY-1].wall) {
                    hexGrid[mouseX][mouseY].mouse = false;
                    mouseY--;
                    hexGrid[mouseX][mouseY].mouse = true;
                    return true;
                }
            }
        } else if (dir == 3) {
            if (mouseY < hexGrid[mouseX].length - 1) {
                if (hexGrid[mouseX][mouseY+1]!=null && !hexGrid[mouseX][mouseY+1].wall) {
                    hexGrid[mouseX][mouseY].mouse = false;
                    mouseY++;
                    hexGrid[mouseX][mouseY].mouse = true;
                    return true;
                }
            }
        } else if (dir == 4) {
            if (mouseX < hexGrid.length - 1 && mouseY > 0) {
                if (hexGrid[mouseX+1][mouseY-1]!=null && !hexGrid[mouseX+1][mouseY-1].wall) {
                    hexGrid[mouseX][mouseY].mouse = false;
                    mouseX++;
                    mouseY--;
                    hexGrid[mouseX][mouseY].mouse = true;
                    return true;
                }
            }
        } else if (dir == 5) {
            if (mouseX < hexGrid.length - 1) {
                if (hexGrid[mouseX+1][mouseY]!=null && !hexGrid[mouseX+1][mouseY].wall) {
                    hexGrid[mouseX][mouseY].mouse = false;
                    mouseX++;
                    hexGrid[mouseX][mouseY].mouse = true;
                    return true;
                }
            }
        }
        return false;
    }
    
    void _drawHexGridHelper(Hexagon hex, int num, int scale, int pos, int offset1,int offset2) {
        //Helper function for first time we draw a grid
        int offset = offset1 - offset2;
        //Goes through each hexagon in a diagonal
        for (int i = 0; i < num; i++) {
            double x;
            //Offsets positions of the center of hexagons to make a proper grid
            if (i ==0 && offset2 > 0) {
                //Weird case to handle odd numbers of hexagons, necessary due to quirk of how the math works
                x = hex.center.x;
                hex.center.x = hex.center.x - _xChangeHex (scale);
                hex.definePoints (hex.center, scale);
                
            } else {
                x = hex.center.x + _xChangeHex (scale);
            }
            //Draws a hexagon
            drawCenterHexagon(hex);
            
            //Adds hexagon to array
            hexGrid[pos][i+offset] = hex;
            
            //Defines new hex for next loop
            double y = hex.center.y + _yChangeHex (scale);
            hex = new Hexagon(new Vector2(x,y), scale);
            numHexes++;
        }
    }
    
    void setMouseStart() {
        boolean placed = false;
        while (!placed) {
            //Some calculations to make sure mouse won't start on the very edge of the board, adjusts to size of board
            //Randomly chooses number of diagonal and position in the diagonal
            int diag = rand.nextInt((numDiags * (3 / 2)) - (numDiags /2) + 1) + (numDiags /2) + 2;
            int num = rand.nextInt(((numDrawn * 3) /4) - 2 + 1) + 2;
            
            //If the random numbers we chose aren't on the edge, draw the mouse there
            if (hexGrid[diag][num]!=null && hexGrid[diag][num-1]!=null && hexGrid[diag][num-2]!=null) {
                hexGrid[diag][num].mouse = true;
                mouseX = diag;
                mouseY = num;
                drawCenterHexagon(hexGrid[diag][num]);
                placed = true;
            }
        }
    }
    
    void setWallsStart() {
        //Calculates the number of walls we want based on difficulty specified and size of the board
        int numWallsToPlace = numHexes / (4 + 4 * walls);
        
        //Loop through until we have placed enough walls
        while (numWallsToPlace > 0) {
            //Randomly generates a number for diagonal and position in that diagonal
            int x = rand.nextInt(hexGrid.length);
            int y = rand.nextInt(hexGrid[x].length);
            
            //If position in array of arrays we generated isn't null, isn't already a wall, and isn't a mouse, put a wall there
            if (hexGrid[x][y] != null && !hexGrid[x][y].wall && !hexGrid[x][y].mouse) {
                hexGrid[x][y].wall = true;
                //Decrement numWallsToPlace
                numWallsToPlace--;
            }
        }
    }
    
    double _xChangeHex (int scale) {
        //Function that does the math we need to place hexagons next to each other in x direction
        return (2.0 * (double)scale * 1.5)*4.25;    
    }
    double _yChangeHex (int scale) {
        //Function that does the math we need to place hexagons next to each other in y direction
        return (2.0 * (double)scale * Math.sqrt(3))*2.15;
    }
    
    static void game() {
        //Runs the game
        App app = new FinalProject();
        app.setWindowBackgroundColor(Vector3.black);
        app.setWindowSizeInWorldUnits(64.0, 64.0);
        app.setWindowCenterInWorldUnits(0.0, 0.0);
        app.setWindowHeightInPixels(512);
        app.setWindowTopLeftCornerInPixels(64, 64);
        app.run();
    }
    static public void main (String[] args) {
        FinalProject.game();
    }
}

