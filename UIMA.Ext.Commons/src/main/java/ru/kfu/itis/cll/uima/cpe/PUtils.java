/**
 * 
 */
package ru.kfu.itis.cll.uima.cpe;

import java.io.File;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.google.common.base.Function;

/**
 * Package-private utils.
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
class PUtils {
	static final Function<File, Resource> file2Resource = new Function<File, Resource>() {
		@Override
		public Resource apply(File f) {
			return new FileSystemResource(f);
		}
	};
}
