package ngga.ring.myblog.data.auth.db

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["ngga.ring.myblog.data.auth.repositories"],
    entityManagerFactoryRef = "authEntityManagerFactory",
    transactionManagerRef = "authTransactionManager"
)
class AuthDatabaseConfig(
    private val authDatabaseProperties: AuthDatabaseProperties
) {

    @Bean(name = ["authDataSource"])
    fun authDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(authDatabaseProperties.url)
            .username(authDatabaseProperties.username)
            .password(authDatabaseProperties.password)
            .driverClassName(authDatabaseProperties.driverClassName)
            .build()
    }

    @Bean(name = ["authEntityManagerFactory"])
    fun authEntityManagerFactory(
        @Qualifier("authDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val vendorAdapter = HibernateJpaVendorAdapter()

        val factoryBean = LocalContainerEntityManagerFactoryBean()
        factoryBean.dataSource = dataSource
        factoryBean.setPackagesToScan("ngga.ring.myblog.data.auth.db.entity")
        factoryBean.persistenceUnitName = "authPersistenceUnit"
        factoryBean.jpaVendorAdapter = vendorAdapter
        factoryBean.setJpaPropertyMap(
            mapOf(
                "hibernate.hbm2ddl.auto" to "update",
                "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect"
            )
        )
        return factoryBean
    }

    @Bean(name = ["authTransactionManager"])
    fun authTransactionManager(
        @Qualifier("authEntityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
