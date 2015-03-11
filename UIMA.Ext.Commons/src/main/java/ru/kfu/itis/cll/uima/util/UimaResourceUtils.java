package ru.kfu.itis.cll.uima.util;

import com.google.common.base.Preconditions;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.ResourceManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static java.lang.String.format;

/**
 * @author Rinat Gareev
 */
public class UimaResourceUtils {

    public static File resolveFile(String path, ResourceManager resMgr)
            throws URISyntaxException, MalformedURLException {
        Preconditions.checkArgument(path != null);
        if (resMgr == null) {
            resMgr = UIMAFramework.newDefaultResourceManager();
        }
        URL modelBaseURL = resMgr.resolveRelativePath(path);
        if (modelBaseURL == null)
            throw new IllegalStateException(format(
                    "Can't resolve path %s using an UIMA relative path resolver", path));
        return new File(modelBaseURL.toURI());
    }

    public static File resolveDirectory(String path, ResourceManager resMgr)
            throws MalformedURLException, URISyntaxException {
        File f = resolveFile(path, resMgr);
        if (!f.isDirectory()) {
            throw new IllegalStateException(format(
                    "Path '%s' is resolved into '%s' but it is not a directory", path, f));
        }
        return f;
    }

    private UimaResourceUtils() {
    }
}
