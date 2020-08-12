CREATE OR ALTER view vw_training_log_summary
as
with raw_data as (
	select a.user_id, a.name, a.org_depart, a.org_team, b.is_upload
		, count(b.user_id) as log_cnt
	from account a with(nolock)
		left outer join el_course_training_log b with(nolock)
			on a.user_id = b.user_id
	where b.is_upload is not null
	group by a.user_id, a.name, a.org_depart, a.org_team, b.is_upload
)
select user_id, name, org_depart, org_team
	, sum(upload_cnt) as upload_cnt
	, sum(training_cnt) as training_cnt
from (	select user_id, name, org_depart, org_team
			, case when is_upload = '1' then log_cnt else 0 end as upload_cnt
			, case when is_upload = '0' then log_cnt else 0 end as training_cnt
		from raw_data
	) as x
group by user_id, name, org_depart, org_team

GO

CREATE OR ALTER view vw_training_log_detail
as
select a.*, b.name, b.org_depart, b.org_team
from el_course_training_log a with(nolock)
	left outer join account b with(nolock)
	on a.user_id = b.user_id

GO

CREATE OR ALTER view [dbo].[vw_certificate_file_summary]
as
with raw_data as (
	select a.user_id, a.name
		, isnull(a.org_depart, '') as org_depart
		, isnull(a.org_team, '') as org_team
		, case when b.doc_id is null then '1' else '0' end as is_upload
		, count(b.user_id) as certi_cnt
	from account a with(nolock)
		left outer join el_certificate_file b with(nolock)
			on a.user_id = b.user_id
	where b.user_id is not null
	group by a.user_id, a.name, a.org_depart, a.org_team, (case when b.doc_id is null then '1' else '0' end)
)
select user_id, name, org_depart, org_team
	, sum(upload_cnt) as upload_cnt
	, sum(certi_cnt) as certi_cnt
from (	select user_id, name, org_depart, org_team
			, case when is_upload = '1' then certi_cnt else 0 end as upload_cnt
			, case when is_upload = '0' then certi_cnt else 0 end as certi_cnt
		from raw_data
	) as x
group by user_id, name, org_depart, org_team

GO

CREATE OR ALTER view [dbo].[vw_certificate_file_detail]
as
select a.*, b.name, b.org_depart, b.org_team
, case when a.doc_id is null then '1' else '0' end as is_upload
from el_certificate_file a with(nolock)
	left outer join account b with(nolock)
	on a.user_id = b.user_id