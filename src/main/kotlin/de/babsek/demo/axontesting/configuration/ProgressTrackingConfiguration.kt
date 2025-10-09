package de.babsek.demo.axontesting.configuration

import com.opencqrs.framework.eventhandler.progress.JdbcProgressTracker
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
class ProgressTrackingConfiguration(
    private val dataSource: DataSource,
    private val transactionManager: PlatformTransactionManager,
) {

    @Bean
    fun jdbcProgressTracker(): JdbcProgressTracker =
        JdbcProgressTracker(dataSource, transactionManager).apply {
            setTablePrefix("EVENTHANDLER_")
        }
}
