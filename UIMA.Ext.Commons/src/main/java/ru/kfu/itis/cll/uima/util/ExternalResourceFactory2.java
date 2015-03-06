/**
 * 
 */
package ru.kfu.itis.cll.uima.util;

import static org.apache.uima.fit.factory.ConfigurationParameterFactory.canParameterBeSet;
import static org.apache.uima.fit.factory.ExternalResourceFactory.PARAM_RESOURCE_NAME;
import static org.apache.uima.fit.factory.ExternalResourceFactory.PREFIX_SEPARATOR;
import static org.apache.uima.fit.factory.ExternalResourceFactory.createExternalResourceBinding;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.resource.ExternalResourceDescription;
import org.apache.uima.resource.metadata.ExternalResourceBinding;
import org.apache.uima.fit.factory.ConfigurationParameterFactory;
import org.apache.uima.fit.factory.ExternalResourceFactory;
import org.apache.uima.fit.internal.ExtendedExternalResourceDescription_impl;

/**
 * Work-around for ?bugs? in UIMAfit {@link ExternalResourceFactory}
 * 
 * @author Rinat Gareev (Kazan Federal University)
 * 
 */
public class ExternalResourceFactory2 {
	/**
	 * Create a new external resource binding.
	 * 
	 * @param aRes
	 *            the resource to bind to
	 * @param aBindTo
	 *            what key to bind to.
	 * @param aNestedRes
	 *            the resource that should be bound.
	 */
	public static void bindExternalResource(ExternalResourceDescription aRes, String aBindTo,
			ExternalResourceDescription aNestedRes) {
		if (!(aRes instanceof ExtendedExternalResourceDescription_impl)) {
			throw new IllegalArgumentException(
					"Nested resources are only supported on instances of ["
							+
							ExtendedExternalResourceDescription_impl.class.getName()
							+ "] which"
							+
							"can be created with uimaFIT's createExternalResourceDescription() methods.");
		}

		ExtendedExternalResourceDescription_impl extRes = (ExtendedExternalResourceDescription_impl) aRes;

		// Create a map of all bindings
		Map<String, ExternalResourceBinding> bindings = new HashMap<String, ExternalResourceBinding>();
		for (ExternalResourceBinding b : extRes.getExternalResourceBindings()) {
			bindings.put(b.getKey(), b);
		}

		// Create a map of all resources
		Map<String, ExternalResourceDescription> resources = new HashMap<String, ExternalResourceDescription>();
		for (ExternalResourceDescription r : extRes.getExternalResources()) {
			resources.put(r.getName(), r);
		}

		// For the current resource, add resource and binding
		ExternalResourceBinding extResBind = createExternalResourceBinding(aBindTo, aNestedRes);
		bindings.put(extResBind.getKey(), extResBind);
		resources.put(aNestedRes.getName(), aNestedRes);

		// Handle nested resources
		bindNestedResources(aNestedRes, bindings, resources);

		// Commit everything to the resource manager configuration
		extRes.setExternalResourceBindings(bindings.values());
		extRes.setExternalResources(resources.values());

	}

	/**
	 * Copy of private method ExternalResourceFactory#bindNestedResources.
	 * <p>
	 * Helper method to recursively bind resources bound to resources.
	 * 
	 * @param aRes
	 *            resource.
	 * @param aBindings
	 *            bindings already made.
	 * @param aResources
	 *            resources already bound.
	 */
	private static void bindNestedResources(ExternalResourceDescription aRes,
			Map<String, ExternalResourceBinding> aBindings,
			Map<String, ExternalResourceDescription> aResources) {
		// Handle nested resources
		if (aRes instanceof ExtendedExternalResourceDescription_impl) {
			ExtendedExternalResourceDescription_impl extRes = (ExtendedExternalResourceDescription_impl) aRes;

			// Tell the external resource its name. This is needed in order to find the resources
			// bound to this resource later on. Set only if the resource supports this parameter.
			// Mind that supporting this parameter is mandatory for resource implementing 
			// ExternalResourceAware.
			if (canParameterBeSet(extRes.getResourceSpecifier(), PARAM_RESOURCE_NAME)) {
				ConfigurationParameterFactory.setParameter(extRes.getResourceSpecifier(),
						PARAM_RESOURCE_NAME, aRes.getName());
			}

			// Create a map of all resources
			Map<String, ExternalResourceDescription> res = new HashMap<String, ExternalResourceDescription>();
			for (ExternalResourceDescription r : extRes.getExternalResources()) {
				res.put(r.getName(), r);
			}

			// Bind nested resources
			for (ExternalResourceBinding b : extRes.getExternalResourceBindings()) {
				// Avoid re-prefixing the resource name
				String key = b.getKey();
				if (!key.startsWith(aRes.getName() + PREFIX_SEPARATOR)) {
					key = aRes.getName() + PREFIX_SEPARATOR + b.getKey();
				}
				// Avoid unnecessary binding and an infinite loop when a resource binds to itself
				if (!aBindings.containsKey(key)) {
					// Mark the current binding as processed so we do not recurse
					aBindings.put(key, b);
					ExternalResourceDescription nestedRes = res.get(b.getResourceName());
					aResources.put(nestedRes.getName(), nestedRes);
					bindNestedResources(nestedRes, aBindings, aResources);
					// Set the proper key on the binding.
					b.setKey(key);
				}
			}
		}
	}
}
