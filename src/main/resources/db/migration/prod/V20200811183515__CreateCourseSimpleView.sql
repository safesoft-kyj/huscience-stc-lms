create or alter view vw_course_simple
as
select a.id
	, title
	, status
	, request_from_date
	, request_to_date
	, from_date
	, to_date
	, case when request_from_date = request_to_date then request_to_date
		else request_from_date + '~' + request_to_date end request_date_prior
	, case when from_date = to_date then to_date
		else from_date + '~' + to_date end date_prior
	, (select count(*) from el_course_section where course_id = a.id) as section_cnt
	, (select count(*) from el_course_quiz where course_id = a.id) as quiz_cnt
	, (select count(*) from el_course_survey where course_id = a.id) as survey_cnt
	, (select count(*) from el_course_account where course_id = a.id) as account_cnt
	, active
	, is_always
	, a.type_id
	, b.course_name as type_name
	, case when is_always = '1' then '상시' else '기간교육' end course_type
	, a.insert_dt
	, is_certi
	, is_quiz
	, is_survey
from el_course a with(nolock)
	inner join el_course_master b with(nolock)
		on a.type_id = b.id