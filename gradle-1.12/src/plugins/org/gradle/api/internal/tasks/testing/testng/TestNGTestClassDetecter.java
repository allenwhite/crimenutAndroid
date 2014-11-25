/*
 * Copyright 2010 the original author or authors.
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
package org.gradle.api.internal.tasks.testing.testng;

import org.gradle.api.internal.tasks.testing.detection.TestClassVisitor;
import org.gradle.api.internal.tasks.testing.detection.TestFrameworkDetector;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class TestNGTestClassDetecter extends TestClassVisitor {
    private boolean isAbstract;
    private String className;
    private String superClassName;
    private boolean test;

    TestNGTestClassDetecter(final TestFrameworkDetector detector) {
        super(detector);
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        isAbstract = (access & Opcodes.ACC_ABSTRACT) != 0;

        this.className = name;
        this.superClassName = superName;
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if ("Lorg/testng/annotations/Test;".equals(desc)) {
            test = true;
        }
        return null;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!isAbstract && !test) {
            return new TestNGTestMethodDetecter(this);
        } else {
            return null;
        }
    }

    public String getClassName() {
        return className;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public boolean isTest() {
        return test;
    }

    void setTest(boolean test) {
        this.test = test;
    }

    public String getSuperClassName() {
        return superClassName;
    }
}
