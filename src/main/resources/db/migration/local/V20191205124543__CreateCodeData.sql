-- Major 코드
CREATE TABLE [dbo].[el_major](
	[major_cd] [varchar](5) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[update_user_id] [varchar](30) NULL,
	[update_dt] [datetime2](7) NULL,
	[major_nm] [varchar](30) NOT NULL,
	[type] [varchar](1) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[major_cd] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA01', N'admin', CAST(N'2019-09-28T19:06:28.8330000' AS DateTime2), N'admin', CAST(N'2019-09-28T19:06:28.8330000' AS DateTime2), N'게시판 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA02', N'admin', CAST(N'2019-10-07T17:05:29.1900000' AS DateTime2), N'admin', CAST(N'2019-10-07T17:05:29.1900000' AS DateTime2), N'사용자 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BA03', N'admin', CAST(N'2019-10-07T18:55:25.5270000' AS DateTime2), N'admin', CAST(N'2019-10-07T18:55:25.5270000' AS DateTime2), N'사용 유무', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC01', N'admin', CAST(N'2019-09-28T19:06:47.1240000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:42:50.2510000' AS DateTime2), N'교육과정', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC02', N'admin', CAST(N'2019-10-01T17:49:54.9420000' AS DateTime2), N'admin', CAST(N'2019-10-01T17:49:54.9420000' AS DateTime2), N'문제 유형', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC03', N'admin', CAST(N'2019-10-02T00:47:01.4400000' AS DateTime2), N'admin', CAST(N'2019-10-02T00:47:01.4400000' AS DateTime2), N'강의구분', N'S')
INSERT [dbo].[el_major] ([major_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [major_nm], [type]) VALUES (N'BC04', N'admin', CAST(N'2019-10-11T10:50:43.3370000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:50:43.3370000' AS DateTime2), N'교육신청일자유형', N'S')
go

-- Minor 코드
CREATE TABLE [dbo].[el_minor](
	[minor_cd] [varchar](10) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[update_user_id] [varchar](30) NULL,
	[update_dt] [datetime2](7) NULL,
	[minor_nm] [varchar](30) NOT NULL,
	[seq] [int] NOT NULL,
	[major_cd] [varchar](5) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[minor_cd] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'0', N'admin', CAST(N'2019-10-07T19:08:08.1760000' AS DateTime2), N'admin', CAST(N'2019-10-07T19:08:08.1760000' AS DateTime2), N'No', 2, N'BA03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'1', N'admin', CAST(N'2019-10-07T19:08:21.5070000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:51:42.2080000' AS DateTime2), N'상시', 1, N'BC04')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'2', N'admin', CAST(N'2019-10-11T10:51:53.0050000' AS DateTime2), N'admin', CAST(N'2019-10-11T10:51:53.0050000' AS DateTime2), N'기간', 2, N'BC04')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BA0101', N'admin', CAST(N'2019-09-28T19:07:18.7850000' AS DateTime2), N'admin', CAST(N'2019-11-28T19:43:41.5830000' AS DateTime2), N'공지게시판', 1, N'BA01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BA0102', N'admin', CAST(N'2019-09-28T19:07:30.8850000' AS DateTime2), N'admin', CAST(N'2019-09-28T19:10:04.4400000' AS DateTime2), N'일반게시판', 2, N'BA01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BA0103', N'admin', CAST(N'2019-09-28T19:11:06.2650000' AS DateTime2), N'admin', CAST(N'2019-09-28T19:11:06.2650000' AS DateTime2), N'자료실', 3, N'BA01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0101', N'admin', CAST(N'2019-09-28T19:08:15.8590000' AS DateTime2), N'admin', CAST(N'2019-10-11T11:09:05.4860000' AS DateTime2), N'온라인', 1, N'BC01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0102', N'admin', CAST(N'2019-09-28T19:11:33.8820000' AS DateTime2), N'admin', CAST(N'2019-10-11T11:09:12.2870000' AS DateTime2), N'오프라인', 2, N'BC01')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0201', N'admin', CAST(N'2019-10-01T17:50:11.5380000' AS DateTime2), N'admin', CAST(N'2019-10-01T17:50:11.5380000' AS DateTime2), N'객관식', 1, N'BC02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0202', N'admin', CAST(N'2019-10-01T17:50:31.2880000' AS DateTime2), N'admin', CAST(N'2019-10-01T17:50:31.2880000' AS DateTime2), N'주관식', 2, N'BC02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0301', N'admin', CAST(N'2019-10-02T00:47:27.1040000' AS DateTime2), N'admin', CAST(N'2019-10-02T00:47:27.1040000' AS DateTime2), N'강의', 1, N'BC03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0302', N'admin', CAST(N'2019-10-02T12:46:30.1640000' AS DateTime2), N'admin', CAST(N'2019-10-23T12:58:49.8460000' AS DateTime2), N'시험', 2, N'BC03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'BC0303', N'admin', CAST(N'2019-10-02T12:46:50.9950000' AS DateTime2), N'admin', CAST(N'2019-10-02T12:46:50.9950000' AS DateTime2), N'설문', 3, N'BC03')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'O', N'admin', CAST(N'2019-10-07T17:06:28.7990000' AS DateTime2), N'admin', CAST(N'2019-10-07T17:06:28.7990000' AS DateTime2), N'외부사용자', 2, N'BA02')
INSERT [dbo].[el_minor] ([minor_cd], [insert_user_id], [insert_dt], [update_user_id], [update_dt], [minor_nm], [seq], [major_cd]) VALUES (N'U', N'admin', CAST(N'2019-10-07T17:06:17.1400000' AS DateTime2), N'admin', CAST(N'2019-10-17T21:22:18.0040000' AS DateTime2), N'내부직원', 1, N'BA02')
go

-- 사용자 Role
CREATE TABLE [dbo].[role](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[memo] [varchar](255) NULL,
	[name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[role] ([memo], [name]) VALUES (N'ADMIN', N'ROLE_ADMIN')
INSERT [dbo].[role] ([memo], [name]) VALUES (N'관리자', N'ROLE_MANAGER')
INSERT [dbo].[role] ([memo], [name]) VALUES (N'직원', N'ROLE_USER')
INSERT [dbo].[role] ([memo], [name]) VALUES (N'외부사용자', N'ROLE_OTHER')
go

-- 시스템 유저
CREATE TABLE [dbo].[account](
	[user_id] [varchar](30) NOT NULL,
	[email] [varchar](255) NULL,
	[enabled] [bit] NOT NULL,
	[name] [varchar](255) NULL,
	[password] [varchar](255) NULL,
	[token_expired] [bit] NOT NULL,
	[user_type] [varchar](255) NULL,
	[eng_name] [varchar](255) NULL,
	[com_job] [varchar](255) NULL,
	[com_position] [varchar](255) NULL,
	[indate] [varchar](255) NULL,
	[org_depart] [varchar](255) NULL,
	[parent_user_id] [varchar](30) NULL,
	[com_num] [varchar](255) NULL,
PRIMARY KEY CLUSTERED
(
	[user_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[account] ([user_id], [email], [enabled], [name], [password], [token_expired], [user_type], [eng_name], [com_job], [com_position], [indate], [org_depart], [parent_user_id], [com_num]) VALUES (N'admin', N'ks.hwang@safesoft.com', 1, N'Admin', N'$2a$10$pb7mkeFDGC7w4gzR7KiJzuIGEhvxyw6S9COtUGq8rn8APISnt37ku', 0, N'O', N'admin', '', '', N'2019-11-20', N'Registered Director', N'', N'admin')
go

--사용자 롤
CREATE TABLE [dbo].[account_roles](
	[user_id] [varchar](30) NOT NULL,
	[role_id] [bigint] NOT NULL
) ON [PRIMARY]
GO
INSERT [dbo].[account_roles] ([user_id], [role_id]) VALUES (N'admin', 1)
go

-- 과정 유형
CREATE TABLE [dbo].[el_course_master](
	[id] [varchar](10) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[course_name] [varchar](100) NOT NULL,
	[is_course_manger_approval] [varchar](255) NULL,
	[is_team_manger_approval] [varchar](255) NULL,
	[minor_cd] [varchar](10) NOT NULL,
	[day] [int] NOT NULL,
	[hour] [numeric](5, 2) NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [is_course_manger_approval], [is_team_manger_approval], [minor_cd], [day], [hour]) VALUES (N'BC0101', N'admin', CAST(N'2019-10-13T16:29:50.1190000' AS DateTime2), N'self-training', N'N', N'N', N'BC0101', 30, CAST(1.00 AS Numeric(5, 2)))
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [is_course_manger_approval], [is_team_manger_approval], [minor_cd], [day], [hour]) VALUES (N'BC0102', N'admin', CAST(N'2019-10-13T16:29:59.2190000' AS DateTime2), N'class training', N'Y', N'Y', N'BC0102', 0, CAST(0.00 AS Numeric(5, 2)))
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [is_course_manger_approval], [is_team_manger_approval], [minor_cd], [day], [hour]) VALUES (N'BC0103', N'admin', CAST(N'2019-10-13T16:30:08.9090000' AS DateTime2), N'부서별 교육', N'N', N'N', N'BC0102', 0, CAST(0.00 AS Numeric(5, 2)))
INSERT [dbo].[el_course_master] ([id], [insert_user_id], [insert_dt], [course_name], [is_course_manger_approval], [is_team_manger_approval], [minor_cd], [day], [hour]) VALUES (N'BC0104', N'admin', CAST(N'2019-10-13T16:30:18.6840000' AS DateTime2), N'외부 교육', N'N', N'Y', N'BC0102', 0, CAST(0.00 AS Numeric(5, 2)))
go

-- 기본 게시판 설정
CREATE TABLE [dbo].[el_border_master](
	[id] [varchar](10) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[border_name] [varchar](100) NOT NULL,
	[is_mail] [varchar](1) NOT NULL,
	[minor_cd] [varchar](10) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0101', N'admin', CAST(N'2019-10-11T10:38:55.4760000' AS DateTime2), N'공지사항', N'N', N'BA0101')
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0102', N'admin', CAST(N'2019-10-11T10:39:16.6510000' AS DateTime2), N'법규/가이드라인', N'N', N'BA0102')
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0103', N'admin', CAST(N'2019-10-11T10:39:33.8600000' AS DateTime2), N'자료실', N'N', N'BA0103')
INSERT [dbo].[el_border_master] ([id], [insert_user_id], [insert_dt], [border_name], [is_mail], [minor_cd]) VALUES (N'BA0104', N'admin', CAST(N'2019-10-16T15:54:13.4920000' AS DateTime2), N'What''s New', N'N', N'BA0102')

go

-- 전자결재 서식
CREATE TABLE [dbo].[el_document_template](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[insert_user_id] [varchar](30) NULL,
	[insert_dt] [datetime2](7) NULL,
	[content] [text] NULL,
	[is_course_manger_approval] [varchar](255) NULL,
	[is_team_manger_approval] [varchar](255) NULL,
	[title] [varchar](100) NOT NULL,
PRIMARY KEY CLUSTERED
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
INSERT [dbo].[el_document_template] ([insert_user_id], [insert_dt], [content], [is_course_manger_approval], [is_team_manger_approval], [title]) VALUES (N'admin', CAST(N'2019-11-19T13:43:50.7860000' AS DateTime2), N'<p><span style="font-family: 맑은 고딕; font-size: 10pt;"><strong><span style="font-size: 12pt;">1. 교육개요</span></strong></span></p>
<table width="634" class="MsoNormalTable" style="border: black; border-image: none; border-collapse: collapse; mso-border-alt: solid windowtext .5pt; mso-yfti-tbllook: 1184; mso-padding-alt: 0cm 5.4pt 0cm 5.4pt; mso-border-insideh: .5pt solid windowtext; mso-border-insidev: .5pt solid windowtext;" border="1" cellspacing="0" cellpadding="0">
<tbody>
<tr style="height: 14.2pt; mso-yfti-irow: 0; mso-yfti-firstrow: yes;">
<td width="142" height="33" style="padding: 0cm 5.4pt; border: 1.5pt solid black; border-image: none; width: 115.85pt;">
<p align="center" class="a" style="text-align: center;"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span style="line-height: 12pt; font-family: 맑은 고딕; font-size: 10pt; font-style: normal; font-weight: 700;">교육기간<span lang="EN-US"><!--?xml:namespace prefix = "o" ns = "urn:schemas-microsoft-com:office:office" /--><o:p></o:p></span></span></b></p></td>
<td width="463" height="33" style="border-width: 1.5pt 1.5pt 1.5pt 0px; border-style: solid solid solid none; border-color: black black black rgb(0, 0, 0); padding: 0cm 5.4pt; border-image: none; mso-border-left-alt: solid windowtext 1.5pt;">
<p align="center" class="a" style="text-align: center;"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 12pt; font-family: 맑은 고딕; font-size: 10pt; font-style: normal; font-weight: 700;">20<span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp;&nbsp; </span></span></b><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span style="line-height: 12pt; font-family: 맑은 고딕; font-size: 10pt; font-style: normal; font-weight: 700;">년 <span lang="EN-US"><span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp;</span></span>월 <span lang="EN-US"><span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp;</span></span>일 <span lang="EN-US" style="font-family: 맑은 고딕; font-size: 10pt;">~ 20<span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp;&nbsp; </span></span>년 <span lang="EN-US"><span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp;</span></span>월 <span lang="EN-US"><span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp;</span></span>일 <span lang="EN-US" style="font-family: 맑은 고딕; font-size: 10pt;">(<span style="font-family: 맑은 고딕; font-size: 10pt; mso-spacerun: yes;">&nbsp;&nbsp; </span>)</span>일간<span lang="EN-US"><o:p></o:p></span></span></b></p></td></tr>
<tr style="height: 14.2pt; mso-yfti-irow: 1;">
<td width="142" height="33" style="border-width: 0px 1.5pt 1.5pt; border-style: none solid solid; border-color: rgb(0, 0, 0) black black; padding: 0cm 5.4pt; border-image: none; width: 115.85pt; mso-border-top-alt: solid windowtext 1.5pt;">
<p align="center" class="a" style="text-align: center;"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span style="line-height: 12pt; font-family: 맑은 고딕; font-size: 10pt; font-style: normal; font-weight: 700;">교육장소<span lang="EN-US"><o:p></o:p></span></span></b></p></td>
<td width="463" height="33" style="border-width: 0px 1.5pt 1.5pt 0px; border-style: none solid solid none; border-color: rgb(0, 0, 0) black black rgb(0, 0, 0); padding: 0cm 5.4pt; mso-border-left-alt: solid windowtext 1.5pt; mso-border-top-alt: solid windowtext 1.5pt;">
<p align="center" class="a" style="text-align: center;"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 12pt; font-family: 나눔고딕; font-size: 10pt; font-style: normal; font-weight: 700;"><o:p><span style="font-family: 맑은 고딕; font-size: 10pt;">&nbsp;</span></o:p></span></b></p></td></tr>
<tr style="height: 14.2pt; mso-yfti-irow: 2; mso-yfti-lastrow: yes;">
<td width="142" height="33" style="border-width: 0px 1.5pt 1.5pt; border-style: none solid solid; border-color: rgb(0, 0, 0) black black; padding: 0cm 5.4pt; border-image: none; width: 115.85pt; mso-border-top-alt: solid windowtext 1.5pt;">
<p align="center" class="a" style="text-align: center;"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span style="line-height: 12pt; font-family: 맑은 고딕; font-size: 10pt; font-style: normal; font-weight: 700;">교육참석목적<span lang="EN-US"><o:p></o:p></span></span></b></p></td>
<td width="463" height="33" style="border-width: 0px 1.5pt 1.5pt 0px; border-style: none solid solid none; border-color: rgb(0, 0, 0) black black rgb(0, 0, 0); padding: 0cm 5.4pt; mso-border-left-alt: solid windowtext 1.5pt; mso-border-top-alt: solid windowtext 1.5pt;">
<p align="center" class="a" style="text-align: center;"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 12pt; font-family: 나눔고딕; font-size: 10pt; font-style: normal; font-weight: 700;"><o:p><span style="font-family: 맑은 고딕; font-size: 10pt;">&nbsp;</span></o:p></span></b></p></td></tr></tbody></table>
<p><span style="font-family: 맑은 고딕; font-size: 12pt;"><strong></strong></span>&nbsp;</p>
<p><span style="font-family: 맑은 고딕; font-size: 12pt;"><strong>2. 교육내용요약</strong></span></p>
<table class="MsoNormalTable" style="border: black; border-image: none; border-collapse: collapse; mso-border-alt: solid windowtext .5pt; mso-yfti-tbllook: 1184; mso-padding-alt: 0cm 5.4pt 0cm 5.4pt; mso-border-insideh: .5pt solid windowtext; mso-border-insidev: .5pt solid windowtext;" border="1" cellspacing="0" cellpadding="0">
<tbody>
<tr style="height: 377.3pt; mso-yfti-irow: 0; mso-yfti-firstrow: yes; mso-yfti-lastrow: yes;">
<td width="565" valign="top" style="padding: 0cm 5.4pt; border: 1.5pt solid black; border-image: none; width: 463.5pt; height: 377.3pt;"><a name="예스폼"></a>
<p class="a"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 12pt; font-family: 나눔고딕; font-size: 10pt; font-style: normal; font-weight: 700;"><o:p><span style="font-family: 맑은 고딕; font-size: 10pt;">&nbsp;</span></o:p></span></b></p></td></tr></tbody></table>
<p class="a"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 12pt; font-family: 나눔고딕; font-size: 10pt; font-style: normal; font-weight: 700;"><o:p><span style="font-family: 맑은 고딕; font-size: 10pt;">&nbsp;</span></o:p></span></b></p>
<p class="a"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 14pt; font-family: 나눔고딕; font-size: 11pt; font-style: normal; font-weight: 700; mso-bidi-font-size: 10.0pt; mso-bidi-font-family: 나눔고딕;"><span style="font-family: 맑은 고딕; font-size: 12pt; mso-list: Ignore;">3. 소감 및 향후 업무반영계획</span></span></b></p>
<table class="MsoNormalTable" style="border: black; border-image: none; border-collapse: collapse; mso-border-alt: solid windowtext .5pt; mso-yfti-tbllook: 1184; mso-padding-alt: 0cm 5.4pt 0cm 5.4pt; mso-border-insideh: .5pt solid windowtext; mso-border-insidev: .5pt solid windowtext;" border="1" cellspacing="0" cellpadding="0">
<tbody>
<tr style="height: 449.9pt; mso-yfti-irow: 0; mso-yfti-firstrow: yes; mso-yfti-lastrow: yes;">
<td width="565" valign="top" style="padding: 0cm 5.4pt; border: 1.5pt solid black; border-image: none; width: 463.5pt; height: 449.9pt;">
<p class="a"><b style="line-height: 12pt; font-family: &quot;바탕&quot;,serif; font-size: 10pt; font-style: normal; font-weight: 700; mso-bidi-font-weight: normal;"><span lang="EN-US" style="line-height: 12pt; font-family: 나눔고딕; font-size: 10pt; font-style: normal; font-weight: 700;"><o:p><span style="font-family: 맑은 고딕; font-size: 10pt;">&nbsp;</span></o:p></span></b></p></td></tr></tbody></table>
<p class="a"><span><span style="font-size: 10pt;">*외부 교육 완료 후</span><span lang="EN-US" style="font-family: 맑은 고딕; font-size: 10pt;"> 3</span><span style="font-size: 10pt;">일 이내 교육참석보고서를 작성하며,&nbsp;</span><span style="font-size: 10pt;">해당 교육은 ‘</span><span lang="EN-US" style="font-family: 맑은 고딕; font-size: 10pt;">Employee Training Log (SOP-TM0001_RD04)</span><span style="font-size: 10pt;">에 기록합니다. </span></span></p><!--EndFragment-->
<p><span style="font-family: 맑은 고딕; font-size: 8pt;">&nbsp;&nbsp;&nbsp; </span>&nbsp;</p>', N'N', N'Y', N'교육참석보고서')