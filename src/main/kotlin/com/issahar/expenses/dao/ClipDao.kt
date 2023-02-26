package com.issahar.expenses.dao

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
import com.fasterxml.jackson.module.kotlin.readValue
import com.ultrasight.cloud.model.*
import com.ultrasight.cloud.util.*
import org.jdbi.v3.sqlobject.customizer.BindList


@RegisterRowMappers(RegisterRowMapper(ClipMapper::class), RegisterRowMapper(ClipFullDataMapper::class))
interface ClipDao : SqlObject, Transactional<ExamDao> {

    @SqlQuery(
        """select
            id,
            instance_uid,
            exam_id,
            clip_time,
            clip_type,
            status,
            images_json,
            network_results,
            cine_rate,
            creation_time
          from Clips
          where instance_uid = :instanceUid"""
    )
    fun getClip(@Bind("instanceUid") instanceUid: String): Clip?

    @SqlQuery(
        """select
            p.*, 
            e.*,
            c.instance_uid,
            c.clip_type,
            c.status as clip_status,
            c.network_results as network_results,
            c.images_json as images_json,
            c.cine_rate
            from Patients p join Exams e on (p.id = e.patient_id) join Clips c on (e.id = c.exam_id)
          where e.study_uid = :studyUid"""
    )
    fun getExamClips(@Bind("studyUid") studyUid: String): List<ClipFullData>

    @SqlQuery(
        """select 
            p.*, 
            e.*,
            c.instance_uid,
            c.clip_type,
            c.status as clip_status,
            c.network_results as network_results,
            c.images_json as images_json,
            c.cine_rate
            from Patients p join Exams e on (p.id = e.patient_id) join Clips c on (e.id = c.exam_id)
            """
    )
    fun getFullClipDataData(): List<ClipFullData>


    @SqlUpdate("""INSERT INTO Clips
          (
            instance_uid,
            exam_id,
            clip_time,
            clip_type,
            status,
            images_json,
            cine_rate,
            creation_time
          )
          VALUES (
          :instanceUid,
          :examId,
          FROM_UNIXTIME(:clipTime * 0.001),
          :clipTypeValue,
          :statusCode,
          :imagesJsonAsString,
          :cineRate,
          FROM_UNIXTIME(:creationTime * 0.001)
          )"""
    )
    @GetGeneratedKeys
    fun addClip(@BindBean clip: Clip): Int

    @SqlUpdate("""UPDATE Clips
        SET 
        network_results = :networkResults , 
        status = :status
        WHERE id = :id"""
    )
    fun updateNetworkResultsAndStatus(@Bind("id") id: Int, @Bind("networkResults") results: String, @Bind("status") statusCode: Int)

    @SqlUpdate("""UPDATE Clips
        SET images_json = :imagesJsonAsString
        WHERE id = :id"""
    )
    fun updateClipImagesJson(@Bind("id") id: Int, @Bind("imagesJsonAsString") imagesJsonAsString: String)

    @SqlUpdate("""UPDATE Clips
        SET status = :status
        WHERE id = :id"""
    )
    fun updateClipStatus(@Bind("id") id: Int, @Bind("status") status: Int)

    @SqlUpdate("""Delete from Clips""")
    fun deleteAllClips()


    @SqlUpdate("""Delete from Clips
                  Where id in (<clipIds>)""")
    fun deleteClips(@BindList("clipIds") clipIds: List<Int>)

}

class ClipMapper : RowMapper<Clip> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): Clip {
        return Clip(
            resultSet.getInt("id"),
            resultSet.getString("instance_uid"),
            resultSet.getInt("exam_id"),
            resultSet.getTimestamp("clip_time")?.time,
            ClipType.fromValue(resultSet.getInt("clip_type")),
            ClipStatus.fromValue(resultSet.getInt("status")),
            resultSet.getString("images_json")?.let { mapper.readValue<List<UiFrame>>(it) },
            resultSet.getString("network_results")?.let { mapper.readValue<NetworkResults>(it) },
            resultSet.getInt("cine_rate"),
            resultSet.getTimestamp("creation_time").time
        )
    }
}

class ClipFullDataMapper : RowMapper<ClipFullData> {
    override fun map(resultSet: ResultSet, statementContext: StatementContext): ClipFullData {
        val firstName = resultSet.getString("first_name")
        val middleName = resultSet.getString("middle_name")
        val lastName = resultSet.getString("last_name")
        val fullName = getFullName(firstName, middleName, lastName)
        return ClipFullData(
            resultSet.getInt("p.id"),
            patientFirstName = firstName,
            patientMiddleName = middleName,
            patientLastName = lastName,
            resultSet.getString("gender"),
            resultSet.getTimestamp("birth_date")?.time?.formatDate(UI_BIRTHDATE_FORMAT),
            resultSet.getString("mrn"),
            resultSet.getInt("e.id"),
            resultSet.getString("study_uid"),
            resultSet.getTimestamp("exam_time")?.time?.formatDate(LICENSE_DATE_TIME_FORMAT),
            Status.fromValue(resultSet.getInt("status")),
            resultSet.getString("instance_uid"),
            ClipType.fromValue(resultSet.getInt("clip_type")),
            ClipStatus.fromValue(resultSet.getInt("clip_status")),
            resultSet.getString("network_results")?.let { mapper.readValue<NetworkResults>(it) },
            resultSet.getString("images_json")?.let { mapper.readValue<List<UiFrame>>(it) },
            resultSet.getInt("cine_rate")
            )
    }
}