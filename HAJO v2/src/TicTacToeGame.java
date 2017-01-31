/**
 * @author Jussi Walli, Antti Auranen, Niklas Niemelä
 *
 */

import java.rmi.*;
import java.rmi.server.*;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
 

public class TicTacToeGame extends Application {

	static int playerNum;
	static int[] board;
	static boolean play;
	static String host;

	// create button objects list
	Button[] btns = new Button[9];
	static TicTacToe TTT;
	
	// visual components
	Stage primaryStage;
	Scene sceneMain, sceneStart;
	
	
	public static void main(String[] args) {
		//If the program is started with wrong arguments -> exit
    	if (args.length != 1) {
    		System.out.println("Usage: TicTacToeGame <serverhost>");
    		System.exit(0);
    		}
		host = "rmi://" + args[0] + "/TicTacToe";
		
		launch(); //start the UI
	}
	
	/**
	 * JavaFX Thread calls this when main window is ready
	 */
	@Override
	public void start(Stage S) throws Exception {
		
		// save address to the primary window
		primaryStage = S;
		
		//connect to the server
		try {
			TTT = (TicTacToe) Naming.lookup(host);
			System.out.println("Connected to server");
			
            playerNum = TTT.registerPlayer();
            System.out.println("Player number: " + playerNum);
            
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//Create the game board
        GridPane root1 = new GridPane();
        
		primaryStage.setTitle("TicTacToe Player " + playerNum);
		for (int i = 0; i < 9; i++) {

	        btns[i] = new Button();
	        btns[i].setText("");
	        GridPane.setConstraints(btns[i], i%3, i/3, 1, 1);
	        btns[i].setMinHeight(100);
	        btns[i].setMinWidth(100);
	        
	        btns[i].setOnAction(new EventHandler<ActionEvent>() {
	 
	            @Override
	            public void handle(ActionEvent event) {
	                playTurn(GridPane.getRowIndex(((Button)event.getSource())), GridPane.getColumnIndex(((Button)event.getSource())) );
	                updateBoard();
	                
	            }
	        });
	        
	        root1.getChildren().add(btns[i]);
		}
		
		//create the start screen
		Button btn = new Button();
        btn.setText("Start Game");
        btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                startGame();
            }
        });
        
        StackPane root2 = new StackPane();
        root2.getChildren().add(btn);
		
        //create the scenes
		sceneMain = new Scene(root1, 300, 300);
		sceneStart = new Scene(root2, 300,300);
		
		//set the starting screen as active
        primaryStage.setScene(sceneStart);
        primaryStage.show();
		
        //create a timed UI-update to present current game board state
        TimerTask task = new TimerTask() {

			@Override
			public void run() {
				
				//this function creates a task for the JavaFX main thread to run when it has time
				Platform.runLater(()->{
					updateBoard();
				});
			}
        	
        };
        
        //start the scheduled event
        Timer t = new Timer();
        t.schedule(task, 0, 500);
        
	}
	
	/**
	 *  Play a turn	
	 */
	void playTurn(int row, int col) {
		try {
			System.out.print("playing turn: ");
			
			//do players turn
			if (TTT.doTurn(playerNum, 3*row+col)==0) {
				System.out.println("success");
			} else {
				System.out.println("failed");
			}

		} catch (Exception e) {
			e.fillInStackTrace();
		}
	}
	
	/**
	 *  Updates the TicTacToe board
	 */
	void updateBoard() {

		try {
			
			//get board from server
			board = TTT.getBoardState();
			
			//convert board to markers
			for (int i = 0; i < btns.length;i++) {
				btns[i].setText("" + convertNumToMarker(board[i]));
			}
			
			//check game is on and game board is visible
			if (!TTT.gameGoing() && primaryStage.getScene() != sceneStart) {
				primaryStage.setScene(sceneStart);
				
				int winner = TTT.getWinner();
				
				//alert players of the winner of the round
				if (winner == playerNum) {
					
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Winner");
					alert.setHeaderText(null);
					alert.setContentText("Congrats player " + playerNum + ", you won the game!");
					alert.showAndWait();
					
				} else if (winner == -1) {
					
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Uhoh");
					alert.setHeaderText(null);
					alert.setContentText("Oh no, nobody wins. :'( ");
					alert.showAndWait();
					
				} else {

					
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Loser");
					alert.setHeaderText(null);
					alert.setContentText("Oh no. Player " + playerNum + ", you lost the game...");
					alert.showAndWait();
					
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  Convert numbers to markers
	 * @param i 
	 * @return 'x'|'o'|' '
	 */
	char convertNumToMarker(int i) {
		switch(i) {
		case 1:
			return 'x';
		case 2:
			return 'o';
		default:
			return ' ';	
		}
	}
	
	/**
	 *  This is used to start the game
	 */
	void startGame() {
		try {
			
			//wait until opponent starts game
			while(!TTT.startGame(playerNum)) {
				Thread.sleep(100);
			}
			
			//change to game board
			primaryStage.setScene(sceneMain);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}

