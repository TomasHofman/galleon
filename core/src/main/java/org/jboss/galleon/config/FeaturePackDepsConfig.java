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
package org.jboss.galleon.config;

import java.util.Collection;
import java.util.Map;

import org.jboss.galleon.Errors;
import org.jboss.galleon.ProvisioningDescriptionException;
import org.jboss.galleon.universe.FeaturePackLocation;
import org.jboss.galleon.universe.FeaturePackLocation.ProducerSpec;
import org.jboss.galleon.universe.UniverseSpec;
import org.jboss.galleon.util.CollectionUtils;

/**
 *
 * @author Alexey Loubyansky
 */
public class FeaturePackDepsConfig extends ConfigCustomizations {

    protected final UniverseSpec defaultUniverse;
    protected final Map<String, UniverseSpec> universeSpecs;
    protected final Map<ProducerSpec, FeaturePackConfig> fpDeps;
    protected final Map<String, FeaturePackConfig> fpDepsByOrigin;
    private final Map<ProducerSpec, String> channelToOrigin;

    protected FeaturePackDepsConfig(FeaturePackDepsConfigBuilder<?> builder) throws ProvisioningDescriptionException {
        super(builder);
        this.fpDeps = CollectionUtils.unmodifiable(builder.fpDeps);
        this.fpDepsByOrigin = CollectionUtils.unmodifiable(builder.fpDepsByOrigin);
        this.channelToOrigin = builder.channelToOrigin;
        this.defaultUniverse = builder.defaultUniverse;
        this.universeSpecs = CollectionUtils.unmodifiable(builder.universeSpecs);
    }

    public boolean hasDefaultUniverse() {
        return defaultUniverse != null;
    }

    public UniverseSpec getDefaultUniverse() {
        return defaultUniverse;
    }

    public boolean hasUniverse(String name) {
        return name == null ? defaultUniverse != null : universeSpecs.containsKey(name);
    }

    public UniverseSpec getUniverseSpec(String name) throws ProvisioningDescriptionException {
        final UniverseSpec universe = name == null ? defaultUniverse : universeSpecs.get(name);
        if(universe == null) {
            throw new ProvisioningDescriptionException((name == null ? "The default" : name) + " universe was not configured");
        }
        return universe;
    }

    public boolean hasUniverseNamedSpecs() {
        return !universeSpecs.isEmpty();
    }

    public Map<String, UniverseSpec> getUniverseNamedSpecs() {
        return universeSpecs;
    }

    public FeaturePackLocation getUserConfiguredSource(FeaturePackLocation fpSource) {
        final UniverseSpec universeSource = fpSource.getUniverse();
        if(defaultUniverse != null && defaultUniverse.equals(universeSource)) {
            return new FeaturePackLocation(null, fpSource.getProducerName(), fpSource.getChannelName(), fpSource.getFrequency(),
                    fpSource.getBuild());
        }
        for (Map.Entry<String, UniverseSpec> entry : universeSpecs.entrySet()) {
            if (entry.getValue().equals(universeSource)) {
                return new FeaturePackLocation(new UniverseSpec(entry.getKey(), null), fpSource.getProducerName(),
                        fpSource.getChannelName(), fpSource.getFrequency(), fpSource.getBuild());
            }
        }
        return fpSource;
    }

    public boolean hasFeaturePackDeps() {
        return !fpDeps.isEmpty();
    }

    public boolean hasFeaturePackDep(ProducerSpec producer) {
        return fpDeps.containsKey(producer);
    }

    public FeaturePackConfig getFeaturePackDep(ProducerSpec producer) {
        return fpDeps.get(producer);
    }

    public Collection<FeaturePackConfig> getFeaturePackDeps() {
        return fpDeps.values();
    }

    public FeaturePackConfig getFeaturePackDep(String origin) throws ProvisioningDescriptionException {
        final FeaturePackConfig fpDep = fpDepsByOrigin.get(origin);
        if(fpDep == null) {
            throw new ProvisioningDescriptionException(Errors.unknownFeaturePackDependencyName(origin));
        }
        return fpDep;
    }

    public String originOf(ProducerSpec producer) {
        return channelToOrigin.get(producer);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((channelToOrigin == null) ? 0 : channelToOrigin.hashCode());
        result = prime * result + ((defaultUniverse == null) ? 0 : defaultUniverse.hashCode());
        result = prime * result + ((fpDeps == null) ? 0 : fpDeps.hashCode());
        result = prime * result + ((fpDepsByOrigin == null) ? 0 : fpDepsByOrigin.hashCode());
        result = prime * result + ((universeSpecs == null) ? 0 : universeSpecs.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        FeaturePackDepsConfig other = (FeaturePackDepsConfig) obj;
        if (channelToOrigin == null) {
            if (other.channelToOrigin != null)
                return false;
        } else if (!channelToOrigin.equals(other.channelToOrigin))
            return false;
        if (defaultUniverse == null) {
            if (other.defaultUniverse != null)
                return false;
        } else if (!defaultUniverse.equals(other.defaultUniverse))
            return false;
        if (fpDeps == null) {
            if (other.fpDeps != null)
                return false;
        } else if (!fpDeps.equals(other.fpDeps))
            return false;
        if (fpDepsByOrigin == null) {
            if (other.fpDepsByOrigin != null)
                return false;
        } else if (!fpDepsByOrigin.equals(other.fpDepsByOrigin))
            return false;
        if (universeSpecs == null) {
            if (other.universeSpecs != null)
                return false;
        } else if (!universeSpecs.equals(other.universeSpecs))
            return false;
        return true;
    }
}
