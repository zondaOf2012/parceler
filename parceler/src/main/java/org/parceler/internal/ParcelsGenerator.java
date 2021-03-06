/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler.internal;

import com.sun.codemodel.*;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.gen.AbstractRepositoryGenerator;
import org.androidtransfuse.gen.ClassGenerationUtil;
import org.androidtransfuse.gen.ClassNamer;
import org.androidtransfuse.gen.UniqueVariableNamer;
import org.parceler.Parcels;
import org.parceler.Repository;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class ParcelsGenerator extends AbstractRepositoryGenerator {

    public static final PackageClass PARCELS_NAME = new PackageClass(Parcels.PARCELS_PACKAGE, Parcels.PARCELS_NAME);
    public static final PackageClass REPOSITORY_NAME = new PackageClass(Parcels.PARCELS_PACKAGE, Parcels.PARCELS_REPOSITORY_NAME);

    private final ClassGenerationUtil generationUtil;
    private final ClassNamer classNamer;

    @Inject
    public ParcelsGenerator(ClassGenerationUtil generationUtil, ClassNamer classNamer, UniqueVariableNamer namer) {
        super(Repository.class, generationUtil, namer, REPOSITORY_NAME, Parcels.ParcelableFactory.class);
        this.generationUtil = generationUtil;
        this.classNamer = classNamer;
    }

    @Override
    protected JExpression generateInstance(JDefinedClass parcelsDefinedClass, JClass inputClass, JClass outputClass) throws JClassAlreadyExistsException {

        String innerClassName = classNamer.numberedClassName(inputClass).append(Parcels.IMPL_EXT).namespaced().build().getClassName();

        JDefinedClass factoryInnerClass = parcelsDefinedClass._class(JMod.PRIVATE | JMod.STATIC | JMod.FINAL, innerClassName);

        factoryInnerClass._implements(generationUtil.ref(Parcels.ParcelableFactory.class).narrow(inputClass));

        JMethod method = factoryInnerClass.method(JMod.PUBLIC, outputClass, Parcels.ParcelableFactory.BUILD_PARCELABLE);
        method.annotate(Override.class);
        JVar input = method.param(inputClass, "input");

        method.body()._return(JExpr._new(outputClass).arg(input));

        return JExpr._new(factoryInnerClass);
    }
}
