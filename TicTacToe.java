import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class TicTacToe extends JFrame {

    //----------------------------------------------------------------------
    // INSTANCE VARIABLE CONSTANTS

    public static final int ROWS = 3;  // ROWS by COLS cells
    public static final int COLS = 3;
 
    // Named-constants of the various dimensions used for graphics drawing
    public static final int CELL_SIZE = 100; // cell width and height (square)
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;  // the drawing canvas
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 8;                   // Grid-line's width
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2; // Grid-line's half-width

    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static final int CELL_PADDING = CELL_SIZE / 6;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
 
    //----------------------------------------------------------------------
    // INSTANCE VARIABLES for game play

    protected GameState currentState;  // the current game state
    protected Mark currentPlayer;  // the current player
    protected Mark[][] board   ; // Game board of ROWS-by-COLS cells
    protected Random random;
    protected int randR;
    protected int randC;
    protected Location move = new Location(0,0);

    //----------------------------------------------------------------------
    // INSTANCE VARIABLES for graphics

    private DrawCanvas canvas; // Drawing canvas (JPanel) for the game board
    private JLabel statusBar;  // Status Bar
 
    //----------------------------------------------------------------------
    // INNER CLASSES

    // Use an enumeration (inner class) to represent the various states of the game.
    public enum GameState {
	PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }
 
    // Use an enumeration (inner class) to represent the markers in each cell
    public enum Mark {
	EMPTY, CROSS, NOUGHT
    }
    
    public Location robotPlayer(){
    	randR = random.nextInt(ROWS);
    	randC = random.nextInt(COLS);
    	if(board[randR][randC] == Mark.EMPTY){
    		board[randR][randC] = Mark.NOUGHT;
    		return move;
    	}
    	else if(currentState != GameState.CROSS_WON && currentState != GameState.NOUGHT_WON && currentState != GameState.DRAW){
    		return robotPlayer();
    	}
    	else{
    		return move;
    	}
    }

    //----------------------------------------------------------------------
    // CONSTRUCTOR

    public TicTacToe() {
	// ----------------------------------------
	// First build all graphics components.

	canvas = new DrawCanvas();  // Construct a drawing canvas (a JPanel)
	canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));

	// The canvas (JPanel) fires a MouseEvent upon mouse-click
	canvas.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
		    // Get the x and y coordinates of the screen pixel that was clicked.
		    int mouseX = e.getX();
		    int mouseY = e.getY();
		    // Convert the x,y screen coordinates to a row and column index.
		    int rowSelected = mouseY / CELL_SIZE;
		    int colSelected = mouseX / CELL_SIZE;
		    // Make the selected move and update the state of the game.
		    makeMoveOrRestart(rowSelected, colSelected);
		    // Refresh the drawing canvas by posting the repaint event, which signals
		    //  the JPanel code to call its paintComponent method
		    repaint();
		}
	    });
 
	// Setup the status bar (JLabel) to display status message
	statusBar = new JLabel("  ");
	statusBar.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 15));
	statusBar.setBorder(BorderFactory.createEmptyBorder(2, 5, 4, 5));
 
	// Create a container for holding graphics components, and add the canvas and status bar.
	Container cp = getContentPane();
	cp.setLayout(new BorderLayout());
	cp.add(canvas, BorderLayout.CENTER);
	cp.add(statusBar, BorderLayout.PAGE_END);
 
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	pack();  // pack all the components in this JFrame
	setTitle("Tic Tac Toe");
	setVisible(true);  // show this JFrame
 
	// ----------------------------------------
	// Now define game components and initialize the game.

	random = new Random();
	board = new Mark[ROWS][COLS]; // allocate array
	restartGame(); // initialize the game board contents and game variables
    }
 

    // Initialize the game-board contents and the status
    public void restartGame() {
	for (int row = 0; row < ROWS; ++row) {
	    for (int col = 0; col < COLS; ++col) {
		board[row][col] = Mark.EMPTY; // all cells empty
	    }
	}
	currentState = GameState.PLAYING; // ready to play
	currentPlayer = Mark.CROSS;       // cross plays first
    }

    // If game is not a win or draw yet and this is a valid move, make the move and update the game state.
    //  Add code here to automatically make the opponent's move, if the currentState is still GameState.PLAYING
    public void makeMoveOrRestart( int rowSelected, int colSelected ) {
	if (currentState == GameState.PLAYING) {
	    if (validMove(rowSelected,colSelected)) {
		updateGameState(currentPlayer, rowSelected, colSelected);
		robotPlayer();
		switchPlayer();
	    }
	    if(currentState != GameState.CROSS_WON){
	    	updateGameState(currentPlayer, randR, randC);
	    	switchPlayer();
	    }
	 else{
		if(currentState != GameState.CROSS_WON){
			robotPlayer();
		}
	}
	}
    else{
    	 restartGame(); // restart the game
    }
  }
    // Is row,col a valid move?
    public boolean validMove( int row, int col ) {
	return row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] == Mark.EMPTY;
    }

    // Make the move and update the state of the game.
    public void updateGameState(Mark theMark, int rowSelected, int colSelected) {
	board[rowSelected][colSelected] = currentPlayer; // Make the move
	if (hasWon(theMark, rowSelected, colSelected)) {  // check for win
	    currentState = (theMark == Mark.CROSS) ? GameState.CROSS_WON : GameState.NOUGHT_WON;
	} else if (isDraw()) {  // check for draw
	    currentState = GameState.DRAW;
	}
	// Otherwise, no change to current state (still GameState.PLAYING).
    }
 
    // Change to next player
    public void switchPlayer() {
	currentPlayer = (currentPlayer == Mark.CROSS) ? Mark.NOUGHT : Mark.CROSS;
    }

    // Return true if it is a draw (i.e., no more empty cell)
    public boolean isDraw() {
	for (int row = 0; row < ROWS; ++row) {
	    for (int col = 0; col < COLS; ++col) {
		if (board[row][col] == Mark.EMPTY) {
		    return false; // an empty cell found, not draw, exit
		}
	    }
	}
	return true;  // no more empty cell, it's a draw
    }
 
    // Return true if the player with "theMark" has won after placing at (rowSelected, colSelected)
    public boolean hasWon(Mark theMark, int rowSelected, int colSelected) {
	return ((board[rowSelected][0] == theMark      // 3-in-the-row
		 && board[rowSelected][1] == theMark
		 && board[rowSelected][2] == theMark) ||
		(board[0][colSelected] == theMark      // 3-in-the-column
		 && board[1][colSelected] == theMark
		 && board[2][colSelected] == theMark) ||
		(board[0][0] == theMark                // 3-in-the-diagonal
		 && board[1][1] == theMark
		 && board[2][2] == theMark) ||
		(board[0][2] == theMark                // 3-in-the-opposite-diagonal
		 && board[1][1] == theMark
		 && board[2][0] == theMark));
    }
 
    //----------------------------------------------------------------------
    // INNER CLASS for performing graphics 

    class DrawCanvas extends JPanel {
	@Override
	public void paintComponent(Graphics g) {  // invoke via repaint()
	    super.paintComponent(g);    // fill background
	    setBackground(Color.WHITE); // set its background color
 
	    // Draw the grid-lines
	    g.setColor(Color.LIGHT_GRAY);
	    for (int row = 1; row < ROWS; ++row) {
		g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
				CANVAS_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
	    }
	    for (int col = 1; col < COLS; ++col) {
		g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
				GRID_WIDTH, CANVAS_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
	    }
 
	    // Draw the Marks of all the cells if they are not empty
	    // Use Graphics2D which allows us to set the pen's stroke
	    Graphics2D g2d = (Graphics2D)g;
	    g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH, BasicStroke.CAP_ROUND,
					  BasicStroke.JOIN_ROUND));  // Graphics2D only
	    for (int row = 0; row < ROWS; ++row) {
		for (int col = 0; col < COLS; ++col) {
		    int x1 = col * CELL_SIZE + CELL_PADDING;
		    int y1 = row * CELL_SIZE + CELL_PADDING;
		    if (board[row][col] == Mark.CROSS) {
			g2d.setColor(Color.RED);
			int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
			int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
			g2d.drawLine(x1, y1, x2, y2);
			g2d.drawLine(x2, y1, x1, y2);
		    } else if (board[row][col] == Mark.NOUGHT) {
			g2d.setColor(Color.BLUE);
			g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
		    }
		}
	    }
 
	    // Print status-bar message
	    if (currentState == GameState.PLAYING) {
		statusBar.setForeground(Color.BLACK);
		if (currentPlayer == Mark.CROSS) {
		    statusBar.setText("X's Turn");
		} else {
		    statusBar.setText("O's Turn");
		}
	    } else if (currentState == GameState.DRAW) {
		statusBar.setForeground(Color.RED);
		statusBar.setText("It's a Draw! Click to play again.");
	    } else if (currentState == GameState.CROSS_WON) {
		statusBar.setForeground(Color.RED);
		statusBar.setText("'X' Won! Click to play again.");
	    } else if (currentState == GameState.NOUGHT_WON) {
		statusBar.setForeground(Color.RED);
		statusBar.setText("'O' Won! Click to play again.");
	    }
	}
    }
 
    //----------------------------------------------------------------------
    // MAIN METHOD

    public static void main(String[] args) {
	// Run GUI codes in the Event-Dispatching thread for thread safety
	SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
		    new TicTacToe(); // Let the constructor do the job
		}
	    });
    }
}
