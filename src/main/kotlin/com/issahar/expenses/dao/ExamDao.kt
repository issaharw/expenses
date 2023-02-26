package com.issahar.expenses.dao

import com.fasterxml.jackson.module.kotlin.readValue
import com.issahar.expenses.model.*
import com.issahar.expenses.util.*
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

@RegisterRowMappers(RegisterRowMapper(ExamMapper::class))
interface ExamDao : SqlObject, Transactional<ExamDao> {

    @SqlQuery(
        """select
            id,
            study_uid,
            patient_id,
            status,
            exam_time,
            user_data,
            performing,
            attending,
            accession_number,
            manufacturer_model,
            transducer_data,
            processing_function,
            creation_time
          from Exams
          where study_uid = :studyUid"""
    )
    fun getExam(@Bind("studyUid") studyUid: String): Exam?

    @SqlQuery(
        """select
            id,
            study_uid,
            patient_id,
            status,
            exam_time,
            user_data,
            creation_time
          from Exams """
    )
    fun getExams(): List<Exam>

    @SqlUpdate("""INSERT INTO Exams
          (
            study_uid,
            patient_id,
            status,
            exam_time,
            user_data,
            performing,
            attending,
            accession_number,
            manufacturer_model,
            transducer_data,
            processing_function,
            creation_time
          )
          VALUES (
          :studyUid,
          :patientId,
          :statusCode,
          FROM_UNIXTIME(:examTime * 0.001),
          :userDataAsString,
          :performing,
          :attending,
          :accessionNumber,
          :manufacturerModel,
          :transducerData,
          :processingFunction,
          FROM_UNIXTIME(:creationTime * 0.001)
          ) """
    )
    @GetGeneratedKeys
    fun addExam(@BindBean exam: Exam): Int

    @SqlUpdate("""Update Exams
        Set user_data = :userData
        Where study_uid = :studyUid
        """
    )
    fun updateUserData(@Bind("studyUid") studyUid: String, @Bind("userData") userData: String)

    @SqlUpdate("""Delete from Exams""")
    fun deleteAllExams()
}

class ExamMapper : RowMapper<Exam> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Exam {
        return Exam(
            resultSet.getInt("id"),
            resultSet.getString("study_uid"),
            resultSet.getInt("patient_id"),
            Status.fromValue(resultSet.getInt("status")),
            resultSet.getTimestamp("exam_time")?.time,
            resultSet.getString("user_data")?.let { mapper.readValue<ExamUserData>(it) },
            resultSet.getString("performing"),
            resultSet.getString("attending"),
            resultSet.getString("accession_number"),
            resultSet.getString("manufacturer_model"),
            resultSet.getString("transducer_data"),
            resultSet.getString("processing_function"),
            resultSet.getTimestamp("creation_time").time
        )
    }
}