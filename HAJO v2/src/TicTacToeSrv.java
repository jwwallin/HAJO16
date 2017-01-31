import java.rmi.RemoteException;

/**
 * 
 */

/**
 * @author Jussi Wallin, Antti Auranen, Niklas Niemelä
 *
 */
@SuppressWarnings("serial")
public class TicTacToeSrv extends java.rmi.server.UnicastRemoteObject implements TicTacToe {

	protected TicTacToeSrv() throws RemoteException {

	}

	// data variables for the game session
	int playerCount = 0;
	int[] board = new int[9];
	int currentTurn = 1;
	boolean gameGoing;
	boolean[] startGame = {false, false};
	int winner;
	
	/**
	 * Function for Client to register itself to the server
	 * 
	 * @return playerNum or -1 if already 2 players
	 */
	@Override
	synchronized public int registerPlayer() throws Exception {
		
		if (playerCount >= 2) return -1; //check that max 2 players are trying to play
		
		playerCount++; //add new player
		System.err.println("Server Debug: Registered player " + playerCount);
		return playerCount; //give client it's player number
	}

	/**
	 * Function for retrieving the current board state
	 * 
	 * @return int[] board
	 */
	@Override
	synchronized public int[] getBoardState() throws Exception {
		
		return board;
	}

	
	/**
	 * Function for Client to Check if it is their turn
	 * 
	 * @return players turn as boolean
	 */
	@Override
	synchronized public boolean getTurn(int playerNum) throws Exception {

		System.err.println("Server Debug: Returned turn boolean to player " + playerNum);
		// check that clients playerNum is the same as current turn holder
		if (playerNum == currentTurn) return true;
		return false;
	}

	/**
	 * Game logic for clients to use when taking turns. Returns an integer based on success or failure (0 == success, -1 == failure).
	 * 
	 * @return int 0 | -1
	 */
	@Override
	synchronized public int doTurn(int playerNum, int pos) throws Exception {
		
		//check nothing is present in this square and game is going and clients player number is same as current turn holder
		if (board[pos] == 0 && gameGoing && currentTurn == playerNum) {
			
			//change to next players turn 
			if (currentTurn == 1) { 
				System.err.println("Server Debug: Set turn to player 2 by " + playerNum);
				currentTurn = 2; }
			
			else {
				System.err.println("Server Debug: Set turn to player 1 by " + playerNum);
				currentTurn = 1;
			}
			
			//set players mark on the square
			board[pos] = playerNum;

			//check if game has ended
			int result = checkGameEnd();
			
			if (result != 0) {
				winner = result;
				gameGoing = false;
				board = new int[9];
				startGame[0] = startGame[1] = false;
			}
			
			return 0; //return success
			
		} else {

			//check if game has ended
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

	/**
	 * Function (Getter) for clients to check if the game is still going on.
	 * 
	 * @return gameGoing
	 */
	@Override
	synchronized public boolean gameGoing() throws Exception {
		return gameGoing;
	}

	/**
	 * Clients announce to server they want the game to begin. Game starts when both players have called this function returns true
	 * if game is started else returns false
	 * 
	 * @return true | false
	 */
	@Override
	synchronized public boolean startGame(int playerNum) throws Exception {
		
		//set that player has asked to begin game
		startGame[playerNum-1] = true;
		
		//check if both players want to begin
		if (startGame[0] == startGame[1]) { 
			gameGoing = true;
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Game logic function to check if game has already ended
	 * 
	 * @return integer depending on which ending condition has been encountered (0 means game hasn't ended)
	 */
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

	/**
	 * Function for clients to see which end condition has been met
	 * 
	 * @return int [-1:2]
	 */
	@Override
	public int getWinner() throws Exception {
		return winner;
	}
	

}

