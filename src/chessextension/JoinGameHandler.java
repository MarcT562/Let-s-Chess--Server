package chessextension;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import java.util.ArrayList;

/**
 *
 * @author Marc 'Darque' Taylor
 */
public class JoinGameHandler extends BaseClientRequestHandler {
    private static final String CMD_START_GAME = "startGame";
    
    private static final String CODE_SUCCESS = "J00";
    private static final String CODE_GAME_DOES_NOT_EXIST = "C01";
    private static final String CODE_GAME_IS_FULL = "C02";
        
    @Override
    public void handleClientRequest(User user, ISFSObject params)
    {
        if (!params.containsKey("gameName"))
            throw new SFSRuntimeException("movement requires parameter 'gameName'");
        
        send("Okay", params, user);
        
        trace("Joining Game " + params.getUtfString("gameName") + " For User : " + user.getName());
        
        ISFSObject response = new SFSObject();
       
        String responseCode = CODE_SUCCESS;
        
        //Put the real codey stuff here.
        ChessExtension gameExt = (ChessExtension) getParentExtension();
        ChessGame game = gameExt.getGame(params.getUtfString("gameName"));
        if(game == null)
        {
            responseCode = CODE_GAME_DOES_NOT_EXIST;
            response.putInt("currentTurn", 1);
        }
        else
        {
            boolean inGame = true;
            if(!game.isUserInGame(user.getName()))
            {
                if(game.isFull())   //If they are not in the game and it is full, they can't join
                {
                    responseCode = CODE_GAME_IS_FULL;
                    inGame = false;
                }
                else    //If they are not in the game, and it isn't full. Join
                {
                    game.addUser(user);
                }
            }
            if(inGame)
            {
                game.addToObject(response, user);
                if(game.isOver())
                {
                    game.playerSeenGameOver(user.getName());
                    if(game.canCloseGame())
                    {
                        gameExt.finishGame(params.getUtfString("gameName"));
                    }   
                }
            }
        }
        
        trace("Joinied Game " + params.getUtfString("gameName") + " For User : " + user.getName() + " With response code : " + responseCode);
        
        response.putUtfString("responseCode", responseCode);
        send(CMD_START_GAME, response, user);
    }
}
