package ru.kfu.itis.cll.uima.io;

import org.junit.Test;

import java.nio.file.Paths;

import static ru.kfu.itis.cll.uima.io.IoUtils.*;
import static org.junit.Assert.*;

/**
 * @author Rinat Gareev
 */
public class IoUtilsTest {

    @Test
    public void testAddExtension() {
        assertEquals(Paths.get("/home/user.ext"), addExtension(Paths.get("/home/user"), "ext"));
        assertEquals(Paths.get("home/user.ext"), addExtension(Paths.get("home/user"), "ext"));
        assertEquals(Paths.get("home/user.ext"), addExtension(Paths.get("home/user/"), "ext"));
        assertEquals(Paths.get("user.ext"), addExtension(Paths.get("user"), "ext"));
        assertEquals(Paths.get("user.ext"), addExtension(Paths.get("user/"), "ext"));
    }
}
