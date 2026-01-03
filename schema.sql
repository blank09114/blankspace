SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 사용자
DROP TABLE IF EXISTS user_tbl;
CREATE TABLE user_tbl (
  user_id VARCHAR(20) PRIMARY KEY,
  user_mail VARCHAR(255) NOT NULL UNIQUE,
  user_name VARCHAR(10) NOT NULL,
  user_pw VARCHAR(255) NOT NULL,
  pw_change_at DATETIME DEFAULT NULL,
  user_role VARCHAR(5) NOT NULL DEFAULT 'USER',
  user_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  user_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  is_blocked BOOLEAN NOT NULL DEFAULT FALSE,
  blocked_reason VARCHAR(20),
  CHECK (user_role IN ('USER','ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 로그인　기록
DROP TABLE IF EXISTS login_log_tbl;
CREATE TABLE login_log_tbl (
  login_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(20) NOT NULL
    REFERENCES user_tbl(user_id)
    ON UPDATE CASCADE,
  login_hash CHAR(64) NOT NULL,
  login_ip VARCHAR(255) NOT NULL,
  login_region VARCHAR(255) NOT NULL,
  login_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  logout_date DATETIME DEFAULT NULL,
  KEY ix_login_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 토큰
DROP TABLE IF EXISTS token_tbl;
CREATE TABLE token_tbl (
  token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(20) NOT NULL
    REFERENCES user_tbl(user_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  token_type VARCHAR(8) NOT NULL,
  token_hash CHAR(64) NOT NULL UNIQUE,
  expires_at DATETIME NOT NULL,
  used_at DATETIME DEFAULT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CHECK (token_type IN ('JOIN','RESET','WITHDRAW')),
  KEY token_user_lookup (user_id, expires_at, used_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 게시판
DROP TABLE IF EXISTS board_tbl;
CREATE TABLE board_tbl (
  board_id INT AUTO_INCREMENT PRIMARY KEY,
  board_name VARCHAR(10) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 카테고리
DROP TABLE IF EXISTS category_tbl;
CREATE TABLE category_tbl (
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  board_id INT NOT NULL
    REFERENCES board_tbl(board_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  category_name VARCHAR(10) NOT NULL,
  UNIQUE (board_id, category_name),
  KEY ix_category_board (board_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 게시글
DROP TABLE IF EXISTS post_tbl;
CREATE TABLE post_tbl (
  post_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  category_id INT NOT NULL
    REFERENCES category_tbl(category_id)
    ON UPDATE CASCADE,
  post_thumbnail_url TEXT,
  post_title VARCHAR(20) NOT NULL,
  post_sub_title VARCHAR(20),
  post_dtl_link TEXT,
  post_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  post_content TEXT,
  KEY post_list_category (category_id, post_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 소설
DROP TABLE IF EXISTS novel_tbl;
CREATE TABLE novel_tbl (
  novel_id INT AUTO_INCREMENT PRIMARY KEY,
  novel_type VARCHAR(2) NOT NULL,
  novel_origin VARCHAR(20),
  novel_name VARCHAR(20) NOT NULL,
  novel_cover_url TEXT,
  novel_intro TEXT,
  CHECK (novel_type IN ('1차','2차'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 회차
DROP TABLE IF EXISTS episode_tbl;
CREATE TABLE episode_tbl (
  episode_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  novel_id INT NOT NULL
    REFERENCES novel_tbl(novel_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  episode_name VARCHAR(20) NOT NULL,
  episode_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  episode_content TEXT,
  KEY episode_list (novel_id, episode_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 세계관
DROP TABLE IF EXISTS world_tbl;
CREATE TABLE world_tbl (
  world_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  novel_id INT NOT NULL
    REFERENCES novel_tbl(novel_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  world_category VARCHAR(3) NOT NULL,
  world_name VARCHAR(20) NOT NULL,
  world_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  world_content TEXT,
  CHECK (world_category IN ('세계관','캐릭터','기타')),
  KEY world_list (novel_id, world_id),
  KEY world_list_category (novel_id, world_category, world_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 댓글
DROP TABLE IF EXISTS comment_tbl;
CREATE TABLE comment_tbl (
  comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT
    REFERENCES post_tbl(post_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  episode_id BIGINT
    REFERENCES episode_tbl(episode_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  user_id VARCHAR(20) NOT NULL
    REFERENCES user_tbl(user_id)
    ON UPDATE CASCADE,
  comment_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  comment_content VARCHAR(500) NOT NULL,
  CHECK ((post_id IS NULL) <> (episode_id IS NULL)),
  KEY post_comment_list (post_id, comment_id),
  KEY episode_comment_list (episode_id, comment_id),
  KEY user_comment_list (user_id, comment_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 대댓글
DROP TABLE IF EXISTS recomment_tbl;
CREATE TABLE recomment_tbl (
  recomment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  comment_id BIGINT NOT NULL
    REFERENCES comment_tbl(comment_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  mention_user_id VARCHAR(20)
    REFERENCES user_tbl(user_id)
    ON UPDATE CASCADE ON DELETE SET NULL,
  author_user_id VARCHAR(20) NOT NULL
    REFERENCES user_tbl(user_id)
    ON UPDATE CASCADE,
  recomment_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  recomment_content VARCHAR(500) NOT NULL,
  KEY recomment_list (comment_id, recomment_id),
  KEY user_recomment_list (author_user_id, recomment_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 방명록
DROP TABLE IF EXISTS guestbook_tbl;
CREATE TABLE guestbook_tbl (
  guestbook_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  guestbook_user_id VARCHAR(20) NOT NULL
    REFERENCES user_tbl(user_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  guestbook_timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  guestbook_is_secret BOOLEAN NOT NULL DEFAULT FALSE,
  guestbook_content VARCHAR(500) NOT NULL,
  answer_timestamp DATETIME DEFAULT NULL,
  answer_content TEXT,
  KEY guestbook_user_list (guestbook_user_id, guestbook_timestamp)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;