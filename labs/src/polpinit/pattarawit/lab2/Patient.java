package polpinit.pattarawit.lab2;

public class Patient {
    public static void main(String[] args){
        if(args.length == 3) {
            System.out.println("Patient's name is " + args[0]);
            System.out.println("Patient's age is " + args[1]);
            System.out.println(args[0] + " comes from " + args[2]);
        } else {
            System.err.println("Patient <patient name> <patient age> <country>");
        }
    }
}
