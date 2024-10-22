package org.among.springbatchstudy.config;

import org.among.springbatchstudy.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ChunkBatchConfig {
    private static Logger logger = LoggerFactory.getLogger(ChunkBatchConfig.class);

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final DataSource dataSource;

    public ChunkBatchConfig(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager, DataSource dataSource) {
        this.jobRepository = jobRepository;
        this.platformTransactionManager = platformTransactionManager;
        this.dataSource = dataSource;
    }

    @Bean
    public Job save() {
        return new JobBuilder("extract-korean-footballer", jobRepository)
                .start(chunkStep())
//                .incrementer(new RunIdIncrementer()) // 왜 배치 두 번 실행 안 되지?
                .build();
    }

    @Bean
    public Step chunkStep() {
        return new StepBuilder("chunk-step", jobRepository)
                .<Player, Player> chunk(10, platformTransactionManager) // 제네릭 없으면 에러가 난다?
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemDatabaseWriter())
                .build();
    }

    @Bean
    public FlatFileItemReader<Player> itemReader() {
        Resource resource = new ClassPathResource("input/player-list.txt");

        return new FlatFileItemReaderBuilder<Player>()
                .name("read-player-list")
                .encoding("UTF-8")
                .resource(resource)
                .delimited().delimiter("|")
                .names(new String[] {"name", "type", "nationality"})
                .targetType(Player.class)
                .build();
    }

    @Bean
    public ItemProcessor<Player, Player> itemProcessor() {
        return item -> {
            logger.info("Processing player: {}", item);
            return "한국".equals(item.getNationality()) && "축구선수".equals(item.getType()) ? item : null;
        };
    }

//    @Bean
//    public ItemWriter<Player> itemConsoleWriter() {
//        return chunk -> {
//            for (Player player : chunk) {
//                logger.info("Player written: {} ({})", player, chunk.size());
//            }
//        };
//    }

    @Bean
    public JdbcBatchItemWriter<Player> itemDatabaseWriter() {
        JdbcBatchItemWriter<Player> itemWriter = new JdbcBatchItemWriter<>(); // 빌더 패턴 못 써?
        itemWriter.setDataSource(dataSource); // yml 파일 설정이 dataSource에 자동 주입돼?
        itemWriter.setSql("INSERT INTO Players (name, type, nationality) VALUES (:name, :type, :nationality)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return itemWriter;
    }
}
