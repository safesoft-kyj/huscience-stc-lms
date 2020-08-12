CREATE OR ALTER view vw_course_self_report1
as
	select a.id
		, a.title
		, c.org_depart
		, c.org_team
		, b.user_id
		, c.name
		, b.course_status
		, b.is_commit
		, (select count(*) from el_course_section with(nolock) where course_id = a.id) as section_cnt
		, a.is_quiz
		, (select count(*) from el_course_quiz with(nolock) where course_id = a.id) as quiz_cnt
		, a.is_survey
		, (select count(*) from el_course_survey with(nolock) where course_id = a.id) as survey_cnt
		, a.is_certi
		, b.from_date
		, b.to_date
		, a.status
		, isnull(convert(varchar(10), f.complete_date, 121), '') as log_apply_date
		, f.is_upload
		--, case when g.name = f.training_course then '1' else '0' end as is_title_match
		--, g.image_size
		--, g.section_cnt
		--, g.section_rate
	from el_course a with(nolock)
		inner join el_course_account b with(nolock)
			on a.id = b.course_id
		left outer join account c with(nolock)
			on b.user_id = c.user_id
		left outer join el_course_training_log f with(nolock)
			on b.id = f.doc_no
		--left outer join el_course_section g
		--	on a.id = g.course_id
		--left outer join (
		--			select a.id, count(*) as section_cnt, max(image_current) as image_current, max(image_size) as image_size
		--		, case when max(image_size) = 0 then 0
		--			else convert(int, convert(decimal, max(image_current)) / max(image_size) * 100) end as section_rate
		--		from el_course_account a with(nolock)
		--			left outer join el_course_section_action b with(nolock)
		--				on a.id = b.doc_id
		--		group by a.id) g
		--	on b.id = g.id
	where a.type_id = 'BC0101'