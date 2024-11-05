-- extract-korean-footballer 배치 잡 실행 시 필요
-- player 테이블이 존재하면 삭제(batch 데이터베이스 내 생성이므로 batch 데이터베이스는 수동 생성 필요)
DROP TABLE IF EXISTS players;

-- player 테이블 생성
CREATE TABLE players (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255),
                         type VARCHAR(255),
                         nationality VARCHAR(255)
);