package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the `libs` extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

        /**
         * Creates a dependency provider for googleCloudLogging (com.google.cloud:google-cloud-logging)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGoogleCloudLogging() {
            return create("googleCloudLogging");
    }

        /**
         * Creates a dependency provider for googleCloudPubSub (com.google.cloud:google-cloud-pubsub)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGoogleCloudPubSub() {
            return create("googleCloudPubSub");
    }

        /**
         * Creates a dependency provider for gson (com.google.code.gson:gson)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("gson");
    }

        /**
         * Creates a dependency provider for julToSl4j (org.slf4j:jul-to-slf4j)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJulToSl4j() {
            return create("julToSl4j");
    }

        /**
         * Creates a dependency provider for kotlinReflect (org.jetbrains.kotlin:kotlin-reflect)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKotlinReflect() {
            return create("kotlinReflect");
    }

        /**
         * Creates a dependency provider for kotlinStdlibJdk8 (org.jetbrains.kotlin:kotlin-stdlib-jdk8)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getKotlinStdlibJdk8() {
            return create("kotlinStdlibJdk8");
    }

        /**
         * Creates a dependency provider for logbackJackson (ch.qos.logback.contrib:logback-jackson)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLogbackJackson() {
            return create("logbackJackson");
    }

        /**
         * Creates a dependency provider for logbackJsonClassic (ch.qos.logback.contrib:logback-json-classic)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLogbackJsonClassic() {
            return create("logbackJsonClassic");
    }

        /**
         * Creates a dependency provider for mockito (org.mockito:mockito-core)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMockito() {
            return create("mockito");
    }

        /**
         * Creates a dependency provider for mockitoKotlin (org.mockito.kotlin:mockito-kotlin)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMockitoKotlin() {
            return create("mockitoKotlin");
    }

        /**
         * Creates a dependency provider for rxJava2 (io.reactivex.rxjava2:rxjava)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRxJava2() {
            return create("rxJava2");
    }

    /**
     * Returns the group of versions at versions
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Returns the group of bundles at bundles
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Returns the group of plugins at plugins
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class VersionAccessors extends VersionFactory  {

        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

            /**
             * Returns the version associated to this alias: googleCloudLoggingVersion (3.14.7)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGoogleCloudLoggingVersion() { return getVersion("googleCloudLoggingVersion"); }

            /**
             * Returns the version associated to this alias: googleCloudPubSubVersion (1.112.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGoogleCloudPubSubVersion() { return getVersion("googleCloudPubSubVersion"); }

            /**
             * Returns the version associated to this alias: grpcNettyVersion (1.46.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGrpcNettyVersion() { return getVersion("grpcNettyVersion"); }

            /**
             * Returns the version associated to this alias: gsonVersion (2.10.1)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getGsonVersion() { return getVersion("gsonVersion"); }

            /**
             * Returns the version associated to this alias: julToSl4jVersion (1.7.30)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJulToSl4jVersion() { return getVersion("julToSl4jVersion"); }

            /**
             * Returns the version associated to this alias: junitJupiterEngineVersion (3.8.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getJunitJupiterEngineVersion() { return getVersion("junitJupiterEngineVersion"); }

            /**
             * Returns the version associated to this alias: kotlinReflectVersion (1.6.10)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlinReflectVersion() { return getVersion("kotlinReflectVersion"); }

            /**
             * Returns the version associated to this alias: kotlinStdlibJdk8Version (1.6.10)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getKotlinStdlibJdk8Version() { return getVersion("kotlinStdlibJdk8Version"); }

            /**
             * Returns the version associated to this alias: logbackJacksonVersion (0.1.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLogbackJacksonVersion() { return getVersion("logbackJacksonVersion"); }

            /**
             * Returns the version associated to this alias: logbackJsonClassicVersion (0.1.5)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getLogbackJsonClassicVersion() { return getVersion("logbackJsonClassicVersion"); }

            /**
             * Returns the version associated to this alias: micronautTestJunitVersion (3.8.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMicronautTestJunitVersion() { return getVersion("micronautTestJunitVersion"); }

            /**
             * Returns the version associated to this alias: mockitoKotlinVersion (4.1.0)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getMockitoKotlinVersion() { return getVersion("mockitoKotlinVersion"); }

            /**
             * Returns the version associated to this alias: rxJava2Version (2.2.21)
             * If the version is a rich version and that its not expressible as a
             * single version string, then an empty string is returned.
             * This version was declared in catalog libs.versions.toml
             */
            public Provider<String> getRxJava2Version() { return getVersion("rxJava2Version"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}