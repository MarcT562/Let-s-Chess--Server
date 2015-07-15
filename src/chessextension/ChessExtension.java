package chessextension;

import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;
import java.util.HashMap;

/**
 *
 * @author Marc 'Darque' Taylor
 */
public class ChessExtension extends SFSExtension
{
    private HashMap<String, ChessGame> _gameList;
    
    @Override
    public void init()
    {
        trace("Chess Extension Initiailizing!!!");
        
        _gameList = new HashMap<String, ChessGame>();
        
        addRequestHandler("move", MoveHandler.class);
        addRequestHandler("startGame", StartGameHandler.class);
        addRequestHandler("joinGame", JoinGameHandler.class);
    }
    
    ChessGame startGame(String gameName)
    {
        if(_gameList.containsKey(gameName))
        {
            trace("Game " + gameName + " Already Exists");
            return null;
        }
        
        trace("Creating game " + gameName);
        
        ChessGame game = new ChessGame();
        _gameList.put(gameName, game);
        return game;
    }

    ChessGame getGame(String gameName)
    {
        return _gameList.get(gameName);
    }
    
    void finishGame(String gameName)
    {
        _gameList.remove(gameName);
    }
}
