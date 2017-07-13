package indi.txf;

/**
 * Created by dabao on 2017/7/13.
 */
public class Read {
    public static void main(String[] args) throws Exception {
        SharedBuffer buffer = new SharedBuffer(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false, false);
        while (true) {
            byte[] bytes = buffer.readBytes();
            if (bytes != null) {
                System.out.println(new String(bytes));
                for (byte b : bytes) {
                    b = 0;
                }
            }
            Thread.sleep(500);
        }
    }
}
