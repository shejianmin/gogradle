/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.blindpirate.gogradle.vcs.git;

import com.github.blindpirate.gogradle.util.Assert;
import com.github.blindpirate.gogradle.vcs.VcsType;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.Optional;

public class GolangRepository {
    public static final String EMPTY_DIR = "GOGRADLE_EMPTY_DIR";
    public static final GolangRepository EMPTY_INSTANCE = new GolangRepository() {
        @Override
        public void all() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void root(Object root) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void url(Object urlOrClosure) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void dir(Object urlOrClosure) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void vcs(String vcs) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void emptyDir() {
            throw new UnsupportedOperationException();
        }

    };

    private boolean all;
    private Object rootPathPattern;
    private Object urlSubstitution;
    private Object dir;
    private VcsType vcsType = VcsType.GIT;

    public void all() {
        this.all = true;
    }

    public void root(Object root) {
        rootPathPattern = root;
    }

    public void dir(Object urlOrClosure) {
        dir = urlOrClosure;
    }

    public void url(Object urlOrClosure) {
        urlSubstitution = urlOrClosure;
    }

    public void vcs(String vcs) {
        Optional<VcsType> vcsOptional = VcsType.of(vcs);
        Assert.isTrue(vcsOptional.isPresent(), "Unknown vcs type: " + vcs);
        this.vcsType = vcsOptional.get();
    }

    public void emptyDir() {
        dir = EMPTY_DIR;
    }

    public VcsType getVcsType() {
        return vcsType;
    }

    public String getUrl(String name) {
        return substitute(name, urlSubstitution);
    }

    private String substitute(String name, Object valueOrClousure) {
        if (valueOrClousure instanceof String) {
            return (String) valueOrClousure;
        } else if (valueOrClousure instanceof Closure) {
            Closure closure = (Closure) valueOrClousure;
            return Assert.isNotNull(closure.call(name)).toString();
        } else {
            return null;
        }
    }

    public String getDir(String name) {
        return substitute(name, dir);
    }


    public boolean match(String name) {
        if (all) {
            return true;
        }

        Assert.isTrue(rootPathPattern != null);

        return nameMatch(name);
    }

    private boolean nameMatch(String name) {
        return (Boolean) InvokerHelper.invokeMethod(rootPathPattern, "isCase", name);
    }

}
