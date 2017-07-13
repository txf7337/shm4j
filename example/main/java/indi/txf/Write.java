package indi.txf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by dabao on 2017/7/13.
 */
public class Write {
    public static void main(String[] args) throws Exception {
        SharedBuffer buffer = new SharedBuffer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false, true);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                System.out.print("Enter something:");
                String words = br.readLine();
                if (words.equals("exit")) {
                    System.exit(0);
                }
                buffer.writeBytes(words.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}