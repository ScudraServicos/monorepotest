package br.com.ume.application.shared.db
import org.hibernate.Session
import org.hibernate.engine.spi.SharedSessionContractImplementor
import org.hibernate.id.UUIDGenerationStrategy
import java.util.UUID

class PostgreSQLUUIDGenerationStrategy : UUIDGenerationStrategy {

    override fun getGeneratedVersion() = 4

    override fun generateUUID(session: SharedSessionContractImplementor?): UUID {
        return (session as Session).doReturningWork { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("select uuid_generate_v4()").use { resultSet ->
                    while (resultSet.next()) {
                        return@doReturningWork resultSet.getObject(1) as UUID
                    }
                }
            }
            throw IllegalArgumentException("Can't fetch a new UUID")
        }
    }
}