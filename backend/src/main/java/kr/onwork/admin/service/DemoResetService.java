package kr.onwork.admin.service;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import kr.onwork.common.error.BusinessException;
import kr.onwork.common.error.ErrorCode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Service;

/**
 * 시연용 데이터 초기화. classpath의 db/reset.sql을 한 트랜잭션으로 실행해
 * 모든 데이터를 기본 더미 상태로 재시드하고, 개발팀장·기획팀장을 오늘 휴가로 설정한다.
 * 운영 기능이 아니라 발표 시연 편의를 위한 도구.
 */
@Service
public class DemoResetService {

    private static final String SCRIPT = "db/reset.sql";

    private final DataSource dataSource;

    public DemoResetService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** reset.sql을 원자적으로 실행(실패 시 전체 롤백 — DB는 기존 상태 유지). */
    public void reset() {
        EncodedResource script = new EncodedResource(new ClassPathResource(SCRIPT), StandardCharsets.UTF_8);
        try (Connection conn = dataSource.getConnection()) {
            boolean prevAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                ScriptUtils.executeSqlScript(conn, script);
                conn.commit();
            } catch (RuntimeException ex) {
                conn.rollback();
                throw new BusinessException(ErrorCode.INTERNAL_ERROR, "시연 초기화 실패: " + rootMessage(ex));
            } finally {
                conn.setAutoCommit(prevAutoCommit);
            }
        } catch (SQLException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "시연 초기화 연결 오류: " + ex.getMessage());
        }
    }

    private String rootMessage(Throwable t) {
        Throwable cur = t;
        while (cur.getCause() != null && cur.getCause() != cur) {
            cur = cur.getCause();
        }
        return cur.getMessage();
    }
}
