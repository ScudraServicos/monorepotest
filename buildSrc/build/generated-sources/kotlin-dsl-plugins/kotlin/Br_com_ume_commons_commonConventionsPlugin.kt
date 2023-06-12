/**
 * Precompiled [br.com.ume.commons.common-conventions.gradle.kts][Br_com_ume_commons_common_conventions_gradle] script plugin.
 *
 * @see Br_com_ume_commons_common_conventions_gradle
 */
public
class Br_com_ume_commons_commonConventionsPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("Br_com_ume_commons_common_conventions_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
