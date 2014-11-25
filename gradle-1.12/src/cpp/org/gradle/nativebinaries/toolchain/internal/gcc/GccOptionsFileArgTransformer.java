/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativebinaries.toolchain.internal.gcc;

import org.gradle.api.internal.tasks.compile.ArgWriter;
import org.gradle.nativebinaries.internal.BinaryToolSpec;
import org.gradle.nativebinaries.toolchain.internal.ArgsTransformer;
import org.gradle.nativebinaries.toolchain.internal.OptionsFileArgsTransformer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Uses an option file for arguments passed to GCC if possible.
 * Certain GCC options do not function correctly when included in an option file, so include these directly on the command line as well.
 */
class GccOptionsFileArgTransformer<T extends BinaryToolSpec> extends OptionsFileArgsTransformer<T> {
    private static final List<String> CLI_ONLY_ARGS = Arrays.asList("-m32", "-m64");

    public GccOptionsFileArgTransformer(ArgsTransformer<T> delegate) {
        super(ArgWriter.unixStyleFactory(), delegate);
    }

    @Override
    protected void transformArgs(List<String> input, List<String> output, File tempDir) {
        List<String> commandLineOnlyArgs = getCommandLineOnlyArgs(input);
        output.addAll(commandLineOnlyArgs);
        super.transformArgs(input, output, tempDir);
    }

    private List<String> getCommandLineOnlyArgs(List<String> allArgs) {
        List<String> commandLineOnlyArgs = new ArrayList<String>(allArgs);
        commandLineOnlyArgs.retainAll(CLI_ONLY_ARGS);
        return commandLineOnlyArgs;
    }
}
