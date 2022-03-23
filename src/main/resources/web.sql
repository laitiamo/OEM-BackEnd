#sql("sys_url_role")
SELECT
*
FROM
oem7.sys_url_role
#end

#sql("sys_url")
SELECT
*
FROM
oem7.sys_url
#end

#sql("KEY_COLUMN_USAGE")
SELECT
KEY_COLUMN_USAGE.REFERENCED_TABLE_SCHEMA AS targetSchema,
KEY_COLUMN_USAGE.REFERENCED_TABLE_NAME AS targetTable,
KEY_COLUMN_USAGE.REFERENCED_COLUMN_NAME AS targetColumn
FROM
KEY_COLUMN_USAGE
WHERE
KEY_COLUMN_USAGE.CONSTRAINT_NAME LIKE '%fk%' AND
KEY_COLUMN_USAGE.TABLE_SCHEMA = 'oem7' AND
KEY_COLUMN_USAGE.TABLE_NAME = ? AND
KEY_COLUMN_USAGE.COLUMN_NAME = ?
#end

#sql("t_work")
SELECT
*
FROM
oem7.t_work
#end

#sql("t_score")
SELECT
*
FROM
oem7.t_score
#end

#sql("t_class")
SELECT
*
FROM
oem7.t_class
#end

#sql("t_gender")
SELECT
*
FROM
oem7.t_gender
#end

#sql("t_grade")
SELECT
*
FROM
oem7.t_grade
#end

#sql("t_major")
SELECT
*
FROM
oem7.t_major
#end

#sql("t_rank")
SELECT
*
FROM
oem7.t_rank
#end

#sql("t_review")
SELECT
*
FROM
oem7.t_review
#end

#sql("t_role")
SELECT
*
FROM
oem7.t_role
#end

#sql("t_student")
SELECT
*
FROM
oem7.t_student
#end

#sql("t_teacher")
SELECT
*
FROM
oem7.t_teacher
#end

#sql("t_user")
SELECT
*
FROM
oem7.t_user
#end

#sql("t_user_work")
SELECT
*
FROM
oem7.t_user_work
#end

#sql("t_user_role")
SELECT
*
FROM
oem7.t_user_role
#end

#sql("t_teacher_link_student")
SELECT
*
FROM
oem7.t_teacher_link_student
#end

#sql("t_video")
SELECT
*
FROM
oem7.t_video
#end

#sql("t_video_sign")
SELECT
*
FROM
oem7.t_video_sign
#end

#sql("t_source")
SELECT
*
FROM
oem7.t_source
#end

#sql("t_subject")
SELECT
*
FROM
oem7.t_subject
#end

#sql("t_inform")
SELECT
*
FROM
oem7.t_inform
#end


#sql("v_work_info")
SELECT
*
FROM
oem7.v_work_info
#end

#sql("v_student_info")
SELECT
*
FROM
oem7.v_student_info
#end

#sql("v_sign_info")
SELECT
*
FROM
oem7.v_sign_info
#end

#sql("v_student_info_teacher")
SELECT
*
FROM
oem7.v_student_info_teacher
#end

#sql("v_teacher_class_info")
SELECT
*
FROM
oem7.v_teacher_class_info
#end

#sql("v_source_info")
SELECT
*
FROM
oem7.v_source_info
#end

#sql("v_teacher_info")
SELECT
*
FROM
oem7.v_teacher_info
#end

#sql("v_video_info")
SELECT
*
FROM
oem7.v_video_info
#end

#sql("v_student_work")
SELECT
*
FROM
oem7.v_student_work
#end

#sql("v_teacher_work")
SELECT
*
FROM
oem7.v_teacher_work
#end

#sql("v_student_work_teacher")
SELECT
*
FROM
oem7.v_student_work_teacher
#end