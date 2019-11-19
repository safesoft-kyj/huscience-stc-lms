CREATE view vw_depart
as
select org_code
	, isnull(p_org_code, '') as p_org_code
	, org_level
	, org_depart
	, isnull(org_order, '') as org_order
from tbl_depart
GO

-- 사용자 정보
CREATE view vw_user
as
select a.*, b.org_depart
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
		from tbl_user a
		where a.isUse = 1
		and a.com_state = 1
		and a.isDelete = 0 ) a
			inner join tbl_depart b
				on a.org_code = b.org_code
GO


-- 조직도 정보
CREATE view vw_depart_tree
as
select org_code as id
, p_org_code as pId
, org_depart as name
, '' as email
, 0 as [open]
, 0 as isParent
, '' as web
, 0 as isEmp
from tbl_depart
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
GO


