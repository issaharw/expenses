package com.issahar.expenses.dao

import com.issahar.expenses.model.Patient
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.jdbi.v3.sqlobject.SqlObject
import org.jdbi.v3.sqlobject.config.RegisterRowMapper
import org.jdbi.v3.sqlobject.config.RegisterRowMappers
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transactional
import java.sql.ResultSet

@RegisterRowMappers(RegisterRowMapper(PatientMapper::class))
interface PatientDao : SqlObject, Transactional<PatientDao> {

    @SqlQuery(
        """select
            id,
            mrn,
            first_name,
            middle_name,
            last_name,
            gender,
            birth_date,
            creation_time
          from Patients
          where mrn = :mrn"""
    )
    fun getPatientByMrn(@Bind("mrn") mrn: String): Patient?

    @SqlQuery(
        """select
            id,
            mrn,
            first_name,
            middle_name,
            last_name,
            gender,
            birth_date,
            creation_time
          from Patients
          where id = :id"""
    )
    fun getPatientById(@Bind("id") id: Int): Patient?

    @SqlUpdate("""INSERT INTO Patients
          (
            mrn,
            first_name,
            middle_name,
            last_name,
            gender,
            birth_date,
            creation_time
          )
          VALUES (
            :mrn,
            :firstName,
            :middleName,
            :lastName,
            :gender,
            FROM_UNIXTIME(:birthDate * 0.001),
            FROM_UNIXTIME(:creationTime * 0.001)
          ) """
    )
    @GetGeneratedKeys
    fun addPatient(@BindBean patient: Patient): Int

    @SqlUpdate("""Delete from Patients""")
    fun deleteAllPatients()
}

class PatientMapper : RowMapper<Patient> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Patient {
        return Patient(
            resultSet.getInt("id"),
            resultSet.getString("mrn"),
            resultSet.getString("first_name"),
            resultSet.getString("middle_name"),
            resultSet.getString("last_name"),
            resultSet.getString("gender"),
            resultSet.getTimestamp("birth_date")?.time,
            resultSet.getTimestamp("creation_time").time
        )
    }
}