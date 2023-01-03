package utilities;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o==null || !(o instanceof Coordinate)) {
            return false;
        }
        Coordinate coordinate = (Coordinate) o;
        return (x==coordinate.x && y==coordinate.y);
    }

    @Override
    public int hashCode() {
        return x*10+y;
    }
}
