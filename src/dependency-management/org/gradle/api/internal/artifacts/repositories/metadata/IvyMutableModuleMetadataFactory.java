/*
 * Copyright 2017 the original author or authors.
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
package org.gradle.api.internal.artifacts.repositories.metadata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ModuleVersionIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.internal.artifacts.ImmutableModuleIdentifierFactory;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.internal.component.external.descriptor.Artifact;
import org.gradle.internal.component.external.descriptor.Configuration;
import org.gradle.internal.component.external.model.ivy.DefaultMutableIvyModuleResolveMetadata;
import org.gradle.internal.component.external.model.ivy.IvyDependencyDescriptor;
import org.gradle.internal.component.external.model.ivy.MutableIvyModuleResolveMetadata;
import org.gradle.internal.component.model.DefaultIvyArtifactName;
import org.gradle.internal.component.model.Exclude;

import java.util.Collection;
import java.util.List;

public class IvyMutableModuleMetadataFactory implements MutableModuleMetadataFactory<MutableIvyModuleResolveMetadata> {
    private static final Configuration DEFAULT_CONFIGURATION = new Configuration(Dependency.DEFAULT_CONFIGURATION, true, true, ImmutableSet.<String>of());
    private static final List<Configuration> DEFAULT_CONFIGURATION_LIST = ImmutableList.of(DEFAULT_CONFIGURATION);
    private static final ImmutableSet<String> SINGLE_DEFAULT_CONFIGURATION_NAME = ImmutableSet.of(Dependency.DEFAULT_CONFIGURATION);

    private final ImmutableModuleIdentifierFactory moduleIdentifierFactory;
    private final ImmutableAttributesFactory attributesFactory;

    public IvyMutableModuleMetadataFactory(ImmutableModuleIdentifierFactory moduleIdentifierFactory, ImmutableAttributesFactory attributesFactory) {
        this.moduleIdentifierFactory = moduleIdentifierFactory;
        this.attributesFactory = attributesFactory;
    }

    @Override
    public MutableIvyModuleResolveMetadata create(ModuleComponentIdentifier from) {
        return create(from, ImmutableList.<IvyDependencyDescriptor>of());
    }

    public MutableIvyModuleResolveMetadata create(ModuleComponentIdentifier from, List<IvyDependencyDescriptor> dependencies) {
        return create(
            from,
            dependencies,
            DEFAULT_CONFIGURATION_LIST,
            createDefaultArtifact(from),
            ImmutableList.<Exclude>of());
    }

    public MutableIvyModuleResolveMetadata create(ModuleComponentIdentifier from,
                                                  List<IvyDependencyDescriptor> dependencies,
                                                  Collection<Configuration> configurationDefinitions,
                                                  Collection<? extends Artifact> artifactDefinitions,
                                                  Collection<? extends Exclude> excludes) {
        ModuleVersionIdentifier mvi = asVersionIdentifier(from);
        return new DefaultMutableIvyModuleResolveMetadata(
            attributesFactory,
            mvi,
            from,
            dependencies,
            configurationDefinitions,
            artifactDefinitions,
            excludes);
    }

    private ImmutableList<? extends Artifact> createDefaultArtifact(ModuleComponentIdentifier from) {
        return ImmutableList.of(new Artifact(new DefaultIvyArtifactName(from.getModule(), "jar", "jar"), SINGLE_DEFAULT_CONFIGURATION_NAME));
    }

    private ModuleVersionIdentifier asVersionIdentifier(ModuleComponentIdentifier from) {
        return moduleIdentifierFactory.moduleWithVersion(from.getGroup(), from.getModule(), from.getVersion());
    }

    @Override
    public MutableIvyModuleResolveMetadata missing(ModuleComponentIdentifier from) {
        MutableIvyModuleResolveMetadata metadata = create(from);
        metadata.setMissing(true);
        return metadata;
    }

}
