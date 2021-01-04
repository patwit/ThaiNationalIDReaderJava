package polpinit.pattarawit.lab2;

public class ChangeMachine {
    public static void main(String[] args){
        //accepts 1 baht coins
        // 2 baht coins
        // 5 baht coins
        // 10 baht coins
        if(args.length == 4){
            int one = Integer.parseInt(args[0]);
            int two = Integer.parseInt(args[1]);
            int five = Integer.parseInt(args[2]);
            int ten = Integer.parseInt(args[3]);
            int total = one + two*2 + five*5 + ten*10;
            // 48 + 120 + 500 + 1200
            // 48*1 + 60*2 + 100*5 + 120*10     48 60 100 120
            // 8 + 60 + 300 + 500 + 1000= 1868
            System.out.println("1-baht coins : " + one);
            System.out.println("2-baht coins : " + two);
            System.out.println("5-baht coins : " + five);
            System.out.println("10-baht coins : " + ten);
            System.out.println("Total amount : " + total);
            //return 20 baht bill
            // 100 baht bill
            // 500 baht bill
            // 1,000 baht bill
            int thousand = total/1000;
            int fiveHundred = (total%1000)/500;
            int oneHundred = (total%500)/100;
            int twenty = (total%100)/20;
            int remain = total%20;
            System.out.println("1,000-baht bill : " + thousand);
            System.out.println("500-baht bill : " + fiveHundred);
            System.out.println("100-baht bill : " + oneHundred);
            System.out.println("20-baht bill : " + twenty);
            System.out.println("Money remain : " + remain);
        } else {
            System.err.println("ChangeMachine <1-baht coins> <2-baht coins> <5-baht coins> <10-baht coins>");
        }
    }
}
