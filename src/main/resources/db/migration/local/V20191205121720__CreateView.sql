CREATE view [dbo].[vw_user]
as
select a.*, isnull(b.org_depart, '') as org_depart
from (	select a.user_id
		, a.kor_name
		, a.eng_name
		, a.password
		,case
			when a.org_code5 <> '' then org_code5
			when a.org_code4 <> '' then org_code4
			when a.org_code3 <> '' then org_code3
			when a.org_code2 <> '' then org_code2
			else a.org_code1 end as org_code
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

GO

CREATE view [dbo].[vw_depart]
as
select org_code
	, isnull(p_org_code, '') as p_org_code
	, org_level
	, org_depart
	, isnull(org_order, '') as org_order
from ​gw.GWareNet10_Dtnc.dbo.tblDepart_1

GO


CREATE view [dbo].[vw_depart_tree]
as
select org_code as id
, p_org_code as pId
, org_depart as name
, '' as email
, 0 as [open]
, 0 as isParent
, '' as web
, 0 as isEmp
from gw.GWareNet10_Dtnc.dbo.tblDepart_1
union all
select user_id
, org_code as pId
, kor_name as name
, email
, 0 as [open]
, 0 as isParent
, '' as web
, 1 as isEmp
from vw_user

