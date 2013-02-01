// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2013 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.pluginmanager.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.pathvisio.pluginmanager.IPluginManager;

/**
 * implementation of the IPluginManager interface
 * functionality to install plugins, connecting to the repository,...
 * 
 * @author martina
 *
 */
public class PluginManager implements IPluginManager {

	private BundleContext context;
	
	public PluginManager (BundleContext context) {
		this.context = context;
	}
	
	/**
	 * method installs all OSGi bundles present in the
	 * provided directory
	 * returns a status report map with status information for each OSGi bundle
	 * 		key = bundle symbolic name (or file name if bundle couldn't be installed)
	 *      value = running / error message 
	 */
	public Map<String, String> runLocalPlugin(File bundleDir) {
		Map<String, String> statusReport = new HashMap<String, String>();
		List<Bundle> bundles = new ArrayList<Bundle>();
		if(bundleDir != null && bundleDir.isDirectory() && bundleDir.listFiles().length > 0) {
			for(File file : bundleDir.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					try {
						Bundle b = context.installBundle(file.toURI().toString());
						System.out.println ("Loading " + file.toURI());
		    			bundles.add(b);
					} catch (Exception ex) {
						statusReport.put(file.getName(), ex.getMessage());
						System.err.println ("Could not install bundle " + ex.getMessage());
					}
				}
			}
		} else {
			// TODO: throw exception --> directory is not valid or empty
		}
		
		for(Bundle b : bundles) {
			try {
				b.start();
				System.out.println("Bundle " + b.getSymbolicName() + " started");
				statusReport.put(b.getSymbolicName(), "running");
			} catch (BundleException e) {
				statusReport.put(b.getSymbolicName(), e.getMessage());
				System.err.println ("Could not start bundle " + b.getSymbolicName() + "\t" + e.getMessage());
				try {
					// necessary so it can be installed at a later point
					b.uninstall();
				} catch (BundleException e1) {}
			}
		}
		return statusReport;
	}
}