/*
 * Copyright 2016-2018 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.galleon.featurepack.pkg.external.test;

import org.jboss.galleon.universe.galleon1.LegacyGalleon1Universe;
import org.jboss.galleon.ProvisioningException;
import org.jboss.galleon.config.FeaturePackConfig;
import org.jboss.galleon.config.ProvisioningConfig;
import org.jboss.galleon.creator.FeaturePackCreator;
import org.jboss.galleon.state.ProvisionedFeaturePack;
import org.jboss.galleon.state.ProvisionedState;
import org.jboss.galleon.test.PmProvisionConfigTestBase;
import org.jboss.galleon.test.util.fs.state.DirState;
/**
 *
 * @author Alexey Loubyansky
 */
public class OptionalExternalDependencyFromIncludedOnExcludedPackageTestCase extends PmProvisionConfigTestBase {

    @Override
    protected void createFeaturePacks(FeaturePackCreator creator) throws ProvisioningException {
        creator
        .newFeaturePack(LegacyGalleon1Universe.newFPID("org.pm.test:fp1", "1", "1.0.0.Final"))
            .addDependency("fp2-dep", FeaturePackConfig.builder(LegacyGalleon1Universe.newFPID("org.pm.test:fp2", "1", "1.0.0.Final").getLocation())
                    .setInheritPackages(false)
                    .excludePackage("p2")
                    .includePackage("p3")
                    .build())
            .newPackage("p1")
                .addDependency("fp2-dep", "p2", true)
                .writeContent("fp1/p1.txt", "p1")
                .getFeaturePack()
            .getCreator()
        .newFeaturePack(LegacyGalleon1Universe.newFPID("org.pm.test:fp2", "1", "1.0.0.Final"))
            .newPackage("p1", true)
                .writeContent("fp2/p1.txt", "p1")
                .getFeaturePack()
            .newPackage("p2")
                .writeContent("fp2/p2.txt", "p2")
                .getFeaturePack()
            .newPackage("p3")
                .writeContent("fp2/p3.txt", "p3")
                .getFeaturePack()
            .getCreator()
        .install();
    }

    @Override
    protected ProvisioningConfig provisioningConfig() throws ProvisioningException {
        return ProvisioningConfig.builder()
                .addFeaturePackDep(FeaturePackConfig.builder(LegacyGalleon1Universe.newFPID("org.pm.test:fp1", "1", "1.0.0.Final").getLocation())
                        .includePackage("p1").build())
                .build();
    }

    @Override
    protected ProvisionedState provisionedState() throws ProvisioningException {
        return ProvisionedState.builder()
                .addFeaturePack(ProvisionedFeaturePack.builder(LegacyGalleon1Universe.newFPID("org.pm.test:fp1", "1", "1.0.0.Final"))
                        .addPackage("p1")
                        .build())
                .addFeaturePack(ProvisionedFeaturePack.builder(LegacyGalleon1Universe.newFPID("org.pm.test:fp2", "1", "1.0.0.Final"))
                        .addPackage("p3")
                        .build())
                .build();
    }

    @Override
    protected DirState provisionedHomeDir() {
        return newDirBuilder()
                .addFile("fp1/p1.txt", "p1")
                .addFile("fp2/p3.txt", "p3")
                .build();
    }
}
