package kr.io.blankspace.repository.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface UserActivityRepository extends JpaRepository<DummyEntityForJpa, Long> {
    interface Row {
        String getActivityType();
        Long getActivityId();
        String getContent();
        java.time.LocalDateTime getActivityAt();

        String getTargetUrl();
        Long getPostId();
        Long getEpisodeId();
        Integer getNovelId();

        Integer getIsSecret();
    }

    @Query(
        value = """
        SELECT * FROM (
            SELECT
                'GUESTBOOK' AS activity_type,
                g.guestbook_id AS activity_id,
                g.guestbook_content AS content,
                g.guestbook_timestamp AS activity_at,
                '/guestbook' AS target_url,
                NULL AS post_id,
                NULL AS episode_id,
                NULL AS novel_id,
                g.guestbook_is_secret AS is_secret
            FROM guestbook_tbl g WHERE g.guestbook_user_id = :userId

            UNION ALL

            SELECT
                'POST_COMMENT' AS activity_type,
                c.comment_id AS activity_id,
                c.comment_content AS content,
                c.comment_timestamp AS activity_at,
                p.post_dtl_link AS target_url,
                c.post_id AS post_id,
                NULL AS episode_id,
                NULL AS novel_id,
                0 AS is_secret
            FROM comment_tbl c
            JOIN post_tbl p ON p.post_id = c.post_id
            WHERE c.user_id = :userId AND c.post_id IS NOT NULL

            UNION ALL

            SELECT
                'EPISODE_COMMENT' AS activity_type,
                c.comment_id AS activity_id,
                c.comment_content AS content,
                c.comment_timestamp AS activity_at,
                CONCAT('/novel/', e.novel_id, '/episode/', e.episode_id) AS target_url,
                NULL AS post_id,
                c.episode_id AS episode_id,
                e.novel_id AS novel_id,
                0 AS is_secret
            FROM comment_tbl c
            JOIN episode_tbl e ON e.episode_id = c.episode_id
            WHERE c.user_id = :userId AND c.episode_id IS NOT NULL
        ) a WHERE (:type = 'ALL' OR a.activity_type = :type)
        ORDER BY a.activity_at DESC
        """,
        countQuery = """
        SELECT COUNT(*)
        FROM (
            SELECT 'GUESTBOOK' AS activity_type, g.guestbook_id AS activity_id, g.guestbook_timestamp AS activity_at
            FROM guestbook_tbl g
            WHERE g.guestbook_user_id = :userId

            UNION ALL

            SELECT 'POST_COMMENT' AS activity_type, c.comment_id AS activity_id, c.comment_timestamp AS activity_at
            FROM comment_tbl c
            WHERE c.user_id = :userId AND c.post_id IS NOT NULL

            UNION ALL

            SELECT 'EPISODE_COMMENT' AS activity_type, c.comment_id AS activity_id, c.comment_timestamp AS activity_at
            FROM comment_tbl c
            WHERE c.user_id = :userId AND c.episode_id IS NOT NULL
        ) a WHERE (:type = 'ALL' OR a.activity_type = :type)
        """,
        nativeQuery = true
    )
    Page<Row> findActivities(@Param("userId") String userId, @Param("type") String type, Pageable pageable);
}