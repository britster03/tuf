class Point {
    int x;
    int y;

    Point(int x_, int y_){
        x = x_; y = y_;
    }
    Number similarity(Point other){
        // Computes the L2 norm between this point and " other " ,
         // and returns the result .
         return null;
    }
}

class ColorPoint extends Point {
    int color;
    float color_factor = 0.3f;

    ColorPoint(int x_, int y_, int color_){
        super(x_, y_); 
        color = color_;
    }
    Float similarity(ColorPoint other){
        // Computes the L2 norm between this point and " other " ,
 // including color in the distance computation . The
 // tradeoff between pointwise distance and color
// distance is controlled with color_factor .
        return null;
    }
}

public class App {
    public static void main(String[] args) {
        Point p = new Point(2,1);
        ColorPoint q = new ColorPoint(3, 5, 127);
        System.out.println(p.similarity(q));
        System.out.println(p.similarity(q));
    }
}