import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * 
 */

/**
 * @author Jussi Wallin, Antti Auranen, Niklas Niemelä
 *
 */
public class TicTacToeSrv extends java.rmi.server.UnicastRemoteObject implements TicTacToe {

	protected TicTacToeSrv() throws RemoteException {

	}

	int playerCount = 0;
	int[] board = new int[9];
	int currentTurn = 1;
	boolean gameGoing;
	boolean[] startGame = {false, false};
	int winner;
	
	@Override
	synchronized public int registerPlayer() throws Exception {
		
		if (playerCount >= 2) return -1; //check that max 2 players are trying to play
		
		playerCount++;
		System.err.println("Server Debug: Registered player " + playerCount);
		return playerCount;
	}

	@Override
	synchronized public int[] getBoardState() throws Exception {

		//System.err.println("Server Debug: Returned board state to player");
		return board;
	}

	@Override
	synchronized public boolean getTurn(int playerNum) throws Exception {

		System.err.println("Server Debug: Returned turn boolean to player " + playerNum);
		if (playerNum == currentTurn) return true;
		return false;
	}

	@Override
	synchronized public int doTurn(int playerNum, int pos) throws Exception {
		
		if (board[pos] == 0 && gameGoing && currentTurn == playerNum) { //check nothing is present in this square
			if (currentTurn == 1) { 
				System.err.println("Server Debug: Set turn to player 2 by " + playerNum);
				currentTurn = 2; }
			
			else {
				System.err.println("Server Debug: Set turn to player 1 by " + playerNum);
				currentTurn = 1; //change to next players turn
			}
			board[pos] = playerNum; //set players mark on the square

			int result = checkGameEnd();
			if (result != 0) {
				winner = result;
				gameGoing = false;
				board = new int[9];
				startGame[0] = startGame[1] = false;
			}
			
			return 0; //return success
		} else {

			int result = checkGameEnd();
			if (result != 0) {
				winner = result;
				gameGoing = false;
				board = new int[9];
				startGame[0] = startGame[1] = false;
			}
			
			return -1; //return failure
		}
	}

	@Override
	synchronized public boolean gameGoing() throws Exception {
		return gameGoing;
	}

	@Override
	synchronized public boolean startGame(int playerNum) throws Exception {
		startGame[playerNum-1] = true;
		if (startGame[0] == startGame[1]) { 
			gameGoing = true;
			return true;
		}
		
		return false;
		
	}
	
	int checkGameEnd() {
		//check columns
		for (int i = 0; i < 3; i++) {
			if (board[i] != 0 && board[i] == board[i+3] && board[i] == board[i+6]) return board[i];
		}
		//check rows
		for (int i = 0; i < 7; i+=3) {
			if (board[i] != 0 && board[i] == board[i+1] && board[i] == board[i+2]) return board[i];
		}
		//check diagonals
		if (board[0] != 0 && board[0] == board[4] && board[0] == board[8]) return board[0];
		if (board[2] != 0 && board[2] == board[4] && board[0] == board[6]) return board[2];
		
		int i;
		//check if completely full
		for (i = 0; i < board.length; i++) {
			if (board[i] == 0) break;
		}
		
		if (i>board.length) return -1;
		
		//no straight of threes
		return 0;
	}

	@Override
	public int getWinner() throws Exception {
		return winner;
	}
	

}
