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
         * Creates a dependency provider for grpcNetty (io.grpc:grpc-netty)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGrpcNetty() {
            return create("grpcNetty");
    }

        /**
         * Creates a dependency provider for gson (com.google.code.gson:gson)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGson() {
            return create("gson");
    }

        /**
         * Creates a dependency provider for jacksonDatatypeJsr310 (com.fasterxml.jackson.datatype:jackson-datatype-jsr310)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJacksonDatatypeJsr310() {
            return create("jacksonDatatypeJsr310");
    }

        /**
         * Creates a dependency provider for jacksonModuleKotlin (com.fasterxml.jackson.module:jackson-module-kotlin)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJacksonModuleKotlin() {
            return create("jacksonModuleKotlin");
    }

        /**
         * Creates a dependency provider for jakartaAnnotationApi (jakarta.annotation:jakarta.annotation-api)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJakartaAnnotationApi() {
            return create("jakartaAnnotationApi");
    }

        /**
         * Creates a dependency provider for julToSl4j (org.slf4j:jul-to-slf4j)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJulToSl4j() {
            return create("julToSl4j");
    }

        /**
         * Creates a dependency provider for junitJupiterApi (org.junit.jupiter:junit-jupiter-api)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunitJupiterApi() {
            return create("junitJupiterApi");
    }

        /**
         * Creates a dependency provider for junitJupiterEngine (org.junit.jupiter:junit-jupiter-engine)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getJunitJupiterEngine() {
            return create("junitJupiterEngine");
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
         * Creates a dependency provider for logbackClassic (ch.qos.logback:logback-classic)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLogbackClassic() {
            return create("logbackClassic");
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
         * Creates a dependency provider for micronautDataHibernateJpa (io.micronaut.data:micronaut-data-hibernate-jpa)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautDataHibernateJpa() {
            return create("micronautDataHibernateJpa");
    }

        /**
         * Creates a dependency provider for micronautDataJdbc (io.micronaut.data:micronaut-data-jdbc)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautDataJdbc() {
            return create("micronautDataJdbc");
    }

        /**
         * Creates a dependency provider for micronautDataProcessor (io.micronaut.data:micronaut-data-processor)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautDataProcessor() {
            return create("micronautDataProcessor");
    }

        /**
         * Creates a dependency provider for micronautHttpClient (io.micronaut:micronaut-http-client)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautHttpClient() {
            return create("micronautHttpClient");
    }

        /**
         * Creates a dependency provider for micronautHttpValidation (io.micronaut:micronaut-http-validation)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautHttpValidation() {
            return create("micronautHttpValidation");
    }

        /**
         * Creates a dependency provider for micronautInjectJava (io.micronaut:micronaut-inject-java)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautInjectJava() {
            return create("micronautInjectJava");
    }

        /**
         * Creates a dependency provider for micronautJacksonDataBind (io.micronaut:micronaut-jackson-databind)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautJacksonDataBind() {
            return create("micronautJacksonDataBind");
    }

        /**
         * Creates a dependency provider for micronautJdbcHikari (io.micronaut.sql:micronaut-jdbc-hikari)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautJdbcHikari() {
            return create("micronautJdbcHikari");
    }

        /**
         * Creates a dependency provider for micronautKotlinRuntime (io.micronaut.kotlin:micronaut-kotlin-runtime)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautKotlinRuntime() {
            return create("micronautKotlinRuntime");
    }

        /**
         * Creates a dependency provider for micronautLiquibase (io.micronaut.liquibase:micronaut-liquibase)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautLiquibase() {
            return create("micronautLiquibase");
    }

        /**
         * Creates a dependency provider for micronautManagement (io.micronaut:micronaut-management)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautManagement() {
            return create("micronautManagement");
    }

        /**
         * Creates a dependency provider for micronautOpenApi (io.micronaut.openapi:micronaut-openapi)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautOpenApi() {
            return create("micronautOpenApi");
    }

        /**
         * Creates a dependency provider for micronautReactor (io.micronaut.reactor:micronaut-reactor)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautReactor() {
            return create("micronautReactor");
    }

        /**
         * Creates a dependency provider for micronautReactorHttpClient (io.micronaut.reactor:micronaut-reactor-http-client)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautReactorHttpClient() {
            return create("micronautReactorHttpClient");
    }

        /**
         * Creates a dependency provider for micronautTestJunit (io.micronaut.test:micronaut-test-junit5)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautTestJunit() {
            return create("micronautTestJunit");
    }

        /**
         * Creates a dependency provider for micronautTestRestAssured (io.micronaut.test:micronaut-test-rest-assured)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautTestRestAssured() {
            return create("micronautTestRestAssured");
    }

        /**
         * Creates a dependency provider for micronautValidation (io.micronaut:micronaut-validation)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getMicronautValidation() {
            return create("micronautValidation");
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
         * Creates a dependency provider for postgresql (org.postgresql:postgresql)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getPostgresql() {
            return create("postgresql");
    }

        /**
         * Creates a dependency provider for rxJava2 (io.reactivex.rxjava2:rxjava)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getRxJava2() {
            return create("rxJava2");
    }

        /**
         * Creates a dependency provider for swaggerAnnotations (io.swagger.core.v3:swagger-annotations)
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getSwaggerAnnotations() {
            return create("swaggerAnnotations");
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
