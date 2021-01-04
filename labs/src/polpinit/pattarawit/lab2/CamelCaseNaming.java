package polpinit.pattarawit.lab2;

public class CamelCaseNaming {
    public static void main(String[] args) {
        if(args.length == 2){
            String firstWord = args[0];
            String secondWord = args[1];
            String result = "";
            result += stringProcess(firstWord);
            result += stringProcess(secondWord);
            System.out.println("The first word is " + firstWord);
            System.out.println("The second word is " + secondWord);
            System.out.println("The concatenate with camel case is " + result);
        } else {
            System.err.println("CamelCaseNaming : <First word> <Second word>");
        }
    }
    

    public static String stringProcess(String word){
        String s = "";
        s += (word.substring(0,1)).toUpperCase();
        if(word.length() >1) {
            s += (word.substring(1, word.length())).toLowerCase();
        }
        return s;
    }
}
