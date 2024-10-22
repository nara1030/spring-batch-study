package org.among.springbatchstudy.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class BasicBatchConfig {
    private static Logger logger = LoggerFactory.getLogger(BasicBatchConfig.class);

    @Bean
    public Job basicJob(JobRepository jobRepository, Step basicStep) {
        logger.info("----- Basic Job Execute -----");
        return new JobBuilder("basic-job", jobRepository)
                .start(basicStep)
                .build();
    }

    /**
     *
     * @param jobRepository
     * @param {basicTask 혹은 greetingTask}
     * @param platformTransactionManager
     * @return
     */
    @Bean
    public Step basicStep(JobRepository jobRepository, Tasklet greetingTask, PlatformTransactionManager platformTransactionManager) {
        logger.info("----- Basic Step Execute -----");
        return new StepBuilder("basic-step", jobRepository)
                .tasklet(greetingTask, platformTransactionManager)
                .build();
    }

//    @Bean
//    public Tasklet basicTask() {
//        return (contribution, chunkContext) -> {
//            logger.info("----- Basic Task Execute -----");
//            logger.info("Hello world");
//            return RepeatStatus.FINISHED;
//        };
//    }

    @Bean
    public BasicTask greetingTask() {
        return new BasicTask();
    }

    class BasicTask implements Tasklet, InitializingBean {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            logger.info("----- Basic Task Execute -----");
            logger.info("Hello world");
            return RepeatStatus.FINISHED;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            logger.info("----- After Properties Set -----");
        }
    }
}
