CREATE OR ALTER view [dbo].[vw_user]
as
select a.*
, isnull(b.org_depart, '') as org_depart
, isnull(c.org_depart, '') as org_team
from (	select a.user_id
		, a.kor_name
		, a.eng_name
		, a.password
		, a.org_code1 as org_code
		, a.org_code2 as org_team_code
		, a.com_position
		, a.duty
		, a.com_job
		, a.email
		, a.indate
		, a.outdate
		, a.appro_sign
		, a.isUse
		, isnull(a.com_num, '') as com_num
		from gw.GWareNet10_Dtnc.dbo.tbluser_1 a
		where a.isUse = 1
		and a.com_state = 1
		and a.isDelete = 0 ) a
			left outer join gw.GWareNet10_Dtnc.dbo.tblDepart_1 b
				on a.org_code = b.org_code
			left outer join gw.GWareNet10_Dtnc.dbo.tblDepart_1 c
				on a.org_team_code = c.org_code