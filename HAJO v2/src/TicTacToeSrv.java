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
			if (currentTurn == 1) currentTurn = 2;
			if (currentTurn == 2) currentTurn = 1; //change to next players turn
			board[pos] = playerNum; //set players mark on the square
			return 0; //return success
		} else {
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
	
	

}
