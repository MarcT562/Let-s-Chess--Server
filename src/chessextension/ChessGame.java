package chessextension;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marc 'Darque' Taylor
 */
public class ChessGame {
    private ArrayList<PieceData> _pieces;
    private int _currentTurn;
    private int _numPlayers;
    private int _playersInGame;
    private int[] _playerColors;
    private String[] _playerNames;
    private User[] _playerUsers;
    private boolean _isGameOver;
    private boolean[] _hasSeenGameOver;
    private int _winner;
    
    public ChessGame()
    {
        initNewChessGame();
    }
    
    private PieceData createChessPiece(String name, String type, int x, int y, int color) {
        return createChessPiece(name, type, x, y, color, true);
    }

    private PieceData createChessPiece(String name, String type, int x, int y, int color, boolean isAlive) {
        PieceData piece = new PieceData(name, type, x, y, color, isAlive);
        _pieces.add(piece);
        return piece;
    }

    private void initNewChessGame() {
        _isGameOver = false;
        _winner = -1;
        _pieces = new ArrayList<PieceData>();
        _currentTurn = 0;
        _numPlayers = 2;
        _playerColors = new int[2];
        _playerColors[0] = 0;
        _playerColors[1] = 1;
        _playerNames = new String[2];
        _playerUsers = new User[2];
        _hasSeenGameOver = new boolean[2];
        _hasSeenGameOver[0] = false;
        _hasSeenGameOver[1] = false;
        
        //Make Pawns
        for (int x = 0; x < 8; x++)
        {
            createChessPiece("BlackPawn" + x, "Pawn", x, 6, 1);
            createChessPiece("WhitePawn" + x, "Pawn", x, 1, 0);
        }
        //Make Rooks
        for (int x = 0; x < 2; x++)
        {
            createChessPiece("BlackRook" + x, "Rook", (x == 0) ? (0) : (7), 7, 1);
            createChessPiece("WhiteRook" + x, "Rook", (x == 0) ? (0) : (7), 0, 0);
        }
        //Make Knights
        for (int x = 0; x < 2; x++)
        {
            createChessPiece("BlackKnight" + x, "Knight", (x == 0) ? (1) : (6), 7, 1);
            createChessPiece("WhiteKnight" + x, "Knight", (x == 0) ? (1) : (6), 0, 0);
        }
        //Make Bishops
        for (int x = 0; x < 2; x++)
        {
            createChessPiece("BlackBishop" + x, "Bishop", (x == 0) ? (2) : (5), 7, 1);
            createChessPiece("WhiteBishop" + x, "Bishop", (x == 0) ? (2) : (5), 0, 0);
        }
        //Make Queens
        createChessPiece("BlackQueen", "Queen", 3, 7, 1);
        createChessPiece("WhiteQueen", "Queen", 3, 0, 0);
        //Make Kings
        createChessPiece("BlackKing", "King", 4, 7, 1);
        createChessPiece("WhiteKing", "King", 4, 0, 0);
    }

    String movePiece(String userName, String pieceName, int x, int y) {
        if(isFull() == false)
        {
            return MoveHandler.CODE_GAME_NOT_FULL;
        }
        
        Integer playerNum = getPlayerNum(userName);
        if(playerNum == null)
        {
            return MoveHandler.CODE_NOT_IN_GAME;
        }
        
        PieceData movingPiece = getPieceByName(pieceName);
        if(movingPiece.getIsAlive() == false)
        {
            return MoveHandler.CODE_PIECE_NOT_ALIVE;
        }
        
        if(movingPiece.getColor() != _playerColors[playerNum])
        {
            return MoveHandler.CODE_PIECE_NOT_YOURS;
        }
        
        if(_currentTurn != playerNum)
        {
            return MoveHandler.CODE_NOT_YOUR_TURN;
        }
        
        PieceData pieceAtDest = getPieceAtLocation(x, y);
        if(pieceAtDest != null)
        {
            if(pieceAtDest.getColor() == movingPiece.getColor())
            {
                return MoveHandler.CODE_SELF_ATTACK_IS_OFF;
            }
            else
            {
                capturePiece(pieceAtDest);
            }
        }
        
        movingPiece.setLocation(x, y);
        _currentTurn++;
        while(_currentTurn >= _numPlayers)  //In case something really weird happens
        {
            _currentTurn -= _numPlayers;
        }
        
        return MoveHandler.CODE_SUCCESS;
    }
    
    PieceData getPieceByName(String name)
    {
        for (PieceData piece : _pieces)
        {
            if(piece.getName().matches(name))
            {
                return piece;
            }
        }
        return null;
    }
    
    PieceData getPieceAtLocation(int x, int y)
    {
        List<PieceData> piecesAtLocation = getPiecesAtLocation(x, y);
        if(piecesAtLocation.size() > 0)
        {
            return piecesAtLocation.get(0);
        }
        else
        {
            return null;
        }
    }
    
    List<PieceData> getPiecesAtLocation(int x, int y)
    {
        List<PieceData> pieces = new ArrayList<PieceData>();
        for (PieceData piece : _pieces)
        {
            if(piece.getX() == x && piece.getY() == y)
            {
                pieces.add(piece);
            }
        }
        return pieces;
    }
    
    Integer getPlayerNum(String name)
    {
        for(int i = 0; i < _numPlayers; i++)
        {
            if(_playerNames[i] != null && _playerNames[i].equals(name))
            {
                return i;
            }
        }
        return null;
    }

    int getPlayerColor(String name) {
        Integer playerNum = getPlayerNum(name);
        if(playerNum != null)
        {
            return _playerColors[playerNum];
        }
        return -1;
    }

    boolean isPlayerTurn(String name) {
        Integer playerNum = getPlayerNum(name);
        if((playerNum != null) && (playerNum == _currentTurn))
        {
            return true;
        }
        return false;
    }

    int getNumPieces() {
        return _pieces.size();
    }

    public boolean isUserInGame(String name) {
        return (getPlayerNum(name) != null);
    }
    
    public int playersInGame()
    {
        return _playersInGame;
    }

    public boolean isFull() {
        return playersInGame() >= _numPlayers;
    }

    public void addUser(User user) {
        int playerIdx = _playersInGame;
        _playerNames[playerIdx] = user.getName();
        _playerUsers[playerIdx] = user;
        _playersInGame++;
    }

    private void capturePiece(PieceData piece) {

        int capturedX = ((piece.getColor() == 1)?(9):(-2));
        int capturedY = getNumCaptured(piece.getColor());

        if(capturedY >= 8)  //Hard coded for now. Board Height
        {
            capturedX += ((piece.getColor() == 1)?(1):(-1));
            capturedY -= 8; //Hard coded for now. Board Height
        }
        
        piece.setIsAlive(false);
        piece.setLocation(capturedX, capturedY);
        
        if(piece.getType().equalsIgnoreCase("King"))
        {
            _isGameOver = true;
            _winner = (piece.getColor() == 0)?(1):(0);
            _hasSeenGameOver[_winner] = true;   //Yes hackyness that is fragile, but this is a prototype
        }
    }

    private int getNumCaptured(int color) {
        List<PieceData> pieces = getColorCaptured(color);
        return pieces.size();
    }

    private List<PieceData> getColorCaptured(int color) {
        List<PieceData> pieces = new ArrayList<PieceData>();
        for (PieceData piece : _pieces)
        {
            if(piece.getIsAlive() == false)
            {
                pieces.add(piece);
            }
        }
        return pieces;
    }
    
    public boolean canCloseGame()
    {
        for (boolean hasSeenGameOver : _hasSeenGameOver)
        {
            if(hasSeenGameOver == false)
            {
                return false;
            }
        }
        return true;
    }

    public void playerSeenGameOver(String name) {
        Integer playerIdx = getPlayerNum(name);
        if(playerIdx != null)
        {
            _hasSeenGameOver[playerIdx] = true;
        }
    }

    public boolean isOver() {
        return _isGameOver;
    }

    public void addToObject(ISFSObject response, User user) {
        int currentTurn = (_isGameOver == true)?(1):((isPlayerTurn(user.getName())?(0):(1)));
        int playerColor = getPlayerColor(user.getName());
        int numPieces = getNumPieces();
        
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> types = new ArrayList<String>();
        ArrayList<Integer> colors = new ArrayList<Integer>();
        ArrayList<Boolean> alive = new ArrayList<Boolean>();
        ArrayList<Integer> xList = new ArrayList<Integer>();
        ArrayList<Integer> yList = new ArrayList<Integer>();
        
       for(PieceData pData : _pieces)
       {
           names.add(pData.getName());
           types.add(pData.getType());
           colors.add(pData.getColor());
           alive.add(pData.getIsAlive());
           xList.add(pData.getX());
           yList.add(pData.getY());
       }
       
       response.putBool("isGameOver", _isGameOver);
       response.putInt("winner", _winner);
       
       response.putInt("currentTurn", currentTurn);
       response.putInt("playerColor", playerColor);
       response.putInt("numPieces", numPieces);
       response.putUtfStringArray("names", names);
       response.putUtfStringArray("types", types);
       response.putIntArray("colors", colors);
       response.putIntArray("x", xList);
       response.putIntArray("y", yList);
       response.putBoolArray("alive", alive);
    }
}
