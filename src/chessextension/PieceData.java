package chessextension;

/**
 *
 * @author Marc 'Darque' Taylor
 */
public class PieceData {
    private String _name;
    private String _type;
    private int _color, _x, _y;
    private boolean _isAlive;

    PieceData(String name, String type, int x, int y, int color, boolean isAlive) {
        _name = name;
        _type = type;
        _x = x;
        _y = y;
        _color = color;
        _isAlive = isAlive;
    }
    
    public String getName() { return _name; }
    public String getType() { return _type; }
    public int getColor() { return _color; }
    public int getX() { return _x; }
    public int getY() { return _y; }
    public boolean getIsAlive() { return _isAlive; }

    void setIsAlive(boolean value) { _isAlive = value; }
    void setLocation(int x, int y) {
        _x = x;
        _y = y;
    }

}
