package ru.kfu.itis.cll.uima.consumer;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import ru.kfu.itis.cll.uima.commons.DocumentMetadata;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;

/**
 * Simply extracts 'path' part from a given source URI and adds ".xmi" suffix.
 * It means that opaque URIs are not supported.
 *
 * @author Rinat Gareev
 */
public class DefaultSourceURI2OutputFilePathFunction implements Function<DocumentMetadata, Path> {

    public static final String XMI_FILE_EXTENSION = ".xmi";

    @Override
    public Path apply(DocumentMetadata metaAnno) {
        String uriStr = metaAnno.getSourceUri();
        if (uriStr == null) {
            return null;
        }
        URI uri = URI.create(uriStr);
        if (uri.isOpaque()) {
            throw new IllegalArgumentException(format("Opaque URIs are not supported: %s", uri));
        }
        String path = uri.getPath();
        Preconditions.checkState(path != null, "URI path is null: " + uri);
        if (!path.endsWith(XMI_FILE_EXTENSION)) {
            path += XMI_FILE_EXTENSION;
        }
        return Paths.get(path);
    }
}
