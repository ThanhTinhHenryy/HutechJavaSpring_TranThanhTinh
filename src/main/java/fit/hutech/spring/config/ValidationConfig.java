package fit.hutech.spring.config;  // hoặc package config nào cũng được

//import org.hibernate.validator.HibernateValidator;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
//import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
//
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;

/*
@Configuration
public class ValidationConfig {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;  // ← thay ApplicationContext bằng cái này

    @Bean
    public Validator validator() {
        ValidatorFactory factory = Validation.byProvider(HibernateValidator.class)
                .configure()
                .constraintValidatorFactory(new SpringConstraintValidatorFactory(beanFactory))  // ← truyền beanFactory
                .buildValidatorFactory();

        return factory.getValidator();
    }
}
*/
//@Configuration
//public class ValidationConfig {
//
//    @Bean
//    public LocalValidatorFactoryBean validatorFactory(ApplicationContext context) {
//        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
//        factory.setConstraintValidatorFactory(
//                new SpringConstraintValidatorFactory(
//                        context.getAutowireCapableBeanFactory()
//                )
//        );
//        return factory;
//    }
//}

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;

@Configuration
public class ValidationConfig {

    @Bean
    public LocalValidatorFactoryBean validator(ApplicationContext applicationContext) {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setConstraintValidatorFactory(
                new SpringConstraintValidatorFactory(applicationContext.getAutowireCapableBeanFactory())
        );
        return factoryBean;
    }
}