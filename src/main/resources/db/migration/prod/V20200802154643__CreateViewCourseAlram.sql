-- 외부교육참석보고서 3, 2, 1일 전 알림에서 참조하는 쿼리
CREATE OR ALTER view vw_course_bc0104_alram
as
with row_data
as (
	select a.id
		, b.to_date
		, DATEADD(day, 1, convert(date, b.to_date)) as day3
		, DATEADD(day, 2, convert(date, b.to_date)) as day2
		, DATEADD(day, 3, convert(date, b.to_date)) as day1
	from el_course_account a with(nolock)
		inner join el_course b with(nolock)
			on a.course_id = b.id
		left outer join el_document c with(nolock)
			on a.id = c.course_doc_id
	where b.type_id = 'BC0104'
	and c.id is null
)
	select id
		, Convert(varchar(10), DATEADD(day, dbo.fn_GetTotalHolidays(to_date, day3)+1, to_date), 121) as day3
		, Convert(varchar(10), DATEADD(day, dbo.fn_GetTotalHolidays(to_date, day2)+2, to_date), 121) as day2
		, Convert(varchar(10), DATEADD(day, dbo.fn_GetTotalHolidays(to_date, day1)+3, to_date), 121) as day1
	from row_data