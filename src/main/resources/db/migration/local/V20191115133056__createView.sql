CREATE TABLE [dbo].[tbl_depart_title](
	[num] [int] NOT NULL,
	[org_num] [int] NULL,
	[org_lng] [nvarchar](10) NULL,
	[org_title] [nvarchar](255) NULL,
 CONSTRAINT [PK_tbl_depart_title] PRIMARY KEY CLUSTERED
(
	[num] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO


CREATE TABLE [dbo].[tbl_depart](
	[num] [int] NOT NULL,
	[org_code] [nvarchar](20) NOT NULL,
	[p_org_code] [nvarchar](20) NULL,
	[org_level] [smallint] NULL,
	[org_depart] [nvarchar](50) NOT NULL,
	[domain_num] [int] NOT NULL,
	[org_order] [int] NULL,
	[org_sign] [nvarchar](20) NULL,
	[isGw] [bit] NULL,
 CONSTRAINT [PK_tbl_depart] PRIMARY KEY CLUSTERED
(
	[num] ASC,
	[org_code] ASC,
	[domain_num] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

CREATE TABLE [dbo].[tbl_user](
	[user_num] [int] NOT NULL,
	[domain_num] [int] NULL,
	[com_name] [nvarchar](50) NULL,
	[com_num] [nvarchar](50) NULL,
	[user_id] [nvarchar](20) NULL,
	[password] [nvarchar](255) NULL,
	[reg_date] [nvarchar](50) NULL,
	[isUse] [bit] NULL,
	[isLogin] [bit] NULL,
	[user_grade] [smallint] NULL,
	[kor_name] [nvarchar](50) NULL,
	[ch_name] [nvarchar](50) NULL,
	[eng_name] [nvarchar](100) NULL,
	[ssn] [nvarchar](30) NULL,
	[birth] [nvarchar](10) NULL,
	[issolar] [bit] NULL,
	[sex] [bit] NULL,
	[email] [nvarchar](255) NULL,
	[MessengerID] [nvarchar](255) NULL,
	[nname] [nvarchar](50) NULL,
	[nemail] [nvarchar](255) NULL,
	[Path] [nvarchar](255) NULL,
	[MessageQuota] [money] NULL,
	[MailBoxQuota] [money] NULL,
	[PdsQuota] [money] NULL,
	[SmsQuota] [int] NULL,
	[cp] [nvarchar](255) NULL,
	[home_tel] [nvarchar](255) NULL,
	[home_zip] [nvarchar](30) NULL,
	[home_addr] [nvarchar](100) NULL,
	[com_tel] [nvarchar](255) NULL,
	[com_in_num] [nvarchar](50) NULL,
	[org_code1] [nvarchar](20) NULL,
	[org_code2] [nvarchar](20) NULL,
	[org_code3] [nvarchar](20) NULL,
	[org_code4] [nvarchar](20) NULL,
	[org_code5] [nvarchar](20) NULL,
	[org_code6] [nvarchar](20) NULL,
	[org_level] [smallint] NULL,
	[org_grade] [smallint] NULL,
	[com_position] [nvarchar](255) NULL,
	[duty] [nvarchar](255) NULL,
	[com_job] [nvarchar](255) NULL,
	[com_state] [smallint] NULL,
	[indate] [nvarchar](10) NULL,
	[outdate] [nvarchar](10) NULL,
	[my_picture] [nvarchar](255) NULL,
	[appro_sign] [nvarchar](50) NULL,
	[access_type] [tinyint] NULL,
	[isRep] [bit] NULL,
	[user_order] [int] NULL,
	[pw_term] [smallint] NULL,
	[pw_date] [datetime] NULL,
	[etc] [ntext] NULL,
	[ForwardList] [ntext] NULL,
	[original_table] [nvarchar](50) NULL,
	[original_num] [int] NULL,
	[isCheckUsage] [tinyint] NULL,
	[VOIP_TEL] [nvarchar](20) NULL,
	[isDepartAppro] [bit] NULL,
	[mailLimit] [int] NULL,
	[incomLimit] [int] NULL,
	[isMessenger] [bit] NULL,
	[Last_login] [datetime] NULL,
	[isusepop3] [bit] NULL,
	[isDelete] [bit] NULL,
	[isFlex] [bit] NULL,
	[last_update] [datetime] NULL,
	[work_bc] [varchar](10) NULL,
	[isGW] [bit] NULL,
	[ssodomain_num] [nvarchar](10) NULL,
 CONSTRAINT [PK_tbl_user] PRIMARY KEY NONCLUSTERED
(
	[user_num] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO


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


