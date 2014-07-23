/**
 * 
 */
package ru.kfu.itis.issst.util.cli;

import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.BaseConverter;

/**
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ClassConverter extends BaseConverter<Class<?>> {

	public ClassConverter(String optionName) {
		super(optionName);
	}

	@Override
	public Class<?> convert(String value) {
		try {
			return Class.forName(value);
		} catch (ClassNotFoundException e) {
			throw new ParameterException(String.format(
					"Class %s does not exist", value));
		}
	}

}
