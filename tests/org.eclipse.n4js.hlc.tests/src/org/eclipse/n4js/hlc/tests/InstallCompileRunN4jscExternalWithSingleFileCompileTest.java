/**
 * Copyright (c) 2016 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.hlc.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.n4js.N4JSGlobals;
import org.eclipse.n4js.cli.helper.N4CliHelper;
import org.eclipse.n4js.hlc.base.BuildType;
import org.eclipse.n4js.hlc.base.ExitCodeException;
import org.eclipse.n4js.utils.io.FileDeleter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Downloads, installs, compiles and runs 'express'.
 */
public class InstallCompileRunN4jscExternalWithSingleFileCompileTest extends AbstractN4jscTest {
	File workspace;

	/** Prepare workspace. */
	@Before
	public void setupWorkspace() throws IOException {
		workspace = setupWorkspace("external_singleProjectOrFileCompile", true, N4JSGlobals.N4JS_RUNTIME);
	}

	/** Delete workspace. */
	@After
	public void deleteWorkspace() throws IOException {
		FileDeleter.delete(workspace.toPath(), true);
	}

	/**
	 * Test for checking the npm support in the headless case by downloading third party package, importing it and
	 * running it with Common JS.
	 */
	@Test
	public void testCompileAndRunWithExternalDependencies() throws IOException, ExitCodeException {
		final String wsRoot = workspace.getAbsolutePath().toString();
		final String packages = wsRoot + "/packages";
		final String fileToCompile = packages + "/external.project/src/Main.n4js";
		final String fileToRun = fileToCompile;

		final String[] args = {
				"--installMissingDependencies",
				"--runWith", "nodejs",
				"--run", fileToRun,
				"--projectlocations", packages, // include runtime library projects in packages folder
				"--buildType", BuildType.singlefile.toString(),
				fileToCompile
		};
		final String out = runAndCaptureOutput(args);
		N4CliHelper.assertExpectedOutput("Application was created!", out);
	}

}
