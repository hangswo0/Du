import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Tests {
    String test(String input) throws Exception {
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        PrintStream printStr = new PrintStream(byteArr);
        System.setOut(printStr);
        String[] args;
        if (!Objects.equals(input, "")) {
            args = input.split(" ");
        } else {
            args = new String[] {};
        }
        Du.main(args);
        String[] lines = byteArr.toString().split(System.lineSeparator());
        String actual = lines[lines.length - 1];
        return actual;
    }
    @Test
    void main() throws Exception {
        assertEquals("No filenames given", test(" "));
        assertEquals("386", test("input/7-minutnoe_chudo.txt"));
        assertEquals("386 603", test("input/7-minutnoe_chudo.txt input/Afera_v_Brunee.txt"));
        assertEquals(Integer.toString(1121629 / 1024), test("input/dir1")); //dir
        int fileSize = 10408;
        assertEquals(fileSize / 1024 + " KB", test("-h input/Ajbolit.txt"));
        assertEquals(Integer.toString((877333 + 412546) / 1024), test("-c input/(bb6)_Tanec_tenej.txt input/A_chto_dalbshe.txt"));
        assertNotEquals(test("--si 7-minutnoe_chudo.txt"), test("input/7-minutnoe_chudo.txt"));
    }
}
