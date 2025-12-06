package edu.pk.jawolh.erecepta.patientrecordservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "edu.pk.jawolh.erecepta.patientrecordservice.repository",

        repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class
)
public class JpaConfig {}