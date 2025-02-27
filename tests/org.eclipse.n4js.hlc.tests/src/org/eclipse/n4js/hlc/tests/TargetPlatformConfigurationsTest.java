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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.n4js.N4JSGlobals;
import org.eclipse.n4js.hlc.base.BuildType;
import org.eclipse.n4js.hlc.base.ErrorExitCode;
import org.eclipse.n4js.hlc.base.ExitCodeException;
import org.eclipse.n4js.hlc.base.N4jscBase;
import org.eclipse.n4js.hlc.base.SuccessExitStatus;
import org.eclipse.n4js.utils.io.FileDeleter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Downloads, installs, compiles and runs 'express' for different target platform configurations.
 */
public class TargetPlatformConfigurationsTest extends AbstractN4jscTest {
	private static final String EXTERNAL_WITH_N4JSD_TPT = "external_with_n4jsd_tpt";

	File workspace;
	String wsRoot;
	String packages;
	File node_modules;

	/** Prepare workspace. */
	@Before
	public void setupWorkspace() throws IOException {
		workspace = setupWorkspace(EXTERNAL_WITH_N4JSD_TPT, true, N4JSGlobals.N4JS_RUNTIME);
		wsRoot = workspace.getAbsolutePath().toString();
		packages = wsRoot + "/packages";
		node_modules = new File(wsRoot, N4JSGlobals.NODE_MODULES);
	}

	/** Delete workspace. */
	@After
	public void deleteWorkspace() throws IOException {
		FileDeleter.delete(workspace.toPath(), true);
	}

	/**
	 * Test creating install location.
	 *
	 * @throws ExitCodeException
	 *             propagated from compiler in case of issues
	 */
	@Test
	public void testCompileCreatesInstallLocation() throws IOException, ExitCodeException {
		// force creating install location
		FileDeleter.delete(node_modules);

		final String[] args = {
				"--installMissingDependencies",
				"--projectlocations", packages,
				"--buildType", BuildType.allprojects.toString()
		};
		SuccessExitStatus status = new N4jscBase().doMain(args);
		assertEquals("Should exit with success", SuccessExitStatus.INSTANCE.code, status.code);
		assertTrue("install location was not created", node_modules.exists());
	}

	/**
	 * Test cleaning install location.
	 *
	 * @throws ExitCodeException
	 *             propagated from compiler in case of issues
	 */
	@Test
	public void testCompileCheckNodeModulesLocation() throws IOException, ExitCodeException {

		// force creating install location
		FileDeleter.delete(node_modules);

		Files.createDirectory(node_modules.toPath());
		File testFile = new File(node_modules, "tst.txt");
		testFile.createNewFile();
		assertTrue("setup error, test file should exist yet at " + testFile.getAbsolutePath(), testFile.exists());

		final String[] args = {
				"--clean",
				"--projectlocations", packages,
				"--buildType", BuildType.allprojects.toString()
		};
		SuccessExitStatus status = new N4jscBase().doMain(args);
		assertEquals("Should exit with success", SuccessExitStatus.INSTANCE.code, status.code);
		assertTrue("node_modules location missing at " + testFile.getAbsolutePath(), testFile.exists());
	}

	/**
	 * Test skip install when compiling without target platform file.
	 */
	@Test
	public void testCompileFailsIfNoDependenciesNotInstalled() {
		final String[] args = {
				"--projectlocations", packages,
				"--buildType", BuildType.allprojects.toString()
		};
		expectCompilerException(args, ErrorExitCode.EXITCODE_COMPILE_ERROR);
	}

	/**
	 * Test compiling with external libraries installation, then invoke compilation without installing. We expect second
	 * invocation to re-use installed external libraries in first invocation.
	 *
	 * @throws ExitCodeException
	 *             propagated from compiler in case of issues
	 */
	@Test
	public void testCompileWithInstallPlusCompileSkipInstall() throws ExitCodeException {
		final String[] argsInstall = {
				"--installMissingDependencies",
				"--projectlocations", packages,
				"--buildType", BuildType.allprojects.toString()
		};
		SuccessExitStatus statusInstall = new N4jscBase().doMain(argsInstall);
		assertEquals("Should exit with success", SuccessExitStatus.INSTANCE.code, statusInstall.code);
		assertTrue("install location was not created", node_modules.exists());

		final String[] argsSkipInstall = {
				// "--installMissingDependencies",
				"--projectlocations", packages,
				"--buildType", BuildType.allprojects.toString()
		};
		SuccessExitStatus statusSkipInstall = new N4jscBase().doMain(argsSkipInstall);
		assertEquals("Should exit with success", SuccessExitStatus.INSTANCE.code, statusSkipInstall.code);
		assertTrue("install location still exists", node_modules.exists());

	}
}
