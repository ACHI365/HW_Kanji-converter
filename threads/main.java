package threads;

import java.util.List;
import java.util.stream.Collectors;

public class main {
    public static void main(String[] args) throws InterruptedException {
        List<String> temp =
                Task2.generate(0, 10000, 10000 );
        int i = 0;
        for (String s : temp) {
            System.out.println(s);
            i++;
        }
        System.out.println(temp.size());
        System.out.println(i);
    }
}
