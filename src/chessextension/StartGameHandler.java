package chessextension;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 *
 * @author Marc 'Darque' Taylor
 */
public class StartGameHandler extends BaseClientRequestHandler {
    private static final String CMD_START_GAME = "startGame";
    
    private static final String CODE_SUCCESS = "C00";
    private static final String CODE_GAME_ALREADY_EXISTS = "C01";
        
    @Override
    public void handleClientRequest(User user, ISFSObject params)
    {
        if (!params.containsKey("gameName"))
            throw new SFSRuntimeException("movement requires parameter 'gameName'");
        
        trace("Starting Game " + params.getUtfString("gameName") + " For User : " + user.getName());
        
        ISFSObject response = new SFSObject();
       
        String responseCode = CODE_SUCCESS;
        
        //Put the real codey stuff here.
        ChessExtension gameExt = (ChessExtension) getParentExtension();
        ChessGame game = gameExt.startGame(params.getUtfString("gameName"));
        if(game == null)
        {
            responseCode = CODE_GAME_ALREADY_EXISTS;
            response.putInt("currentTurn", 1);
        }
        else
        {
            game.addUser(user);
            game.addToObject(response, user);
        }
        
        trace("Started Game " + params.getUtfString("gameName") + " For User : " + user.getName() + " with Response Code : " + responseCode);
        
        response.putUtfString("responseCode", responseCode);
        send(CMD_START_GAME, response, user);
    }
}
