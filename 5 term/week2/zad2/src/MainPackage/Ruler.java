package MainPackage;

/**
 * Class let you to draw a ruler
 */
public class Ruler {
    /**
     * Public method to draw ruler for user
     * @param length define amount of sectors
     * @param deep define sector deepness
     */
    public void drawRuler(int length, int deep){
        deep*=2;
        drawRuler(length,deep,0);
    }

    /**
     * private method do draw ruler recursive include iterator
     * @param length define amount of sectors
     * @param deep define sector deepness
     * @param iterator sectors iterator
     */
    private void drawRuler(int length, int deep, int iterator){
        drawLine(deep, deep, iterator);
        if(length==0)
            return;
        drawInterval(deep - 2, deep);
        drawRuler(length - 1, deep,iterator + 1);
    }

    /**
     * Private method to draw ruler interval recursively
     * @param len amount of "-"
     * @param max that param help calculate distance before line
     */
    private static void drawInterval(int len, int max) {
        if (len > 1) {
            //System.out.println("interval 1: "+len);
            drawInterval(len - 2, max);
            drawLine(len, max);
            //System.out.println("interval 2: "+len);
            drawInterval(len - 2, max);
        }
    }

    /**
     * Private method to draw single line
     * @param len length of line
     * @param max that param help calculate distance before line
     * @param label sector number
     */
    private static void drawLine(int len, int max, int label)
    {
        //System.out.println("line deep: "+len);

        for (int i = 0; i < (max - len) / 2; i++)
            System.out.print(" ");
        for (int i =0; i < len;i++)
            System.out.print("-");
        if(label >= 0)
            System.out.print(" "+label);
        System.out.println("");
    }

    /**
     * private method to draw line without label
     * @param len length of line
     * @param max that param help calculate distance before line
     */
    private static void drawLine(int len, int max){
        drawLine(len, max,-1);
    }
}
