package ngga.ring.myblog.data.primary.db

import jakarta.persistence.EntityManagerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["ngga.ring.myblog.data.primary"], // Repository untuk primary
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
class PrimaryDatabaseConfig(
    private val primaryDatabaseProperties: PrimaryDatabaseProperties
) {

    @Primary
    @Bean(name = ["primaryDataSource"])
    fun primaryDataSource(): DataSource {
        return DataSourceBuilder.create()
            .url(primaryDatabaseProperties.url)
            .username(primaryDatabaseProperties.username)
            .password(primaryDatabaseProperties.password)
            .driverClassName(primaryDatabaseProperties.driverClassName)
            .build()
    }

    @Primary
    @Bean(name = ["primaryEntityManagerFactory"])
    fun primaryEntityManagerFactory(
        @Qualifier("primaryDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        val vendorAdapter = HibernateJpaVendorAdapter()

        val factoryBean = LocalContainerEntityManagerFactoryBean()
        factoryBean.dataSource = dataSource
        factoryBean.setPackagesToScan("ngga.ring.myblog.data.primary.db.entity")
        factoryBean.persistenceUnitName = "primaryPersistenceUnit"
        factoryBean.jpaVendorAdapter = vendorAdapter
        factoryBean.setJpaPropertyMap(
            mapOf(
                "hibernate.hbm2ddl.auto" to "update",
                "hibernate.dialect" to "org.hibernate.dialect.MySQLDialect" // Hibernate 7 pakai ini
            )
        )
        return factoryBean
    }

    @Primary
    @Bean(name = ["primaryTransactionManager"])
    fun primaryTransactionManager(
        @Qualifier("primaryEntityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
