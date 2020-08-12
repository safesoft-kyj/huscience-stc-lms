create or alter view vw_course_account_simple
as
select a.id
	, b.org_depart
	, b.name
	, a.from_date
	, a.to_date
	, a.request_date
	, a.request_type
	, a.fn_status
	, a.course_status
	, a.is_commit
	, c.is_always
	, a.course_id
	, c.type_id
	, a.is_attendance
	, certificate_bind_date
	, a.insert_dt
from el_course_account a with(nolock)
	inner join account b with(nolock)
		on a.user_id = b.user_id
	inner join el_course c with(nolock)
		on a.course_id = c.id