package com.sysco.masterdata_inbound.database;

import com.sysco.masterdata_inbound.exception.NonRetryableDatabaseException;
import com.sysco.masterdata_inbound.exception.RetryableDatabaseException;
import com.sysco.masterdata_inbound.sql.SqlStatement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseExecutor {

    private final JdbcTemplate jdbcTemplate;

    public void execute(SqlStatement statement) {

        try {

            jdbcTemplate.update(statement.sql(), statement.params().toArray());

        } catch (CannotGetJdbcConnectionException ex) {

            log.error("Database connection failure", ex);

            throw new RetryableDatabaseException("Database connection failure", ex);

        } catch (DataAccessResourceFailureException ex) {

            log.error("Database temporarily unavailable", ex);

            throw new RetryableDatabaseException("Database temporarily unavailable", ex);

        } catch (BadSqlGrammarException ex) {

            log.error("Invalid SQL generated", ex);

            throw new NonRetryableDatabaseException("Invalid SQL generated", ex);

        } catch (DataIntegrityViolationException ex) {

            log.error("Database constraint violation", ex);

            throw new NonRetryableDatabaseException("Database constraint violation", ex);

        } catch (Exception ex) {

            log.error("Unexpected database error", ex);

            throw new NonRetryableDatabaseException("Unexpected database error", ex);
        }
    }
}