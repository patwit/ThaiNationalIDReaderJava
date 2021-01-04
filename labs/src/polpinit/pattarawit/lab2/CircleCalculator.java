package polpinit.pattarawit.lab2;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CircleCalculator {
    public static void main(String[] args){
        if(args.length == 1 && Double.parseDouble(args[0]) >= 0){
            double radius = Double.parseDouble(args[0]);
            int places = 2;
            double area = round(Math.PI*radius*radius, places);
            double circumference = round(2*Math.PI*radius, places);
            System.out.println("An area of a circle with radius of " + radius + " is " + area );
            System.out.println("A circumference is " + circumference);
        } else {
            System.err.println("CircleCalculator <radius of a circle>");
        }
    }

    static double round(double number, int places){
        BigDecimal bd = new BigDecimal(Double.toString(number));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
