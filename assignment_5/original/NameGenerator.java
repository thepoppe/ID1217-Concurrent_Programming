package original;
import java.util.Random;

public class NameGenerator {
    static final Random random = new Random();
    static final String[] names = { "John Doe", "Jane Doe", "Alice Clark", "Bob Kyle", "Charlie Woods", "David Ire", "Eve Lee", "Frank Gordon", "Grace Hope" ,"Heidi Stein", "Isaac Turner", "Judy Knight", "Kevin Sander", "Linda Benedict", "Michael Carter", "Nancy Farrigan", "Oscar Von Gogh", "Peggy Strand", "Quincy Hope", "Rita McGalagher",
    "Samuel Adams", "Tara O'Conner", "Steve Michaelsson", "Tina Jackson", "Ursula Grey", "Victor Thomas", "Wendy Freewill", "Xander Xavier", "Yvonne Real", "Zack Zachary",
    "Amanda Hugankiss", "Ben Dover", "Chris P. Bacon", "Drew Peacock", "Eileen Dover", "Gail Forcewind", "Hugh Jass", "Ivana Tinkle", "Justin Time", "Kris P. Cream", "Lou Natic"};

    
    public static String generateName() {
        return names[random.nextInt(names.length)];
    }
}
