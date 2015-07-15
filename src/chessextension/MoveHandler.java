package chessextension;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSRuntimeException;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 *
 * @author Marc 'Darque' Taylor
 */
public class MoveHandler extends BaseClientRequestHandler {
    private static final String CMD_MOVE = "move";
    
    public static final String CODE_SUCCESS = "M00";
    public static final String CODE_NOT_YOUR_TURN = "M01";
    public static final String CODE_OFF_OF_BOARD = "M02";
    public static final String CODE_GAME_NOT_FOUND = "M03";
    public static final String CODE_PIECE_NOT_ALIVE = "M04";
    public static final String CODE_PIECE_NOT_YOURS = "M05";
    public static final String CODE_NOT_IN_GAME = "M06";
    public static final String CODE_SELF_ATTACK_IS_OFF = "M07";
    public static final String CODE_GAME_NOT_FULL = "M08";
        
    @Override
    public void handleClientRequest(User user, ISFSObject params)
    {
        if (!params.containsKey("gameName"))
            throw new SFSRuntimeException("movement requires parameter 'gameName'");
        if (!params.containsKey("piece"))
            throw new SFSRuntimeException("movement requires parameter 'gameName'");
        if (!params.containsKey("x"))
            throw new SFSRuntimeException("movement requires parameter 'x'");
        if (!params.containsKey("y"))
            throw new SFSRuntimeException("movement requires parameter 'y'");
        
        int x = params.getInt("x");
        int y = params.getInt("y");
        String pieceName = params.getUtfString("piece");
       
        String responseCode = CODE_SUCCESS;
        int playerNum = 0;
        
        ChessExtension gameExt = (ChessExtension) getParentExtension();
        ChessGame game = gameExt.getGame(params.getUtfString("gameName"));
        if(game == null)
        {
            responseCode = CODE_GAME_NOT_FOUND;
        }
        else
        {
            responseCode = game.movePiece(user.getName(), pieceName, x, y);
        }
        
        //Put the real codey stuff here.
        
        ISFSObject response = new SFSObject();
        response.putUtfString("responseCode", responseCode);
        response.putInt("playerNum", playerNum);
        response.putUtfString("pieceName", pieceName);
        response.putInt("x", x);
        response.putInt("y", y);
        send(CMD_MOVE, response, user);
    }
}
