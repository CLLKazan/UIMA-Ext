/**
 * 
 */
package ru.kfu.itis.issst.util.cli;

import java.io.File;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class FileValueValidator implements IValueValidator<File> {

	@Override
	public void validate(String name, File value) throws ParameterException {
		if (!value.isFile()) {
			throw new ParameterException(String.format(
					"%s is not an existing file", value));
		}
	}

}
