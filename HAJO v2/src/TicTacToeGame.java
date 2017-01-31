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
	
	Button[] btns = new Button[9];
	static TicTacToe TTT;
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

	@Override
	public void start(Stage S) throws Exception {

		primaryStage = S;
		
		try {
			TTT = (TicTacToe) Naming.lookup(host);
			System.out.println("Connected to server");
			
            playerNum = TTT.registerPlayer();
            System.out.println("Player number: " + playerNum);
            
		} catch(Exception e) {
			
		}
		
		
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
		
		sceneMain = new Scene(root1, 300, 300);
		sceneStart = new Scene(root2, 300,300);
		
        primaryStage.setScene(sceneStart);
        primaryStage.show();
		
        //create a timed UI-update to represent current gamestate
        
        TimerTask task = new TimerTask() {

			@Override
			public void run() {
				
				Platform.runLater(()->{
					updateBoard();
				});
			}
        	
        };

        Timer t = new Timer();
        t.schedule(task, 0, 500);
        
	}
	
	void playTurn(int row, int col) {
		try {
			System.out.print("playing turn: ");
			if (TTT.doTurn(playerNum, 3*row+col)==0) {
				System.out.println("success");
			} else {
				System.out.println("failed");
			}
			
			
			
		} catch (Exception e) {
			
		}
	}
	
	void updateBoard() {

		try {
			
			board = TTT.getBoardState();
			for (int i = 0; i < btns.length;i++) {
				btns[i].setText("" + convertNumToMarker(board[i]));
			}
			
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
			
			//primaryStage.show();
			
		} catch (Exception e) {
			
		}
	}
	// Convert numbers to markers
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
	// This is used to start the game
	void startGame() {
		try {
			while(!TTT.startGame(playerNum)) {
				Thread.sleep(100);
			}
			primaryStage.setScene(sceneMain);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

